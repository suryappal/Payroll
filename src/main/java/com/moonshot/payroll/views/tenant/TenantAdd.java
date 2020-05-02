/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moonshot.payroll.views.tenant;

import com.moonshot.payroll.DTO.TenantDTO;
import com.moonshot.payroll.response.PayrollResponseCode;
import com.moonshot.payroll.response.PayrollResponseMessage;
import com.moonshot.payroll.service.MasterDataService;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.eclipse.persistence.sessions.serializers.Serializer;

/**
 *
 * @author surya
 */
@Named(value = "tenantAdd")
@ViewScoped
public class TenantAdd implements Serializable  {

    /**
     * Creates a new instance of TenantAdd
     */
    
    int id;
    String name;
    
    public TenantAdd() {
    }
    
    public String addTenant(){
        String redirectUrl;
        TenantDTO tenantDTO = new TenantDTO();
        tenantDTO.setId(id);
        tenantDTO.setName(name);
        PayrollResponseMessage responseMessage = new PayrollResponseMessage();
        FacesMessage message;
        
        MasterDataService mds = new MasterDataService();
        int response =mds.addTenant(tenantDTO);
        
        if ( response != PayrollResponseCode.SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(response));
            redirectUrl = "TenantAdd";
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(response));
            redirectUrl = "TenantList";
        }       
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);
        
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

   
    
    
}
