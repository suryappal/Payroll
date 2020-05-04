/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moonshot.payroll.response;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author surya
 */
public class PayrollResponseMessage {

    protected Map<Integer, String> responseMessageMap;

    public PayrollResponseMessage() {
        responseMessageMap = new HashMap<>();
        responseMessageMap.put(PayrollResponseCode.SUCCESS, "Success");
        responseMessageMap.put(PayrollResponseCode.SERVICE_CONNECTION_FAILURE, "Ki je hoche bojha jache na");
        responseMessageMap.put(PayrollResponseCode.DB_DUPLICATE, "Duplicate key in DB. Contact admin.");
        responseMessageMap.put(PayrollResponseCode.USER_AUTH_FAILURE, "User authentication failed.");
    }

    public String getResponseMessage (Integer responseCode) {
        return responseMessageMap.get(responseCode);
    }
    
    

}
