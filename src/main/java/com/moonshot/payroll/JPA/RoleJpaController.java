/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moonshot.payroll.JPA;

import com.moonshot.payroll.JPA.exceptions.IllegalOrphanException;
import com.moonshot.payroll.JPA.exceptions.NonexistentEntityException;
import com.moonshot.payroll.JPA.exceptions.PreexistingEntityException;
import com.moonshot.payroll.entities.Role;
import com.moonshot.payroll.entities.RolePK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.moonshot.payroll.entities.Tenant;
import com.moonshot.payroll.entities.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author surya
 */
public class RoleJpaController implements Serializable {

    public RoleJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Role role) throws PreexistingEntityException, Exception {
        if (role.getRolePK() == null) {
            role.setRolePK(new RolePK());
        }
        if (role.getUserList() == null) {
            role.setUserList(new ArrayList<User>());
        }
        role.getRolePK().setTenantId(role.getTenant().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tenant tenant = role.getTenant();
            if (tenant != null) {
                tenant = em.getReference(tenant.getClass(), tenant.getId());
                role.setTenant(tenant);
            }
            List<User> attachedUserList = new ArrayList<User>();
            for (User userListUserToAttach : role.getUserList()) {
                userListUserToAttach = em.getReference(userListUserToAttach.getClass(), userListUserToAttach.getUserPK());
                attachedUserList.add(userListUserToAttach);
            }
            role.setUserList(attachedUserList);
            em.persist(role);
            if (tenant != null) {
                tenant.getRoleList().add(role);
                tenant = em.merge(tenant);
            }
            for (User userListUser : role.getUserList()) {
                Role oldRoleOfUserListUser = userListUser.getRole();
                userListUser.setRole(role);
                userListUser = em.merge(userListUser);
                if (oldRoleOfUserListUser != null) {
                    oldRoleOfUserListUser.getUserList().remove(userListUser);
                    oldRoleOfUserListUser = em.merge(oldRoleOfUserListUser);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRole(role.getRolePK()) != null) {
                throw new PreexistingEntityException("Role " + role + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Role role) throws IllegalOrphanException, NonexistentEntityException, Exception {
        role.getRolePK().setTenantId(role.getTenant().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Role persistentRole = em.find(Role.class, role.getRolePK());
            Tenant tenantOld = persistentRole.getTenant();
            Tenant tenantNew = role.getTenant();
            List<User> userListOld = persistentRole.getUserList();
            List<User> userListNew = role.getUserList();
            List<String> illegalOrphanMessages = null;
            for (User userListOldUser : userListOld) {
                if (!userListNew.contains(userListOldUser)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain User " + userListOldUser + " since its role field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (tenantNew != null) {
                tenantNew = em.getReference(tenantNew.getClass(), tenantNew.getId());
                role.setTenant(tenantNew);
            }
            List<User> attachedUserListNew = new ArrayList<User>();
            for (User userListNewUserToAttach : userListNew) {
                userListNewUserToAttach = em.getReference(userListNewUserToAttach.getClass(), userListNewUserToAttach.getUserPK());
                attachedUserListNew.add(userListNewUserToAttach);
            }
            userListNew = attachedUserListNew;
            role.setUserList(userListNew);
            role = em.merge(role);
            if (tenantOld != null && !tenantOld.equals(tenantNew)) {
                tenantOld.getRoleList().remove(role);
                tenantOld = em.merge(tenantOld);
            }
            if (tenantNew != null && !tenantNew.equals(tenantOld)) {
                tenantNew.getRoleList().add(role);
                tenantNew = em.merge(tenantNew);
            }
            for (User userListNewUser : userListNew) {
                if (!userListOld.contains(userListNewUser)) {
                    Role oldRoleOfUserListNewUser = userListNewUser.getRole();
                    userListNewUser.setRole(role);
                    userListNewUser = em.merge(userListNewUser);
                    if (oldRoleOfUserListNewUser != null && !oldRoleOfUserListNewUser.equals(role)) {
                        oldRoleOfUserListNewUser.getUserList().remove(userListNewUser);
                        oldRoleOfUserListNewUser = em.merge(oldRoleOfUserListNewUser);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RolePK id = role.getRolePK();
                if (findRole(id) == null) {
                    throw new NonexistentEntityException("The role with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(RolePK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Role role;
            try {
                role = em.getReference(Role.class, id);
                role.getRolePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The role with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<User> userListOrphanCheck = role.getUserList();
            for (User userListOrphanCheckUser : userListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Role (" + role + ") cannot be destroyed since the User " + userListOrphanCheckUser + " in its userList field has a non-nullable role field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Tenant tenant = role.getTenant();
            if (tenant != null) {
                tenant.getRoleList().remove(role);
                tenant = em.merge(tenant);
            }
            em.remove(role);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Role> findRoleEntities() {
        return findRoleEntities(true, -1, -1);
    }

    public List<Role> findRoleEntities(int maxResults, int firstResult) {
        return findRoleEntities(false, maxResults, firstResult);
    }

    private List<Role> findRoleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Role.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Role findRole(RolePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Role.class, id);
        } finally {
            em.close();
        }
    }

    public int getRoleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Role> rt = cq.from(Role.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
