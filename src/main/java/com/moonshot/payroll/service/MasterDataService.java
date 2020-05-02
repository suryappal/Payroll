/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moonshot.payroll.service;

import com.moonshot.payroll.DAO.TenantDAO;
import com.moonshot.payroll.DTO.TenantDTO;
import com.moonshot.payroll.JPA.exceptions.IllegalOrphanException;
import com.moonshot.payroll.JPA.exceptions.NonexistentEntityException;
import com.moonshot.payroll.JPA.exceptions.PreexistingEntityException;
import com.moonshot.payroll.entities.Role;
import com.moonshot.payroll.entities.Tenant;
import com.moonshot.payroll.response.PayrollResponseCode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author surya
 */
public class MasterDataService {
    
       private EntityManagerFactory emf;

    public MasterDataService() {

        emf = Persistence.createEntityManagerFactory("com.moonshot_Payroll_war_1.0-SNAPSHOTPU");
    }
    
    public int addTenant(TenantDTO tenantDTO){
        
        Tenant tenant = new Tenant();
        tenant.setId(tenantDTO.getId());
        tenant.setName(tenantDTO.getName());
        
        TenantDAO tenantDAO= new TenantDAO(emf);
           try {
               tenantDAO.create(tenant);
               return PayrollResponseCode.SUCCESS;
           } catch (PreexistingEntityException pe){
               return PayrollResponseCode.DB_DUPLICATE;
           } catch (Exception ex) {
               Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
               return PayrollResponseCode.SERVICE_CONNECTION_FAILURE;
           }
    }
    
    public void editTenant(TenantDTO tenantDTO){
        
        Tenant tenant = new Tenant();
        tenant.setId(tenantDTO.getId());
        tenant.setName(tenantDTO.getName());
        List<Role> roleList = new ArrayList<>();
        tenant.setRoleList(roleList);
        
        
        TenantDAO tenantDAO=new TenantDAO(emf);
        
           try {
               tenantDAO.edit(tenant);
           } catch (NonexistentEntityException ex) {
               Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
           } catch (Exception ex) {
               Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
           }
    }
   
    public void deleteTenant(TenantDTO tenantDTO){
        
        TenantDAO tenantDAO=new TenantDAO(emf);
           try {
               tenantDAO.destroy(tenantDTO.getId());
           } catch (IllegalOrphanException ex) {
               Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
           } catch (NonexistentEntityException ex) {
               Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
           }
        
    }

    
    public TenantDTO getTenantValue(int tenantId){
        
        TenantDAO tenantDAO = new TenantDAO(emf);        
        Tenant tenant = tenantDAO.findTenant(tenantId);
        TenantDTO tenantDTO = new TenantDTO();
        tenantDTO.setId(tenant.getId());
        tenantDTO.setName(tenant.getName());
        return tenantDTO;
    } 
    
    public List<TenantDTO> getTenantList(){
        TenantDAO tenantDAO = new TenantDAO(emf); 
        List<TenantDTO> tenantDTOList = new ArrayList<>();
        
        List<Tenant> tenantList = tenantDAO.findTenantEntities();
        
        for (Tenant tenant:tenantList) {
            TenantDTO tenantDTO= new TenantDTO();
            tenantDTO.setId(tenant.getId());
            tenantDTO.setName(tenant.getName());
            tenantDTOList.add(tenantDTO);
        }
        
        return tenantDTOList;
    }
    
    
}
