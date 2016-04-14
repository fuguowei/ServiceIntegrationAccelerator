/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import qut.edu.au.Utility;
import qut.edu.au.services.Service;

/**
 *
 * @author fuguo
 */
public class ServiceInterfaceAnalysis {

    private static String filePath = "TestData/ES/Fedex/";
    //private static String filePath = "TestData/SaaS/Amazon/";
    //private static String filePath = "TestData/ES/Paypal/";
    //private static String filePath = "~/WSDL";

    public static void main(String[] args) {
        ArrayList<File> ｕnprocessedFiles = Utility.getUnprocessedFile(filePath, "wsdl");      

        for(File file: ｕnprocessedFiles) {
            String fileName = file.getName();
            long endTime, startTime, duration;
            StructuralInterfaceAnalysis structuralInterfaceAnalysis = new StructuralInterfaceAnalysis();

            Service service = new Service(filePath + "/" + fileName);
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
            service.outPutOperations(fileName);

            //service.visualiseAllOperationsBEModel();
            //service.outputBEModelToXML();
            service.outBEtoJson();
            //service.outputOperationsToXML();            
            //set to "processed"
            //Utility.writeFile("output/processedWSDL.txt", fileName+"\n", true);            
        }
        
        System.exit(1);
    }

}
