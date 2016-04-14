/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.analysis;

import java.util.ArrayList;
import java.util.Random;
import qut.edu.au.Utility;
import qut.edu.au.introspection.TreeNode;
import qut.edu.au.services.Operation;
import qut.edu.au.services.Parameter;
import qut.edu.au.services.Service;

/**
 *
 * @author fuguo
 */
public class LoadKnownPaths {

    private Operation operation;
    private Service service;

    public LoadKnownPaths(Operation op, Service sv) {
        operation = op;
        service = sv;
    }

    public static void main(String arg[]) {
        //Service service = new Service("TestData/ES/Fedex/TrackService_v9.wsdl");
        //Operation operation = service.getOperation("track");

        Service service = new Service("TestData/ES/Fedex/ShipService_v15.wsdl");
        Operation operation = service.getOperation("processShipment");
        LoadKnownPaths load = new LoadKnownPaths(operation, service);
        load.discoverSuccessfulPathRealService();
        System.exit(1);
    }

    private void discoverSuccessfulPathRealService() {
        ArrayList<Parameter> templParameters = null;
        ArrayList<Parameter> allParameters = new ArrayList<Parameter>();
        ArrayList<ArrayList<Parameter>> allsets = new ArrayList<ArrayList<Parameter>>();
        ArrayList<Parameter> minimumParameters = new ArrayList<Parameter>();
        ArrayList<Parameter> inputParameters = operation.getSimpleInputParameterList();

        /*
         ArrayList<Parameter> inputParameters = operation.getSimpleInputParameterList();
         for (Parameter parameter : inputParameters) {
         if (parameter.isCompulsory()) {
         minimumParameters.add(parameter);
         }
         if (templParameters.contains(parameter)) {
         knownSuccessPath.add(parameter);
         }
         }
        
        
         System.out.println("the nubmer of inputParameter:"+ inputParameters.size());
         System.out.println("the nubmer of compulsory ones:"+ minimumParameters.size());
         for (Parameter knonOne : knownSuccessPath) {
         System.out.print(knonOne.getName()+",");
         }
         System.out.println();
        
         knownSuccessPath = new ArrayList<Parameter>();
        
         */
        System.out.println();
        System.out.println("GroundHomeDeliveryShipment");

        templParameters = operation.getKnownPath("GroundHomeDeliveryShipment.xml");
        ArrayList<Parameter> groundHomeDeliveryShipmentKnownSuccessPath = new ArrayList<Parameter>();

        for (Parameter parameter : inputParameters) {
            if (parameter.isCompulsory()) {
                minimumParameters.add(parameter);
            }
            if (templParameters.contains(parameter)) {
                Parameter firstLevel = parameter.getParentParameter();
                Parameter secondLevel = firstLevel.getParentParameter();
                Parameter thirdLevel = secondLevel.getParentParameter();

                if (firstLevel.getName().equals("RequestedShipment")) {
                    if (!groundHomeDeliveryShipmentKnownSuccessPath.contains(parameter)) {
                        groundHomeDeliveryShipmentKnownSuccessPath.add(parameter);
                    }
                } else if (secondLevel.getName().equals("RequestedShipment")) {
                    if (!groundHomeDeliveryShipmentKnownSuccessPath.contains(firstLevel)) {
                        groundHomeDeliveryShipmentKnownSuccessPath.add(firstLevel);
                    }
                } else if (thirdLevel.getName().equals("RequestedShipment")) {
                    if (!groundHomeDeliveryShipmentKnownSuccessPath.contains(firstLevel)) {
                        groundHomeDeliveryShipmentKnownSuccessPath.add(firstLevel);
                    }
                }
            }
        }

        allsets.add(groundHomeDeliveryShipmentKnownSuccessPath);

        for (Parameter knonOne : groundHomeDeliveryShipmentKnownSuccessPath) {
            //System.out.print(knonOne.getName()+",");
            allParameters.add(knonOne);
        }

        System.out.println();
        System.out.println("the total number now is: " + allParameters.size());

        System.out.println("different ones between GroundHomeDelivery and the whole set");

        System.out.println();
        ArrayList<Parameter> groundHomeDeliveryShipmentAppointmentKnownSuccessPath = new ArrayList<Parameter>();
        System.out.println("GroundHomeDelivery ShipmentAppointment");
        templParameters = operation.getKnownPath("GroundHomeDeliveryShipmentAppointment.xml");

        for (Parameter parameter : inputParameters) {
            if (parameter.isCompulsory()) {
                minimumParameters.add(parameter);
            }
            if (templParameters.contains(parameter)) {
                Parameter firstLevel = parameter.getParentParameter();
                Parameter secondLevel = firstLevel.getParentParameter();
                Parameter thirdLevel = secondLevel.getParentParameter();
                if (firstLevel.getName().equals("RequestedShipment")) {
                    if (!groundHomeDeliveryShipmentAppointmentKnownSuccessPath.contains(parameter)) {
                        groundHomeDeliveryShipmentAppointmentKnownSuccessPath.add(parameter);
                    }
                } else if (secondLevel.getName().equals("RequestedShipment")) {
                    if (!groundHomeDeliveryShipmentAppointmentKnownSuccessPath.contains(firstLevel)) {
                        groundHomeDeliveryShipmentAppointmentKnownSuccessPath.add(firstLevel);
                    }
                } else if (thirdLevel.getName().equals("RequestedShipment")) {
                    if (!groundHomeDeliveryShipmentAppointmentKnownSuccessPath.contains(firstLevel)) {
                        groundHomeDeliveryShipmentAppointmentKnownSuccessPath.add(firstLevel);
                    }
                }

                //groundHomeDeliveryShipmentAppointmentKnownSuccessPath.add(parameter);
            }
        }

        allsets.add(groundHomeDeliveryShipmentAppointmentKnownSuccessPath);
        System.out.println("different ones between GroundHomeDelivery and GroundHomeDelivery ShipmentAppointment");

        for (Parameter knonOne : groundHomeDeliveryShipmentAppointmentKnownSuccessPath) {
            //System.out.print(knonOne.getName()+",");            
            if (!allParameters.contains(knonOne)) {
                allParameters.add(knonOne);
            }
        }

        ArrayList<Parameter> common = new ArrayList<Parameter>(groundHomeDeliveryShipmentKnownSuccessPath);
        common.retainAll(groundHomeDeliveryShipmentAppointmentKnownSuccessPath);

        /*
         System.out.println("common ones");
        
         for (Parameter knonOne : common) {
         System.out.print(knonOne.getName()+",");
         }
         */
        System.out.println();
        System.out.println("the total number now is: " + allParameters.size());

        ArrayList<Parameter> groundInternationalKnownSuccessPath = new ArrayList<Parameter>();
        System.out.println();
        System.out.println("GroundInternational");
        templParameters = operation.getKnownPath("GroundInternational.xml");

        for (Parameter parameter : inputParameters) {
            if (parameter.isCompulsory()) {
                minimumParameters.add(parameter);
            }
            if (templParameters.contains(parameter)) {
                Parameter firstLevel = parameter.getParentParameter();
                Parameter secondLevel = firstLevel.getParentParameter();
                Parameter thirdLevel = secondLevel.getParentParameter();

                if (firstLevel.getName().equals("RequestedShipment")) {
                    if (!groundInternationalKnownSuccessPath.contains(parameter)) {
                        groundInternationalKnownSuccessPath.add(parameter);
                    }
                } else if (secondLevel.getName().equals("RequestedShipment")) {
                    if (!groundInternationalKnownSuccessPath.contains(firstLevel)) {
                        groundInternationalKnownSuccessPath.add(firstLevel);
                    }
                } else if (thirdLevel.getName().equals("RequestedShipment")) {
                    if (!groundInternationalKnownSuccessPath.contains(firstLevel)) {
                        groundInternationalKnownSuccessPath.add(firstLevel);
                    }
                }

                //groundInternationalKnownSuccessPath.add(parameter);
            }
        }

        allsets.add(groundInternationalKnownSuccessPath);

        for (Parameter knonOne : groundInternationalKnownSuccessPath) {
            //System.out.print(knonOne.getName()+",");
            if (!allParameters.contains(knonOne)) {
                allParameters.add(knonOne);
            }
        }

        common.retainAll(groundInternationalKnownSuccessPath);

        System.out.println();
        System.out.println("the total number now is: " + allParameters.size());

        ArrayList<Parameter> GroundInternationalMPSknownSuccessPath = new ArrayList<Parameter>();
        System.out.println();
        System.out.println("GroundInternational MPS");
        templParameters = operation.getKnownPath("GroundInternational MPS.xml");

        for (Parameter parameter : inputParameters) {
            if (parameter.isCompulsory()) {
                minimumParameters.add(parameter);
            }
            if (templParameters.contains(parameter)) {
                Parameter firstLevel = parameter.getParentParameter();
                Parameter secondLevel = firstLevel.getParentParameter();
                Parameter thirdLevel = secondLevel.getParentParameter();
                if (firstLevel.getName().equals("RequestedShipment")) {
                    if (!GroundInternationalMPSknownSuccessPath.contains(parameter)) {
                        GroundInternationalMPSknownSuccessPath.add(parameter);
                    }
                } else if (secondLevel.getName().equals("RequestedShipment")) {
                    if (!GroundInternationalMPSknownSuccessPath.contains(firstLevel)) {
                        GroundInternationalMPSknownSuccessPath.add(firstLevel);
                    }
                } else if (thirdLevel.getName().equals("RequestedShipment")) {
                    if (!GroundInternationalMPSknownSuccessPath.contains(firstLevel)) {
                        GroundInternationalMPSknownSuccessPath.add(firstLevel);
                    }
                }

                //GroundInternationalMPSknownSuccessPath.add(parameter);
            }
        }

        allsets.add(GroundInternationalMPSknownSuccessPath);
        common.retainAll(GroundInternationalMPSknownSuccessPath);

        System.out.println("different ones between GroundInternational and GroundInternational MPS");
        for (Parameter knonOne : GroundInternationalMPSknownSuccessPath) {
            //System.out.print(knonOne.getName()+",");
            if (!allParameters.contains(knonOne)) {
                allParameters.add(knonOne);
            }
            if (!groundInternationalKnownSuccessPath.contains(knonOne)) {
                System.out.print(knonOne.getName() + ",");
            }
        }
        System.out.println();
        System.out.println("the total number now is: " + allParameters.size());

        ArrayList<Parameter> IntlReturnShipmentknownSuccessPath = new ArrayList<Parameter>();
        System.out.println();
        System.out.println("IntlReturnShipment");
        templParameters = operation.getKnownPath("IntlReturnShipment.xml");

        for (Parameter parameter : inputParameters) {
            if (parameter.isCompulsory()) {
                minimumParameters.add(parameter);
            }
            if (templParameters.contains(parameter)) {
                Parameter firstLevel = parameter.getParentParameter();
                Parameter secondLevel = firstLevel.getParentParameter();
                Parameter thirdLevel = secondLevel.getParentParameter();
                if (firstLevel.getName().equals("RequestedShipment")) {
                    if (!IntlReturnShipmentknownSuccessPath.contains(parameter)) {
                        IntlReturnShipmentknownSuccessPath.add(parameter);
                    }
                } else if (secondLevel.getName().equals("RequestedShipment")) {
                    if (!IntlReturnShipmentknownSuccessPath.contains(firstLevel)) {
                        IntlReturnShipmentknownSuccessPath.add(firstLevel);
                    }
                } else if (thirdLevel.getName().equals("RequestedShipment")) {
                    if (!IntlReturnShipmentknownSuccessPath.contains(firstLevel)) {
                        IntlReturnShipmentknownSuccessPath.add(firstLevel);
                    }
                }

                //IntlReturnShipmentknownSuccessPath.add(parameter);
            }
        }

        allsets.add(IntlReturnShipmentknownSuccessPath);

        common.retainAll(IntlReturnShipmentknownSuccessPath);

        System.out.println("different ones between GroundInternational and IntlReturnShipment");

        for (Parameter knonOne : IntlReturnShipmentknownSuccessPath) {
            //System.out.print(knonOne.getName()+",");
            if (!allParameters.contains(knonOne)) {
                allParameters.add(knonOne);
            }

            if (!groundInternationalKnownSuccessPath.contains(knonOne)) {
                System.out.print(knonOne.getName() + ",");
            }
        }
        System.out.println();
        System.out.println("the total number now is: " + allParameters.size());

        ArrayList<Parameter> IntraIndiaCODknownSuccessPath = new ArrayList<Parameter>();
        System.out.println();
        System.out.println("IntraIndiaCOD");
        templParameters = operation.getKnownPath("IntraIndiaCOD.xml");

        for (Parameter parameter : inputParameters) {
            if (parameter.isCompulsory()) {
                minimumParameters.add(parameter);
            }
            if (templParameters.contains(parameter)) {
                Parameter firstLevel = parameter.getParentParameter();
                Parameter secondLevel = firstLevel.getParentParameter();
                Parameter thirdLevel = secondLevel.getParentParameter();
                if (firstLevel.getName().equals("RequestedShipment")) {
                    if (!IntraIndiaCODknownSuccessPath.contains(parameter)) {
                        IntraIndiaCODknownSuccessPath.add(parameter);
                    }
                } else if (secondLevel.getName().equals("RequestedShipment")) {
                    if (!IntraIndiaCODknownSuccessPath.contains(firstLevel)) {
                        IntraIndiaCODknownSuccessPath.add(firstLevel);
                    }
                } else if (thirdLevel.getName().equals("RequestedShipment")) {
                    if (!IntraIndiaCODknownSuccessPath.contains(firstLevel)) {
                        IntraIndiaCODknownSuccessPath.add(firstLevel);
                    }
                }
                //IntraIndiaCODknownSuccessPath.add(parameter);
            }
        }

        allsets.add(IntraIndiaCODknownSuccessPath);

        common.retainAll(IntraIndiaCODknownSuccessPath);

        for (Parameter knonOne : IntraIndiaCODknownSuccessPath) {
            //System.out.print(knonOne.getName()+",");
            if (!allParameters.contains(knonOne)) {
                allParameters.add(knonOne);
            }
        }
        System.out.println();
        System.out.println("the total number now is: " + allParameters.size());

        ArrayList<Parameter> IntraMXExpSaverShipment = new ArrayList<Parameter>();
        System.out.println();
        System.out.println("IntraMXExpSaverShipment");
        templParameters = operation.getKnownPath("IntraMXExpSaverShipment.xml");

        for (Parameter parameter : inputParameters) {
            if (parameter.isCompulsory()) {
                minimumParameters.add(parameter);
            }
            if (templParameters.contains(parameter)) {
                Parameter firstLevel = parameter.getParentParameter();
                Parameter secondLevel = firstLevel.getParentParameter();
                Parameter thirdLevel = secondLevel.getParentParameter();
                if (firstLevel.getName().equals("RequestedShipment")) {
                    if (!IntraMXExpSaverShipment.contains(parameter)) {
                        IntraMXExpSaverShipment.add(parameter);
                    }
                } else if (secondLevel.getName().equals("RequestedShipment")) {
                    if (!IntraMXExpSaverShipment.contains(firstLevel)) {
                        IntraMXExpSaverShipment.add(firstLevel);
                    }
                } else if (thirdLevel.getName().equals("RequestedShipment")) {
                    if (!IntraMXExpSaverShipment.contains(firstLevel)) {
                        IntraMXExpSaverShipment.add(firstLevel);
                    }
                }

                //IntraMXExpSaverShipment.add(parameter);
            }
        }

        allsets.add(IntraMXExpSaverShipment);
        common.retainAll(IntraMXExpSaverShipment);

        for (Parameter knonOne : IntraMXExpSaverShipment) {
            //System.out.print(knonOne.getName()+",");
            if (!allParameters.contains(knonOne)) {
                allParameters.add(knonOne);
            }
        }
        System.out.println();
        System.out.println("the total number now is: " + allParameters.size());

        ArrayList<Parameter> IntraUAESOShipmentknownSuccessPath = new ArrayList<Parameter>();
        System.out.println();
        System.out.println("IntraUAESOShipment");
        templParameters = operation.getKnownPath("IntraUAESOShipment.xml");

        for (Parameter parameter : inputParameters) {
            if (parameter.isCompulsory()) {
                minimumParameters.add(parameter);
            }
            if (templParameters.contains(parameter)) {
                Parameter firstLevel = parameter.getParentParameter();
                Parameter secondLevel = firstLevel.getParentParameter();
                Parameter thirdLevel = secondLevel.getParentParameter();
                if (firstLevel.getName().equals("RequestedShipment")) {
                    if (!IntraUAESOShipmentknownSuccessPath.contains(parameter)) {
                        IntraUAESOShipmentknownSuccessPath.add(parameter);
                    }
                } else if (secondLevel.getName().equals("RequestedShipment")) {
                    if (!IntraUAESOShipmentknownSuccessPath.contains(firstLevel)) {
                        IntraUAESOShipmentknownSuccessPath.add(firstLevel);
                    }
                } else if (thirdLevel.getName().equals("RequestedShipment")) {
                    if (!IntraUAESOShipmentknownSuccessPath.contains(firstLevel)) {
                        IntraUAESOShipmentknownSuccessPath.add(firstLevel);
                    }
                }

                //IntraUAESOShipmentknownSuccessPath.add(parameter);
            }
        }

        allsets.add(IntraUAESOShipmentknownSuccessPath);

        common.retainAll(IntraUAESOShipmentknownSuccessPath);

        for (Parameter knonOne : IntraUAESOShipmentknownSuccessPath) {
            //System.out.print(knonOne.getName()+",");
            if (!allParameters.contains(knonOne)) {
                allParameters.add(knonOne);
            }
        }
        System.out.println();
        System.out.println("the total number now is: " + allParameters.size());

        ArrayList<Parameter> PriorityAlertLTLFreightShipmentknownSuccessPath = new ArrayList<Parameter>();
        System.out.println();
        System.out.println("PriorityAlertLTLFreightShipment");
        templParameters = operation.getKnownPath("PriorityAlertLTLFreightShipment.xml");
        //PriorityAlertLTLFreightShipment

        for (Parameter parameter : inputParameters) {
            if (parameter.isCompulsory()) {
                minimumParameters.add(parameter);
            }
            if (templParameters.contains(parameter)) {
                Parameter firstLevel = parameter.getParentParameter();
                Parameter secondLevel = firstLevel.getParentParameter();
                Parameter thirdLevel = secondLevel.getParentParameter();
                if (firstLevel.getName().equals("RequestedShipment")) {
                    if (!PriorityAlertLTLFreightShipmentknownSuccessPath.contains(parameter)) {
                        PriorityAlertLTLFreightShipmentknownSuccessPath.add(parameter);
                    }
                } else if (secondLevel.getName().equals("RequestedShipment")) {
                    if (!PriorityAlertLTLFreightShipmentknownSuccessPath.contains(firstLevel)) {
                        PriorityAlertLTLFreightShipmentknownSuccessPath.add(firstLevel);
                    }
                } else if (thirdLevel.getName().equals("RequestedShipment")) {
                    if (!PriorityAlertLTLFreightShipmentknownSuccessPath.contains(firstLevel)) {
                        PriorityAlertLTLFreightShipmentknownSuccessPath.add(firstLevel);
                    }
                }

                //PriorityAlertLTLFreightShipmentknownSuccessPath.add(parameter);
            }
        }

        allsets.add(PriorityAlertLTLFreightShipmentknownSuccessPath);

        common.retainAll(PriorityAlertLTLFreightShipmentknownSuccessPath);

        for (Parameter knonOne : PriorityAlertLTLFreightShipmentknownSuccessPath) {
            //System.out.print(knonOne.getName()+",");
            if (!allParameters.contains(knonOne)) {
                allParameters.add(knonOne);
            }
        }
        System.out.println();
        System.out.println("the total number now is: " + allParameters.size());

        ArrayList<Parameter> SmartPostShipmentknownSuccessPath = new ArrayList<Parameter>();
        System.out.println();
        System.out.println("SmartPostShipment");
        templParameters = operation.getKnownPath("SmartPostShipment.xml");

        for (Parameter parameter : inputParameters) {
            if (parameter.isCompulsory()) {
                minimumParameters.add(parameter);
            }
            if (templParameters.contains(parameter)) {
                Parameter firstLevel = parameter.getParentParameter();
                Parameter secondLevel = firstLevel.getParentParameter();
                Parameter thirdLevel = secondLevel.getParentParameter();
                if (firstLevel.getName().equals("RequestedShipment")) {
                    if (!SmartPostShipmentknownSuccessPath.contains(parameter)) {
                        SmartPostShipmentknownSuccessPath.add(parameter);
                    }
                } else if (secondLevel.getName().equals("RequestedShipment")) {
                    if (!SmartPostShipmentknownSuccessPath.contains(firstLevel)) {
                        SmartPostShipmentknownSuccessPath.add(firstLevel);
                    }
                } else if (thirdLevel.getName().equals("RequestedShipment")) {
                    if (!SmartPostShipmentknownSuccessPath.contains(firstLevel)) {
                        SmartPostShipmentknownSuccessPath.add(firstLevel);
                    }
                }

                //SmartPostShipmentknownSuccessPath.add(parameter);
            }
        }

        allsets.add(SmartPostShipmentknownSuccessPath);

        common.retainAll(SmartPostShipmentknownSuccessPath);

        for (Parameter knonOne : SmartPostShipmentknownSuccessPath) {
            //System.out.print(knonOne.getName()+",");
            if (!allParameters.contains(knonOne)) {
                allParameters.add(knonOne);
            }
        }
        System.out.println();
        System.out.println("the total number now is: " + allParameters.size());

        ArrayList<Parameter> SmartPostShipmentPrintReturnknownSuccessPath = new ArrayList<Parameter>();
        System.out.println();
        System.out.println("SmartPostShipment - PrintReturn");
        templParameters = operation.getKnownPath("SmartPostShipment - PrintReturn.xml");

        for (Parameter parameter : inputParameters) {
            if (parameter.isCompulsory()) {
                minimumParameters.add(parameter);
            }
            if (templParameters.contains(parameter)) {
                Parameter firstLevel = parameter.getParentParameter();
                Parameter secondLevel = firstLevel.getParentParameter();
                Parameter thirdLevel = secondLevel.getParentParameter();
                if (firstLevel.getName().equals("RequestedShipment")) {
                    if (!SmartPostShipmentPrintReturnknownSuccessPath.contains(parameter)) {
                        SmartPostShipmentPrintReturnknownSuccessPath.add(parameter);
                    }
                } else if (secondLevel.getName().equals("RequestedShipment")) {
                    if (!SmartPostShipmentPrintReturnknownSuccessPath.contains(firstLevel)) {
                        SmartPostShipmentPrintReturnknownSuccessPath.add(firstLevel);
                    }
                } else if (thirdLevel.getName().equals("RequestedShipment")) {
                    if (!SmartPostShipmentPrintReturnknownSuccessPath.contains(firstLevel)) {
                        SmartPostShipmentPrintReturnknownSuccessPath.add(firstLevel);
                    }
                }

                //SmartPostShipmentPrintReturnknownSuccessPath.add(parameter);
            }
        }
        allsets.add(SmartPostShipmentPrintReturnknownSuccessPath);
        common.retainAll(SmartPostShipmentPrintReturnknownSuccessPath);

        for (Parameter knonOne : SmartPostShipmentPrintReturnknownSuccessPath) {
            //System.out.print(knonOne.getName()+",");
            if (!allParameters.contains(knonOne)) {
                allParameters.add(knonOne);
            }
        }
        System.out.println();
        System.out.println("the total number now is: " + allParameters.size());

        ArrayList<Parameter> USExpressFreightknownSuccessPath = new ArrayList<Parameter>();
        System.out.println();
        System.out.println("USExpressFreight");
        templParameters = operation.getKnownPath("USExpressFreight.xml");

        for (Parameter parameter : inputParameters) {
            if (parameter.isCompulsory()) {
                minimumParameters.add(parameter);
            }
            if (templParameters.contains(parameter)) {

                Parameter firstLevel = parameter.getParentParameter();
                Parameter secondLevel = firstLevel.getParentParameter();
                Parameter thirdLevel = secondLevel.getParentParameter();
                if (firstLevel.getName().equals("RequestedShipment")) {
                    if (!USExpressFreightknownSuccessPath.contains(parameter)) {
                        USExpressFreightknownSuccessPath.add(parameter);
                    }
                } else if (secondLevel.getName().equals("RequestedShipment")) {
                    if (!USExpressFreightknownSuccessPath.contains(firstLevel)) {
                        USExpressFreightknownSuccessPath.add(firstLevel);
                    }
                } else if (thirdLevel.getName().equals("RequestedShipment")) {
                    if (!USExpressFreightknownSuccessPath.contains(firstLevel)) {
                        USExpressFreightknownSuccessPath.add(firstLevel);
                    }
                }

                //USExpressFreightknownSuccessPath.add(parameter);
            }
        }

        allsets.add(USExpressFreightknownSuccessPath);

        common.retainAll(USExpressFreightknownSuccessPath);

        for (Parameter knonOne : USExpressFreightknownSuccessPath) {
            //System.out.print(knonOne.getName()+",");
            if (!allParameters.contains(knonOne)) {
                allParameters.add(knonOne);
            }
        }
        System.out.println();
        System.out.println("the total number now is: " + allParameters.size());

        System.out.println("all parameters: ");
        for (Parameter knonOne : allParameters) {
            System.out.print(knonOne.getName() + ",");
        }

        System.out.println();
        System.out.println("Common ones across all subtypes: ");
        String content = "<?xml version=\"1.0\"?>\n"
                + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n"
                + "targetNamespace=\"http://www.w3schools.com\"\n"
                + "xmlns=\"http://www.w3schools.com\"\n"
                + "elementFormDefault=\"qualified\">" + "\n\n";
        content = content + "<xs:element name=\"Shipment\">\n"
                + "  <xs:complexType>\n"
                + "    <xs:sequence>\n";
        int counter = 0;
        for (Parameter knonOne : common) {
            content = content + "      <xs:element name=\"" + knonOne.getName()
                    + "\" type=\"xs:string\"/>\n";
            //content = content +"      <xs:element name=\""+ knonOne.getName()+"\" type=\"xs:string\"/>\n";
            System.out.print(knonOne.getName() + "," + knonOne.getSimpleIndex() + ", parent: "
                    + knonOne.getParentParameter().getName() + ", parent: " + knonOne.getParentParameter().getParentParameter().getName() + "\n");
            counter++;
        }

        content = content + "    </xs:sequence>\n"
                + "  </xs:complexType>\n"
                + "</xs:element>\n\n";

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~total common" + counter);

        ArrayList<String> names = new ArrayList<String>();
        names.add("groundHomeDeliveryShipment");
        names.add("groundHomeDeliveryShipmentAppointment");
        names.add("groundInternational");
        names.add("GroundInternationalMPS");
        names.add("IntlReturnShipment");
        names.add("IntraIndiaCOD");
        names.add("IntraMXExpSaver");
        names.add("IntraUAESOShipment");
        names.add("PriorityAlertLTLFreightShipment");
        names.add("SmartPostShipment");
        names.add("SmartPostShipmentPrintReturn");
        names.add("USExpressFreight");

        //set 1: groundHomeDeliveryShipmentKnownSuccessPath
        //set 2: groundHomeDeliveryShipmentAppointmentKnownSuccessPath
        //set 3: groundInternationalKnownSuccessPath
        //set 4: GroundInternationalMPSknownSuccessPath
        //set 5: IntlReturnShipmentknownSuccessPath
        //set 6: IntraIndiaCODknownSuccessPath
        //set 7: IntraMXExpSaverShipment
        //set 8: IntraUAESOShipmentknownSuccessPath
        //set 9: PriorityAlertLTLFreightShipmentknownSuccessPath
        //set 10: SmartPostShipmentknownSuccessPath
        //set 11: SmartPostShipmentPrintReturnknownSuccessPath
        //set 12: USExpressFreightknownSuccessPath
        for (int i = 0; i < allsets.size(); i++) {
            ArrayList<Parameter> set = allsets.get(i);
            System.out.println();

            ArrayList<Parameter> commonAttributes = new ArrayList<Parameter>(set);

            for (int j = i + 1; j < allsets.size(); j++) {
                ArrayList<Parameter> otherSet = allsets.get(j);
                commonAttributes.retainAll(otherSet);
                if (commonAttributes.size() > 0) {
                    System.out.println("------------------------------------------------------------");
                    System.out.println(names.get(i) + " and " + names.get(j) + " have common attributes");
                    System.out.println("------------------------------------------------------------");
                }
                if (otherSet.containsAll(set)) {
                    System.out.println(names.get(i) + " is a subtype of " + names.get(j));
                }
                //else if ((otherSet.containsAll(set)))
                //    System.out.println(names.get(i) + " is a subtype of "+ names.get(j));
            }

            if (i > 0) {
                for (int j = 0; j < i; j++) {
                    ArrayList<Parameter> otherSet = allsets.get(j);
                    commonAttributes.retainAll(otherSet);
                    if (commonAttributes.size() > 0) {
                        System.out.println("------------------------------------------------------------");
                        System.out.println(names.get(i) + " and " + names.get(j) + " have common attributes");
                        System.out.println("------------------------------------------------------------");
                    }

                    if (otherSet.containsAll(set)) {
                        System.out.println(names.get(i) + " is a subtype of " + names.get(j));
                    }
                    //else if ((otherSet.containsAll(set)))
                    //    System.out.println(names.get(i) + " is a subtype of "+ names.get(j));
                }
            }

            System.out.println();
            System.out.println();
            System.out.println("variant: set " + names.get(i));

            content = content + "<xs:element name=\"" + names.get(i) + "\">\n"
                    + "  <xs:complexType>\n"
                    + "    <xs:sequence>\n";

            counter = 0;
            for (Parameter knonOne : set) {
                if (!common.contains(knonOne)) {
                    content = content + "      <xs:element name=\"" + knonOne.getName()
                            + "\" type=\"xs:string\"/>\n";
                    System.out.print(knonOne.getName() + "," + knonOne.getSimpleIndex() + ", parent: "
                            + knonOne.getParentParameter().getName() + ", parent: " + knonOne.getParentParameter().getParentParameter().getName() + "\n");
                    counter++;
                }
            }

            content = content + "    </xs:sequence>\n"
                    + "  </xs:complexType>\n"
                    + "</xs:element>\n\n";

            System.out.println("~~~~~~~~~~~~~~~~~~~~~~total variant" + counter);
        }

        content = content + "</xs:schema>";

        Utility.writeFile("output/common.xsd", content, false);

        /*
         System.out.println();        
         System.out.println();        
         System.out.println("variant: groundHomeDeliveryShipment");
         counter =0;        
         for (Parameter knonOne : groundHomeDeliveryShipmentKnownSuccessPath) {
         if (!common.contains(knonOne)) {
         System.out.print(knonOne.getName()+","+ knonOne.getSimpleIndex()+", parent: "+ 
         knonOne.getParentParameter().getName()+ ", parent: "+knonOne.getParentParameter().getParentParameter().getName()+"\n");
         counter++;
         }
         }
         System.out.println("~~~~~~~~~~~~~~~~~~~~~~total variant"+counter);
        
        
         System.out.println();        
         System.out.println();        
         System.out.println("variant: groundHomeDeliveryShipmentAppointment");
         counter =0;
         for (Parameter knonOne : groundHomeDeliveryShipmentAppointmentKnownSuccessPath) {
         if (!common.contains(knonOne)) {
         System.out.print(knonOne.getName()+","+ knonOne.getSimpleIndex()+", parent: "+ 
         knonOne.getParentParameter().getName()+ ", parent: "+knonOne.getParentParameter().getParentParameter().getName()+"\n");
         counter++;
         }
         }
         System.out.println("~~~~~~~~~~~~~~~~~~~~~~total variant"+counter);
        
         boolean isSubset = groundHomeDeliveryShipmentKnownSuccessPath.containsAll(groundHomeDeliveryShipmentAppointmentKnownSuccessPath);
         if (isSubset) {
         System.out.println("groundHomeDeliveryShipmentAppointmentKnownSuccessPath is a subtype of groundHomeDeliveryShipmentKnownSuccessPath");
         }
         */
        //----------------print all the indcies-------------------------------
        //set 1: groundHomeDeliveryShipmentKnownSuccessPath
        //set 2: groundHomeDeliveryShipmentAppointmentKnownSuccessPath
        //set 3: groundInternationalKnownSuccessPath
        //set 4: GroundInternationalMPSknownSuccessPath
        //set 5: IntlReturnShipmentknownSuccessPath
        //set 6: IntraIndiaCODknownSuccessPath
        //set 7: IntraMXExpSaverShipment
        //set 8: IntraUAESOShipmentknownSuccessPath
        //set 9: PriorityAlertLTLFreightShipmentknownSuccessPath
        //set 10: SmartPostShipmentknownSuccessPath
        //set 11: SmartPostShipmentPrintReturnknownSuccessPath
        //set 12: USExpressFreightknownSuccessPath
        int count = 1;
        for (Parameter param : allParameters) {
            param.setSimpleIndex(count);
            for (int i = 0; i < allsets.size(); i++) {
                ArrayList<Parameter> set = allsets.get(i);
                for (Parameter param2 : set) {
                    if (param2.equals(param)) {
                        param2.setSimpleIndex(count);
                        break;
                    }
                }
            }
            count++;
        }
       
        System.out.println("-------------------all indices--------------------");
        for (Parameter param3 : allParameters) {
            System.out.print(param3.getSimpleIndex() + ",");
        }
        System.out.println();
        for (Parameter param3 : allParameters) {
            System.out.print(param3.getName() + ",");
        }
        System.out.println();        
        System.out.println("-------------------all indices--------------------");
       
 
        System.out.println("-------------------common ones--------------------");
        for (Parameter param3 : common) {
            System.out.print(param3.getSimpleIndex() + ",");
        }
        System.out.println();
        for (Parameter param3 : common) {
            System.out.println("-"+param3.getName());
        }
        System.out.println();
                
        System.out.println("-------------------common ones--------------------");
        
        for (int i = 0; i < allsets.size(); i++) {           
            ArrayList<Parameter> set1 = allsets.get(i);
            System.out.println("-------------------substype "+i+" -------------------name: -"+names.get(i));
            for (Parameter param4 : set1) {
                System.out.print(param4.getSimpleIndex() + ",");
            }
            System.out.println();
            for (Parameter param4 : set1) {
                if (!common.contains(param4))
                    System.out.println("-"+param4.getName());
            }
            System.out.println();
            System.out.println("-------------------substype "+i+" --------------------");
        }
        
    }

}
