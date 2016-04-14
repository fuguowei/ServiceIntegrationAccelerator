/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import qut.edu.au.petrinet.logic.Arc;
import qut.edu.au.petrinet.logic.Petrinet;
import qut.edu.au.petrinet.logic.Place;
import qut.edu.au.petrinet.logic.Transition;
import qut.edu.au.tree.Tree;
import qut.edu.au.visualisation.GraphViz;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import qut.edu.au.analysis.ServiceInterfaceAnalysis;

/**
 *
 * @author sih
 */
public class Utility {

    public static String TreetoXml(TreeModel model) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();

        // Build an XML document from the tree model
        Document doc = impl.createDocument(null, null, null);
        Element root = createTree(doc, model, model.getRoot());
        doc.appendChild(root);

        // Transform the document into a string
        DOMSource domSource = new DOMSource(doc);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter sw = new StringWriter();
        StreamResult sr = new StreamResult(sw);
        transformer.transform(domSource, sr);
        return sw.toString();
    }

    public static Element createTree(Document doc, TreeModel model, Object node) {
        Element el = doc.createElement(node.toString());
        for (int i = 0; i < model.getChildCount(node); i++) {
            Object child = model.getChild(node, i);
            el.appendChild(createTree(doc, model, child));
        }
        return el;
    }

            
    /*
     public static void storeObject(Object object) {
     ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), "ServiceAnalysisDB");
     try {
     db.store(object);
     // do something with db4o
     } finally {
     db.close();
     }
     }
     */
    public static String readFile(String fileName, String keyWord) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            if (keyWord==null) {
                while (line != null) {
                    sb.append(line);
                    sb.append("\n");
                    line = br.readLine();
                }
            } else {
                while (line != null) {
                    if (line.equals(keyWord))
                        return "TRUE";
                    line = br.readLine();
                }
                return "FALSE";
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

    public static void writeFile(String fileName, String content, boolean ifAppend) {
        FileOutputStream fop = null;
        File file;

        try {
            file = new File(fileName);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            fop = new FileOutputStream(file, ifAppend);

            // get the content in bytes
            byte[] contentInBytes = content.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*
         try{
         //String data = " This content will append to the end of the file";
 
         File file =new File(fileName);
 
         //if file doesnt exists, then create it
         if(!file.exists()){
         file.createNewFile();
         }
 
         //true = append file
         FileWriter fileWritter = new FileWriter(file.getName(),true);
         BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
         bufferWritter.write(content);
         bufferWritter.close();
         }catch(IOException e){
         e.printStackTrace();
         } 
         */
    }

    public static HashMap readConfiguration() {
        String fileName = "Configurations/systemParameters.xml";
        HashMap map = new HashMap();
        String heights = readXMLFile("SystemVariables", "Heights", fileName);
        String maxNumberOfParameters = readXMLFile("SystemVariables", "MaxNumberOfParameters", fileName);
        String totalNumberofAcceptablePaths = readXMLFile("SystemVariables", "TotalNumberofAcceptablePaths", fileName);
        String experimentsValueDeviation = readXMLFile("SystemVariables", "ExperimentsValueDeviation", fileName);
        String normalformTransitionKernelVarianceForGroup = readXMLFile("SystemVariables", "NormalformTransitionKernelVarianceForGroup", fileName);
        String normalformTransitionKernelVarianceWithinGroup = readXMLFile("SystemVariables", "NormalformTransitionKernelVarianceWithinGroup", fileName);
        String uniformTransitionKernalVarianceForGroup = readXMLFile("SystemVariables", "UniformTransitionKernalVarianceForGroup", fileName);
        String uniformTransitionKernalVarianceWithinGroup = readXMLFile("SystemVariables", "UniformTransitionKernalVarianceWithinGroup", fileName);
        String totalnubmerOfAttempts = readXMLFile("SystemVariables", "TotalnubmerOfAttempts", fileName);
        String weightMinToll = readXMLFile("SystemVariables", "WeightMinToll", fileName);
        String markovBlanketThreshHold = readXMLFile("SystemVariables", "MarkovBlanketThreshHold", fileName);
        String groupCountingFactor = readXMLFile("SystemVariables", "GroupCountingFactor", fileName);
        String initialGroupProbabilityFactor = readXMLFile("SystemVariables", "InitialGroupProbabilityFactor", fileName);
        String initialParameterProbabilityFactor = readXMLFile("SystemVariables", "InitialParameterProbabilityFactor", fileName);
        String groupGap = readXMLFile("SystemVariables", "GroupGap", fileName);
        String debug = readXMLFile("SystemVariables", "Debug", fileName);
        String searchMethoGenerateExpereimentData = readXMLFile("SystemVariables", "SearchMethoGenerateExpereimentData", fileName);
        String deviationFactor = readXMLFile("SystemVariables", "DeviationFactor", fileName);
        String bruteForce = readXMLFile("SystemVariables", "BruteForce", fileName);
        String logInterval = readXMLFile("SystemVariables", "LogInterval", fileName);
        String realService = readXMLFile("SystemVariables", "RealService", fileName);

        map.put("Heights", heights);
        map.put("MaxNumberOfParameters", maxNumberOfParameters);
        map.put("TotalNumberofAcceptablePaths", totalNumberofAcceptablePaths);
        map.put("ExperimentsValueDeviation", experimentsValueDeviation);
        map.put("NormalformTransitionKernelVarianceForGroup", normalformTransitionKernelVarianceForGroup);
        map.put("NormalformTransitionKernelVarianceWithinGroup", normalformTransitionKernelVarianceWithinGroup);
        map.put("UniformTransitionKernalVarianceForGroup", uniformTransitionKernalVarianceForGroup);
        map.put("UniformTransitionKernalVarianceWithinGroup", uniformTransitionKernalVarianceWithinGroup);
        map.put("TotalnubmerOfAttempts", totalnubmerOfAttempts);
        map.put("WeightMinToll", weightMinToll);
        map.put("MarkovBlanketThreshHold", markovBlanketThreshHold);
        map.put("GroupCountingFactor", groupCountingFactor);
        map.put("InitialGroupProbabilityFactor", initialGroupProbabilityFactor);
        map.put("InitialParameterProbabilityFactor", initialParameterProbabilityFactor);
        map.put("GroupGap", groupGap);
        map.put("Debug", debug);
        map.put("SearchMethoGenerateExpereimentData", searchMethoGenerateExpereimentData);
        map.put("DeviationFactor", deviationFactor);
        map.put("BruteForce", bruteForce);
        map.put("LogInterval", logInterval);
        map.put("RealService", realService);

        return map;
    }

    public static String readXMLFile(String serviceName, String attribute, String fileName) {
        String result = null;
        if (serviceName != null) {
            try {
                //File fXmlFile = new File("Track/sampleValues.xml");
                File fXmlFile = new File(fileName);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fXmlFile);

                //optional, but recommended
                //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getElementsByTagName(serviceName);
                if (nList.getLength() > 0) {
                    Node nNode = nList.item(0);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        if (eElement.getElementsByTagName(attribute).getLength() > 0) {
                            result = eElement.getElementsByTagName(attribute).item(0).getTextContent();
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    /*
     public static String readXMLFileV2(String serviceName, String parameterName, String fileName) {
     String result = null;
     try {
     //File fXmlFile = new File("Track/sampleValues.xml");
     File fXmlFile = new File(fileName);            
     DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
     DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
     Document doc = dBuilder.parse(fXmlFile);
            
     doc.getDocumentElement().normalize();
     NodeList nList = doc.getElementsByTagName(serviceName);
     if (nList.getLength() > 0) {
     Node nNode = nList.item(0);
     if (nNode.getNodeType() == Node.ELEMENT_NODE) {
     Element eElement = (Element) nNode;
     if (eElement.getElementsByTagName(parameterName).getLength()>0) {                        
     result = eElement.getElementsByTagName("Entity").item(0).getTextContent();
     }
     }
     }
     } catch (Exception e) {
     e.printStackTrace();
     }
     return result;
     }
     */

    public List<String> traverseTree(DefaultMutableTreeNode node, List<String> allParameters) {
        int childCount = node.getChildCount();
        //List<String> allParameters = new ArrayList<>();
        allParameters.add(node.toString());
        //System.out.println("---" + node.toString() + "---");

        for (int i = 0; i < childCount; i++) {

            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            if (childNode.getChildCount() > 0) {
                traverseTree(childNode, allParameters);
            } else {
                allParameters.add(childNode.toString());
                //allParameters = result + childNode.toString()+",";
            }
        }
        allParameters.add(node.toString());
        return allParameters;
        //result = result + node.toString();
        //return result;
        //System.out.println("+++" + node.toString() + "+++");
    }

    public static List<String> traverseTreeGetPair(DefaultMutableTreeNode node, List<String> allParameters) {
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);

            if (childNode.getParent() != null && childNode.getParent() != childNode && childNode.getChildCount() != 0) {
                String parentString = childNode.getParent().toString();
                String childString = childNode.toString();
                if (parentString != null) {
                    String nonBOParent = null;
                    String nonBO2Child = null;
                    nonBOParent = readXMLFile("OpenShipping", parentString, "Configurations/NonBOs.xml");
                    nonBO2Child = readXMLFile("OpenShipping", childString, "Configurations/NonBOs.xml");
                    if (nonBOParent == null && nonBO2Child == null) {
                        String meaningfulNameParent = readXMLFile("OpenShip", parentString, "Configurations/PredefinedBOs.xml");
                        String meaningfulNameChild = readXMLFile("OpenShip", childString, "Configurations/PredefinedBOs.xml");
                        //Now, it's the time to check if the parent and the child is not just simple nesting.
                        //We check if the relationship is actually strong dependence, weak dependence, or coincidence
                        //if (checkIfChildBOExclusivelyDominated(String))

                        if (meaningfulNameParent == null) {
                            meaningfulNameParent = parentString;
                        }
                        if (meaningfulNameChild == null) {
                            meaningfulNameChild = childString;
                        }

                        String pairString = meaningfulNameParent + " -> " + meaningfulNameChild + ";";
                        allParameters.add(pairString);
                    }
                }
            }
            if (childNode.getChildCount() > 0) {
                traverseTreeGetPair(childNode, allParameters);
            }
        }
        String parentString = node.toString();
        String nonBOParent = readXMLFile("OpenShip", parentString, "Configurations/NonBOs.xml");
        if (nonBOParent == null) {
            String meaningfulNameParent = readXMLFile("OpenShip", parentString, "Configurations/PredefinedBOs.xml");
            if (meaningfulNameParent == null) {
                meaningfulNameParent = parentString;
            }
            allParameters.add(meaningfulNameParent);
        }
        return allParameters;
    }

    public static Tree generateTree(List<String> allParameters, Tree tree, int parentIndex, String rootString) {
        //Tree tree = new Tree();
        //parentIndex = 0;

        for (int i = 0, size = allParameters.size(); i < size; i++) {
            if (i <= parentIndex - 1) {
                continue;
            }
            String currentString = allParameters.get(i);
            String parentString = null;
            if (parentIndex == 0) {
                parentString = rootString;
            } else {
                parentString = allParameters.get(parentIndex - 1);
            }
            tree.addNode(currentString, parentString);
        }
        if (parentIndex < allParameters.size() - 1) {
            tree = generateTree(allParameters, tree, parentIndex + 1, rootString);
        }
        return tree;
    }

    /*    
     digraph G {
     subgraph place {
     graph [shape=circle,color=gray];
     node [shape=circle,fixedsize=true,width=2];
     "customer waiting";
     cutting;
     "idle barber";
     "customer paying";
     }
     subgraph transitions {
     node [shape=rect,height=0.2,width=2];
     enter;
     "start cutting";
     "finish cutting";
     exit;
     }
     enter -> "customer waiting";
     "customer waiting" -> "start cutting";
     "idle barber" -> "start cutting";
     "start cutting" -> cutting;
     cutting -> "finish cutting";
     "finish cutting" -> "customer paying";
     "finish cutting" -> "idle barber";
     "customer paying" -> exit;
     }
     */
    /*
     public static void generatePNMLDocument(Petrinet petriNet, String serviceName, String entityName, String flag) {
     //ArrayList<HashMap> Places = new ArrayList<HashMap>();
     try {
     ModelRepository mr = ModelRepository.getInstance();
     mr.createDocumentWorkspace(entityName + "_" + flag);
     mr.setPrettyPrintStatus(true);

     PetriNetDocHLAPI doc = new PetriNetDocHLAPI();
     PetriNetHLAPI net = null;

     try {
     net = new PetriNetHLAPI(entityName, PNTypeHLAPI.COREMODEL, new NameHLAPI(entityName + "BehaviouralModel"), doc);
     } catch (InvalidIDException ex) {
     Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
     } catch (VoidRepositoryException ex) {
     Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
     }

     PageHLAPI page = new PageHLAPI(entityName + "BehaviouralModel", new NameHLAPI("BehaviouralModel"), null, net); //use of "null" is authorized but not encouraged

     for (Place place : petriNet.getPlaces()) {
     PlaceHLAPI p1 = new PlaceHLAPI(place.getName());
     p1.setContainerPageHLAPI(page);
     }

     for (Transition transition : petriNet.getTransitions()) {
     TransitionHLAPI t1 = new TransitionHLAPI(transition.getName());
     t1.setContainerPageHLAPI(page);
     }

     List<PlaceHLAPI> places = page.getObjects_hlcorestructure_PlaceHLAPI();
     List<TransitionHLAPI> transitions = page.getObjects_hlcorestructure_TransitionHLAPI();

     int counter = 0;
     for (Arc arc : petriNet.getArcs()) {
     counter++;
     PlaceHLAPI currentPlace = null;
     TransitionHLAPI currenTransition = null;
     for (PlaceHLAPI placeHLAPI : places) {
     if (arc.getPlace().getName().equals(placeHLAPI.getId())) {
     currentPlace = placeHLAPI;
     break;
     }
     }
     for (TransitionHLAPI transitionHLAPI : transitions) {
     if (arc.getTransition().getName().equals(transitionHLAPI.getId())) {
     currenTransition = transitionHLAPI;
     break;
     }
     }
     if (currentPlace != null && currenTransition != null) {
     if (arc.getDirection() == Arc.Direction.PLACE_TO_TRANSITION) {
     try {
     new ArcHLAPI("a" + counter, currentPlace, currenTransition, page);
     } catch (InvalidIDException ex) {
     Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
     } catch (VoidRepositoryException ex) {
     Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
     }
     } else if (arc.getDirection() == Arc.Direction.TRANSITION_TO_PLACE) {
     try {
     new ArcHLAPI("a" + counter, currenTransition, currentPlace, page);
     } catch (InvalidIDException ex) {
     Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
     } catch (VoidRepositoryException ex) {
     Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
     }
     }
     }

     }

     String directory = "output/BEModel/" + serviceName;
     File theDir = new File(directory);
     if (!theDir.exists()) {
     theDir.mkdirs();
     }
     String fileName = null;
     if (flag != null && flag.equals("ACTUAL")) {
     fileName = directory + "/" + entityName + "(actual).pnml";
     } else if (flag != null && flag.equals("ABSTRACT")) {
     fileName = directory + "/" + entityName + "(abstract).pnml";
     } else if (flag != null && flag.equals("LIFECYCLE")) {
     fileName = directory + "/" + entityName + "(lifecycle).pnml";
     }

     try {
     PNMLUtils.exportPetriNetDocToPNML(doc, fileName);
     } catch (UnhandledNetType ex) {
     Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
     } catch (OCLValidationFailed ex) {
     Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
     } catch (IOException ex) {
     Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
     } catch (ValidationFailedException ex) {
     Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
     } catch (BadFileFormatException ex) {
     Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
     } catch (OtherException ex) {
     Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
     }

     mr.destroyCurrentWorkspace();
     } catch (InvalidIDException ex) {
     Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
     } catch (VoidRepositoryException ex) {
     Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
     }
     }

     */
    public static void generatePetrinet(Petrinet net, String fileName, String serviceName) {
        GraphViz gv = new GraphViz();
        gv.addln(gv.start_graph());
        gv.addln("rankdir=LR;");
        //gv.addln("size=\"8,5\"");
        gv.addln(gv.start_subGraph("place"));
        gv.addln("graph [shape=circle,color=gray];");
        gv.addln("node [shape=circle,fixedsize=false,width=2];");
        for (Place place : net.getPlaces()) {
            gv.addln(place.getName() + ";");
        }
        gv.addln(gv.end_graph());
        gv.addln(gv.start_subGraph("transition"));
        gv.addln("node [shape=rect,height=0.2,width=2];");
        for (Transition transition : net.getTransitions()) {
            if (!transition.getName().contains("st")) {
                gv.addln(transition.getName() + ";");
            }
        }
        gv.addln(gv.end_graph());

        gv.addln(gv.start_subGraph("transition"));
        gv.addln("node [shape=rect,height=0.2,width=2, color=red, style=filled];");
        for (Transition transition : net.getTransitions()) {
            if (transition.getName().contains("st")) {
                gv.addln(transition.getName() + ";");
            }
        }
        gv.addln(gv.end_graph());

        for (Arc arc : net.getArcs()) {
            gv.addln(arc.toString() + ";");
        }

        gv.addln(gv.end_graph());
        System.out.println(gv.getDotSource());
        String type = "pdf";
        String directory = "output/BEModel/" + serviceName;
        File theDir = new File(directory);
        if (!theDir.exists()) {
            theDir.mkdirs();
        }
        File out = new File(directory + "/" + fileName + "." + type);   // Linux
        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);
    }

    public static void generateGraph(java.util.List<String> graphStrings, String fileName) {
        GraphViz gv = new GraphViz();
        gv.addln(gv.start_graph());
        for (String graphString : graphStrings) {
            gv.addln(graphString);
        }
        //gv.addln("A -> B [style=dotted];");
        gv.addln(gv.end_graph());
        System.out.println(gv.getDotSource());

//      String type = "gif";
//      String type = "dot";
//      String type = "fig";    // open with xfig
        String type = "pdf";
//      String type = "ps";
//      String type = "svg";    // open with inkscape
//      String type = "png";
//      String type = "plain";
        File out = new File("output/" + fileName + "." + type);   // Linux
//      File out = new File("c:/eclipse.ws/graphviz-java-api/out." + type);    // Windows
        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);
    }

    public static void main(String arg[]) {

        //readXMLFileV2("ValidCombinations/track_knownPath.xml");
        /*
         ArrayList<Parameter> parameters = getKnownPath("ValidCombinations/track_knownPath.xml");
         if (parameters != null)
         for(Parameter parameter: parameters)
         if (parameter.getParentParameter()!=null)
         System.out.println("Parameter: "+ parameter.getName()+ " its Parent: "+ parameter.getParentParameter().getName());
         else System.out.println("Parameter: "+ parameter.getName()+ " its Parent: root");
         */
        HashMap map = new HashMap();
        map.put(21, "Twenty One");
        map.put(21, "Twenty two");
        map.put("31", "Thirty One");
        map.put(31, 234.5);

        Iterator<Integer> keySetIterator = map.keySet().iterator();
        while (keySetIterator.hasNext()) {
            Integer key = keySetIterator.next();
            System.out.println("key: " + key + " value: " + map.get(key));
        }

        Double value = (Double) map.get(31);
        if (value > 200) {
            System.out.println("key: 21" + value);
        }

    }


    /* Get a list of unprocessed files */
    public static ArrayList<File> getUnprocessedFile(String filePath, String ext) {
        File dir = new File(filePath);
        FileFilter fileFilter = new WildcardFileFilter("*." + ext);
        File[] files = dir.listFiles(fileFilter);
        ArrayList<File> ｕnprocessedFiles = new ArrayList<File>();
        
        if (files.length > 0) {
            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);            
            for (File file: files){
                String processedFiles = "";                
                try {
                    processedFiles = Utility.readFile("output/processedWSDL.txt", file.getName());
                } catch (IOException ex) {
                    Logger.getLogger(ServiceInterfaceAnalysis.class.getName()).log(Level.SEVERE, null, ex);
                }                
                if (processedFiles.equals("FALSE")) {
                    ｕnprocessedFiles.add(file);
                } else {  //exit the loop when hiting the first processed? 
                    break;
                }
            }
        }
        return ｕnprocessedFiles;
    }
}
