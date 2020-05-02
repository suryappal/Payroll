/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moonshot.payroll.views.tenant;

import com.moonshot.payroll.DTO.TenantDTO;
import com.moonshot.payroll.service.MasterDataService;
import java.io.Serializable;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author surya
 */
@Named(value = "tenantEdit")
@ViewScoped
public class TenantEdit implements Serializable {

    private int id;
    private String name;
    private TenantDTO editTenantDTO;

    public TenantEdit() {
    }

    public void fillEditTenantValues() {

        MasterDataService mds = new MasterDataService();
        editTenantDTO = mds.getTenantValue(id);

        id = editTenantDTO.getId();
        name = editTenantDTO.getName();

    }

    public String save() {

        String redirectUrl;
        TenantDTO tenantDTO = new TenantDTO();
        tenantDTO.setId(id);
        tenantDTO.setName(name);

        MasterDataService mds = new MasterDataService();
        mds.editTenant(tenantDTO);
        redirectUrl = "TenantList";
        return redirectUrl;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TenantDTO getEditTenantDTO() {
        return editTenantDTO;
    }

    public void setEditTenantDTO(TenantDTO editTenantDTO) {
        this.editTenantDTO = editTenantDTO;
    }

}
