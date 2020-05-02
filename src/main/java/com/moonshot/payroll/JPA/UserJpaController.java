/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moonshot.payroll.JPA;

import com.moonshot.payroll.JPA.exceptions.NonexistentEntityException;
import com.moonshot.payroll.JPA.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.moonshot.payroll.entities.Role;
import com.moonshot.payroll.entities.User;
import com.moonshot.payroll.entities.UserPK;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author surya
 */
public class UserJpaController implements Serializable {

    public UserJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) throws PreexistingEntityException, Exception {
        if (user.getUserPK() == null) {
            user.setUserPK(new UserPK());
        }
        user.getUserPK().setRoleId(user.getRole().getRolePK().getId());
        user.getUserPK().setRoleTenantId(user.getRole().getRolePK().getTenantId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Role role = user.getRole();
            if (role != null) {
                role = em.getReference(role.getClass(), role.getRolePK());
                user.setRole(role);
            }
            em.persist(user);
            if (role != null) {
                role.getUserList().add(user);
                role = em.merge(role);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUser(user.getUserPK()) != null) {
                throw new PreexistingEntityException("User " + user + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(User user) throws NonexistentEntityException, Exception {
        user.getUserPK().setRoleId(user.getRole().getRolePK().getId());
        user.getUserPK().setRoleTenantId(user.getRole().getRolePK().getTenantId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User persistentUser = em.find(User.class, user.getUserPK());
            Role roleOld = persistentUser.getRole();
            Role roleNew = user.getRole();
            if (roleNew != null) {
                roleNew = em.getReference(roleNew.getClass(), roleNew.getRolePK());
                user.setRole(roleNew);
            }
            user = em.merge(user);
            if (roleOld != null && !roleOld.equals(roleNew)) {
                roleOld.getUserList().remove(user);
                roleOld = em.merge(roleOld);
            }
            if (roleNew != null && !roleNew.equals(roleOld)) {
                roleNew.getUserList().add(user);
                roleNew = em.merge(roleNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                UserPK id = user.getUserPK();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(UserPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getUserPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            Role role = user.getRole();
            if (role != null) {
                role.getUserList().remove(user);
                role = em.merge(role);
            }
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
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

    public User findUser(UserPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
