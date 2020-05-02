/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moonshot.payroll.DAO;

import com.moonshot.payroll.JPA.TenantJpaController;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author surya
 */
public class TenantDAO extends TenantJpaController {

    public TenantDAO(EntityManagerFactory emf) {
        super(emf);
    }
       
    
}
