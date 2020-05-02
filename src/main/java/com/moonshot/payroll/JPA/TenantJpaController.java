/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moonshot.payroll.JPA;

import com.moonshot.payroll.JPA.exceptions.IllegalOrphanException;
import com.moonshot.payroll.JPA.exceptions.NonexistentEntityException;
import com.moonshot.payroll.JPA.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.moonshot.payroll.entities.Role;
import com.moonshot.payroll.entities.Tenant;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author surya
 */
public class TenantJpaController implements Serializable {

    public TenantJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tenant tenant) throws PreexistingEntityException, Exception {
        if (tenant.getRoleList() == null) {
            tenant.setRoleList(new ArrayList<Role>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Role> attachedRoleList = new ArrayList<Role>();
            for (Role roleListRoleToAttach : tenant.getRoleList()) {
                roleListRoleToAttach = em.getReference(roleListRoleToAttach.getClass(), roleListRoleToAttach.getRolePK());
                attachedRoleList.add(roleListRoleToAttach);
            }
            tenant.setRoleList(attachedRoleList);
            em.persist(tenant);
            for (Role roleListRole : tenant.getRoleList()) {
                Tenant oldTenantOfRoleListRole = roleListRole.getTenant();
                roleListRole.setTenant(tenant);
                roleListRole = em.merge(roleListRole);
                if (oldTenantOfRoleListRole != null) {
                    oldTenantOfRoleListRole.getRoleList().remove(roleListRole);
                    oldTenantOfRoleListRole = em.merge(oldTenantOfRoleListRole);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTenant(tenant.getId()) != null) {
                throw new PreexistingEntityException("Tenant " + tenant + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tenant tenant) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tenant persistentTenant = em.find(Tenant.class, tenant.getId());
            List<Role> roleListOld = persistentTenant.getRoleList();
            List<Role> roleListNew = tenant.getRoleList();
            List<String> illegalOrphanMessages = null;
            for (Role roleListOldRole : roleListOld) {
                if (!roleListNew.contains(roleListOldRole)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Role " + roleListOldRole + " since its tenant field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Role> attachedRoleListNew = new ArrayList<Role>();
            for (Role roleListNewRoleToAttach : roleListNew) {
                roleListNewRoleToAttach = em.getReference(roleListNewRoleToAttach.getClass(), roleListNewRoleToAttach.getRolePK());
                attachedRoleListNew.add(roleListNewRoleToAttach);
            }
            roleListNew = attachedRoleListNew;
            tenant.setRoleList(roleListNew);
            tenant = em.merge(tenant);
            for (Role roleListNewRole : roleListNew) {
                if (!roleListOld.contains(roleListNewRole)) {
                    Tenant oldTenantOfRoleListNewRole = roleListNewRole.getTenant();
                    roleListNewRole.setTenant(tenant);
                    roleListNewRole = em.merge(roleListNewRole);
                    if (oldTenantOfRoleListNewRole != null && !oldTenantOfRoleListNewRole.equals(tenant)) {
                        oldTenantOfRoleListNewRole.getRoleList().remove(roleListNewRole);
                        oldTenantOfRoleListNewRole = em.merge(oldTenantOfRoleListNewRole);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tenant.getId();
                if (findTenant(id) == null) {
                    throw new NonexistentEntityException("The tenant with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tenant tenant;
            try {
                tenant = em.getReference(Tenant.class, id);
                tenant.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tenant with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Role> roleListOrphanCheck = tenant.getRoleList();
            for (Role roleListOrphanCheckRole : roleListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Tenant (" + tenant + ") cannot be destroyed since the Role " + roleListOrphanCheckRole + " in its roleList field has a non-nullable tenant field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(tenant);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tenant> findTenantEntities() {
        return findTenantEntities(true, -1, -1);
    }

    public List<Tenant> findTenantEntities(int maxResults, int firstResult) {
        return findTenantEntities(false, maxResults, firstResult);
    }

    private List<Tenant> findTenantEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tenant.class));
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

    public Tenant findTenant(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tenant.class, id);
        } finally {
            em.close();
        }
    }

    public int getTenantCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tenant> rt = cq.from(Tenant.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
