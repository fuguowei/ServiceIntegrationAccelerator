/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.services;

import qut.edu.au.entities.EntityPair;
import java.util.ArrayList;
import java.util.Collections;
import qut.edu.au.entities.BusinessEntity;
import qut.edu.au.Utility;
import static qut.edu.au.Utility.writeFile;

/**
 *
 * @author sih
 */
public class ServiceBEDataModel {

    private ArrayList<BusinessEntity> entities = null;
    private ArrayList<EntityPair> nestingPair = null;
    private ArrayList<EntityPair> exclusiveContainmentPair = null; //this is mandatory exclusive containment
    private ArrayList<EntityPair> optionalexclusiveContainmentPair = null; //this is optional exclusive containment
    private ArrayList<EntityPair> weakInclusiveContainmentPair = null;
    private ArrayList<EntityPair> strongInclusiveContainmentPair = null;  // this is inclusive containment    
    private ArrayList<EntityPair> associationPair = null;    // this is association, but it has not been proeprly implementated

    public ServiceBEDataModel() {
        this.entities = new ArrayList<BusinessEntity>();
        this.nestingPair = new ArrayList<EntityPair>();
        this.exclusiveContainmentPair = new ArrayList<EntityPair>();
        this.optionalexclusiveContainmentPair = new ArrayList<EntityPair>();        
        this.weakInclusiveContainmentPair = new ArrayList<EntityPair>();
        this.strongInclusiveContainmentPair = new ArrayList<EntityPair>();
        this.associationPair = new ArrayList<EntityPair>();
    }

    public void sortBEDataModel() {
        Collections.sort(nestingPair, new EntityPairComparator());
        Collections.sort(exclusiveContainmentPair, new EntityPairComparator());        
        Collections.sort(optionalexclusiveContainmentPair, new EntityPairComparator());                
        Collections.sort(weakInclusiveContainmentPair, new EntityPairComparator());        
        Collections.sort(strongInclusiveContainmentPair, new EntityPairComparator());        
        Collections.sort(associationPair, new EntityPairComparator());        
    }

    public void outPutStatistics(String outfileName, boolean ifAppend) {
        //writeFile
        String entityString = "The number of entities :" + this.getEntities().size() + "\n";
        entityString = entityString + "The number of nesting pairs :" + this.getNestingPair().size() + "\n";
        entityString = entityString + "The number of mandatory strong containment pairs :" + this.getExclusiveContainmentPair().size() + "\n";
        entityString = entityString + "The number of optional strong containment pairs :" + this.getWeakInclusiveContainmentPair().size() + "\n";
        entityString = entityString + "The number of weak containment pairs :" + this.getStrongInclusiveContainmentPair().size() + "\n";
        entityString = entityString + "The number of association pairs :" + this.getAssociationPair().size() + "\n";

        entityString = entityString + "The entities are :\n";
        for (BusinessEntity entity : this.getEntities()) {
            entityString = entityString + entity.getName() + "(" + entity.getType() + ")\n";
        }
        entityString = entityString + "The nesting pairs are :\n";

        for (EntityPair entityPair : this.getNestingPair()) {
            entityString = entityString + entityPair.getMainEntity().getName() + " -> (nesting) " + entityPair.getSlaveEntity().getName() + "\n";
        }

        entityString = entityString + "The mandatory strong containment pairs are :\n";
        for (EntityPair entityPair : this.getExclusiveContainmentPair()) {
            entityString = entityString + entityPair.getMainEntity().getName() + " -> (msc) " + entityPair.getSlaveEntity().getName() + "\n";
        }

        entityString = entityString + "The optional strong containment pairs are :\n";
        for (EntityPair entityPair : this.getWeakInclusiveContainmentPair()) {
            entityString = entityString + entityPair.getMainEntity().getName() + " -> (osc) " + entityPair.getSlaveEntity().getName() + "\n";
        }

        entityString = entityString + "The weak containment pairs are :\n";
        for (EntityPair entityPair : this.getStrongInclusiveContainmentPair()) {
            entityString = entityString + entityPair.getMainEntity().getName() + " -> (wc) " + entityPair.getSlaveEntity().getName() + "\n";
        }

        entityString = entityString + "The association pairs are :\n";
        for (EntityPair entityPair : this.getAssociationPair()) {
            entityString = entityString + entityPair.getMainEntity().getName() + " -> (ass) " + entityPair.getSlaveEntity().getName() + "\n";
        }

        writeFile(outfileName, entityString, ifAppend);
    }

    public void visualise(String outfileName) {
        // Visulise the BE data model        

        /*
         ArrayList<String> nestedGraphStrings = new ArrayList<String>();
         for (EntityPair entityPair : this.getNestingPair()) {
         String pairString = entityPair.getMainEntity().getName()+ " -> " + entityPair.getSlaveEntity().getName() + " [style=solid];";                    
         nestedGraphStrings.add(pairString);
         }
         */
        //Utility.generateGraph(nestedGraphStrings, outfileName+"_"+"NestingGraph");
        ArrayList<String> mandatoryStrongDepGraphStrings = new ArrayList<String>();
        for (EntityPair entityPair : this.getExclusiveContainmentPair()) {
            String pairString = entityPair.getMainEntity().getName() + " -> " + entityPair.getSlaveEntity().getName() + " [style=bold];";
            mandatoryStrongDepGraphStrings.add(pairString);
        }
        //Utility.generateGraph(mandatoryStrongDepGraphStrings, outfileName+"_"+"MandatoryStrongDependenceGraph");

        ArrayList<String> optionalStrongDepGraphStrings = new ArrayList<String>();
        for (EntityPair entityPair : this.getWeakInclusiveContainmentPair()) {
            String pairString = entityPair.getMainEntity().getName() + " -> " + entityPair.getSlaveEntity().getName() + " [style=solid];";
            optionalStrongDepGraphStrings.add(pairString);
        }

        ArrayList<String> weakDepGraphStrings = new ArrayList<String>();
        for (EntityPair entityPair : this.getStrongInclusiveContainmentPair()) {
            String pairString = entityPair.getMainEntity().getName() + " -> " + entityPair.getSlaveEntity().getName() + " [style=dashed];";
            weakDepGraphStrings.add(pairString);
        }

        ArrayList<String> associationGraphStrings = new ArrayList<String>();
        for (EntityPair entityPair : this.getAssociationPair()) {
            String pairString = entityPair.getMainEntity().getName() + " -> " + entityPair.getSlaveEntity().getName() + " [style=dotted];";
            associationGraphStrings.add(pairString);
        }

        ArrayList<String> combined = new ArrayList<String>();
        //combined.addAll(nestedGraphStrings);        
        combined.addAll(mandatoryStrongDepGraphStrings);
        combined.addAll(optionalStrongDepGraphStrings);
        combined.addAll(weakDepGraphStrings);
        combined.addAll(associationGraphStrings);

        Utility.generateGraph(combined, outfileName);
    }

    public void addEntity(BusinessEntity entity) {
        this.entities.add(entity);
    }

    public ArrayList<BusinessEntity> getEntities() {
        return entities;
    }

    public ArrayList<EntityPair> getNestingPair() {
        return nestingPair;
    }

    public ArrayList<EntityPair> getExclusiveContainmentPair() {
        return exclusiveContainmentPair;
    }
    
    public ArrayList<EntityPair> getOptionalExclusiveContainmentPair() {
        return optionalexclusiveContainmentPair;
    }
    

    public ArrayList<EntityPair> getWeakInclusiveContainmentPair() {
        return weakInclusiveContainmentPair;
    }

    public ArrayList<EntityPair> getStrongInclusiveContainmentPair() {
        return strongInclusiveContainmentPair;
    }

    public ArrayList<EntityPair> getAssociationPair() {
        return associationPair;
    }

    //private HashMap<BusinessEntity, BusinessEntity> nestingPair = null;
    //private HashMap<BusinessEntity, BusinessEntity> strongDependencePair= null;
    //private HashMap<BusinessEntity, BusinessEntity> strongInclusiveContainmentPair = null;
    //private HashMap<BusinessEntity, BusinessEntity> associationPair = null;
    public void addNestingPair(EntityPair entityPair) {
        this.nestingPair.add(entityPair);
    }

    public void addExclusiveContainmentPair(EntityPair entityPair) {
        if (!entityPair.getMainEntity().equals(entityPair.getSlaveEntity())) {
            this.exclusiveContainmentPair.add(entityPair);
        }
    }
    
    public void addOptionalExclusiveContainmentPair(EntityPair entityPair) {
        if (!entityPair.getMainEntity().equals(entityPair.getSlaveEntity())) {
            this.optionalexclusiveContainmentPair.add(entityPair);
        }
    }
    

    public void addWeakInclusiveContainmentPair(EntityPair entityPair) {
        if (!entityPair.getMainEntity().equals(entityPair.getSlaveEntity())) {
            this.weakInclusiveContainmentPair.add(entityPair);
        }
    }

    public void addStrongInclusiveContainmentPair(EntityPair entityPair) {
        if (!entityPair.getMainEntity().equals(entityPair.getSlaveEntity())) {
            this.strongInclusiveContainmentPair.add(entityPair);
        }
    }

    public void addAssociationPair(EntityPair entityPair) {
        if (!entityPair.getMainEntity().equals(entityPair.getSlaveEntity())) {
            this.associationPair.add(entityPair);
        }
    }
}
