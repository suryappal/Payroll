/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moonshot.payroll.views.tenant;

import com.moonshot.payroll.DTO.TenantDTO;
import com.moonshot.payroll.service.MasterDataService;
import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author surya
 */
@Named(value = "tenantList")
@ViewScoped
public class TenantList implements Serializable {

    private int id;
    private String name;
    private List<TenantDTO> tenantDTOList;
    private TenantDTO selectedTenantDTO;

    public TenantList() {
    }

    public void fillTenantValues() {
        MasterDataService mds = new MasterDataService();
        tenantDTOList = mds.getTenantList();

    }
    
      public String delete() {

        String redirectUrl;  

        MasterDataService mds = new MasterDataService();
        mds.deleteTenant(selectedTenantDTO);
        redirectUrl = "TenantList";
        return redirectUrl;

    }

    public String goToEditTenant() {
        return "TenantEdit";
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

    public List<TenantDTO> getTenantDTOList() {
        return tenantDTOList;
    }

    public void setTenantDTOList(List<TenantDTO> tenantDTOList) {
        this.tenantDTOList = tenantDTOList;
    }

    public TenantDTO getSelectedTenantDTO() {
        return selectedTenantDTO;
    }

    public void setSelectedTenantDTO(TenantDTO selectedTenantDTO) {
        this.selectedTenantDTO = selectedTenantDTO;
    }

}
