/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moonshot.payroll.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author surya
 */
@Embeddable
public class UserPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "id")
    private String id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "role_id")
    private int roleId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "role_tenant_id")
    private int roleTenantId;

    public UserPK() {
    }

    public UserPK(String id, int roleId, int roleTenantId) {
        this.id = id;
        this.roleId = roleId;
        this.roleTenantId = roleTenantId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getRoleTenantId() {
        return roleTenantId;
    }

    public void setRoleTenantId(int roleTenantId) {
        this.roleTenantId = roleTenantId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        hash += (int) roleId;
        hash += (int) roleTenantId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserPK)) {
            return false;
        }
        UserPK other = (UserPK) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        if (this.roleId != other.roleId) {
            return false;
        }
        if (this.roleTenantId != other.roleTenantId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.moonshot.payroll.entities.UserPK[ id=" + id + ", roleId=" + roleId + ", roleTenantId=" + roleTenantId + " ]";
    }
    
}
