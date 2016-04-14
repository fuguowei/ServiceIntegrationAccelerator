/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.analysis;

/**
 *
 * @author fuguo
 */
public class Association {
    private String operation;
    private String masterEntity;

    public String getMasterEntity() {
        return masterEntity;
    }

    public String getOperation() {
        return operation;
    }

    public Association(String operation, String masterEntity) {
        this.operation = operation;
        this.masterEntity = masterEntity;
    }
    
}
