/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package qut.edu.au.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import qut.edu.au.analysis.Association;
import qut.edu.au.petrinet.logic.Petrinet;
import qut.edu.au.services.Operation;

/**
 *
 * @author sih
 */
public class BusinessEntity {
    private String name;
    private String type;
    private boolean compulsory;
    @JsonIgnore private Petrinet abstractCreateBehaviouralModel;
    @JsonIgnore private Petrinet actualCreateBehaviouralModel;
    @JsonIgnore private Petrinet abstractReadBehaviouralModel;
    @JsonIgnore private Petrinet actualReadBehaviouralModel;
    @JsonIgnore private Petrinet abstractUpdateBehaviouralModel;
    @JsonIgnore private Petrinet actualUpdateBehaviouralModel;
    @JsonIgnore private Petrinet abstractDeleteBehaviouralModel;
    @JsonIgnore private Petrinet actualDeleteBehaviouralModel;
    @JsonIgnore private Petrinet lifeCycle;
    private Attribute key;
    
    @JsonIgnore private Operation createOperation;
    @JsonIgnore private Operation updateOperation;
    @JsonIgnore private Operation deleteOperation;
    @JsonIgnore private ArrayList<Association> associations;
    @JsonIgnore private Operation readOperation;

    public ArrayList<Association> getAssociations() {
        return associations;
    }
    
    public void addAssociations(Association association) {
        this.associations.add(association);
    }
    
    

    
    public boolean isCompulsory() {
        return compulsory;
    }

    public void setCompulsory(boolean compulsory) {
        this.compulsory = compulsory;
    }
    

    public Attribute getKey() {
        return key;
    }

    public void setKey(String keyName) {
        for(Attribute attribute: this.getAttributes())
            if (attribute.getName().equals(keyName))
                    this.key = attribute;
    }
    
    public Petrinet getLifeCycle() {
        return lifeCycle;
    }

    public void setLifeCycle(Petrinet lifeCycle) {
        this.lifeCycle = lifeCycle;
    }
    
    public Petrinet getAbstractCreateBehaviouralModel() {
        return abstractCreateBehaviouralModel;
    }

    public Petrinet getActualCreateBehaviouralModel() {
        return actualCreateBehaviouralModel;
    }

    public Petrinet getAbstractReadBehaviouralModel() {
        return abstractReadBehaviouralModel;
    }

    public Petrinet getActualReadBehaviouralModel() {
        return actualReadBehaviouralModel;
    }

    public Petrinet getAbstractUpdateBehaviouralModel() {
        return abstractUpdateBehaviouralModel;
    }

    public Petrinet getActualUpdateBehaviouralModel() {
        return actualUpdateBehaviouralModel;
    }

    public Petrinet getAbstractDeleteBehaviouralModel() {
        return abstractDeleteBehaviouralModel;
    }

    public Petrinet getActualDeleteBehaviouralModel() {
        return actualDeleteBehaviouralModel;
    }

    public void setAbstractCreateBehaviouralModel(Petrinet abstractCreateBehaviouralModel) {
        this.abstractCreateBehaviouralModel = abstractCreateBehaviouralModel;
    }

    public void setActualCreateBehaviouralModel(Petrinet actualCreateBehaviouralModel) {
        this.actualCreateBehaviouralModel = actualCreateBehaviouralModel;
    }

    public void setAbstractReadBehaviouralModel(Petrinet abstractReadBehaviouralModel) {
        this.abstractReadBehaviouralModel = abstractReadBehaviouralModel;
    }

    public void setActualReadBehaviouralModel(Petrinet actualReadBehaviouralModel) {
        this.actualReadBehaviouralModel = actualReadBehaviouralModel;
    }

    public void setAbstractUpdateBehaviouralModel(Petrinet abstractUpdateBehaviouralModel) {
        this.abstractUpdateBehaviouralModel = abstractUpdateBehaviouralModel;
    }

    public void setActualUpdateBehaviouralModel(Petrinet actualUpdateBehaviouralModel) {
        this.actualUpdateBehaviouralModel = actualUpdateBehaviouralModel;
    }

    public void setAbstractDeleteBehaviouralModel(Petrinet abstractDeleteBehaviouralModel) {
        this.abstractDeleteBehaviouralModel = abstractDeleteBehaviouralModel;
    }

    public void setActualDeleteBehaviouralModel(Petrinet actualDeleteBehaviouralModel) {
        this.actualDeleteBehaviouralModel = actualDeleteBehaviouralModel;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    private ArrayList<Attribute> attributes;

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public String getName() {
        return name;
    }

    public BusinessEntity(String name) {
        this.name = name;
        this.attributes = new ArrayList<Attribute>();
        this.associations = new ArrayList<Association>();
    }
    
    public void addAttributes(ArrayList<Attribute> attributeList) {        
        this.attributes.addAll(attributeList);
    }
    
    public void setCreateOperation(Operation createOperation) {
        this.createOperation = createOperation;
    }

    public void setReadOperation(Operation readOperation) {
        this.readOperation = readOperation;
    }

    public void setUpdateOperation(Operation updateOperation) {
        this.updateOperation = updateOperation;
    }

    public void setDeleteOperation(Operation deleteOperation) {
        this.deleteOperation = deleteOperation;
    }

    public Operation getCreateOperation() {
        return createOperation;
    }

    public Operation getReadOperation() {
        return readOperation;
    }

    public Operation getUpdateOperation() {
        return updateOperation;
    }

    public Operation getDeleteOperation() {
        return deleteOperation;
    }
    
    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof BusinessEntity)
        {
            sameSame = this.name.equals(((BusinessEntity) object).getName());
        }
        return sameSame;
    }        
}
