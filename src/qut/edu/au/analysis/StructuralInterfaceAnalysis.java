/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.analysis;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import qut.edu.au.entities.Attribute;
import qut.edu.au.entities.BusinessEntity;
import qut.edu.au.entities.EntityPair;
import qut.edu.au.services.Operation;
import qut.edu.au.services.Parameter;
import qut.edu.au.services.Service;
import qut.edu.au.Utility;
import static qut.edu.au.Utility.readXMLFile;

/**
 *
 * @author sih
 */
public class StructuralInterfaceAnalysis {

    public void identifyBEandRelationForAllOperations(Service service) {
        ArrayList<Operation> operations = service.getOperations();
        if (operations.size() > 0) {
            for (Operation operation : operations) {
                for (Parameter parameter : operation.getInputParameters()) {
                    identifyBEandRelation(service, operation, null, parameter, null);
                }
                for (Parameter parameter : operation.getOutputParameters()) {
                    identifyBEandRelation(service, operation, null, parameter, "OUTPUT");
                }
            }
        }
        //XStream xstream = new XStream(); 
        //String serviceString = xstream.toXML(service.getOperation("createOpenShipment").getServiceBEDataModel());

        /*
         for (EntityPair entityPair : service.getOperation("createOpenShipment").getServiceBEDataModel().getNestingPair()) {
         System.out.println("parent entity: " + entityPair.getMainEntity().getName() + " slave enity: " + entityPair.getSlaveEntity().getName());
         }
         */
        //service.getOperation("createOpenShipment").getServiceBEDataModel()
        //System.out.println("------------------------------------service------------------------------------");
        //System.out.println(serviceString);
        //System.out.println("------------------------------------service------------------------------------");
    }

    public void identifyBEandRelation(Service service, Operation operation, BusinessEntity entity, Parameter parameter, String intputOrOutput) {
        DefaultMutableTreeNode node = parameter.getRoot();
        BusinessEntity newEntity = null;
        if (parameter.isComplex()) { // to prevent the root
            //int childCount = root.getChildCount();
            //BusinessEntity entity = OntologyCheck(service.getServiceName(), parameter.getName());
            /**
             * String parameterName = node.toString(); Parameter tempParameter =
             * new Parameter(); String[] parts = parameterName.split(":"); if
             * (parameterName.contains("COMPLEX")) {
             * tempParameter.setComplex(true); }
             * tempParameter.setName(parts[0]); //must be the name if
             * (parts.length == 3) { tempParameter.setType(parts[1]); }
             * BusinessEntity newEntity =null; if (tempParameter.isComplex()) {
             * }
             *
             */

            newEntity = OntologyCheck(service.getServiceName(), parameter.getName(), parameter.getType());

            /*
             if (!operation.getOutputServiceBEDataModel().getEntities().contains(newEntity)) {
                
             }
             if (!operation.getOutputServiceBEDataModel().getEntities().contains(newEntity))
             */
            if (newEntity != null) {
                newEntity.setCompulsory(parameter.isCompulsory());
                ArrayList<Attribute> attributesList = addAndConvertAttributes(parameter.getRoot());
                if (!attributesList.isEmpty()) {
                    newEntity.addAttributes(attributesList);
                    //set the key
                    String key = Utility.readXMLFile(newEntity.getName(), "Key", "Configurations/BusinessEntityKeys.xml");
                    if (key != null) {
                        newEntity.setKey(key);
                    }
                }

                if (intputOrOutput != null && intputOrOutput.equals("OUTPUT")) {
                    if (!operation.getOutputServiceBEDataModel().getEntities().contains(newEntity)) {
                        operation.getOutputServiceBEDataModel().addEntity(newEntity);
                    }
                } else if (!operation.getServiceBEDataModel().getEntities().contains(newEntity)) {
                    operation.getServiceBEDataModel().addEntity(newEntity);
                }

                if (entity != null) {
                    EntityPair entityPair = new EntityPair(entity, newEntity);
                    if (intputOrOutput != null && intputOrOutput.equals("OUTPUT")) {
                        if (!operation.getOutputServiceBEDataModel().getNestingPair().contains(entityPair)) {
                            operation.getOutputServiceBEDataModel().addNestingPair(entityPair);
                        }
                    } else {
                        if (!operation.getServiceBEDataModel().getNestingPair().contains(entityPair)) {
                            operation.getServiceBEDataModel().addNestingPair(entityPair);
                        }
                    }
                }
                parameter.setMappedEntity(newEntity);
                node.setUserObject(parameter);
            }
        }
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            Parameter tempParameter = (Parameter) childNode.getUserObject();

            // String parameterName = childNode.toString();
            tempParameter.setRoot(childNode);

            /*
             String[] parts = parameterName.split(":");
             if (parameterName.contains("COMPLEX")) {
             tempParameter.setComplex(true);
             tempParameter.setRoot(childNode);
             }
             tempParameter.setName(parts[0]); //must be the name                
             if (parts.length == 3) {
             tempParameter.setType(parts[1]);
             }
             */
            /*
             if (childNode.getChildCount() == 0) {
             if (intputOrOutput != null && intputOrOutput.equals("OUTPUT")) {
             operation.setOutputParameterString(operation.getOutputParameterString() + tempParameter.getName() + ",");
             operation.setNumberOfOutputParameters(operation.getNumberOfOutputParameters() + 1);
             } else {
             //operation.setInputParameterString(operation.getInputParameterString() + tempParameter.getName() + ",");
             operation.setNumberOfInputParameters(operation.getNumberOfInputParameters() + 1);
             }
             }
             */
            //BusinessEntity newEntity =null;
            if (tempParameter.isComplex()) {
                identifyBEandRelation(service, operation, newEntity, tempParameter, intputOrOutput);
            }
            // add to set and etc.
            //allParameters.add(childNode.toString());
            //allParameters = result + childNode.toString()+",";
        }
        //BusinessEntity entity = OntologyCheck(service.getServiceName(), parameter);        
    }

    private ArrayList<Attribute> addAndConvertAttributes(DefaultMutableTreeNode node) {
        ArrayList<Attribute> attributesList = new ArrayList<Attribute>();
        if (node != null) {
            int childCount = node.getChildCount();
            if (node.getChildCount() > 0) {
                for (int i = 0; i < childCount; i++) {
                    DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);

                    Parameter parameter = (Parameter) childNode.getUserObject();
                    Attribute attribute = new Attribute(parameter.getName());
                    attribute.setType(parameter.getType());
                    attributesList.add(attribute);
                }
            }
        }
        return attributesList;
    }

    public void refineBERelation(Service service) {
        boolean mandatoryStrongDep = false;
        boolean optionalStrongDep = false;
        boolean weakDep = false;
        ArrayList<Operation> operations = service.getOperations();
        if (operations.size() > 0) {
            for (Operation operation : operations) {
                //if (operation.getName().equals("createOpenShipment") || operation.getName().equals("addPackagesToOpenShipment")) {

                ArrayList<BusinessEntity> entities = operation.getServiceBEDataModel().getEntities();
                if (entities != null) {
                    for (BusinessEntity entity : entities) {
                        //if (entity.getName().equals("OpenshipOrder")) {
                        // get all entites that are nested in entity                            
                        ArrayList<BusinessEntity> nestedEntities = operation.getNestedEntities(entity);
                        for (BusinessEntity nestedEntity : nestedEntities) {
                              //  if (nestedEntity.getName().equals("ShippingLabel") || nestedEntity.getName().equals("Pickup") || nestedEntity.getName().equals("Shipper")) {
                            //check the rules again - OpenshipOrder has no relationship with Pickup? strange----
                            if (service.ifMandatoryStrongDomination(entity, nestedEntity)) { //strong containment can override weak containment
                                mandatoryStrongDep = true;
                            } else {
                                boolean sameEntity = false;
                                ArrayList<BusinessEntity> dominaters = service.getDominaters(nestedEntity);
                                for (BusinessEntity tempEntity: dominaters) {
                                    if (tempEntity.equals(entity)) {
                                        sameEntity = true;
                                    } else {
                                        sameEntity = false;
                                        break;
                                    }
                                }
                                if (sameEntity) {
                                    optionalStrongDep = true;
                                }
                            }
                            if (!mandatoryStrongDep && !optionalStrongDep) {
                                if (service.ifDomination(nestedEntity, entity)) {
                                    weakDep = true;
                                }
                            }

                            /*
                             ArrayList<Operation> operationsThatManipulateEntity = service.getOperationsThatManipulateEntity(nestedEntity);
                             mainLoop:
                             for (Operation operationThatManipulatesEntity : operationsThatManipulateEntity) {
                             ArrayList<BusinessEntity> entities2 = operationThatManipulatesEntity.getServiceBEDataModel().getEntities();; //TBD, we need to check output as well.
                             if (entities2 != null) {
                             for (BusinessEntity entity2 : entities2) {
                             if (service.ifDomination(entity2, nestedEntity)) {
                             if (entity2.equals(entity)) {
                             strongDep = true;
                             } else {
                             strongDep = false;
                             break mainLoop;
                             }
                             }
                             }
                             }
                             }
                             */
                            EntityPair entityPair = new EntityPair(entity, nestedEntity);

                            if (mandatoryStrongDep || optionalStrongDep) {
                                if (nestedEntity.isCompulsory())
                                    operation.getServiceBEDataModel().addExclusiveContainmentPair(entityPair);
                                else
                                    operation.getServiceBEDataModel().addOptionalExclusiveContainmentPair(entityPair);
                                        
                            //} else if (optionalStrongDep) {  //TBD TO REMOVE optional Strong Depedence
                            //    operation.getServiceBEDataModel().addOptionalStrongDependencePair(entityPair);
                            }
                            if (weakDep) {
                                if (service.ifAssociation(entity, nestedEntity)) {
                                    operation.getServiceBEDataModel().addAssociationPair(entityPair);
                                } else {
                                    if (nestedEntity.isCompulsory())
                                        //operation.getServiceBEDataModel().add
                                        operation.getServiceBEDataModel().addStrongInclusiveContainmentPair(entityPair);
                                    else
                                        operation.getServiceBEDataModel().addWeakInclusiveContainmentPair(entityPair);
                                }
                            }
                            mandatoryStrongDep = false;
                            optionalStrongDep = false;
                            weakDep = false;
                            //for (Parameter parameter : operation.getInputParameters()) {
                            //    identifyBEandRelation(service, operation, null, parameter);
                            //}
                        //}
                        }
                    //}
                    }
                }
            //}
                /*
                 XStream xstream = new XStream();
                 String strongPair = xstream.toXML(operation.getServiceBEDataModel().getStrongDependencePair());
                 System.out.println("-------------------------------Operation " + operation.getName() + "-----Strong Pair------------------------------------");
                 System.out.println(strongPair);
                 System.out.println("-------------------------------Operation " + operation.getName() + "-----Strong Pair------------------------------------");

                 String weakPair = xstream.toXML(operation.getServiceBEDataModel().getWeakDependencePair());
                 System.out.println("-------------------------------Operation " + operation.getName() + "-----Weak Pair------------------------------------");
                 System.out.println(weakPair);
                 System.out.println("-------------------------------Operation " + operation.getName() + "-----Weak Pair------------------------------------");
                 */
            }
        }

        //ArrayList<BusinessEntity> entities = service.getEntities();        
        //if (entities.size()>0) {
//            for (BusinessEntity entity : entities) {
        //for (Parameter parameter : operation.getInputParameters()) {
        //    identifyBEandRelation(service, operation, null, parameter);
        //}
        //          }
//        }
    }

    public BusinessEntity OntologyCheck(String serviceName, String parameterName, String parameterType) {
        //TODO we need to implement a more sophisticated Ontology mechanism
        //String attribute = null;

        //if (parameterType !=null)
///            attribute = parameterType;
//        else
//            attribute = parameterName;
        String entityName = null;
        if (parameterType != null) {
            entityName = readXMLFile(serviceName, parameterType, "Configurations/NonBOs.xml");
        } else {
            entityName = readXMLFile(serviceName, parameterName, "Configurations/NonBOs.xml");
        }

        //String entityName = readXMLFile(serviceName, attribute, "Configurations/BORepository.xml");
        BusinessEntity entity = null;
        if (entityName == null) {
            //check to see if there is a meaningFull Name
            String meaningfulBEName = null;
            if (parameterType != null) {
                meaningfulBEName = readXMLFile(serviceName, parameterType, "Configurations/PredefinedBOs.xml");
            } else {
                meaningfulBEName = readXMLFile(serviceName, parameterName, "Configurations/PredefinedBOs.xml");
            }

            if (meaningfulBEName == null) {
                meaningfulBEName = parameterName;
                if (parameterType != null && !meaningfulBEName.equals(parameterType)) {
                    meaningfulBEName = parameterName + parameterType;  //For now
                }
            }
            entity = new BusinessEntity(meaningfulBEName);
            entity.setType(parameterType);
        }
        return entity;
    }
    
    public void printJsonfile() {
        
    }

    public static void main(String[] args) throws FileNotFoundException {
        //Service service = new Service("InternetOfService/Amazon/AWSMechanicalTurkRequester.wsdl");
        //Service service = new Service("InternetOfService/Amazon/AWSECommerceService.wsdl");        
        //Service service = new Service("InternetOfService/WeatherForecastService.wsdl");        
        //Service service = new Service("InternetOfService/ec2.wsdl");
        //Service service = new Service("InternetOfService/PurchaseOrderRequest_Out.wsdl");                
        //Service service = new Service("InternetOfService/findpeoplefree.wsdl");

        long endTime, startTime, duration;
        StructuralInterfaceAnalysis structuralInterfaceAnalysis = new StructuralInterfaceAnalysis();
        
        //-------------------------Enterprise services----------------------------------------------/
        //Service service = new Service("TestData/ES/UPS/Ship.wsdl");        
        Service service = new Service("TestData/ES/Fedex/OpenShipService_v9.wsdl");
        //Service service = new Service("TestData/ES/Fedex/TrackService_v9.wsdl");
        //Service service = new Service("TestData/InternetServices/WeatherForecastService.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + duration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + duration);
        service.visualiseBEModel();
        service.outPutStatistics(duration, 0, false);
        service.visualiseAllOperationsBEModel();
        service.outputBEModelToXML();
        service.outputOperationsToXML();        
        
        /*
        
        Service service2 = new Service("TestData/ES/Fedex/ShipService_v13.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service2);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + duration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service2);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + duration);
        service2.visualiseBEModel();
        //service2.outPutStatistics(duration);
        
        

        Service service3 = new Service("TestData/ES/Fedex/PickupService_v9.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service3);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + duration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service3);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + duration);
        service3.visualiseBEModel();
        //service3.outPutStatistics(duration);
        
        
        Service service4 = new Service("TestData/ES/Fedex/ReturnTagService_v1.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service4);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + duration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service4);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + duration);
        service4.visualiseBEModel();
         //service4.outPutStatistics(duration);       
        
        Service service5 = new Service("TestData/ES/Fedex/CloseService_v3.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service5);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + duration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service5);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + duration);
        service5.visualiseBEModel();
        //service5.outPutStatistics(duration);
        
        
        Service service6 = new Service("TestData/ES/Fedex/AddressValidationService_v3.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service6);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + duration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service6);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + duration);
        service6.visualiseBEModel();
         //service6.outPutStatistics(duration);
           
        
        Service service7 = new Service("TestData/ES/UPS/Ship.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service7);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + duration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service7);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + duration);
        service7.visualiseBEModel();
        service7.outPutStatistics(duration, 0,false);
        //service6.visualiseAllOperationsBEModel();
        
        
        //-------------------------SaaS----------------------------------------------/
        Service service7 = new Service("TestData/SaaS/Amazon/AmazonS3.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service7);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + duration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service7);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + duration);
        service7.visualiseBEModel();
        service7.outPutStatistics(duration, 0, false);
        service7.visualiseAllOperationsBEModel();
        service7.outputBEModelToXML();
        service7.outputOperationsToXML();        
        

        Service service8 = new Service("TestData/SaaS/Amazon/AWSECommerceService.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service8);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + duration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service8);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + duration);
        service8.visualiseBEModel();
        service8.outPutStatistics(duration, 0, false);

        Service service9 = new Service("TestData/SaaS/Amazon/ec2.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service9);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + duration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service9);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + duration);
        service9.visualiseBEModel();
        service9.outPutStatistics(duration, 0, false);
        
        /*
        Service service10 = new Service("TestData/SaaS/Amazon/AWSMechanicalTurkRequester.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service10);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + duration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service10);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + duration);
        service10.visualiseBEModel();
         //service10.outPutStatistics(duration);
        //service9.visualiseAllOperationsBEModel();
        
                
        //-------------------------IS----------------------------------------------/
        Service service11 = new Service("TestData/InternetServices/findpeoplefree.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service11);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + duration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service11);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + duration);
        service11.outPutStatistics(duration, 0, false);
        //service11.outPutStatistics(duration);
                
                
        /*
        Service service12 = new Service("TestData/InternetServices/mailboxvalidator.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service12);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + duration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service12);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + duration);
        service12.visualiseBEModel();
        //service12.outPutStatistics(duration);

        Service service13 = new Service("TestData/InternetServices/WeatherForecastService.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service13);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + duration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service13);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + duration);
        service13.visualiseBEModel();
         //service13.outPutStatistics(duration);
        
        */
        
        System.exit(1);
        //service.visualiseAllOperationsBEModel();

    }

}
