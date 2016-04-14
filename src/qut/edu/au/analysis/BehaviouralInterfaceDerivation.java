/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.analysis;

import com.eviware.soapui.model.iface.Response;
import com.eviware.soapui.support.xml.XmlUtils;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import static qut.edu.au.Utility.generatePetrinet;
import qut.edu.au.entities.Attribute;
import qut.edu.au.entities.BusinessEntity;
import qut.edu.au.petrinet.logic.Arc;
import qut.edu.au.petrinet.logic.Petrinet;
import qut.edu.au.petrinet.logic.Place;
import qut.edu.au.petrinet.logic.Transition;
import qut.edu.au.services.Operation;
import qut.edu.au.services.Parameter;
import qut.edu.au.services.Service;
//import qut.edu.au.services.ServiceBEBehaviouralModel;
import qut.edu.au.treeSearch.PriorityList;
import qut.edu.au.treeSearch.Search;

/**
 *
 * @author fuguo
 */
public class BehaviouralInterfaceDerivation {

    private Service service;
    //private boolean compusoryWeakContainmentCalled = false;
    //private boolean strongContainmentCalled = false;
    //private boolean optionalWeakContainmentCalled = false;
    private ArrayList<String> filteredEntityList = null;

    public void mapToCRUDoperations(Service service) {
        for (BusinessEntity entity : service.getServiceBEDataModel().getEntities()) {
            mapToCRUDoperationsForEntity(service, entity);
        }
    }

    /*
     private boolean matchParameterWithAttribute(ArrayList<Parameter> parameterSet, ArrayList<Attribute> attributes, int numberOfMatching) {
     int matchingCounter = 0;
     for (Attribute attribute : attributes) {
     if (containInParameterSet(parameterSet, attribute)) {
     matchingCounter++;
     break;
     }
     }
     if (matchingCounter >= numberOfMatching) {
     return true;
     }
     return false;
     }
     */
    private boolean matchParameterWithAttribute(ArrayList<String> parameterSet, ArrayList<Attribute> attributes, int numberOfMatching) {
        int matchingCounter = 0;
        for (Attribute attribute : attributes) {
            if (parameterSet.contains(attribute.getName())) {
                matchingCounter++;
                break;
            }
        }
        if (matchingCounter >= numberOfMatching) {
            return true;
        }
        return false;
    }

    /*
     private boolean containInParameterSet(ArrayList<Parameter> parameterSet, Attribute attribute) {
     for (Parameter parameter : parameterSet) {
     if (parameter.getName().equals(attribute.getName())) {
     return true;
     }
     }
     return false;
     }
     */
    private int matchResponseXMLwithAttribute(ArrayList<Attribute> attributes, String responseXML) {
        int matchingCounter = 0;
        for (Attribute attribute : attributes) {
            if (responseXML.indexOf(attribute.getName()) > 0) {
                matchingCounter++;
            }
        }
        //TBD, use XQuery to serach
        /*
         XmlObject xml =null;
         try {
         xml = XmlUtils.createXmlObject(response);
         } catch (XmlException ex) {
         Logger.getLogger(BehaviouralInterfaceDerivation.class.getName()).log(Level.SEVERE, null, ex);
         }                
         //String queryExpression =                        
         for $x in doc("books.xml")/bookstore/book
         where $x/price>30
         return $x/title        
         //xml.execQuery(response)
         */
        return matchingCounter;
    }

    //this operation  is very ugly and stupid, TBD    
    private void makingFakeDataForNow(Service service) {

        // createOpenShipment (this is operation to create an openshiporder, i.e., Corder = {createOpenShipment}
        // addPackagesToOpenShipment (this is operation to create an PackageLineItem, i.e., Uitem = {addPackagesToOpenShipment}
        // confirmOpenShipment (this is operation to confirm creating an openshiporder, i.e., Uorder = {createOpenShipment, confirmOpenShipment}
        // modifyOpenShipment (this is operation to update an openshiporder, i.e., Uorder = {modifyOpenShipment}
        // -- Fedex
        //OpenShip service
        String serviceName = service.getServiceName();

        if (serviceName.equals("OpenShip")) {
            for (Operation operation : service.getOperations()) {
                if (operation.getName().equals("addPackagesToOpenShipment")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("PackageLineItem")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } //TBD -- to deal with more than one operations for creating a business entity
                //if (operation.getName().equals("confirmOpenShipment")) {
                //    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                //        if (eachEntity.getName().equals("OpenshipOrder")) {
                //            eachEntity.(operation);
                //        }
                //    }
                //}
                else if (operation.getName().equals("createOpenShipment")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("OpenshipOrder")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("deleteOpenShipment")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("OpenshipOrder")) {
                            eachEntity.setDeleteOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("deletePackagesFromOpenShipment")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("PackageLineItem")) {
                            eachEntity.setDeleteOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("modifyOpenShipment")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("OpenshipOrder")) {
                            eachEntity.setUpdateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("modifyPackageInOpenShipment")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("PackageLineItem")) {
                            eachEntity.setUpdateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("retrieveOpenShipment")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("OpenshipOrder")) {
                            eachEntity.setReadOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("retrievePackageInOpenShipment")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("PackageLineItem")) {
                            eachEntity.setReadOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("createConsolidation")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Consolidation")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("retrieveConsolidation")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Consolidation")) {
                            eachEntity.setReadOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("deleteOpenConsolidation")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Consolidation")) {
                            eachEntity.setDeleteOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("modifyConsolidation")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Consolidation")) {
                            eachEntity.setUpdateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("createPendingShipment")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("PendingShipment")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("deletePendingShipment")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("PendingShipment")) {
                            eachEntity.setDeleteOperation(operation);
                            break;
                        }
                    }
                }
            }
        } // -- Fedex
        //Ship service
        else if (serviceName.equals("Ship")) {
            for (Operation operation : service.getOperations()) {
                if (operation.getName().equals("processShipment")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("ShipOrder")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("processTag")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Tag")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("deleteShipment")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("ShipOrder")) {
                            eachEntity.setDeleteOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("deleteTag")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Tag")) {
                            eachEntity.setDeleteOperation(operation);
                            break;
                        }
                    }
                }
            }
        } // -- Fedex
        //Pickup service
        else if (serviceName.equals("Pickup")) {
            for (Operation operation : service.getOperations()) {
                if (operation.getName().equals("createPickup")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Pickup")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("cancelPickup")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Pickup")) {
                            eachEntity.setDeleteOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("getPickupAvailability")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Pickup")) {
                            eachEntity.setReadOperation(operation);
                            break;
                        }
                    }
                }
            }
        } //Close service
        //Return service
        else if (serviceName.equals("ReturnTag")) {
            for (Operation operation : service.getOperations()) {
                if (operation.getName().equals("getExpressTagAvailability")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("ReturnTag")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                    break;
                }
            }
        } else if (serviceName.equals("Close")) {
            for (Operation operation : service.getOperations()) {
                if (operation.getName().equals("smartPostClose")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("SmartPostClose")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("groundClose")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("GroundClose")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("closeWithDocuments")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("CloseDocument")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("groundCloseWithDocuments")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("GroundCloseWithDocuments")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                }
            }
        } //AddressValidation service
        else if (serviceName.equals("AddressValidation")) {
            for (Operation operation : service.getOperations()) {
                if (operation.getName().equals("addressValidation")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("AddressValidation")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                    break;
                }
            }
        } //Amazon Advertising -- AWSECommerceService
        else if (serviceName.equals("AWSECommerceServiceBinding")) {
            for (Operation operation : service.getOperations()) {
                if (operation.getName().equals("CartAdd")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Items")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("ItemLookup")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Items")) {
                            eachEntity.setReadOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("CartCreate")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Cart")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("CartGet")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Cart")) {
                            eachEntity.setReadOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("CartModify")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Cart")) {
                            eachEntity.setUpdateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("CartClear")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Cart")) {
                            eachEntity.setDeleteOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("SimilarityLookup")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Similarity")) {
                            eachEntity.setReadOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("BrowseNodeLookup")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("BrowseNode")) {
                            eachEntity.setReadOperation(operation);
                            break;
                        }
                    }
                }

            }
        } //Amazon S3 -- AmazonS3SoapBinding
        else if (serviceName.equals("AmazonS3SoapBinding")) {
            for (Operation operation : service.getOperations()) {
                if (operation.getName().equals("CreateBucket")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Bucket")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("DeleteBucket")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Bucket")) {
                            eachEntity.setDeleteOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("ListBucket")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Bucket")) {
                            eachEntity.setReadOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("GetBucketAccessControlPolicy")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("AccessControlList")) {
                            eachEntity.setReadOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("SetBucketAccessControlPolicy")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("AccessControlList")) {
                            Association association = new Association("SetObjectAccessControlPolicy", "Bucket");
                            eachEntity.addAssociations(association);
                            break;
                        }
                    }
                } //this overrides the previouis one, TBD - we should cope with this kind of scenaria where a business entity is associated with
                //two different entities, and correspondlingly they have disparate "create" operations.                
                else if (operation.getName().equals("SetObjectAccessControlPolicy")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("AccessControlList")) {
                            Association association = new Association("SetObjectAccessControlPolicy", "Object");
                            eachEntity.addAssociations(association);
                            break;
                        }
                    }
                } else if (operation.getName().equals("GetObject")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Object")) {
                            eachEntity.setReadOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("PutObject")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Object")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("GetBucketLoggingStatus")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("BucketLoggingStatus")) {
                            eachEntity.setReadOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("SetBucketLoggingStatus")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("BucketLoggingStatus")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("DeleteObject")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Object")) {
                            eachEntity.setDeleteOperation(operation);
                            break;
                        }
                    }
                }
            }
        } //Amazon AWSMechanicalTurkRequesterBinding -- AWSMechanicalTurkRequesterBinding
        else if (serviceName.equals("AWSMechanicalTurkRequesterBinding")) {
            for (Operation operation : service.getOperations()) {
                if (operation.getName().equals("ApproveAssignment")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Assignment")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("ApproveRejectedAssignment")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("ApprovedRejectedAssignment")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("AssignQualification")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Qualification")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("RevokeQualification")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Qualification")) {
                            eachEntity.setDeleteOperation(operation);
                            break;
                        }
                    }

                } else if (operation.getName().equals("BlockWorker")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("BlockedWorker")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("GetBlockedWorkers")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("BlockedWorker")) {
                            eachEntity.setReadOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("UnblockWorker")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("BlockedWorker")) {
                            eachEntity.setDeleteOperation(operation);
                            break;
                        }
                    }

                } else if (operation.getName().equals("ChangeHITTypeOfHIT")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("HITType")) {
                            eachEntity.setUpdateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("CreateHIT")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("HIT")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("GetHIT")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("HIT")) {
                            eachEntity.setReadOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("RegisterHITType")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("HITType")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("CreateQualificationType")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("QualificationType")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("UpdateQualificationType")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("QualificationType")) {
                            eachEntity.setUpdateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("GetQualificationType")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("QualificationType")) {
                            eachEntity.setReadOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("DisposeHIT")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("HIT")) {
                            eachEntity.setDeleteOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("DisposeQualificationType")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("QualificationType")) {
                            eachEntity.setDeleteOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("ExtendHIT")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("HIT")) {
                            eachEntity.setUpdateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("GetAssignment")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Assignment")) {
                            eachEntity.setReadOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("GetHIT")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("HIT")) {
                            eachEntity.setReadOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("GetQualificationType")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("QualificationType")) {
                            eachEntity.setReadOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("GrantBonus")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("GrantedBonus")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("GrantQualification")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("Qualification")) {
                            eachEntity.setUpdateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("Help")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("HelpEntity")) {
                            eachEntity.setCreateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("GetQualificationScore")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("QualificationScore")) {
                            eachEntity.setReadOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("UpdateQualificationScore")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("QualificationScore")) {
                            eachEntity.setUpdateOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("GetAccountBalance")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("AccountBalance")) {
                            eachEntity.setReadOperation(operation);
                            break;
                        }
                    }
                } else if (operation.getName().equals("GetBonusPayments")) {
                    for (BusinessEntity eachEntity : service.getServiceBEDataModel().getEntities()) {
                        if (eachEntity.getName().equals("BonusPayment")) {
                            eachEntity.setReadOperation(operation);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void mapToCRUDoperationsForEntity(Service service, BusinessEntity entity) {
        ArrayList<Operation> operationsThatManipulateEntity = service.getOperationsThatManipulateEntity(entity);
        //ArrayList<BusinessEntity> dominaters = new ArrayList<BusinessEntity>();        
        for (Operation operationThatManipulatesEntity : operationsThatManipulateEntity) {
            //ValidCombination validCombination = operationThatManipulatesEntity.retrieveValidCombination();
            //if (validCombination.getCombinations().size() > 0) {
            //Combination combination = validCombination.getCombinations().get(0); //get the first combination for now
            //String response = combination.getResponse();

            //ArrayList<Parameter> inputParameters = combination.getParameterSet();
            //ArrayList<Parameter> inputParameters = operationThatManipulatesEntity.getInputParameters();
            //ArrayList<Parameter> outputParameters = operationThatManipulatesEntity.getOutputParameters();
            ArrayList<String> inputParameters = operationThatManipulatesEntity.getInputParameterString();
            ArrayList<String> outputParameters = operationThatManipulatesEntity.getOutputParameterString();

            ArrayList<Attribute> attributes = entity.getAttributes();
            Attribute key = entity.getKey();
            if (key != null) {
                if (inputParameters.contains(key)) {  //if key(bo) ∈ Imin
                    if (matchParameterWithAttribute(inputParameters, attributes, 10)) //else if Imin ∩ A(bo) 6= ∅ then
                    {
                        entity.setUpdateOperation(operationThatManipulatesEntity);
                    } //else if (matchResponseXMLwithAttribute(attributes, response) > 10) //Assign 10 to the threshhold for now  //if Orcv ∩ A(bo) 6= ∅ then                      
                    else if (matchParameterWithAttribute(outputParameters, attributes, 10)) //Assign 10 to the threshhold for now  //if Orcv ∩ A(bo) 6= ∅ then
                    {
                        entity.setReadOperation(operationThatManipulatesEntity);
                    } //else if (response != null) {
                    else {
                        entity.setDeleteOperation(operationThatManipulatesEntity);
                    }
                } else if (matchParameterWithAttribute(inputParameters, attributes, 10) //Assign 10 to the threshhold for now
                        //&& outputParameters.contains(key.getName())) {
                        && outputParameters.contains(key)) {
                    entity.setCreateOperation(operationThatManipulatesEntity);
                }
            }
            //}
        }
    }

    private int chooseOperation(ArrayList<Operation> operations) {
        Random randomGenerator = new Random(System.currentTimeMillis());
        //randome number, treat the tree as the uniform distribution for now
        //String randomElement = treeString.get(randomGenerator.nextInt(treeString.size()+path.size()));
        int randomNumber = randomGenerator.nextInt(operations.size());
        return randomNumber;
    }

    private ArrayList<Petrinet> generateProtocolForCompulsoryEntities(BusinessEntity entity, String flag) {
        ArrayList<Petrinet> result = new ArrayList<Petrinet>();
        Place preEntityCreation = null;
        //Transition createEntity = null;
        Transition readEntity = null;
        //Place entityReady =null;

        ArrayList<BusinessEntity> weakContainmentEntities = service.getDepedentEntities(entity, "WEAK", "YES");  //get the compulsory weak entities
        //ArrayList<BusinessEntity> weakAssociationEntities = service.getDepedentEntities(entity, "ASSOCIATION", "YES");  //get the compulsory weak entities

        //ArrayList<BusinessEntity> weakEntities = new ArrayList<BusinessEntity>(weakContainmentEntities);
        //weakEntities.addAll(weakAssociationEntities);

        if (!weakContainmentEntities.isEmpty()) {
            //if (compusoryWeakContainmentCalled) return result;            
            for (BusinessEntity weakDepdendenceEntity : weakContainmentEntities) {

                Petrinet petrinet = null;
                if (flag != null && flag.equals("ACTUAL")) {
                    petrinet = weakDepdendenceEntity.getActualCreateBehaviouralModel();
                } else {
                    petrinet = weakDepdendenceEntity.getAbstractCreateBehaviouralModel();
                }

                if (petrinet == null) {
                    petrinet = generateProtocolForCreateBO(weakDepdendenceEntity, flag);
                }

                //if (!filteredEntityList.contains(weakDepdendenceEntity.getName()))
                //    continue;
                //Petrinet petrinet = new Petrinet("Compuslory weak entity: "+ entity.getName());                
                //preEntityCreation = petrinet.place("Pre" + weakDepdendenceEntity.getName());
                if (petrinet != null) {
                    preEntityCreation = petrinet.getInitialPlace();
                    //new code 
                    //Petrinet netstPetrinet = generateAbstractProtocolForCreateBO(weakDepdendenceEntity);                

                    //petrinet.merge(netstPetrinet);
                    //old code
                    //createEntity = petrinet.transition("Create_" + weakDepdendenceEntity.getName());                
                    //petrinet.arc("a1", preEntityCreation, createEntity);
                    readEntity = petrinet.transition("Read" + weakDepdendenceEntity.getName());
                    petrinet.arc("a2", preEntityCreation, readEntity);

                    //entityReady = petrinet.place(weakDepdendenceEntity.getName() + "_ready");
                    //petrinet.arc("a3", createEntity, entityReady);               
                    petrinet.arc("a4", readEntity, petrinet.getLastPlace());

                    //petrinet.merge(netstPetrinet);                
                    result.add(petrinet);
                }
            }
            //compusoryWeakContainmentCalled =true;
        }
        return result;
    }

    private ArrayList<Petrinet> generateProtocolForOptionalEntities(BusinessEntity entity, String flag) {
        ArrayList<Petrinet> result = new ArrayList<Petrinet>();
        Place preEntityCreation = null;
        //Transition createEntity = null;
        Transition emptyEntity = null;
        Place entityReady = null;

        ArrayList<BusinessEntity> weakContainmentEntities = service.getDepedentEntities(entity, "WEAK", "NO");  //get the compulsory weak entities
        //ArrayList<BusinessEntity> weakAssociationEntities = service.getDepedentEntities(entity, "ASSOCIATION", "NO");  //get the compulsory weak entities

        //ArrayList<BusinessEntity> weakEntities = new ArrayList<BusinessEntity>(weakContainmentEntities);
        //weakEntities.addAll(weakAssociationEntities);

        //ArrayList<BusinessEntity> weakEntities = service.getDepedentEntities(entity, "WEAK", "NO");  //get the optional weak entities
        if (!weakContainmentEntities.isEmpty()) {
            //if (optionalWeakContainmentCalled) return result;

            for (BusinessEntity weakDepdendenceEntity : weakContainmentEntities) {
                Petrinet petrinet = null;
                if (flag != null && flag.equals("ACTUAL")) {
                    petrinet = weakDepdendenceEntity.getActualCreateBehaviouralModel();
                } else {
                    petrinet = weakDepdendenceEntity.getAbstractCreateBehaviouralModel();
                }

                if (petrinet == null) {
                    petrinet = generateProtocolForCreateBO(weakDepdendenceEntity, flag);
                }

                //if (!filteredEntityList.contains(weakDepdendenceEntity.getName()))
                //    continue;
                //Petrinet petrinet = new Petrinet("Optional weak entity: "+ entity.getName());
                if (petrinet != null) {

                    // preEntityCreation = petrinet.place("Pre" + weakDepdendenceEntity.getName());
                    preEntityCreation = petrinet.getInitialPlace();

                    //createEntity = petrinet.transition("Create_" + weakDepdendenceEntity.getName());
                    //petrinet.arc("a1", preEntityCreation, createEntity);
                    emptyEntity = petrinet.transition("empty");
                    petrinet.arc("a2", preEntityCreation, emptyEntity);

                    //entityReady = petrinet.place(weakDepdendenceEntity.getName() + "_ready");
                    entityReady = petrinet.getLastPlace();
                    //petrinet.arc("a3", createEntity, entityReady);

                    petrinet.arc("a4", emptyEntity, entityReady);
                    result.add(petrinet);
                }
            }
            //optionalWeakContainmentCalled = true;
        }
        return result;
    }

    private ArrayList<Petrinet> generateProtocolForStrongEntities(BusinessEntity entity, String flag) {
        ArrayList<Petrinet> result = new ArrayList<Petrinet>();
        //Transition createEntity = null;
        //Place entityCreated =null;

        ArrayList<BusinessEntity> strongEntities = service.getDepedentEntities(entity, "STRONG", null);
        if (strongEntities != null && !strongEntities.isEmpty()) {
            //if (strongContainmentCalled) return result;
            for (BusinessEntity strongDepdendenceEntity : strongEntities) {
                Petrinet petrinet = null;
                
                if (flag != null && flag.equals("ACTUAL")) {
                    petrinet = strongDepdendenceEntity.getActualCreateBehaviouralModel();
                } else {
                    petrinet = strongDepdendenceEntity.getAbstractCreateBehaviouralModel();
                }

                if (petrinet == null) {
                    petrinet = generateProtocolForCreateBO(strongDepdendenceEntity, flag);
                }

                if (petrinet != null) {
                    //Petrinet petrinet = new Petrinet("Strong entity: "+ entity.getName());                
                    //createEntity = petrinet.transition("Create_" + strongDepdendenceEntity.getName());                
                    
                    if (petrinet.getPlaces().contains(strongDepdendenceEntity+"_st0")) { //sometime the retrieved one comes with removed head already
                        petrinet.getPlaces().remove(0);
                        petrinet.getArcs().remove(0);
                    }
                    //entityCreated = petrinet.place(strongDepdendenceEntity.getName() + "_created", 1);
                    //int index = petrinet.getIndexOfTransition(strongDepdendenceEntity.getName()+"_Ending_st");
                    //petrinet.arc("a3", petrinet.getTransitions().get(index), entityCreated);                
                    result.add(petrinet);
                }
            }
            //strongContainmentCalled = true;
        }
        return result;
    }

    private Petrinet generateLifeCycle(BusinessEntity entity) {
        Petrinet result = new Petrinet(entity.getName() + "_lifeCycle");

        Petrinet createNet = entity.getActualCreateBehaviouralModel();
        Petrinet readNet = entity.getActualReadBehaviouralModel();
        Petrinet updateNet = entity.getActualUpdateBehaviouralModel();
        Petrinet deleteNet = entity.getActualDeleteBehaviouralModel();

        if (createNet == null) {
            createNet = generateProtocolForCreateBO(entity, "ACTUAL");
            if(createNet != null)
                entity.setActualCreateBehaviouralModel(createNet);
            else
                return null;
        }
        

        if (createNet != null) {
            result.merge(createNet);
        }
        
        if (updateNet == null) {
            updateNet = getLifeCycleNode(entity, "update");
            if (updateNet != null) {
                result.merge(updateNet);
                if (createNet!=null) {
                    Transition lifecyle = result.transition("st_lifeCycle");
                    result.arc("a1", createNet.getLastPlace(), lifecyle);  //connect create and read
                    result.arc("a2", lifecyle, updateNet.getPlaces().get(0));
                }
            }
        }

        if (readNet == null) {
            readNet = getLifeCycleNode(entity, "read");
            if (readNet != null) {
                result.merge(readNet);

                Integer index = result.getIndexOfTransition("st_lifeCycle");
                Transition lifecyle = null;
                if (index == null) {
                    lifecyle = result.transition("st_lifeCycle");
                    if (createNet!=null)                    
                        result.arc("a1", createNet.getLastPlace(), lifecyle);  //connect create and update
                } else {
                    lifecyle = result.getTransitions().get(index);
                }
                result.arc("a3", lifecyle, readNet.getPlaces().get(0));

                if (updateNet != null) {  //connect update and read
                    Transition lifecyleSt2 = result.transition("st_lifeCycle1");
                    result.arc("a4", updateNet.getLastPlace(), lifecyleSt2);
                    result.arc("a5", lifecyleSt2, readNet.getPlaces().get(0));
                }
            }
        }

        if (deleteNet == null) {
            deleteNet = getLifeCycleNode(entity, "delete");
            if (deleteNet != null) {
                result.merge(deleteNet);
                Integer index = result.getIndexOfTransition("st_lifeCycle");
                Transition lifecyle = null;
                if (index == null) {
                    lifecyle = result.transition("st_lifeCycle");
                    if (createNet!=null)                    
                        result.arc("a6", createNet.getLastPlace(), lifecyle);  //connect create
                } else {
                    lifecyle = result.getTransitions().get(index);
                }
                result.arc("a7", lifecyle, deleteNet.getPlaces().get(0));
                
                if (updateNet != null) {  //connect update and read                    
                    index = result.getIndexOfTransition("st_lifeCycle1");
                    lifecyle = null;
                    if (index == null) {
                        lifecyle = result.transition("st_lifeCycle1");
                        if (updateNet!=null)                    
                            result.arc("a1", updateNet.getLastPlace(), lifecyle);  //connect create and update
                    } else {
                        lifecyle = result.getTransitions().get(index);
                    }
                    result.arc("a8", lifecyle, deleteNet.getPlaces().get(0));
                }                
            }
        }
        return result;
    }

    private Petrinet getLifeCycleNode(BusinessEntity entity, String operationString) {
        Petrinet net = new Petrinet(entity.getName() + "_"+operationString);
        Place p1 = null, p2 = null;
        Transition t1 = null;
        Operation operation = null;
        if (operationString.equals("read")) {
            p1 = net.place("pre" + entity.getName() + "_Rd", 1);
            p2 = net.place("post" + entity.getName() + "_Rd");
            operation = entity.getReadOperation();
            if (operation == null) {
                return null;
            }
            t1 = net.transition(operation.getName());
        } else if (operationString.equals("update")) {
            p1 = net.place("pre" + entity.getName() + "_Up", 1);
            p2 = net.place("post" + entity.getName() + "_Up");
            operation = entity.getUpdateOperation();
            if (operation == null) {
                return null;
            }
            t1 = net.transition(operation.getName());
        } else if (operationString.equals("delete")) {
            p1 = net.place("pre" + entity.getName() + "_Dl", 1);
            p2 = net.place("post" + entity.getName() + "_Dl");
            operation = entity.getDeleteOperation();
            if (operation == null) {
                return null;
            }
            t1 = net.transition(operation.getName());
        }
        net.arc("a1", p1, t1);
        net.arc("a1", t1, p2);
        return net;
    }

    private Petrinet generateProtocolForCreateBO(BusinessEntity entity, String flag) {
        //initialisation of the petri net model
        Petrinet result = new Petrinet(entity.getName() + "_C");
        
        Transition t1 = result.transition(entity.getName() + "_st0");  //first silent transition
        Transition t2 = null;
        Place p1 = result.place(entity.getName() + "_q0", 1);

        result.arc("a1", p1, t1);

        Place preEntityCreation = null;
        Place entityCreated = null;

        ArrayList<Petrinet> petrinets = generateProtocolForCompulsoryEntities(entity, flag);
        if (!petrinets.isEmpty()) {
            t2 = result.transition(entity.getName() + "_st1");  //second silent transition
            for (Petrinet net : petrinets) {
                Place firstPlace = net.getInitialPlace();
                result.arc("a1", t1, firstPlace);
                result.merge(net);
                Place lastPlace = net.getLastPlace();
                result.arc("a1", lastPlace, t2);
            }
        }
        
        preEntityCreation = result.place("pre" + entity.getName() + "_Cr");
        entityCreated = result.place("Post" + entity.getName() + "_Cr");
        
        if (t2 != null) {
            result.arc("a1", t2, preEntityCreation);
        } else {
            result.arc("a1", t1, preEntityCreation);
        }

        //Transition endingTransition = null;
        if (flag != null && flag.equals("ACTUAL")) {
            Operation operation = null;
            operation = entity.getCreateOperation();
            
            if (operation != null) {
                Transition createParentEntity = result.transition(operation.getName());
                result.arc("a1", preEntityCreation, createParentEntity);
                result.arc("a1", createParentEntity, entityCreated);
                //endingTransition = result.transition(entity.getName() + "_Ending_st");
            } else {
                return null;
            }
        } else {
            Transition entityTransition = null;
            entityTransition = result.transition("cr_" + entity.getName());
            result.arc("a1", preEntityCreation, entityTransition);
            result.arc("a1", entityTransition, entityCreated);
            //endingTransition = result.transition(entity.getName() + "_Ending_st");
        }

        Transition endingTransition = result.transition(entity.getName() + "_Ending_st");

        petrinets = generateProtocolForStrongEntities(entity, flag);
        if (!petrinets.isEmpty()) {
            for (Petrinet net : petrinets) {
                //net.getPlaces().remove(0);
                //net.getArcs().remove(0);
                result.merge(net);
                Transition firstTransition = net.getfirstTransition();
                result.arc("a1", entityCreated, firstTransition);                
                result.arc("a1", net.getLastPlace(), endingTransition);
            }
        } else {
            result.arc("a1", entityCreated, endingTransition);
        }
        
        petrinets = generateProtocolForOptionalEntities(entity, flag);
        if (!petrinets.isEmpty()) {
            //find the Ending transition.            
            for (Petrinet net : petrinets) {
                result.merge(net);
                Place firstPlace = net.getInitialPlace();                
                result.arc("a1", t1, firstPlace);
                result.arc("a1", net.getLastPlace(), endingTransition);
            }
        }

        int index = result.getIndexOfTransition(entity.getName() + "_Ending_st");
        Place pEnd = result.place(entity.getName() + "_q_end", 1);
        result.arc("a1", result.getTransitions().get(index), pEnd);
        
        ArrayList<BusinessEntity> AssociationEntities = service.getDepedentEntities(entity, "ASSOCIATION", null);

        Transition assoSilentTrans = null;
        
        if (!AssociationEntities.isEmpty()) {
            //if (compusoryWeakContainmentCalled) return result;            
            for (BusinessEntity associatoinDepdendenceEntity : AssociationEntities) {
                ArrayList<Association> associations = associatoinDepdendenceEntity.getAssociations();
                if (!associations.isEmpty()) {
                    if (assoSilentTrans == null)
                        assoSilentTrans = result.transition("Tasso");
                    
                    Association asso = associations.get(0);
                    if (asso.getMasterEntity().equals(entity)) {
                        Transition assoTransition = result.transition(asso.getOperation());
                        Transition emptyTransition = result.transition("empty");
                        
                        Place preEntityAsso = result.place("pre" + entity.getName() + "_Asso"+"_"+entity.getName()+"_"+associatoinDepdendenceEntity.getName());
                        Place postEntityAsso = result.place("post" + entity.getName() + "_Asso"+"_"+entity.getName()+"_"+associatoinDepdendenceEntity.getName());
                        
                        result.arc("a1", result.getLastPlace(),assoSilentTrans);
                        result.arc("a1", assoSilentTrans,preEntityAsso);
                        result.arc("a1", preEntityAsso,assoTransition);
                        result.arc("a1", assoTransition,postEntityAsso);
                        result.arc("a1", preEntityAsso,emptyTransition);
                        result.arc("a1", emptyTransition,postEntityAsso);
                    }
                }
            }
        }        
        return result;
    }

    private Petrinet generateProtocolForCreateBO2(BusinessEntity entity) {

        //initialisation of the petri net model
        Petrinet result = new Petrinet(entity.getName() + "_CREATE");
        Transition t1 = result.transition(entity.getName() + "_st0");  //first silent transition
        Place p1 = result.place(entity.getName() + "_q0", 1);
        result.arc("a1", p1, t1);

        //ArrayList<Transition> transitions = new ArrayList<Transition>();
        //Transition initialTransition = new Transition("t0");
        //transitions.add(initialTransition);
        //ArrayList<Place> places = new ArrayList<Place>();
        //Place initialPrePlace = new Place("q0");
        //Place initialPostPlace = new Place("q0'");        
        //places.add(initialPrePlace);
        //places.add(initialPostPlace);
        //ArrayList<Flow> flowsOverall = new ArrayList<Flow>();
        //ArrayList<Flow> flowsCreate = new ArrayList<Flow>();        
        //Flow initialFlow = new Flow(initialPrePlace, initialTransition, initialPostPlace);
        //if (entity.getCreateOperations() != null && !entity.getCreateOperations().isEmpty()) {
        ArrayList<Operation> operations = new ArrayList<Operation>();
            //for (Operation operation : entity.getCreateOperations()) {
        //    operations.add(operation);
        //}

        //ArrayList<Operation> operations = entity.getCreateOperations();
        Place firstPlace = null;

        Transition lastTransition = null;

        while (operations.size() > 0) {

            // ArrayList<Operation> tempOperations = operations;
            ArrayList<Operation> tempOperations = new ArrayList<Operation>();
            for (Operation operation : operations) {
                tempOperations.add(operation);
            }

            //tempOperations = operations;
            String responseMessage = null;
            Operation currentOperation = null;
            do {
                int currentIndex = 0;
                if (tempOperations.size() > 0) {
                    currentIndex = chooseOperation(tempOperations);
                }
                currentOperation = tempOperations.get(currentIndex);

                /* TBD Revert this code back when we have valid combinations
                
                 ValidCombination validCombination = currentOperation.retrieveValidCombination();
                 if (validCombination != null && validCombination.getCombinations().size() > 0) {
                 Combination combination = validCombination.getCombinations().get(0); //get the first combination for now
                 ArrayList<Parameter> inputParameters = combination.getParameterSet();                    
                 ArrayList<String> requestResponse = this.service.fakeInvokeServiceOperation(currentOperation.getName(), inputParameters);
                 if (requestResponse != null && requestResponse.size()==2) {
                 responseMessage= requestResponse.get(1);
                 }
                 }
                 */
                ArrayList<String> requestResponse = this.service.fakeInvokeServiceOperation(currentOperation.getName(), null);
                if (requestResponse != null && requestResponse.size() == 2) {
                    responseMessage = requestResponse.get(1);
                }
                tempOperations.remove(currentIndex);
            } while (responseMessage != null && !responseMessage.contains("successfully") && (tempOperations.size() > 0));

            if (currentOperation != null) {
                String operationName = currentOperation.getName();
                //Place prePlace = new Place("PreCon_"+operationName);                
                //Place postPlace = new Place("PostCon_"+operationName);                
                //lastPostPlace = p;
                Place p = result.place("PreCon_" + operationName);

                // make the connection bwetteen the create operations and the first silent transition                
                if (lastTransition != null) {
                    result.arc("arc_" + operationName, lastTransition, p);
                } else if (firstPlace == null) {
                    result.arc("arc_" + operationName, t1, p);
                    firstPlace = p;
                }

                Transition t = result.transition(operationName);
                lastTransition = t;
                result.arc("arc_" + operationName, p, t);
                //places.add(prePlace);
                //places.add(postPlace);
                //transitions.add(transition);                
                //Flow createOperationFlow = new Flow(prePlace,transition,postPlace);                
                //flowsCreate.add(createOperationFlow);
            }
            operations.remove(currentOperation);
        }

        if (lastTransition != null) {  //got create operations
            Place p = result.place("Post_" + entity.getName() + "_CREATION");
            result.arc("arc_" + entity.getName() + "_CREATION", lastTransition, p);
            Transition t2 = result.transition(entity.getName() + "_st1");  //second silent transition
            result.arc("arc_joint", result.getCurrentPlace(), t2);

        }
        //}

        //Flow connectionFlow = new Flow(lastPostPlace, firstSilentTransition,null);        
        ArrayList<BusinessEntity> strongEntities = service.getDepedentEntities(entity, "STRONG", null);
        if (strongEntities != null && !strongEntities.isEmpty()) {
            //ArrayList<Petrinet> strongEntitiesBEModels = new ArrayList<Petrinet>();
            for (BusinessEntity strongDepdendenceEntity : strongEntities) {
                Petrinet strongEntityBEModel = generateProtocolForCreateBO2(strongDepdendenceEntity);
                if (strongEntityBEModel != null) {
                    result.merge(strongEntityBEModel);
                    //strongEntitiesBEModels.add(strongEntityBEModel);
                }
            }
            //for (Petrinet BEModel : strongEntitiesBEModels) {
            //    result.merge(BEModel);
            //}
        }

        ArrayList<BusinessEntity> weakEntities = service.getDepedentEntities(entity, "WEAK", "YES");
        if (weakEntities != null && !weakEntities.isEmpty()) {
            //ArrayList<Petrinet> weakEntitiesBEModels = new ArrayList<Petrinet>();
            for (BusinessEntity weakDepdendenceEntity : weakEntities) {
                Petrinet weakEntityBEModel = generateProtocolForCreateBO2(weakDepdendenceEntity);
                result.setCurrentTransition(0); //set the first silent transition as the current transition
                //weakEntityBEModel.getCurrentPlace()
                if (weakEntityBEModel != null) {
                    result.merge(weakEntityBEModel);

                    if (weakDepdendenceEntity.isCompulsory()) {
                        Integer index = result.getIndexOfTransition(entity.getName() + "_st1");
                        if (index != null) {
                            result.arc("mergingPetrinet", weakEntityBEModel.getCurrentPlace(), result.getTransitions().get(index));
                        }
                    }
                }
                //weakEntitiesBEModels.add(weakEntityBEModel);
            }
        }

        if ((result.getPlaces().size() == 1) && (result.getTransitions().size() == 1)) {
            return null;
        }
        //ArrayList<BusinessEntity> entities = service.get        
        //Transition secondSilentTransition = new Transition("st");
        //Flow connectionFlow2 = new Flow(lastPostPlace, firstSilentTransition,null);
        return result;
    }

    private void generateValidCombinationsForService() {
        if (this.service != null) {
            ArrayList<Operation> operations = this.service.getOperations();
            if (operations.size() > 0) {
                Search search = new Search();
                for (Operation operation : operations) {
                    if (operation.getName().equals("createOpenShipment")) {
                        PriorityList list = search.upperConfidenceTreeSearch(1, 10, service, operation);
                    }
                }
            }
        }
    }
    
    public void categoriseOperations(Service service) {
        for(Operation operation: service.getOperations()) {
            //operation.getAccecptedParameterSets()
            //operation.getServiceBEDataModel()
        }
            
    }

    public void generateProtocols(Service service) {

        makingFakeDataForNow(service);

        for (BusinessEntity entity : service.getServiceBEDataModel().getEntities()) {
            //each entity has a petri net
            //if (entity.getName().equals("OpenshipOrder")) {
            //Petrinet net = generateProtocolForCreateBO(entity);

            //1. categorise the operations
            
            
            //2. check if one entity doesn't have any dependencies, then skip it.
            //3. derive the actual be models.
            //if (entity.getName().equals("OpenshipOrder")) {
            //    makingFakeDataForNow(service);
            //}
            //if (service.ifPossessDependency(entity)) {
            //if (entity.getName().equals("Bucket")) {
            Petrinet net = generateProtocolForCreateBO(entity, "ABSTRACT");
            if (net != null) {
                entity.setAbstractCreateBehaviouralModel(net);
                //generatePetrinet(net, entity.getName() + "AbstractCreateBEmodel",service.getServiceName());                
                //generatePNMLDocument(net, service.getServiceName(), entity.getName(), "ABSTRACT");
            }

            net = generateProtocolForCreateBO(entity, "ACTUAL");
            if (net != null) {
                entity.setActualCreateBehaviouralModel(net);
                generatePetrinet(net, entity.getName() + "ExecutableCreateBEmodel",service.getServiceName());
                //generatePNMLDocument(net, service.getServiceName(), entity.getName(), "ACTUAL");  //TODO ENABLE IT BACK
            }

            net = generateLifeCycle(entity);
            if (net != null && !net.getArcs().isEmpty()) {
                generatePetrinet(net, entity.getName() + "lifecyle", service.getServiceName());
                entity.setLifeCycle(net);
                //generatePNMLDocument(net, service.getServiceName(), entity.getName(), "LIFECYCLE");  //TODO ENABLE IT BACK
            }

            //}
            //}
            // Generate actual protocols                
            //net = generateProtocolForCreateBO(entity);
            //generatePetrinet(net, entity.getName() + "CreateBEmodel");                
            //entity.setAbstractBEBehaviouralModel(net);                
            //generatePNMLDocument(net, service.getServiceName(), entity.getName());
            //}
        }
    }

    public static void main(String[] args) {

        long endTime, startTime, structuralDuration, behaviouralDuration;
        BehaviouralInterfaceDerivation behaviouralDerivation = null;
        StructuralInterfaceAnalysis structuralInterfaceAnalysis = new StructuralInterfaceAnalysis();

        //-------------------------Enterprise services----------------------------------------------/
        Service service = new Service("TestData/ES/Fedex/OpenShipService_v7.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service);
        endTime = System.nanoTime();
        structuralDuration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        //System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + structuralDuration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service);
        endTime = System.nanoTime();
        structuralDuration = structuralDuration + (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in structural derivation: " + structuralDuration);

        behaviouralDerivation = new BehaviouralInterfaceDerivation(service);
        //behaviouralDerivation.mapToCRUDoperations(service);
        startTime = System.nanoTime();
        behaviouralDerivation.generateProtocols(service);
        endTime = System.nanoTime();
        behaviouralDuration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds
        System.out.print("Time consumed in generating abstract protocols for serivce: " + behaviouralDuration + " seconds");
        service.outPutStatistics(structuralDuration, behaviouralDuration, true);

        
        
        //System.out.print("first arguement "+ args[0]);
                
        Service service2 = new Service("TestData/ES/Fedex/ShipService_v13.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service2);
        endTime = System.nanoTime();
        structuralDuration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + structuralDuration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service2);
        endTime = System.nanoTime();
        structuralDuration = structuralDuration + (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + structuralDuration);
               

        behaviouralDerivation = new BehaviouralInterfaceDerivation(service2);
        //behaviouralDerivation.mapToCRUDoperations(service);
        startTime = System.nanoTime();
        behaviouralDerivation.generateProtocols(service2);
        endTime = System.nanoTime();
        behaviouralDuration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds
        System.out.print("Time consumed in generating abstract protocols for serivce: " + behaviouralDuration + " seconds");
        service2.outPutStatistics(structuralDuration, behaviouralDuration, true);
        
        /*

        Service service3 = new Service("TestData/ES/Fedex/PickupService_v9.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service3);
        endTime = System.nanoTime();
        structuralDuration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + structuralDuration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service3);
        endTime = System.nanoTime();
        structuralDuration = structuralDuration + (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + structuralDuration);

        behaviouralDerivation = new BehaviouralInterfaceDerivation(service3);
        //behaviouralDerivation.mapToCRUDoperations(service);
        startTime = System.nanoTime();
        behaviouralDerivation.generateProtocols(service3);
        endTime = System.nanoTime();
        behaviouralDuration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds
        System.out.print("Time consumed in generating abstract protocols for serivce: " + behaviouralDuration + " seconds");
        service3.outPutStatistics(structuralDuration, behaviouralDuration, true);

        Service service4 = new Service("TestData/ES/Fedex/ReturnTagService_v1.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service4);
        endTime = System.nanoTime();
        structuralDuration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + structuralDuration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service4);
        endTime = System.nanoTime();
        structuralDuration = structuralDuration + (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + structuralDuration);

        behaviouralDerivation = new BehaviouralInterfaceDerivation(service4);
        //behaviouralDerivation.mapToCRUDoperations(service);
        startTime = System.nanoTime();
        behaviouralDerivation.generateProtocols(service4);
        endTime = System.nanoTime();
        behaviouralDuration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds
        System.out.print("Time consumed in generating abstract protocols for serivce: " + behaviouralDuration + " seconds");
        service4.outPutStatistics(structuralDuration, behaviouralDuration, true);

        Service service5 = new Service("TestData/ES/Fedex/CloseService_v3.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service5);
        endTime = System.nanoTime();
        structuralDuration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + structuralDuration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service5);
        endTime = System.nanoTime();
        structuralDuration = structuralDuration + (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + structuralDuration);

        behaviouralDerivation = new BehaviouralInterfaceDerivation(service5);
        //behaviouralDerivation.mapToCRUDoperations(service);
        startTime = System.nanoTime();
        behaviouralDerivation.generateProtocols(service5);
        endTime = System.nanoTime();
        behaviouralDuration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds
        System.out.print("Time consumed in generating abstract protocols for serivce: " + behaviouralDuration + " seconds");
        service5.outPutStatistics(structuralDuration, behaviouralDuration, true);

        Service service6 = new Service("TestData/ES/Fedex/AddressValidationService_v3.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service6);
        endTime = System.nanoTime();
        structuralDuration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + structuralDuration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service6);
        endTime = System.nanoTime();
        structuralDuration = structuralDuration + (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + structuralDuration);

        behaviouralDerivation = new BehaviouralInterfaceDerivation(service6);
        //behaviouralDerivation.mapToCRUDoperations(service);
        startTime = System.nanoTime();
        behaviouralDerivation.generateProtocols(service6);
        endTime = System.nanoTime();
        behaviouralDuration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds
        System.out.print("Time consumed in generating abstract protocols for serivce: " + behaviouralDuration + " seconds");
        service6.outPutStatistics(structuralDuration, behaviouralDuration, true);

        //-------------------------SaaS----------------------------------------------/
        Service service7 = new Service("TestData/SaaS/Amazon/AmazonS3.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service7);
        endTime = System.nanoTime();
        structuralDuration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + structuralDuration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service7);
        endTime = System.nanoTime();
        structuralDuration = structuralDuration + (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + structuralDuration);

        behaviouralDerivation = new BehaviouralInterfaceDerivation(service7);
        //behaviouralDerivation.mapToCRUDoperations(service);
        startTime = System.nanoTime();
        behaviouralDerivation.generateProtocols(service7);
        endTime = System.nanoTime();
        //behaviouralDuration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        behaviouralDuration = (endTime - startTime) / 1000000; //milliseconds
        System.out.print("Time consumed in generating abstract protocols for serivce: " + behaviouralDuration + " seconds");
        service7.outPutStatistics(structuralDuration, behaviouralDuration, true);

        Service service8 = new Service("TestData/SaaS/Amazon/AWSECommerceService.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service8);
        endTime = System.nanoTime();
        structuralDuration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + structuralDuration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service8);
        endTime = System.nanoTime();
        structuralDuration = structuralDuration + (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + structuralDuration);

        behaviouralDerivation = new BehaviouralInterfaceDerivation(service8);
        //behaviouralDerivation.mapToCRUDoperations(service);
        startTime = System.nanoTime();
        behaviouralDerivation.generateProtocols(service8);
        endTime = System.nanoTime();
        behaviouralDuration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds
        System.out.print("Time consumed in generating abstract protocols for serivce: " + behaviouralDuration + " seconds");
        service8.outPutStatistics(structuralDuration, behaviouralDuration, true);

        /*
         Service service9 = new Service("TestData/SaaS/Amazon/ec2.wsdl");
         startTime = System.nanoTime();
         structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service9);
         endTime = System.nanoTime();
         structuralDuration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
         System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + structuralDuration);

         startTime = System.nanoTime();
         structuralInterfaceAnalysis.refineBERelation(service9);
         endTime = System.nanoTime();
         structuralDuration = structuralDuration+(endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
         System.out.print("time consumed in executing refineBERelation: " + duration);
        
         behaviouralDerivation = new BehaviouralInterfaceDerivation(service9);
         //behaviouralDerivation.mapToCRUDoperations(service);
         startTime = System.nanoTime();
         behaviouralDerivation.generateProtocols(service9);
         endTime = System.nanoTime();
         behaviouralDuration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
         System.out.print("Time consumed in generating abstract protocols for serivce: " + behaviouralDuration + " seconds");
         service9.outPutStatistics(structuralDuration, behaviouralDuration);
        
         */
        Service service10 = new Service("TestData/SaaS/Amazon/AWSMechanicalTurkRequester.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service10);
        endTime = System.nanoTime();
        structuralDuration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + structuralDuration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service10);
        endTime = System.nanoTime();
        structuralDuration = structuralDuration + (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + structuralDuration);

        behaviouralDerivation = new BehaviouralInterfaceDerivation(service10);
        //behaviouralDerivation.mapToCRUDoperations(service);
        startTime = System.nanoTime();
        behaviouralDerivation.generateProtocols(service10);
        endTime = System.nanoTime();
        behaviouralDuration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds
        System.out.print("Time consumed in generating abstract protocols for serivce: " + behaviouralDuration + " seconds");
        service10.outPutStatistics(structuralDuration, behaviouralDuration, true);

        //-------------------------IS----------------------------------------------/
        Service service11 = new Service("TestData/InternetServices/findpeoplefree.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service11);
        endTime = System.nanoTime();
        structuralDuration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + structuralDuration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service11);
        endTime = System.nanoTime();
        structuralDuration = structuralDuration + (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + structuralDuration);

        behaviouralDerivation = new BehaviouralInterfaceDerivation(service11);
        //behaviouralDerivation.mapToCRUDoperations(service);
        startTime = System.nanoTime();
        behaviouralDerivation.generateProtocols(service11);
        endTime = System.nanoTime();
        behaviouralDuration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds
        System.out.print("Time consumed in generating abstract protocols for serivce: " + behaviouralDuration + " seconds");
        service11.outPutStatistics(structuralDuration, behaviouralDuration, true);

        Service service12 = new Service("TestData/InternetServices/mailboxvalidator.wsdl");
        startTime = System.nanoTime();
        structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service12);
        endTime = System.nanoTime();
        structuralDuration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + structuralDuration);

        startTime = System.nanoTime();
        structuralInterfaceAnalysis.refineBERelation(service12);
        endTime = System.nanoTime();
        structuralDuration = structuralDuration + (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
        System.out.print("time consumed in executing refineBERelation: " + structuralDuration);

        behaviouralDerivation = new BehaviouralInterfaceDerivation(service12);
        //behaviouralDerivation.mapToCRUDoperations(service);
        startTime = System.nanoTime();
        behaviouralDerivation.generateProtocols(service12);
        endTime = System.nanoTime();
        behaviouralDuration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds
        System.out.print("Time consumed in generating abstract protocols for serivce: " + behaviouralDuration + " seconds");
        service12.outPutStatistics(structuralDuration, behaviouralDuration, true);

        /*
         Service service13 = new Service("TestData/InternetServices/WeatherForecastService.wsdl");
         startTime = System.nanoTime();
         structuralInterfaceAnalysis.identifyBEandRelationForAllOperations(service13);
         endTime = System.nanoTime();
         structuralDuration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
         System.out.print("time consumed in executing identifyBEandRelationForAllOperations: " + structuralDuration);

         startTime = System.nanoTime();
         structuralInterfaceAnalysis.refineBERelation(service13);
         endTime = System.nanoTime();
         structuralDuration = structuralDuration+(endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
         System.out.print("time consumed in executing refineBERelation: " + structuralDuration);
        
         behaviouralDerivation = new BehaviouralInterfaceDerivation(service13);
         //behaviouralDerivation.mapToCRUDoperations(service);
         startTime = System.nanoTime();
         behaviouralDerivation.generateProtocols(service13);
         endTime = System.nanoTime();
         behaviouralDuration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds
         System.out.print("Time consumed in generating abstract protocols for serivce: " + behaviouralDuration + " seconds");
         service13.outPutStatistics(structuralDuration, behaviouralDuration);                
         */
        System.exit(1);

    }

    public BehaviouralInterfaceDerivation(Service service) {
        this.service = service;
        filteredEntityList = new ArrayList<String>();
        filteredEntityList.add("OpenshipOrder");
        filteredEntityList.add("PackageLineItem");
        filteredEntityList.add("SpecialService");
        //filteredEntityList.add("AssociatedTracking");
        filteredEntityList.add("CommoditiesCommodity");
        //filteredEntityList.add("PriorityAlert");
        //filteredEntityList.add("LabelSpecification");
    }
}
