/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.introspection;

import java.util.ArrayList;
import qut.edu.au.services.Parameter;
import java.io.File;
import qut.edu.au.services.Operation;
import qut.edu.au.services.Service;

/**
 *
 * @author fuguo
 */
public class AcceptParameterAnalysis {
    public static void main(String arg[]) {
        Service service = new Service("TestData/ES/Fedex/ShipService_v15.wsdl");
        Operation operation = service.getOperation("processShipment");
        ArrayList<ArrayList<Parameter>> knownPaths = new ArrayList<ArrayList<Parameter>>();
        //for ()
        /*
        for(Parameter parameter: operation.getSimpleInputParameterList()) {
            System.out.print(parameter.getName()+"-"+parameter.getSimpleIndex()+",");
        }
        */  
        File dir = new File("ValidCombinations/processShipment");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                String filleName = child.getName();
                // Do something with child            
                ArrayList<Parameter> knownPath = operation.getKnownPath(filleName);
                if (knownPath!=null) {
                    System.out.println(filleName);
                    knownPaths.add(knownPath);
                    
                    //categorise them
                    Parameter parent = null;
                    int previousIndex = 0;                                        
                    
                    for(Parameter parameter: knownPath) {
                        int index = parameter.getSimpleIndex()+1;
                        if (previousIndex!=0 && previousIndex>index) {
                            System.out.print("WRONG");
                        }
                        previousIndex=index;
                        System.out.print(index+",");
                        /*
                        if (parameter.getParentParameter().equals(parent))
                            System.out.print(parameter.getSimpleIndex()+"-"+parameter.getName()+",");
                            //System.out.print(parameter+",");
                            //System.out.print(parameter.getSimpleIndex()+",");            
                        else {
                            parent = parameter.getParentParameter();
                            //System.out.print(parameter.getSimpleIndex()+",");
                            if (parent==null)
                                //System.out.print("Parent: "+parent.getParameterUniqueIDinTree()+": ["+ parameter.getName()+",");
                                System.out.print("Parent: "+parent.getName()+": ["+ parameter.getName()+",");
                            else
                                //System.out.print("] Parent: "+parent.getParameterUniqueIDinTree()+": ["+ parameter.getName()+",");
                                System.out.print("] Parent: "+parent.getName()+": ["+ parameter.getName()+",");
                            //System.out.print("Parent: "+parent+": "+ parameter+",");
                        
                        }
                        */
                        
                    }
                        
                            //System.out.print("Paraent: "+parent.getName() parameter.getSimpleIndex()+"-"+parameter.getName()+",");                    
                    System.out.println();
                    System.out.println();
                }
            }
        } else {
            // Handle the case where dir is not really a directory.
            // Checking dir.isDirectory() above would not be sufficient
            // to avoid race conditions with another process that deletes
            // directories.
        }
        
        /*
        for(ArrayList<Parameter> knownPath: knownPaths) {
            for(Parameter parameter: knownPath)
                System.out.print(parameter.getSimpleIndex()+",");            
            System.out.println();
            System.out.println();
        }    
        */
        System.exit(1);
    }
    //ArrayList<Parameter> templParameters = operation.getKnownPath();

}
