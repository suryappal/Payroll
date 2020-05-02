/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moonshot.payroll.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author surya
 */
@Entity
@Table(name = "role")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Role.findAll", query = "SELECT r FROM Role r")})
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RolePK rolePK;
    @Size(max = 45)
    @Column(name = "name")
    private String name;
    @JoinColumn(name = "tenant_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Tenant tenant;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private List<User> userList;

    public Role() {
    }

    public Role(RolePK rolePK) {
        this.rolePK = rolePK;
    }

    public Role(int id, int tenantId) {
        this.rolePK = new RolePK(id, tenantId);
    }

    public RolePK getRolePK() {
        return rolePK;
    }

    public void setRolePK(RolePK rolePK) {
        this.rolePK = rolePK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    @XmlTransient
    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rolePK != null ? rolePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Role)) {
            return false;
        }
        Role other = (Role) object;
        if ((this.rolePK == null && other.rolePK != null) || (this.rolePK != null && !this.rolePK.equals(other.rolePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.moonshot.payroll.entities.Role[ rolePK=" + rolePK + " ]";
    }
    
}
