/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.services;

import com.eviware.soapui.SoapUI;
import qut.edu.au.entities.EntityPair;
import com.eviware.soapui.impl.WsdlInterfaceFactory;
import com.eviware.soapui.impl.support.components.ResponseXmlDocument;
import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.WsdlRequest;
import com.eviware.soapui.impl.wsdl.WsdlSubmit;
import com.eviware.soapui.impl.wsdl.WsdlSubmitContext;
import com.eviware.soapui.impl.wsdl.support.ExternalDependency;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlContext;
import com.eviware.soapui.model.iface.MessagePart;
import com.eviware.soapui.model.iface.Request;
import com.eviware.soapui.model.iface.Response;
import com.eviware.soapui.model.iface.Submit.Status;
import com.eviware.soapui.settings.WsdlSettings;
import com.eviware.soapui.support.SoapUIException;
import com.eviware.soapui.support.editor.EditorDocument;
import com.eviware.soapui.support.xml.XmlUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.wsdl.BindingOperation;
import org.apache.log4j.Priority;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import qut.edu.au.entities.BusinessEntity;
import qut.edu.au.TreeExampleInputParameters;
import qut.edu.au.Utility;
import static qut.edu.au.Utility.writeFile;
import qut.edu.au.analysis.AnalysisStats;
import qut.edu.au.analysis.ParameterAnalysis;
import qut.edu.au.entities.Attribute;
import qut.edu.au.petrinet.logic.Petrinet;
import qut.edu.au.treeSearch.Search;

/**
 *
 * @author sih
 */
public class Service {

    private String serviceWSDLName;
    private ArrayList<Operation> operations;
    private String serviceName;
    //private ArrayList<BusinessEntity> entities;
    private ServiceBEDataModel serviceBEDataModel; //the BEDataMOdel inherent in inputs

    private final static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Service.class);

    public void outPutOperations(String fileID) {
        String outfileName = "output/" + serviceName + "-" + fileID + ".operations";
        String thingsToPrint = "";
        for (Operation operation : this.getOperations()) {
            thingsToPrint = thingsToPrint + operation.getName() + "\n";
            //thingsToPrint = thingsToPrint + "Number of Input Parameters: " + operation.getNumberOfInputParameters() + "\n";
            //thingsToPrint = thingsToPrint + "Number of Output Parameters: " + operation.getNumberOfOutputParameters() + "\n";
            //thingsToPrint = thingsToPrint + "Input parameters are : ( " + operation.getInputParameterString() + "\n";
            //thingsToPrint = thingsToPrint + "Output parameters are : ( " + operation.getOutputParameterString() + "\n";
            //averageNumberOfInputParameters = averageNumberOfInputParameters + operation.getNumberOfInputParameters();
            //averageNumberOfOutputParameters = averageNumberOfOutputParameters + operation.getNumberOfOutputParameters();
        }
        writeFile(outfileName, thingsToPrint, false);
    }

    public void outPutStatistics(long structuralTimeTaken, long behaviouralTimeTaken, boolean includeBehaviouir) {
        String outfileName = "output/" + serviceName + ".stats";
        String thingsToPrint = "The strucual statistics of " + this.serviceName + ":\n";
        thingsToPrint = thingsToPrint + "The overal time taken : " + structuralTimeTaken + "seconds and the average time is "
                + structuralTimeTaken / this.getOperations().size() + "\n";
        thingsToPrint = thingsToPrint + "The number of operations:  " + this.getOperations().size() + "\n";
        thingsToPrint = thingsToPrint + "The operations are :  \n";
        double averageNumberOfInputParameters = 0;
        double averageNumberOfOutputParameters = 0;
        int nubmerOfOperations = this.getOperations().size();
        for (Operation operation : this.getOperations()) {
            thingsToPrint = thingsToPrint + "The statistics of Operation " + operation.getName() + " are: \n";
            thingsToPrint = thingsToPrint + "Number of Input Parameters: " + operation.getNumberOfInputParameters() + "\n";
            thingsToPrint = thingsToPrint + "Number of Output Parameters: " + operation.getNumberOfOutputParameters() + "\n";
            thingsToPrint = thingsToPrint + "Input parameters are : ( " + operation.getInputParameterString() + "\n";
            thingsToPrint = thingsToPrint + "Output parameters are : ( " + operation.getOutputParameterString() + "\n";

            averageNumberOfInputParameters = averageNumberOfInputParameters + operation.getNumberOfInputParameters();

            averageNumberOfOutputParameters = averageNumberOfOutputParameters + operation.getNumberOfOutputParameters();
        }

        //System.out.println((int) Math.ceil(a / 100.0));        
        averageNumberOfInputParameters = averageNumberOfInputParameters / nubmerOfOperations;
        averageNumberOfOutputParameters = averageNumberOfOutputParameters / nubmerOfOperations;
        thingsToPrint = thingsToPrint + "The average number of input parameters is: " + (int) Math.ceil(averageNumberOfInputParameters) + "\n";
        thingsToPrint = thingsToPrint + "The average number of output parameters is: " + (int) Math.ceil(averageNumberOfOutputParameters) + "\n";

        ServiceBEDataModel be = this.getServiceBEDataModel();
        double size = be.getEntities().size();
        thingsToPrint = thingsToPrint + "The average number of entities :" + (int) Math.ceil(size / nubmerOfOperations) + "\n";
        size = be.getNestingPair().size();
        thingsToPrint = thingsToPrint + "The average number of nesting pairs :" + (int) Math.ceil(size / nubmerOfOperations) + "\n";
        size = be.getExclusiveContainmentPair().size();
        thingsToPrint = thingsToPrint + "The average number of mandatory strong containment pairs :" + (int) Math.ceil(size / nubmerOfOperations) + "\n";
        size = be.getWeakInclusiveContainmentPair().size();
        thingsToPrint = thingsToPrint + "The average number of optional strong containment pairs :" + (int) Math.ceil(size / nubmerOfOperations) + "\n";
        size = be.getStrongInclusiveContainmentPair().size();
        thingsToPrint = thingsToPrint + "The average number of weak containment pairs :" + (int) Math.ceil(size / nubmerOfOperations) + "\n";
        size = be.getAssociationPair().size();
        thingsToPrint = thingsToPrint + "The average number of association pairs :" + (int) Math.ceil(size / nubmerOfOperations) + "\n";

        writeFile(outfileName, thingsToPrint, false);
        //writeFile(outfileName, entityString, ifAppend);
        this.getServiceBEDataModel().outPutStatistics(outfileName, true);

        if (includeBehaviouir) {
            thingsToPrint = "----------------------------------------------------------------------------------------------------" + "\n";
            thingsToPrint = thingsToPrint + "The behavioural statistics of " + this.serviceName + ":\n";
            thingsToPrint = thingsToPrint + "The overal time taken : " + behaviouralTimeTaken + "\n";

            int nubmerOfAbstractPlaces = 0, nubmerOfActualPlaces = 0, nubmerOfLifeCyclePlaces = 0;
            int nubmerOfAbstractTransitions = 0, nubmerOfActualTransitions = 0, nubmerOfLifeCycleTransitions = 0;
            int nubmerOfAbstractFlows = 0, nubmerOfActualFlows = 0, nubmerOfLifeCycleFlows = 0;
            int numberOfAbstractNets = 0, numberOfActualNets = 0, nubmerOfLifeCycles = 0;

            for (BusinessEntity entity : this.getServiceBEDataModel().getEntities()) {
                Petrinet abstractNet = entity.getAbstractCreateBehaviouralModel();
                if (abstractNet != null) {
                    numberOfAbstractNets = numberOfAbstractNets + 1;
                    nubmerOfAbstractPlaces = nubmerOfAbstractPlaces + abstractNet.getPlaces().size();
                    nubmerOfAbstractTransitions = nubmerOfAbstractTransitions + abstractNet.getTransitions().size();
                    nubmerOfAbstractFlows = nubmerOfAbstractFlows + abstractNet.getArcs().size();
                }
                Petrinet actualtNet = entity.getActualCreateBehaviouralModel();
                if (actualtNet != null) {
                    numberOfActualNets = numberOfActualNets + 1;
                    nubmerOfActualPlaces = nubmerOfActualPlaces + actualtNet.getPlaces().size();
                    nubmerOfActualTransitions = nubmerOfActualTransitions + actualtNet.getTransitions().size();
                    nubmerOfActualFlows = nubmerOfActualFlows + actualtNet.getArcs().size();
                }

                Petrinet lifeCycle = entity.getLifeCycle();
                if (lifeCycle != null) {
                    nubmerOfLifeCycles = nubmerOfLifeCycles + 1;
                    nubmerOfLifeCyclePlaces = nubmerOfLifeCyclePlaces + lifeCycle.getPlaces().size();
                    nubmerOfLifeCycleTransitions = nubmerOfLifeCycleTransitions + lifeCycle.getTransitions().size();
                    nubmerOfLifeCycleFlows = nubmerOfLifeCycleFlows + lifeCycle.getArcs().size();
                }
            }

            thingsToPrint = thingsToPrint + "the number of abstract nets : " + numberOfAbstractNets + "\n";
            thingsToPrint = thingsToPrint + "the number of abstract places : " + nubmerOfAbstractPlaces + "\n";
            thingsToPrint = thingsToPrint + "the number of abstract transitions : " + nubmerOfAbstractTransitions + "\n";
            thingsToPrint = thingsToPrint + "the number of abstract flows : " + nubmerOfAbstractFlows + "\n";
            thingsToPrint = thingsToPrint + "the number of actual nets : " + numberOfActualNets + "\n";
            thingsToPrint = thingsToPrint + "the number of actual places : " + nubmerOfActualPlaces + "\n";
            thingsToPrint = thingsToPrint + "the number of actual transitions : " + nubmerOfActualTransitions + "\n";
            thingsToPrint = thingsToPrint + "the number of actual flows : " + nubmerOfActualFlows + "\n";
            thingsToPrint = thingsToPrint + "the number of lifecycles : " + nubmerOfLifeCycles + "\n";
            thingsToPrint = thingsToPrint + "the number of places in lifecycles : " + nubmerOfLifeCyclePlaces + "\n";
            thingsToPrint = thingsToPrint + "the number of transitions in lifecycles: " + nubmerOfLifeCycleTransitions + "\n";
            thingsToPrint = thingsToPrint + "the number of flows in lifcycels : " + nubmerOfLifeCycleFlows + "\n";

            writeFile(outfileName, thingsToPrint, true);
        }
    }

    private void generateServiceBEDataModel() {
        for (Operation operation : this.operations) {
            ServiceBEDataModel beDataModel = operation.getServiceBEDataModel();
            ArrayList<BusinessEntity> businessEntities = beDataModel.getEntities();
            //the code below is temporary, as it is for demo purpose
            
            ArrayList<String> demoBusinessEntities = new ArrayList<String>();
            demoBusinessEntities.add("OpenshipOrder");
            demoBusinessEntities.add("PackageLineItem");
            demoBusinessEntities.add("SpecialService");
            demoBusinessEntities.add("DangerousGood");
            demoBusinessEntities.add("PriorityAlert");
            demoBusinessEntities.add("Consolidation");
            demoBusinessEntities.add("ShipOrder");
            demoBusinessEntities.add("Shipper");
            demoBusinessEntities.add("Shipment");
            demoBusinessEntities.add("Shipment");
            demoBusinessEntities.add("Recipient");
            demoBusinessEntities.add("ShippingLabel");
            demoBusinessEntities.add("Pickup");
            demoBusinessEntities.add("CustomsClearance");
            demoBusinessEntities.add("Payment");
            demoBusinessEntities.add("PendingShipment");
            demoBusinessEntities.add("Payor");
            

            // if the service'BE model has the entity already, then skip it;
            for (BusinessEntity eachEntity : businessEntities) {
                if (!this.serviceBEDataModel.getEntities().contains(eachEntity)) {
                    //this if statement is temporary
                    //if (demoBusinessEntities.contains(eachEntity.getName())) 
                    {
                        //TBD there is a bug here, we should merge all entities that have the same name, rather than skip it
                        this.serviceBEDataModel.addEntity(eachEntity);
                    }
                }
            }

            for (EntityPair entityPair : beDataModel.getNestingPair()) {
                
                if (!this.serviceBEDataModel.getNestingPair().contains(entityPair)) {
                    //this if statement is temporary
                    BusinessEntity mainEntity =  entityPair.getMainEntity();
                    BusinessEntity slaveEntity =  entityPair.getSlaveEntity();
                    //if (demoBusinessEntities.contains(mainEntity.getName()) && demoBusinessEntities.contains(slaveEntity.getName()))
                    {
                        this.serviceBEDataModel.addNestingPair(entityPair);
                    }
                    
                }
            }
            for (EntityPair entityPair : beDataModel.getExclusiveContainmentPair()) {
                if (!this.serviceBEDataModel.getExclusiveContainmentPair().contains(entityPair)) {
                    //this if statement is temporary
                    BusinessEntity mainEntity =  entityPair.getMainEntity();
                    BusinessEntity slaveEntity =  entityPair.getSlaveEntity();                    
                    //if (demoBusinessEntities.contains(mainEntity.getName()) && demoBusinessEntities.contains(slaveEntity.getName()))
                    {                        
                        this.serviceBEDataModel.addExclusiveContainmentPair(entityPair);
                    }
                    
                }
            }
            
            for (EntityPair entityPair : beDataModel.getOptionalExclusiveContainmentPair()) {
                if (!this.serviceBEDataModel.getOptionalExclusiveContainmentPair().contains(entityPair)) {
                    //this if statement is temporary
                    BusinessEntity mainEntity =  entityPair.getMainEntity();
                    BusinessEntity slaveEntity =  entityPair.getSlaveEntity();                    
                    //if (demoBusinessEntities.contains(mainEntity.getName()) && demoBusinessEntities.contains(slaveEntity.getName()))
                    {                        
                        this.serviceBEDataModel.addOptionalExclusiveContainmentPair(entityPair);
                    }
                    
                }
            }
            
            for (EntityPair entityPair : beDataModel.getWeakInclusiveContainmentPair()) {
                if (!this.serviceBEDataModel.getWeakInclusiveContainmentPair().contains(entityPair)) {
                    //this if statement is temporary
                    BusinessEntity mainEntity =  entityPair.getMainEntity();
                    BusinessEntity slaveEntity =  entityPair.getSlaveEntity();                    
                    //if (demoBusinessEntities.contains(mainEntity.getName()) && demoBusinessEntities.contains(slaveEntity.getName()))
                    {                                            
                        this.serviceBEDataModel.addWeakInclusiveContainmentPair(entityPair);
                    }
                }
            }

            for (EntityPair entityPair : beDataModel.getStrongInclusiveContainmentPair()) {
                if (!this.serviceBEDataModel.getStrongInclusiveContainmentPair().contains(entityPair)) {
                    //this if statement is temporary
                    BusinessEntity mainEntity =  entityPair.getMainEntity();
                    BusinessEntity slaveEntity =  entityPair.getSlaveEntity();                    
                    //if (demoBusinessEntities.contains(mainEntity.getName()) && demoBusinessEntities.contains(slaveEntity.getName()))
                    {                                            
                        this.serviceBEDataModel.addStrongInclusiveContainmentPair(entityPair);
                    }
                }
            }
            for (EntityPair entityPair : beDataModel.getAssociationPair()) {
                if (!this.serviceBEDataModel.getAssociationPair().contains(entityPair)) {
                    //this if statement is temporary
                    BusinessEntity mainEntity =  entityPair.getMainEntity();
                    BusinessEntity slaveEntity =  entityPair.getSlaveEntity();                    
                    //if (demoBusinessEntities.contains(mainEntity.getName()) && demoBusinessEntities.contains(slaveEntity.getName()))
                    {                        
                        this.serviceBEDataModel.addAssociationPair(entityPair);                        
                    }
                }
            }
        }

        //Does strong dependence have higher priority? sometimes we two entities have both strong and weak dependence
        //ArrayList<EntityPair> weakPairs = this.serviceBEDataModel.getWeakDependencePair();
        //ArrayList<EntityPair> strongPairs = this.serviceBEDataModel.getStrongDependencePair();

        /*
         for (int i = 0; i < this.serviceBEDataModel.getWeakDependencePair().size(); i++) {
         if (this.serviceBEDataModel.getStrongDependencePair().contains(this.serviceBEDataModel.getWeakDependencePair().get(i))) {
         this.serviceBEDataModel.getWeakDependencePair().remove(i);
         }
         }
         */
        for (int i = 0; i < this.serviceBEDataModel.getStrongInclusiveContainmentPair().size(); i++) {
            if (this.serviceBEDataModel.getExclusiveContainmentPair().contains(this.serviceBEDataModel.getStrongInclusiveContainmentPair().get(i))
                    || this.serviceBEDataModel.getWeakInclusiveContainmentPair().contains(this.serviceBEDataModel.getStrongInclusiveContainmentPair().get(i))) {
                this.serviceBEDataModel.getStrongInclusiveContainmentPair().remove(i);
            }
        }
    }

    public void visualiseBEModel() {
        this.getServiceBEDataModel().visualise(this.serviceName);
    }

    public void outputOperationsToXML() {
        XStream xstream = new XStream();
        String fileName = "output/" + this.serviceName + "_Operations.xml";
        for (Operation operation : this.getOperations()) {
            if (operation.getName().equals("track")) {
                String serviceString = xstream.toXML(operation.getCompulsoryInputParameterList());
                Utility.writeFile(fileName, serviceString, true);
            }
        }
    }

    public void outBEtoJson() {
        //String fileName = "output/json/" + this.serviceName+"/";

        String fileName = "output/" + this.serviceName + ".json";

        ObjectMapper mapper = new ObjectMapper();
        //for (ServiceBEDataModel entity : this.getServiceBEDataModel()) {
        try {
            //fileName = fileName +entity.getName()+".json";
            ServiceBEDataModel model = this.getServiceBEDataModel();
            model.sortBEDataModel();
            mapper.writeValue(new File(fileName), model);

            //String jsonInString = mapper.writeValueAsString(model);
            //System.out.println(jsonInString);
            //Convert object to JSON string and pretty print
            //jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(model);
            //System.out.println(jsonInString);
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
        // }

    }

    public void outputBEModelToXML() throws FileNotFoundException {
        XStream xstream = new XStream();
        String fileName = "output/" + this.serviceName;

        String serviceString = xstream.toXML(this.getServiceBEDataModel());
        Utility.writeFile(fileName + "_BEModel.xml", serviceString, false);

        JsonArrayBuilder entitiesBuilder = Json.createArrayBuilder();
        //JsonArrayBuilder entitiesNestingBuilder = Json.createArrayBuilder();
        //JsonArrayBuilder entitiesExlusiveBuilder = Json.createArrayBuilder();
        //JsonArrayBuilder entitiesInclusiveBuilder = Json.createArrayBuilder();
        //JsonArrayBuilder entitiesAssociationBuilder = Json.createArrayBuilder();

        JsonObjectBuilder finalBuilder = Json.createObjectBuilder();

        for (BusinessEntity entity : this.getServiceBEDataModel().getEntities()) {

            JsonObjectBuilder entityBuilder = Json.createObjectBuilder();
            entityBuilder = entityBuilder.add("name", entity.getName());

            JsonObjectBuilder attributeBuilder = Json.createObjectBuilder();
            int counter = 0;
            for (Attribute attribute : entity.getAttributes()) {
                JsonObjectBuilder eachAttributeBuilder = Json.createObjectBuilder();
                eachAttributeBuilder = eachAttributeBuilder.add("name", attribute.getName());
                eachAttributeBuilder = eachAttributeBuilder.add("type", attribute.getType());
                counter++;
                attributeBuilder.add("attriubte" + counter, eachAttributeBuilder);
            }
            entityBuilder = entityBuilder.add("attriubtes", attributeBuilder);

            JsonObjectBuilder childrenBuilder = Json.createObjectBuilder();

            for (EntityPair pair : this.getServiceBEDataModel().getNestingPair()) {
                counter = 0;
                if (pair.getMainEntity().equals(entity)) {
                    BusinessEntity child = pair.getSlaveEntity();
                    counter++;
                    JsonObjectBuilder childBuilder = Json.createObjectBuilder();
                    childBuilder = childBuilder.add("name", child.getName());
                    childBuilder = childBuilder.add("type", child.getType());
                    childrenBuilder.add("child" + counter, childBuilder);
                }
            }
            entityBuilder = entityBuilder.add("children", childrenBuilder);
            entitiesBuilder.add(entityBuilder);

        }

        /*
         for (EntityPair pair : this.getServiceBEDataModel().getNestingPair()) {
         JsonObjectBuilder pairBuilder = Json.createObjectBuilder();
         pairBuilder = pairBuilder.add("Main entity", pair.getMainEntity().getName());
         pairBuilder = pairBuilder.add("Nesting entity", pair.getSlaveEntity().getName());
         entitiesNestingBuilder.add(pairBuilder);
         }
        
        
         for (EntityPair pair : this.getServiceBEDataModel().getMandatoryStrongDependencePair()) {
         JsonObjectBuilder pairBuilder = Json.createObjectBuilder();        
         pairBuilder = pairBuilder.add("Main entity", pair.getMainEntity().getName());
         pairBuilder = pairBuilder.add("Slave entity", pair.getSlaveEntity().getName());
         entitiesExlusiveBuilder.add(pairBuilder);
         }
        
         for (EntityPair pair : this.getServiceBEDataModel().getOptionalStrongDependencePair()) {
         JsonObjectBuilder pairBuilder = Json.createObjectBuilder();        
         pairBuilder = pairBuilder.add("Main entity", pair.getMainEntity().getName());
         pairBuilder = pairBuilder.add("Slave entity", pair.getSlaveEntity().getName());
         entitiesExlusiveBuilder.add(pairBuilder);
         }
        

         for (EntityPair pair : this.getServiceBEDataModel().getWeakDependencePair()) {
         JsonObjectBuilder pairBuilder = Json.createObjectBuilder();        
         pairBuilder = pairBuilder.add("Main entity", pair.getMainEntity().getName());
         pairBuilder = pairBuilder.add("Slave entity", pair.getSlaveEntity().getName());
         entitiesInclusiveBuilder.add(pairBuilder);
         }
        
         for (EntityPair pair : this.getServiceBEDataModel().getCoincidencePair()) {
         JsonObjectBuilder pairBuilder = Json.createObjectBuilder();        
         pairBuilder = pairBuilder.add("Main entity", pair.getMainEntity().getName());
         pairBuilder = pairBuilder.add("Slave entity", pair.getSlaveEntity().getName());
         entitiesAssociationBuilder.add(pairBuilder);
         }
         */
        finalBuilder.add("", entitiesBuilder);
        /*
         finalBuilder.add("NestingPairs", entitiesNestingBuilder);
         finalBuilder.add("ExclusivePairs", entitiesExlusiveBuilder);
         finalBuilder.add("InclusivePairs", entitiesExlusiveBuilder);
         finalBuilder.add("AssociationPairs", entitiesAssociationBuilder);
         */

        JsonObject empJsonObject = finalBuilder.build();
        //System.out.println("Employee JSON String\n"+empJsonObject);

        //write to file
        OutputStream os = new FileOutputStream(fileName + ".json");
        JsonWriter jsonWriter = Json.createWriter(os);
        /**
         * We can get JsonWriter from JsonWriterFactory also JsonWriterFactory
         * factory = Json.createWriterFactory(null); jsonWriter =
         * factory.createWriter(os);
         */
        jsonWriter.writeObject(empJsonObject);
        jsonWriter.close();

        String serviceString2 = xstream.toXML(this.getServiceBEDataModel().getEntities());
        Utility.writeFile(fileName + "_Entities.xml", serviceString2, false);
        String serviceString3 = xstream.toXML(this.getServiceBEDataModel().getNestingPair());
        Utility.writeFile(fileName + "_NestingPairs.xml", serviceString3, false);
        String serviceString4 = xstream.toXML(this.getServiceBEDataModel().getExclusiveContainmentPair());
        Utility.writeFile(fileName + "_MandatoryStrongDependencePairs.xml", serviceString4, false);
        String serviceString5 = xstream.toXML(this.getServiceBEDataModel().getWeakInclusiveContainmentPair());
        Utility.writeFile(fileName + "_OptionalStrongDependencePairs.xml", serviceString5, false);
        String serviceString6 = xstream.toXML(this.getServiceBEDataModel().getStrongInclusiveContainmentPair());
        Utility.writeFile(fileName + "_WeakDependencePairs.xml", serviceString6, false);
        String serviceString7 = xstream.toXML(this.getServiceBEDataModel().getAssociationPair());
        Utility.writeFile(fileName + "_AssociationPairs.xml", serviceString7, false);
    }

    public void visualiseAllOperationsBEModel() {
        for (Operation operation : this.operations) {
            ServiceBEDataModel beDataModel = operation.getServiceBEDataModel();
            beDataModel.visualise(this.serviceName + "-" + operation.getName());
        }
    }

    public ArrayList<BusinessEntity> getDominaters(BusinessEntity entity) {

        ArrayList<Operation> operationsThatManipulateEntity = this.getOperationsThatManipulateEntity(entity);
        ArrayList<BusinessEntity> dominaters = new ArrayList<BusinessEntity>();

        for (Operation operationThatManipulatesEntity : operationsThatManipulateEntity) {
            ArrayList<BusinessEntity> entities2 = operationThatManipulatesEntity.getServiceBEDataModel().getEntities();; //TBD, we need to check output as well.
            if (entities2 != null) {
                for (BusinessEntity entity2 : entities2) {
                    if (this.ifDomination(entity2, entity)) {
                        dominaters.add(entity2);
                    }
                }
            }
        }
        return dominaters;
    }

    /*    
     public void visualiseBEModel() {
     ArrayList<String> graphStrings = new ArrayList<String>();                           
     for (Operation operation : this.operations) {
     ServiceBEDataModel beDataModel = operation.getServiceBEDataModel();
     HashMap<BusinessEntity, BusinessEntity> nestingPart = beDataModel.getNestingPair();
     Iterator<BusinessEntity> keySetIterator = nestingPart.keySet().iterator();
     while(keySetIterator.hasNext()){
     BusinessEntity key = keySetIterator.next();
     String pairString = key.getName() + " -> " + nestingPart.get(key).getName() + ";";                    
     graphStrings.add(pairString);
     }            
     }        
     Utility.generateGraph(graphStrings, this.serviceName+"_"+"NestingGraph");
     }
     */
    public ArrayList<Operation> getOperationsThatManipulateEntity(BusinessEntity entity) {
        ArrayList<Operation> operations = new ArrayList<Operation>();
        for (Operation operation : this.operations) {
            ArrayList<BusinessEntity> entities = operation.getServiceBEDataModel().getEntities();
            if (entities.contains(entity)) {
                operations.add(operation);
            }
            //if (operation.getEntities())
            //        return operation;
        }
        return operations;
    }

    //we need to modify the defintion of strong dependence, every time when we see e1, we see e2, when we see e2, we see e1
    public boolean ifMandatoryStrongDomination(BusinessEntity entity1, BusinessEntity entity2) {
        boolean strongDependence = false;
        for (Operation operation : this.operations) {
            ArrayList<BusinessEntity> entities = operation.getServiceBEDataModel().getEntities();
            if (!entities.isEmpty()) {
                if (entities.contains(entity1)) {
                    if (!entities.contains(entity2)) {
                        //strongDependence = false;
                        return false;
                    } else {
                        strongDependence = true;
                    }
                }
                if (entities.contains(entity2)) {
                    if (!entities.contains(entity1)) {
                        //strongDependence = false;
                        return false;
                    } else {
                        strongDependence = true;
                    }
                }
            }
        }
        return strongDependence;
    }

    public boolean ifAssociation(BusinessEntity entity1, BusinessEntity entity2) {
        boolean association = false;
        for (Operation operation : this.operations) {
            ArrayList<BusinessEntity> inputEntities = operation.getServiceBEDataModel().getEntities();
            ArrayList<BusinessEntity> outPutEntities = operation.getOutputServiceBEDataModel().getEntities();
            if (inputEntities.contains(entity2) && outPutEntities.contains(entity1)) {
                association = true;
                break;
            }
        }
        return association;
    }

    public boolean ifDomination(BusinessEntity dominater, BusinessEntity dominatee) {
        boolean dominationCondition1 = false;
        boolean dominationCondition2 = true;
        boolean dominationCondition3 = false;

        //every operation uses dominatee as an input parameter, dominater should be used as an input as well
        for (Operation operation : this.operations) {
            ArrayList<BusinessEntity> entities = operation.getServiceBEDataModel().getEntities();
            if (!entities.isEmpty()) {
                if (entities.contains(dominatee)) {
                    if (entities.contains(dominater)) {
                        dominationCondition1 = true;
                    } else {
                        dominationCondition1 = false;
                        break;
                    }

                }
                /*
                 for (BusinessEntity entity : entities) {
                 if (dominatee.equals(entity)) { //dominatee exists in one operation
                 dominateeExisted = true; 
                        
                 //ArrayList<BusinessEntity> entities2 = operation.getEntities();
                 if (entities.size()>0) {
                 for (BusinessEntity entity2 : entities) {
                 if (dominater.getName().equals(entity2.getName())) { //dominator exists in one operation
                 dominaterExisted = true;
                 break;
                 }
                 }
                 }
                 //every operation uses dominatee as an input parameter, dominater should be used as an input as well
                 if (dominaterExisted) {
                 dominationCondition1 = true;
                 break;
                 }
                 }                        
                 }
                 */
            }
        }

        //every operation uses dominatee as an output parameter, dominater should be used as an output as well
        if (dominationCondition1) {

            /* TBD to enable back            
             for (Operation operation : this.operations) {
             ArrayList<BusinessEntity> entities = operation.getOutputServiceBEDataModel().getEntities();
             if (!entities.isEmpty()) {
             if (entities.contains(dominatee)) {
             if (entities.contains(dominater)) {
             dominationCondition2 = true;
             } else {
             dominationCondition2 = false;
             break;
             }
             }
             }
             }
             */
            if (dominationCondition2) {
                //there is at least one operation uses dominater as an input parameter, dominatee is not used as an input parameter
                for (Operation operation : this.operations) {
                    ArrayList<BusinessEntity> entities = operation.getServiceBEDataModel().getEntities();
                    if (!entities.isEmpty()) {
                        if (entities.contains(dominater) && !entities.contains(dominatee)) {
                            dominationCondition3 = true;
                            break;
                        }
                    }
                }

                /* TBD to enable back
                 //there is at least one operation uses dominater as an output parameter, dominatee is not used as an output parameter
                 for (Operation operation : this.operations) {
                 ArrayList<BusinessEntity> entities = operation.getOutputServiceBEDataModel().getEntities();
                 if (!entities.isEmpty()) {
                 if (entities.contains(dominater) && !entities.contains(dominatee)) {
                 dominationCondition3 = true;
                 }
                 break;
                 }
                 }
                 */
            }

        }

        if (dominationCondition1 && dominationCondition2 && dominationCondition3) {
            return true;
        } else {
            return false;
        }
    }

    /*        
     public ArrayList<BusinessEntity> getEntities() {
     return entities;
     }
    
     public void addEntity(BusinessEntity entity) {
     this.entities.add(entity);
     }
     */
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Operation getOperation(String operationName) {
        if (this.operations.isEmpty()) {
            getOperations();
        }
        for (Operation operation : this.operations) {
            if (operation.getName().equals(operationName)) {
                return operation;
            }
        }
        return null;
    }

    public ArrayList<String> fakeInvokeServiceOperation(String operationName, ArrayList<Parameter> path) {
        ArrayList<String> result = new ArrayList<String>();
        String request = "request";
        String responseXML = "response successfully";
        result.add(request);
        result.add(responseXML);
        return result;

    }

    public ArrayList<String> invokeServiceOperation(String operationName, ArrayList<Parameter> path) {
        ArrayList<String> result = new ArrayList<String>();
        WsdlProject project = null;
        try {
            project = new WsdlProject();
        } catch (XmlException ex) {
            log.log(Priority.FATAL, this.serviceName, ex);
        } catch (IOException ex) {
            log.log(Priority.FATAL, this.serviceName, ex);
        } catch (SoapUIException ex) {
            log.log(Priority.FATAL, this.serviceName, ex);
        }
        SoapUI.getSettings().setString(WsdlSettings.XML_GENERATION_TYPE_EXAMPLE_VALUE, "true");
        SoapUI.getSettings().setString(WsdlSettings.XML_GENERATION_SKIP_COMMENTS, "true");

        WsdlInterface iface = null;
        try {
            iface = WsdlInterfaceFactory.importWsdl(project, this.serviceWSDLName, true)[0];
        } catch (SoapUIException ex) {
            log.log(Priority.FATAL, this.serviceName, ex);
        }

        WsdlOperation operation = (WsdlOperation) iface.getOperationByName(operationName);
        ParameterAnalysis param = new ParameterAnalysis();
        WsdlContext wsdlContext = iface.getWsdlContext();
        BindingOperation bindingOperation = null;
        try {
            bindingOperation = operation.findBindingOperation(wsdlContext.getDefinition());
        } catch (Exception ex) {
            log.log(Priority.FATAL, this.serviceName, ex);
        }

        if (bindingOperation == null) {
            log.error(this.serviceName + " no bindingOperation generated");
        }

        ArrayList<DefaultMutableTreeNode> root = null;
        HashMap hashMap = null;
        String xmlContent = null;
        param.setIface(iface);
        param.setWsdlContext(wsdlContext);
        try {
            AnalysisStats.resetStats();
            hashMap = param.buildSoapMessageFromInput(bindingOperation, true, false, null, path, serviceName);
        } catch (Exception ex) {
            //log.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            log.error(ex);
        }

        Iterator<ArrayList<DefaultMutableTreeNode>> keySetIterator = hashMap.keySet().iterator();

        while (keySetIterator.hasNext()) {
            root = keySetIterator.next();
            xmlContent = (String) hashMap.get(root);
        }

        WsdlRequest request = operation.addNewRequest("My request");
        request.setRequestContent(operation.createRequest(true));

        /*
         for (Parameter parameter : path)
         log.info("Parameter to be tried this time:"+parameter.getName()+" ID"+ parameter.getParameterUniqueIDinTree());        
         log.info("------------------------------------REQUEST CONTENT-------------------------------------\n");
         log.info(xmlContent);
         */
        String title = "------------------------------------REQUEST CONTENT (NUMBER OF PARAMETER: " + path.size() + ")-------------------------------------\n";

        writeFile("InvocationLog/" + operationName + ".xml", title, true);
        writeFile("InvocationLog/" + operationName + ".xml", xmlContent, true);
        //writeFile(outfileName, thingsToPrint,false);

        String responseXML = null;
        Response response = null;
        if (xmlContent != null) {
            request.setRequestContent(xmlContent);

            WsdlSubmit submit = null;
            try {
                submit = (WsdlSubmit) request.submit(new WsdlSubmitContext(request), false);
            } catch (Request.SubmitException ex) {
                log.error(ex);
                //log.getLogger(DepthFirstTreeIterator.class.getName()).log(Level.FATAL, null, ex);
            }
            response = submit.getResponse();
            responseXML = response.getContentAsXml();
            title = "------------------------------------RESPONSE CONTENT-------------------------------------\n";
            writeFile("InvocationLog/" + operationName + ".xml", title, true);
            writeFile("InvocationLog/" + operationName + ".xml", responseXML, true);
            //log.info(responseXML);
        }
        result.add(xmlContent);
        result.add(responseXML);
        return result;
    }

    public ArrayList<BusinessEntity> getDepedentEntities(BusinessEntity entity, String relationFlag, String compulsory) {
        ArrayList<BusinessEntity> entities = new ArrayList<BusinessEntity>();
        ArrayList<EntityPair> pairs = null;

        if (relationFlag.equals("STRONG")) {
            pairs = this.serviceBEDataModel.getExclusiveContainmentPair();
        } else if (relationFlag.equals("WEAK")) {
            pairs = this.serviceBEDataModel.getStrongInclusiveContainmentPair();
        } else if (relationFlag.equals("ASSOCIATION")) {
            pairs = this.serviceBEDataModel.getAssociationPair();
        } else {
            return null;
        }

        for (EntityPair pair : pairs) {
            if (pair.getMainEntity().equals(entity)) {
                if (compulsory != null && compulsory.equals("YES")) {
                    if (pair.getSlaveEntity().isCompulsory()) {
                        //to really get the one that is attached with the service
                        for (BusinessEntity tempEntity : this.getServiceBEDataModel().getEntities()) {
                            if (tempEntity.equals(pair.getSlaveEntity())) {
                                entities.add(tempEntity);
                                break;
                            }
                        }
                    }
                } else if (compulsory != null && compulsory.equals("NO")) {
                    if (!pair.getSlaveEntity().isCompulsory()) {
                        //to really get the one that is attached with the service
                        for (BusinessEntity tempEntity : this.getServiceBEDataModel().getEntities()) {
                            if (tempEntity.equals(pair.getSlaveEntity())) {
                                entities.add(tempEntity);
                                break;
                            }
                        }
                    }
                } else {
                    for (BusinessEntity tempEntity : this.getServiceBEDataModel().getEntities()) {
                        if (tempEntity.equals(pair.getSlaveEntity())) {
                            entities.add(tempEntity);
                            break;
                        }
                    }
                }
            }
        }
        return entities;
    }

    public boolean ifPossessDependency(BusinessEntity entity) {
        ArrayList<EntityPair> pairs = new ArrayList<EntityPair>(this.serviceBEDataModel.getExclusiveContainmentPair());
        pairs.addAll(this.serviceBEDataModel.getStrongInclusiveContainmentPair());
        pairs.addAll(this.serviceBEDataModel.getAssociationPair());
        for (EntityPair pair : pairs) {
            if (pair.getMainEntity().equals(entity)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Operation> getOperations() {
        if (this.operations.isEmpty()) {
            WsdlProject project = null;
            try {
                project = new WsdlProject();
            } catch (XmlException ex) {
                Logger.getLogger(TreeExampleInputParameters.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TreeExampleInputParameters.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SoapUIException ex) {
                Logger.getLogger(TreeExampleInputParameters.class.getName()).log(Level.SEVERE, null, ex);
            }
            //SoapUI.getSettings().setString(WsdlSettings.XML_GENERATION_TYPE_EXAMPLE_VALUE, "true");
            WsdlInterface iface = null;
            try {
                //iface = WsdlInterfaceFactory.importWsdl(project, "Openshipping/OpenShipService_v7.wsdl", true)[0];
                iface = WsdlInterfaceFactory.importWsdl(project, this.serviceWSDLName, true)[0];
                String serviceName = iface.getName().replace("ServiceSoapBinding", "");
                this.setServiceName(serviceName);
                //iface = WsdlInterfaceFactory.importWsdl(project, "SAP/PurchaseOrder.wsdl", true)[0];
                //iface = WsdlInterfaceFactory.importWsdl(project, "Track/TrackService_v8.wsdl", true)[0];
            } catch (SoapUIException ex) {
                Logger.getLogger(TreeExampleInputParameters.class.getName()).log(Level.SEVERE, null, ex);
            }

            for (int i = 0; i < iface.getOperationCount(); i++) {
                // get "Help" operation
                WsdlOperation wsdlOperation = iface.getOperationAt(i);
                //wsdlOperation.
                ParameterAnalysis parameterAnalysis = new ParameterAnalysis();

                WsdlContext wsdlContext = iface.getWsdlContext();
                //String bindingOperationName = operation.getConfig().getBindingOperationName();

                BindingOperation bindingOperation = null;
                try {
                    bindingOperation = wsdlOperation.findBindingOperation(wsdlContext.getDefinition());
                } catch (Exception ex) {
                    Logger.getLogger(TreeExampleInputParameters.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (bindingOperation == null) {
                    //log.error("no bindingOperation generated");
                }
                ArrayList<DefaultMutableTreeNode> nodesInput = null, nodesOutput = null;
                HashMap hashMapInput = null, hashMapOutput = null;
                String xmlContentInput = null, xmlContentOutput = null;
                parameterAnalysis.setIface(iface);
                parameterAnalysis.setWsdlContext(wsdlContext);

                try {
                    AnalysisStats.resetStats();
                    hashMapInput = parameterAnalysis.buildSoapMessageFromInput(bindingOperation, true, false, null, null, this.serviceName);
                } catch (Exception ex) {
                    Logger.getLogger(TreeExampleInputParameters.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (hashMapInput != null) {
                    Iterator<ArrayList<DefaultMutableTreeNode>> keySetIteratorInput = hashMapInput.keySet().iterator();

                    while (keySetIteratorInput.hasNext()) {
                        nodesInput = keySetIteratorInput.next();
                        xmlContentInput = (String) hashMapInput.get(nodesInput);
                    }

                    Operation operation = new Operation(wsdlOperation.getName());
                    ArrayList<Parameter> parameters = new ArrayList<Parameter>();

                    for (DefaultMutableTreeNode root : nodesInput) {
                        Parameter parameter = (Parameter) root.getUserObject();
                        //Parameter parameter = new Parameter();
                        parameter.setRoot(root);
                        /*
                         String rootName = root.toString();
                         String[] parts = rootName.split(":");
                         if (rootName.contains("COMPLEX")) {
                         parameter.setComplex(true);
                         }
                         if (parts.length == 0) {
                         parameter.setName(rootName);
                         } else if (parts.length == 1) {
                         parameter.setName(parts[0]); //must be the name
                         } else if (parts.length == 2) {
                         parameter.setType(parts[1]);
                         }
                         */
                        parameters.add(parameter);
                    }
                    operation.setSimpleInputParameterList(AnalysisStats.simpleParameterList);
                    operation.setComplexInputParameterList(AnalysisStats.complexParameterList);
                    operation.setCompulsoryInputParameterList(AnalysisStats.compulsoryInputParameterList);
                    operation.setOptionalInputParameterList(AnalysisStats.optionalInputParameterList);
                    operation.setInputParameterString(AnalysisStats.listofPreliminary);
                    operation.setNumberOfInputParameters(AnalysisStats.simpleParameterList.size());
                    /*
                     AnalysisStats.compulsoryInputParameterList = new ArrayList<Parameter>();
                     AnalysisStats.optionalInputParameterList = new ArrayList<Parameter>();
                     AnalysisStats.listofPreliminary = new ArrayList<String>();
                     AnalysisStats.parameterUniqueIDinTree =0;
                     */
                    operation.setInputParameters(parameters);
                    operation.setInputParametersWithMockValues(xmlContentInput);

                    try {
                        AnalysisStats.resetStats();
                        hashMapOutput = parameterAnalysis.buildSoapMessageFromOutput(bindingOperation, true, false, null, this.serviceName);
                    } catch (Exception ex) {
                        Logger.getLogger(TreeExampleInputParameters.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if (hashMapOutput != null) {

                        Iterator<ArrayList<DefaultMutableTreeNode>> keySetIteratorOutput = hashMapOutput.keySet().iterator();

                        while (keySetIteratorOutput.hasNext()) {
                            nodesOutput = keySetIteratorOutput.next();
                            xmlContentOutput = (String) hashMapOutput.get(nodesOutput);
                        }

                        parameters = new ArrayList<Parameter>();

                        for (DefaultMutableTreeNode root : nodesOutput) {
                            Parameter parameter = (Parameter) root.getUserObject();
                            parameter.setRoot(root);
                            /*
                             String rootName = root.toString();
                             String[] parts = rootName.split(":");
                             if (rootName.contains("COMPLEX")) {
                             parameter.setComplex(true);
                             }
                             if (parts.length == 0) {
                             parameter.setName(rootName);
                             } else if (parts.length == 1) {
                             parameter.setName(parts[0]); //must be the name
                             } else if (parts.length == 2) {
                             parameter.setType(parts[1]);
                             }
                             */
                            parameters.add(parameter);
                        }
                        operation.setOutputParameters(parameters);
                        operation.setOutPutParametersWithMockValues(xmlContentOutput);
                        operation.setOutputParameterString(AnalysisStats.listofPreliminary);
                        operation.setNumberOfOutputParameters(AnalysisStats.listofPreliminary.size());

                    }
                    this.operations.add(operation);
                }
            }
        }

        return this.operations;

    }

    public String getServiceWSDLName() {
        return serviceWSDLName;
    }

    public Service(String serviceWSDLName) {
        this.serviceWSDLName = serviceWSDLName;
        //this.entities = new ArrayList<BusinessEntity>();
        this.operations = new ArrayList<Operation>();
        this.serviceBEDataModel = new ServiceBEDataModel();
    }

    public ServiceBEDataModel getServiceBEDataModel() {
        if (this.serviceBEDataModel.getEntities().isEmpty()) {
            generateServiceBEDataModel();
        }
        return serviceBEDataModel;

    }

}
