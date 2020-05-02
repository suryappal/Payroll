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

/**
 *
 * @author surya
 */
@Embeddable
public class RolePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "tenant_id")
    private int tenantId;

    public RolePK() {
    }

    public RolePK(int id, int tenantId) {
        this.id = id;
        this.tenantId = tenantId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) tenantId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RolePK)) {
            return false;
        }
        RolePK other = (RolePK) object;
        if (this.id != other.id) {
            return false;
        }
        if (this.tenantId != other.tenantId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.moonshot.payroll.entities.RolePK[ id=" + id + ", tenantId=" + tenantId + " ]";
    }
    
}
