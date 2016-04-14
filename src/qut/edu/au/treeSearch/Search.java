/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.treeSearch;

import com.eviware.soapui.model.iface.Response;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.util.ArrayList;
import java.util.Random;
import qut.edu.au.services.Operation;
import qut.edu.au.services.Parameter;
import qut.edu.au.services.Service;
import static qut.edu.au.Utility.writeFile;
import qut.edu.au.analysis.Combination;

/**
 *
 * @author sih
 */
public class Search {

    private final static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Search.class);

    public static ArrayList<Parameter> fixedDepthPath(ArrayList<Parameter> path, int currentLevel, int depth, PriorityList priorityList, Operation operation) {
        ArrayList<Parameter> compulsoryInputParameters = operation.getCompulsoryInputParameterList();

        if (currentLevel == depth) {
            return path;
        }
//        String lastElement =null;
//        if (path != null && !path.isEmpty()) {
//          lastElement = path.get(path.size()-1);
//      }
        
        Random randomGenerator = new Random();
        //randome number, treat the tree as the uniform distribution for now
        //String randomElement = treeString.get(randomGenerator.nextInt(treeString.size()+path.size()));
        int randomNumber = randomGenerator.nextInt(operation.getNumberOfInputParameters());

        Parameter chosenParameter = operation.getSimpleInputParameterList().get(randomNumber);
        while (compulsoryInputParameters.contains(chosenParameter) || path.contains(chosenParameter)) {
            randomNumber = randomGenerator.nextInt(operation.getNumberOfInputParameters());
            chosenParameter = operation.getSimpleInputParameterList().get(randomNumber);
        }
        //add all associated complex parameters
        Parameter parentParameter = chosenParameter.getParentParameter();
        while (parentParameter.getParameterUniqueIDinTree() != 0 && parentParameter != null && !path.contains(parentParameter)) {
            path.add(parentParameter);
            parentParameter = parentParameter.getParentParameter();
        }
        path.add(chosenParameter);
        return path;
        //return fixedDepthPath(path, currentLevel+1, depth, priorityList, operation);
    }

    public PriorityList initialiseList(int size) {
        //Random randomGenerator = new Random();
        //randome number between 1 and treeString.size(), treat the tree as the uniform distribution for now        
        //String randomElement = treeString.get(randomGenerator.nextInt(treeString.size()+1));
        //Node node = new Node();
        //node.uniformDistance(1, size);        
        PriorityList list = new PriorityList(1, size);
        return list;
    }
    
    public PriorityList upperConfidenceTreeSearch(int n, int max, Service service, Operation operation) {
        PriorityList list = initialiseList(operation.getNumberOfInputParameters());
        int count = 0;
        int depth = 1;
        ArrayList<Parameter> path = operation.getCompulsoryInputParameterList();
        //String responseMessage = service.invokeServiceOperation(operation.getName(), path);
        String responseMessage =null;
        while (count * depth < n) {
            if (count > max) {
                depth = depth + 1;
                count = 0;
            }
            if (path.size() >= operation.getNumberOfInputParameters()+operation.getComplexInputParameterList().size()) {
                System.out.println("break finally!!!!!");
                operation.saveTheValidCombination();
                break;
            }
            path = fixedDepthPath(path, 0, depth, list, operation);
            //path = operation.getCompulsoryInputParameterList();
            
            /*
             try {
             Thread.sleep(30000);
             } catch (InterruptedException ex) {
             log.log(Priority.FATAL, Search.class.getName(), ex);
             }
             */
            ArrayList<String> requestResponse = service.invokeServiceOperation(operation.getName(), path);
            if (requestResponse != null && requestResponse.size()==2) {
                responseMessage= requestResponse.get(1);
            }
            //if (!responseMessage.contains("ERROR")) //record the path (combination) as accepted combination
            if (responseMessage != null && responseMessage.contains("successfully")) //record the path (combination) as accepted combination
            //there may be many such combinations
            {
                Combination combination = new Combination(path, requestResponse.get(0), responseMessage);
                operation.setAccecptedParameterSets(combination);
                /*
                ValidCombination validCombination = operation.getAccecptedParameterSets();
                if ( validCombination != null) {                    
                    boolean contained = false;
                    for (ArrayList<Parameter> parameterList: validCombination.getCombinations()) {
                        if (parameterList.containsAll(path)) {
                            contained = true;
                            break;
                        }
                    }
                if (!contained) {
                        validCombination.getCombinations().add(path);
                        operation.setAccecptedParameterSets(validCombination);
                    }
                }
                */  
                
                /*
                XStream xstream = new XStream();
                String xmlContent =null;
                try {
                    xmlContent = readFile("Serialisation/"+operation.getName()+".xml");
                } catch (IOException ex) {
                    Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                Operation tempOperation =null;
                if (xmlContent != null) {
                    tempOperation = (Operation)xstream.fromXML(xmlContent);
                }
                
                if (xmlContent == null || tempOperation == null) {
                    if (operation.getAccecptedParameterSets() == null) {
                        operation.setAccecptedParameterSets(new ArrayList<ArrayList<Parameter>>());
                    }
                    operation.getAccecptedParameterSets().add(path);
                    String xmlString = xstream.toXML(operation);
                    writeFile("Serialisation/"+operation.getName()+".xml", xmlString, true);
                }
                */                
                
                /*
                 ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), "ServiceAnalysisDB");
                 try {
                 ObjectSet result = db.queryByExample(operation);
                 if (result.size() == 0) {
                 if (operation.getAccecptedParameterSets() == null)
                 operation.setAccecptedParameterSets(new ArrayList<ArrayList<Parameter>>());
                 operation.getAccecptedParameterSets().add(path);
                 db.store(operation);
                 } else {
                 Operation found = (Operation) result.next();
                 if (!found.getAccecptedParameterSets().containsAll(path)) {
                 found.getAccecptedParameterSets().add(path);
                 }
                 db.store(found);
                 }
                 } finally {
                 db.close();
                 }
                 */
                list = updateList(responseMessage, path, list, max);
            }
        }
        return list;
    }

    public PriorityList updateList(String message, ArrayList<Parameter> path, PriorityList list, int rate) {
        if (message.contains("successful")) {
            if (partofTree(path, list)) {
                //update the tree here
            }
            update_probability(path, list, rate);
        }
        return list;
    }

    public boolean partofTree(ArrayList<Parameter> path, PriorityList tree) {

        return true;
    }

    public void update_probability(ArrayList<Parameter> path, PriorityList tree, int rate) {

    }

    public static void main(String arg[]) {

        /*
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
         SoapUI.getSettings().setString(WsdlSettings.XML_GENERATION_TYPE_EXAMPLE_VALUE, "true");
         // import amazon wsdl
         WsdlInterface iface = null;
         String serviceName = null;
         try {
         iface = WsdlInterfaceFactory.importWsdl(project, "TestData/ES/Fedex/TrackService_v9.wsdl", true)[0];
         serviceName = iface.getName().replace("ServiceSoapBinding", "");
         //iface = WsdlInterfaceFactory.importWsdl(project, "SAP/PurchaseOrder.wsdl", true)[0];
         //iface = WsdlInterfaceFactory.importWsdl(project, "Track/TrackService_v8.wsdl", true)[0];
         } catch (SoapUIException ex) {
         Logger.getLogger(TreeExampleInputParameters.class.getName()).log(Level.SEVERE, null, ex);
         }
         // get "Help" operation
         WsdlOperation operation = (WsdlOperation) iface.getOperationByName("track");        
         //WsdlOperation operation = (WsdlOperation) iface.getOperationByName("PurchaseOrderRequest_Out");        
         //WsdlOperation operation = (WsdlOperation) iface.getOperationByName("track");
         ParameterAnalysis param = new ParameterAnalysis();
         WsdlContext wsdlContext = iface.getWsdlContext();
         //String bindingOperationName = operation.getConfig().getBindingOperationName();
         BindingOperation bindingOperation = null;
         try {
         bindingOperation = operation.findBindingOperation(wsdlContext.getDefinition());
         } catch (Exception ex) {
         Logger.getLogger(TreeExampleInputParameters.class.getName()).log(Level.SEVERE, null, ex);
         }
         if (bindingOperation == null) {
         //log.error("no bindingOperation generated");
         }
         ArrayList<DefaultMutableTreeNode> root = null;
         HashMap hashMap = null;
         String xmlContent = null;
         param.setIface(iface);
         param.setWsdlContext(wsdlContext);
         //First time, call it to get the parameters only        
         try {
         hashMap = param.buildSoapMessageFromInput(bindingOperation, true, false, null,null,serviceName);
         } catch (Exception ex) {
         Logger.getLogger(TreeExampleInputParameters.class.getName()).log(Level.SEVERE, null, ex);
         }
         Iterator<ArrayList<DefaultMutableTreeNode>> keySetIterator = hashMap.keySet().iterator();
         while (keySetIterator.hasNext()) {
         root = keySetIterator.next();
         xmlContent = (String) hashMap.get(root);
         }
         System.out.println("Global_total_number_of_parameters: " + AnalysisStats.Global_total_number_of_parameters);
         System.out.println("Global_total_number_of_comlex_parameters: " + AnalysisStats.Global_total_number_of_complex_parameters);
         */
        //Service service = new Service("TestData/ES/Fedex/OpenShipService_v7.wsdl");
        Service service = new Service("TestData/ES/Fedex/TrackService_v9.wsdl");

        Search search = new Search();
        Operation operation = service.getOperation("track");

        //Operation operation = service.getOperation("createOpenShipment");        
        PriorityList list = search.upperConfidenceTreeSearch(1, 10, service, operation);

        //listofPreliminary is zero here.
        //if (AnalysisStats.listofPreliminary.size()>0) {
        //System.out.println("fixed_depth_path:");
        //ArrayList<String> path = new ArrayList<String>();            
        //path = fixedDepthPath(path,0,10,null,AnalysisStats.listofPreliminary);
//            PriorityList list = search.upperConfidenceTreeSearch(1, 10, service, operation); 
        //for (String s : path)
        // System.out.println(s);
//        }
        System.exit(1);
    }
}
