/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.introspection;

import com.rits.cloning.Cloner;
import static java.lang.Math.abs;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.Combinations;
import org.joda.time.DateTime;
import static qut.edu.au.Utility.readConfiguration;
import static qut.edu.au.Utility.writeFile;
import qut.edu.au.services.Group;
import qut.edu.au.services.Operation;
import qut.edu.au.services.Parameter;
import qut.edu.au.services.Service;

/**
 *
 * @author fuguo TODO (1) Test if the tree is updated correctly (2) set one
 * known successful path, set the distribution accordingly (3 times) for
 * example, if the total set of parameters are {a,b,c,d,e,f,g,h,i,j,k,l,m,n} one
 * known successful parameter path = {a, b, c, d, e} the distribution of e given
 * {a, b, c, d} is 1/10 with uniform, but as it is a known successful path, the
 * distribution of e given {a, b, c, d} should be reasonable 3 times more than
 * others, meaning it should be 1/13*3, whereas others (f,g,h,i,j,k,l,m,n)
 * should be 1/13. and then test it (3) set a number of accepted but clustered
 * parameters on the service side. (try different levels of diffuse parameters
 * for example, {a,b,c,d,e,k,l} {a,b,c,d,f,k,l} {a,b,c,d,f,i,l} and then test
 * the client side to see what the proportion of accepted path is, and at which
 * point it fails for instance, 2 out of 3?, All these are the statistics we got
 * can be used for the papers
 */
public class MonteCarloIntrospection {

    private ArrayList<Parameter> paramPath = new ArrayList<Parameter>();
    private ArrayList<Group> groupPath = new ArrayList<Group>();
    private ArrayList<Parameter> parametersPathUnderGroupPath = new ArrayList<Parameter>();

    private ArrayList<Parameter> knownSuccessPath = new ArrayList<Parameter>();
    private ArrayList<Parameter> minimumParameters = new ArrayList<Parameter>();
    private ArrayList<Group> knownGroups = new ArrayList<Group>();
    private ArrayList<Group> allGroups = new ArrayList<Group>();
    private ArrayList<Group> minimumGroups = new ArrayList<Group>();   
    private ArrayList<ArrayList<Integer>> searchMethodGenerated = new ArrayList<ArrayList<Integer>>();
            
    //private ArrayList<ArrayList<Parameter>> paths = new ArrayList<ArrayList<Parameter>>();
    private Operation operation;
    private Service service;
    private ServiceSimulator simulator;
    private boolean success = false;
    //private boolean secondSuccess = false;
    private boolean newSuccess = false;    
    //private boolean thirdSuccess = false;    
    private TreeNode initialTree = null;
    private Integer previousFoundIndex = null;
    private double secondTransitionKernelVariance =0;
    private double thirdTransitionKernelVariance =0;
    private long subtypeCounter = 0;
    private boolean moderate =false;
    
    //private GroupTreeNode treeForGroups = null;
    //private TreeNode parametersTree = null;

    //private TreeNode treeForParameters = null;
    //private TreeNode initialTree = null;
    /*
     private static final double transitionKernelVarianceForGroup = 0.125;
     private static final double transitionKernelVarianceWithinGroup = 0.125;
     private static final double uniformTransitionKernalVariance = 0.5;
     private static final double uniformTransitionKernalVarianceGroupParameter = 0.0;
     private static final long totalnubmerOfAttempts = 1000000000;
     private static final double weightMinTol = 0.000001;
     public static final double countingFactor = 500; // this decides how many times we will stick to the known path
     //public static double initialGroupProbabilityFactor = 1.12109375; // this decides how many times we will stick to the known path    
     public static double initialGroupProbabilityFactor = 1; // this decides how many times we will stick to the known path    
     public static final double initialParameterProbabilityFactor = 1; // this decides how many times we will stick to the known path    
     //private ArrayList<ArrayList<HashMap>> markovBlanket = new ArrayList<ArrayList<HashMap>>();
     private final double markovBlanketThreshHold = 0.7;
     public static final int heights[] = {10, 15, 20, 25, 30};
     public static final int maxNumberOfParameters = 50;
     public static final int totalNumberofPaths = 20;
     private static int groupGap = 5;
     //private final boolean debug = false;
     1000000000
     */
    public static double experimentsValueDeviation;
    private static double transitionKernelVarianceForGroup;
    private static double transitionKernelVarianceWithinGroup;
    private static double uniformTransitionKernalVariance;
    private static double uniformTransitionKernalVarianceGroupParameter;
    private static long totalnubmerOfAttempts;
    private static double weightMinTol;
    public static double countingFactor; // this decides how many times we will stick to the known path
    //public static double initialGroupProbabilityFactor = 1.12109375; // this decides how many times we will stick to the known path    
    public static double initialGroupProbabilityFactor; // this decides how many times we will stick to the known path    
    public static double initialParameterProbabilityFactor; // this decides how many times we will stick to the known path    
    //private ArrayList<ArrayList<HashMap>> markovBlanket = new ArrayList<ArrayList<HashMap>>();
    private static double markovBlanketThreshHold;
    public static ArrayList<Integer> heights = new ArrayList<Integer>();
    public static int maxNumberOfParameters;
    public static int totalNumberofPaths;
    public static int groupGap;
    private static String debug;
    public static String realService;
    private static String bruteForce;
    private static String searchMethoGenerateExpereimentData;
    private int searchMethoGenerateExpereimentDataCounter = 0;
    private static double deviationFactor;
    private static String logFileName;
    private static long logInterval;
    public static int numberOfCompulsoryParameters=1;
    private static long[] stats;
    private static boolean[] visited;

    public MonteCarloIntrospection(Service serv, Operation oper, ServiceSimulator mockup) {
        this.operation = oper;
        this.service = serv;
        this.simulator = mockup;
    }

    private static boolean initialisation() {
        HashMap map = readConfiguration();

        if (!map.isEmpty()) {

            String heightsString = (String) map.get("Heights");
            String[] parts = heightsString.split(",");
            for (String part : parts) {
                heights.add(Integer.parseInt(part));
            }
            maxNumberOfParameters = Integer.parseInt((String) map.get("MaxNumberOfParameters"));
            totalNumberofPaths = Integer.parseInt((String) map.get("TotalNumberofAcceptablePaths"));
            stats = new long[totalNumberofPaths];
            visited=new boolean[totalNumberofPaths];
            experimentsValueDeviation = Double.parseDouble((String) map.get("ExperimentsValueDeviation"));
            transitionKernelVarianceForGroup = Double.parseDouble((String) map.get("NormalformTransitionKernelVarianceForGroup"));
            transitionKernelVarianceWithinGroup = Double.parseDouble((String) map.get("NormalformTransitionKernelVarianceWithinGroup"));
            deviationFactor = Double.parseDouble((String) map.get("DeviationFactor"));
            uniformTransitionKernalVariance = Double.parseDouble((String) map.get("UniformTransitionKernalVarianceForGroup"));
            uniformTransitionKernalVarianceGroupParameter = Double.parseDouble((String) map.get("UniformTransitionKernalVarianceWithinGroup"));
            totalnubmerOfAttempts = Long.parseLong((String) map.get("TotalnubmerOfAttempts"));
            weightMinTol = Double.parseDouble((String) map.get("WeightMinToll"));
            markovBlanketThreshHold = Double.parseDouble((String) map.get("MarkovBlanketThreshHold"));
            countingFactor = Double.parseDouble((String) map.get("GroupCountingFactor"));
            initialGroupProbabilityFactor = Double.parseDouble((String) map.get("InitialGroupProbabilityFactor"));
            initialParameterProbabilityFactor = Double.parseDouble((String) map.get("InitialParameterProbabilityFactor"));
            groupGap = Integer.parseInt((String) map.get("GroupGap"));
            debug = (String) map.get("Debug");
            bruteForce = (String) map.get("BruteForce");
            searchMethoGenerateExpereimentData = (String) map.get("SearchMethoGenerateExpereimentData");
            logInterval = Long.parseLong((String) map.get("LogInterval"));
            realService=(String) map.get("RealService");
            
            return true;
        }
        return false;

    }

    public static void main(String arg[]) {
        //Service service = new Service("TestData/ES/Fedex/TrackService_v9.wsdl");
        //Operation operation = service.getOperation("track");

        //Service service = new Service("TestData/ES/Fedex/ShipService_v15.wsdl");
        //Operation operation = service.getOperation("processShipment");
        if (initialisation()) {
            if (arg.length > 0) //give the log file name as the input parameter
            {
                logFileName = arg[0];
            }

            ServiceSimulator provider = new ServiceSimulator();
            MonteCarloIntrospection search = new MonteCarloIntrospection(null, null, provider);
            //MonteCarloIntrospection search = new MonteCarloIntrospection(service, operation, null);
            //MonteCarloIntrospection search = new MonteCarloIntrospection(provider);
            //Operation operation = service.getOperation("createOpenShipment");        
            //Book book = search.discoverSuccessfulPath();
            search.discoverSuccessfulPath(true);  //use experiments
            //search.discoverSuccessfulPathExperiments();

            //listofPreliminary is zero here.
            //if (AnalysisStats.listofPreliminary.size()>0) {
            //System.out.println("fixed_depth_path:");
            //ArrayList<String> path = new ArrayList<String>();            
            //path = fixedDepthPath(path,0,10,null,AnalysisStats.listofPreliminary);
//            PriorityList list = search.upperConfidenceTreeSearch(1, 10, service, operation); 
            //for (String s : path)
            // System.out.println(s);
//        }
        }
        System.exit(1);
    }

    private TreeNode searchParametersUnderPath(Random rg, Group group, GroupTreeNode tn) {
        TreeNode treeForParameters = null;
        // now search for the parameters under this group, because the tree has to be with this group in this particular position
        ArrayList<Parameter> parametersUnderGroup = group.getParameters();

        //do it regardless of if the tree exists        
        if (parametersUnderGroup != null && parametersUnderGroup.size() > 0) {
            ArrayList<Parameter> allparametersUnderGroup = new ArrayList<Parameter>();

            Parameter initialOne = new Parameter();
            initialOne.setName("null");
            initialOne.setSimpleIndex(parametersUnderGroup.get(0).getSimpleIndex() - 1);
            allparametersUnderGroup.add(initialOne);
            allparametersUnderGroup.addAll(parametersUnderGroup);
            Parameter omega = new Parameter();
            omega.setName("OMEGA");
            int lastIndex = parametersUnderGroup.get(parametersUnderGroup.size() - 1).getSimpleIndex();
            omega.setSimpleIndex(lastIndex + 1);
            allparametersUnderGroup.add(omega);

            if (tn.getTreeForParameters() != null) {
                treeForParameters = tn.getTreeForParameters();
            } else {
                treeForParameters = new TreeNode(0, allparametersUnderGroup.size() - 1, rg);
            }

            // now search the parameter, do we need to search? maybe not, as we always start from the first node
            Distribution priorForParameters = new Distribution(allparametersUnderGroup.size(), rg);
            int lowerIndex = allparametersUnderGroup.get(0).getSimpleIndex();
            //int upperIndex = allparametersUnderGroup.get(allparametersUnderGroup.size() - 1).getSimpleIndex();
            //lowerIndex is the baseline
            while (parametersPathUnderGroupPath.isEmpty()) {
                treeForParameters = searchForParameterUnderGroup(rg, allparametersUnderGroup,
                        lowerIndex, treeForParameters, priorForParameters, lowerIndex);
            }

            group.setParametersChosen(parametersPathUnderGroupPath);
            parametersPathUnderGroupPath = new ArrayList<Parameter>();
        }
        return treeForParameters;
    }

    private TreeNode searchGroupPath(Random rg, ArrayList<Group> groups, int index, GroupTreeNode tree, Distribution prior) {
        // base case
        int totalSize = groups.size() - 1;  //one for "null"
        if (index >= totalSize) {
            int paramSize = groupPath.size();
            Group lastGroup = groupPath.get(paramSize - 1);
            if (lastGroup.getName() != null && lastGroup.getName().equals("OMEGA")) {
                groupPath.remove(paramSize - 1);
            }

            if (groupPath.isEmpty()) {
                return null;
            }
            // create tree node
            //only update if all the path is accepted

            ArrayList<Parameter> parametersToTry = new ArrayList<Parameter>();
            if (debug.equals("TRUE")) {
                System.out.println("Groups to try:------------------------------");
            }
            for (Group group : groupPath) {
                if (debug.equals("TRUE")) {
                    System.out.print(group.getGroupNumber() + ",");
                }
                ArrayList<Parameter> parametersChosen = group.getParametersChosen();
                //int size = 0;
                //if (parametersChosen != null) {
                //   size = parametersChosen.size();
                //}
                //double selectionRate = Math.round(((double) size / (double) group.getParameters().size()) * 100);
                //System.out.print(group.getGroupNumber() + ", rate: [" + selectionRate + "][");
                if (parametersChosen != null && parametersChosen.size() > 0) {
                    //for (Parameter parameter : parametersChosen) {
                    //    System.out.print(parameter.getSimpleIndex() + ",");
                    // }
                    parametersToTry.addAll(parametersChosen);
                }
                //System.out.print("]");
            }

            for (Parameter miniParameter : minimumParameters) {
                if (!parametersToTry.contains(miniParameter)) {
                    for (int i = 0; i < parametersToTry.size(); i++) {
                        if (parametersToTry.get(i).getSimpleIndex() > miniParameter.getSimpleIndex()) {
                            parametersToTry.add(i, miniParameter);
                            break;
                        }
                    }
                }
            }
            if (debug.equals("TRUE")) {
                System.out.println();
                System.out.println("------------------------------------------------");
            }

            if (debug.equals("TRUE")) {
                System.out.println("Parameters to try:------------------------------");
                for (Parameter parameter : parametersToTry) {
                    System.out.print(parameter.getSimpleIndex() + ",");
                }
                System.out.println();
                System.out.println("------------------------------------------------");
            }
            //now it's time to try these babies and update the trees accordingly

            if (this.simulator != null) {
                boolean accepted = this.simulator.invokeOperation(parametersToTry);
                if (accepted) {
                    success = true;
                    //System.out.println("WA HA HA...................SUCCEESS....................................................................");                    
                    ArrayList<Integer> indices = new ArrayList<Integer>();
                    for (Parameter parameter : parametersToTry) {
                        indices.add(parameter.getSimpleIndex());
                    }

                    for (int i = 0; i < simulator.getCurrentSuccessfulPathIndices().size(); i++) {
                        if (indices.equals(simulator.getCurrentSuccessfulPathIndices().get(i))) {
                            stats[i] = abs(stats[i]) + 1; //record the occurences
                            break;
                        }
                    }
                    // create tree node
                    //TreeNode tn = new TreeNode(index, parameters.size());
                    //create tree node for the current path (i.e., the parameters in paramPath)
                    //System.out.println("Creating a tree node for OMEGA" + " its size is  "+ (totalSize-theRealParameter.getSimpleIndex()));

                    GroupTreeNode tn = new GroupTreeNode(lastGroup.getGroupNumber(), totalSize, rg); // we also update the tree for parameters
                    //ArrayList<TreeNode> treeNodes = new ArrayList<TreeNode>();
                    tn.setTreeForParameters(groups.get(index).getTreeForParameters());
                    //treeNodes.add(tn);
                    //treeNodes.add(groups.get(index).getTreeForParameters());
                    groupPath = new ArrayList<Group>();
                    return tn;
                    //TreeNode tn = new TreeNode(lastParameter.getSimpleIndex(), totalSize);
                    //return tn;
                }

            } else if (this.service != null) {
                String responseMessage = null;
                /*
                 ArrayList<String> requestResponse = service.invokeServiceOperation(operation.getName(), paramPath);
                 if (requestResponse != null && requestResponse.size() == 2) {
                 responseMessage = requestResponse.get(1);
                 }
                 System.out.println(requestResponse.get(0));
                 System.out.println(requestResponse.get(1));
                 */
                //if (!responseMessage.contains("ERROR")) //record the path (combination) as accepted combination
                if (responseMessage != null && responseMessage.contains("successfully")) //record the path (combination) as accepted combination
                //there may be many such combinations
                {
                    System.out.println("WA HA HA...................SUCCEESS....................................................................");
                    // create tree node

                    GroupTreeNode tn = new GroupTreeNode(groupPath.get(groupPath.size() - 1).getGroupNumber() + 1, totalSize, rg); // we also update the tree for parameters
                    //ArrayList<TreeNode> treeNodes = new ArrayList<TreeNode>();
                    tn.setTreeForParameters(groups.get(index).getTreeForParameters());
                    //treeNodes.add(tn);
                    //treeNodes.add(groups.get(index).getTreeForParameters());
                    groupPath = new ArrayList<Group>();
                    return tn;

                    //TreeNode tn = new TreeNode(lastParameter.getSimpleIndex(), totalSize);
                    //return tn;
                    //Combination combination = new Combination(minimumParameters, requestResponse.get(0), responseMessage);
                    //operation.setAccecptedParameterSets(combination);
                    //book.addPath(minimumPath);
                    //list = updateList(responseMessage, path, list, max);
                }
            }
            //System.out.println("");
            groupPath = new ArrayList<Group>();
            //groupPath.addAll(minimumGroups);
            return null;
        }

        int i = 0;

        do {
            boolean stickToPath = false;
            //pick up the child node with a counter that is greater then 0, if all the children's counter is less than 0, then we go for the random search
            if (tree != null) {
                for (int j = 0; j < tree.children.length; j++) {
                    GroupTreeNode child = (GroupTreeNode) tree.children[j];
                    if (child != null && child.getCounter() > 0) {  // there shoud be only one child with counter greater than 0
                        stickToPath = true;
                        i = index + j + 1;
                        child.setCounter(child.getCounter() - 1);
                        break;
                    }
                }
            }

            if (!stickToPath) {
                if (groupPath.size() < knownGroups.size()) {
                    //System.out.println("index is: " + index + "  i is: " + i);
                    //do {
                    int s = (totalSize + 1) - (index + 1);
                    // zero tjhe prior
                    prior.zero();
                    // for each probability value
                    for (int j = 0; j < s; j++) {
                        prior.setProb(j, 1.0);//(double)(s-j));
                    }
                    // normalise the prior
                    prior.normalise();
                    //randome number, treat the tree as the uniform distribution for now        
                    if (tree != null) {
                        i = index + 1 + tree.getDistribution().sampleWithTransition(transitionKernelVarianceForGroup);
                        GroupTreeNode tempNode = (GroupTreeNode) tree.children[i - index - 1];
                        if (tempNode != null) {
                            tempNode.setCounter((int) (tree.getDistribution().probability(i - index - 1) * countingFactor));
                        }
                    } else {
                        i = index + 1 + prior.sampleWithTransition(uniformTransitionKernalVariance);
                    }
                } else {
                    i = totalSize;
                }
            }
        } while (groupPath.isEmpty() && groups.size() - 1 == i); //prevent the method picking up OMEGA in the beginning

        //} while (parameterIndicies.contains(i));
        //System.out.println("index is: " + index + "  i is: " + i);
        int indexOfChild = i - index - 1;

        if (i == index) {
            System.out.print("Wrong......................");
        }

        Group group = groups.get(i);
        groupPath.add(group);

        // now search for the parameters under this group, because the tree has to be with this group in this particular position
        ArrayList<Parameter> parametersUnderGroup = group.getParameters();
        //do it regardless of if the tree exists        
        if (parametersUnderGroup != null && parametersUnderGroup.size() > 0) {
            ArrayList<Parameter> allparametersUnderGroup = new ArrayList<Parameter>();

            Parameter initialOne = new Parameter();
            initialOne.setName("null");
            initialOne.setSimpleIndex(parametersUnderGroup.get(0).getSimpleIndex() - 1);
            allparametersUnderGroup.add(initialOne);
            allparametersUnderGroup.addAll(parametersUnderGroup);
            Parameter omega = new Parameter();
            omega.setName("OMEGA");
            int lastIndex = parametersUnderGroup.get(parametersUnderGroup.size() - 1).getSimpleIndex();
            omega.setSimpleIndex(lastIndex + 1);
            allparametersUnderGroup.add(omega);

            // now search the parameter, do we need to search? maybe not, as we always start from the first node        
            Distribution priorForParameters = new Distribution(allparametersUnderGroup.size(), rg);
            int lowerIndex = allparametersUnderGroup.get(0).getSimpleIndex();
            //int upperIndex = allparametersUnderGroup.get(allparametersUnderGroup.size() - 1).getSimpleIndex();

            // get the tree for the current group
            GroupTreeNode node = null;
            if (tree != null) {
                node = (GroupTreeNode) tree.getChildren()[indexOfChild];
            }
            TreeNode treeForParametersUnderGroup = null;
            if (node != null && node.getTreeForParameters() != null) {
                //treeForParametersUnderGroup = new TreeNode(node.getTreeForParameters());                
                Cloner cloner = new Cloner();
                treeForParametersUnderGroup = cloner.deepClone(node.getTreeForParameters());
                //treeForParametersUnderGroup = node.getTreeForParameters();
                //treeForParametersUnderGroup.setChildren(node.getTreeForParameters().getChildren());
                //treeForParametersUnderGroup.setDistribution(node.getTreeForParameters().getDistribution());
                //treeForParametersUnderGroup.setPastDistribution(node.getTreeForParameters().getPastDistribution());
            } else {
                treeForParametersUnderGroup = new TreeNode(0, allparametersUnderGroup.size() - 1, rg);
            }

            if (treeForParametersUnderGroup.getDistribution().getSize() == 0) {
                System.out.print("wrong.............");
            }

            while (parametersPathUnderGroupPath.isEmpty()) {
                searchForParameterUnderGroup(rg, allparametersUnderGroup,
                        lowerIndex, treeForParametersUnderGroup, priorForParameters, lowerIndex);
            }
            group.setParametersChosen(parametersPathUnderGroupPath);
            group.setTreeForParameters(treeForParametersUnderGroup);
            parametersPathUnderGroupPath = new ArrayList<Parameter>();
        }
        /*
         ArrayList<Parameter> parametersUnderGroup = group.getParameters();

         //do it regardless of if the tree exists        
         if (parametersUnderGroup != null && parametersUnderGroup.size() > 0) {
         ArrayList<Parameter> allparametersUnderGroup = new ArrayList<Parameter>();

         Parameter initialOne = new Parameter();
         initialOne.setName("null");
         initialOne.setSimpleIndex(parametersUnderGroup.get(0).getSimpleIndex() - 1);
         allparametersUnderGroup.add(initialOne);
         allparametersUnderGroup.addAll(parametersUnderGroup);
         Parameter omega = new Parameter();
         omega.setName("OMEGA");
         int lastIndex = parametersUnderGroup.get(parametersUnderGroup.size() - 1).getSimpleIndex();
         omega.setSimpleIndex(lastIndex + 1);
         allparametersUnderGroup.add(omega);

         if (tree != null && tree.getTreeForParameters() != null) {
         treeForParameters = tree.getTreeForParameters();
         } else {
         treeForParameters = new TreeNode(0, allparametersUnderGroup.size() - 1);
         }

         // now search the parameter, do we need to search? maybe not, as we always start from the first node        
         Distribution priorForParameters = new Distribution(allparametersUnderGroup.size());
         int lowerIndex = allparametersUnderGroup.get(0).getSimpleIndex();
         int upperIndex = allparametersUnderGroup.get(allparametersUnderGroup.size() - 1).getSimpleIndex();

         searchForParameterUnderGroup(rg, allparametersUnderGroup,
         lowerIndex, treeForParameters, priorForParameters, lowerIndex, upperIndex);
         //get the tree and update it
         if (tree != null) {
         tree.setTreeForParameters(treeForParameters);
         }
         group.setParametersChosen(parametersPathUnderGroupPath);
         parametersPathUnderGroupPath = new ArrayList<Parameter>();
         }
         */

        TreeNode result;

        // the importance weight variable
        double w;
        // get importance weight
        if (tree == null) {
            w = (double) (totalSize - i);
        } else {
            w = tree.getDistribution().getImportanceWeight(indexOfChild, null, weightMinTol);
        }
        // if (w >= 1) //we don't wanna end up having negative weight
        //     w = Math.log(w);
        // recurse
        if (tree != null) {
            result = searchGroupPath(rg, groups, i, (GroupTreeNode) tree.getChildren()[indexOfChild], prior);
        } else {
            result = searchGroupPath(rg, groups, i, null, prior);
        }

        // remove parameter from list
        //groupPath.remove(groups.get(i));
        if (result != null) {
            // if the tree is null
            if (tree == null) {
                // create tree node
                GroupTreeNode tn = new GroupTreeNode(index, totalSize, rg);
                // add the result child
                tn.getChildren()[indexOfChild] = result;
                tn.setTreeForParameters(groups.get(index).getTreeForParameters());

                //GroupTreeNode node = (GroupTreeNode) tn.getChildren()[indexOfChild];
                //node.setTreeForParameters(result.get(1));
                //tn.updatePastDistribution();
                // update the distribution                
                tn.getDistribution().updatePDF(indexOfChild, w);

                //if the probability of current attribute (indexofChild) given the previous one (index) is greater than the threshold;
                // we record it in the markov blanket - a table                
                //TODO It doesn't make sense to keep track the Markov blanket as the path searched here may not the ideal one                
                if (i != totalSize) { //we don't record markov blanket for OMEGA
                    Group currentGroup = groups.get(i);
                    double currentProbability = tn.getDistribution().probability(indexOfChild);
                    if (currentProbability > markovBlanketThreshHold) {  //to test this                    
                        int givenIndex = groups.get(index).getGroupNumber();
                        Integer originalFrequency = (Integer) currentGroup.getMarkovBlanket().get(givenIndex);
                        if ((originalFrequency == null)) // if the new probability is greater, then replace the old one
                        {
                            currentGroup.getMarkovBlanket().put(givenIndex, 1);
                        } else {
                            currentGroup.getMarkovBlanket().put(givenIndex, originalFrequency + 1);
                        }
                    }
                }
                //TreeNode treeForParameters = searchParametersUnderPath(rg, previousGroup, tn);
                //tn.setTreeForParameters(treeForParameters);

                //ArrayList<TreeNode> treeNodes = new ArrayList<TreeNode>();
                //treeNodes.add(tn);
                //if (previousGroup != null) {
                //    treeNodes.add(previousGroup.getTreeForParameters());
                //}
                return tn;
            }

            //if (tree.getChildren()[indexOfChild] == null) // add the result child
            //{
            tree.getChildren()[indexOfChild] = result;
            tree.setTreeForParameters(groups.get(index).getTreeForParameters());

            //GroupTreeNode node = (GroupTreeNode) tree.getChildren()[indexOfChild];
            //node.setTreeForParameters(result.get(1));
            //}
            //TreeNode treeForParameters = searchParametersUnderPath(rg, previousGroup, tree);
            //tree.setTreeForParameters(treeForParameters);            
            //tree.setTreeForParameters(result.get(1));            
            tree.updatePastDistribution();
            // update the distribution
            tree.getDistribution().updatePDF(indexOfChild, w);

            //if the probability of current attribute (indexofChild) given the previous one (index) is greater than the threshold
            // we record it in the markov blanket - a table
            if (i != totalSize) { //we don't record markov blanket for OMEGA
                Group currentGroup = groups.get(i);
                double currentProbability = tree.getDistribution().probability(indexOfChild);
                if (currentProbability > markovBlanketThreshHold) {  //to test this
                    Integer givenIndex = groups.get(index).getGroupNumber();
                    Integer originalFrequency = (Integer) currentGroup.getMarkovBlanket().get(givenIndex);
                    if ((originalFrequency == null)) // if the new probability is greater, then replace the old one
                    {
                        currentGroup.getMarkovBlanket().put(givenIndex, 1);
                    } //System.out.println(parameters.get(i).getName()+" "+ key + " "+ parameters.get(i).getMarkovBlanket().get(key));
                    else {
                        currentGroup.getMarkovBlanket().put(givenIndex, originalFrequency + 1);
                    }
                }
            }
            //tree.getDistribution().updatePDFwithNeighbours(indexOfChild, w, 5);
            //System.out.println("update the "+indexOfChild+" th child of tree (" + tree + ") with tree "+ result);
            // return
            //ArrayList<TreeNode> treeNodes = new ArrayList<TreeNode>();
            //treeNodes.add(tree);
            //if (previousGroup != null) {
            //    treeNodes.add(previousGroup.getTreeForParameters());
            //}
            return tree;
        }

        return result;
    }

    private void generateExperimentData(Random rg, ArrayList<Parameter> parameters, int index, TreeNode tree, Distribution prior) {

        // base case
        int totalSize = parameters.size() - 1;  //one for "null"
        //int realIndex = 0;
        //if (index > 0) {
        //    realIndex = index - 1;
        //}

        if (index >= totalSize) {
            //if (paramPath.size()>15) {
            //for (Parameter par : paramPath)
            //    System.out.print(par.getName() + ", (" + par.getSimpleIndex() + ") ");
            //System.out.println();
            //System.out.println("Number of parameters this run: " + paramPath.size());
            //}
            int paramSize = paramPath.size();
            Parameter lastParameter = (Parameter) paramPath.get(paramSize - 1);

            if (lastParameter.getName().equals("OMEGA")) {
                paramPath.remove(paramSize - 1);
            }

            if (paramPath.isEmpty()) {
                return;
            }

            if (this.simulator != null) {
                // we allow the serach method to generate test data
                if (searchMethoGenerateExpereimentDataCounter < totalNumberofPaths) {
                    /*
                    if (searchMethoGenerateExpereimentDataCounter == 0) { //remove all excecpt the know path the first time                        
                        while (simulator.getCurrentSuccessfulPathIndices().size() > 1) {
                            simulator.getCurrentSuccessfulPathIndices().remove(1);
                        }
                    }
                    */
                    ArrayList<Integer> values = new ArrayList<Integer>();
                    for (Parameter parametersToTry1 : paramPath) {
                        values.add(parametersToTry1.getSimpleIndex());
                    }
                    if (!searchMethodGenerated.contains(values)) {
                        //SHIT, it does not work if we just add, we should call setCurrentSuccessfulPathIndices                        
                        //simulator.getCurrentSuccessfulPathIndices().add(values);
                        searchMethodGenerated.add(values);
                        searchMethoGenerateExpereimentDataCounter++;
                    }
                } else {
                    return;
                }
            }
            return;
        }

        int i = 0;

        //System.out.println("index is: " + index + "  i is: " + i);
        //do {
        int s = (totalSize + 1) - (index + 1);
        // zero tjhe prior
        prior.zero();
        // for each probability value
        for (int j = 0; j < s; j++) {
            prior.setProb(j, 1.0);//(double)(s-j));
        }
        // normalise the prior
        prior.normalise();

        //            a1
        //      a2      a3    a4
        //    a3 a4   a4
        //  a4   
        //randome number, treat the tree as the uniform distribution for now        
        if (tree != null) {
            i = index + 1 + tree.getDistribution().sampleWithTransition(transitionKernelVarianceWithinGroup);
        } else {
            i = index + 1 + prior.sampleWithTransition(uniformTransitionKernalVariance);
        }
        
        if (i==1) {
            System.out.println("wiered................");
        }

        //firstly check if the current index is the leaf, if it is, 
        //allowing search other nodes in other groups, otherwise, search within the group
        /*
         Parameter currParam = parameters.get(index);
         Group currentGroup = currParam.getGroupBelongTo();
         Parameter newParam = parameters.get(i);
         Group newGroup = newParam.getGroupBelongTo();
         if (!currentGroup.equals(newGroup)) {
            
         }
         */
        //} while (parameterIndicies.contains(i));
        //System.out.println("index is: " + index + "  i is: " + i);
        int indexOfChild = i - index - 1; //current one - previous one

        //System.out.println(i);
        // for ever parameter
        //for (int i = index; i < parameters.size(); i++) {
        // add parameter to list
        Parameter parameter = (Parameter) parameters.get(i);
        paramPath.add(parameter);
        // the importance weight variable
        double w;
        // get importance weight
        if (tree == null) {
            w = (double) (totalSize - i);
        } else {
            w = tree.getDistribution().getImportanceWeight(indexOfChild, null, weightMinTol);
        }
        // if (w >= 1) //we don't wanna end up having negative weight
        //     w = Math.log(w);
        // recurse
        if (tree != null) {
            generateExperimentData(rg, parameters, i, tree.getChildren()[indexOfChild], prior);
        } else {
            generateExperimentData(rg, parameters, i, null, prior);
        }

        // remove parameter from list         
        paramPath.remove(parameters.get(i));

    }

    private double calculateNewVariance(ArrayList<ArrayList<Integer>> differences) {
        if (!differences.isEmpty()) {
            DescriptiveStatistics ds = new DescriptiveStatistics();
            for (ArrayList<Integer> difference : differences) {
                DescriptiveStatistics eachNode = new DescriptiveStatistics();
                for (Integer difference1 : difference) {
                    eachNode.addValue(difference1);
                }
                ds.addValue(eachNode.getStandardDeviation());
            }
            return ds.getMean() * deviationFactor;
        } else {
            return 0;
        }

    }

    //what about the Brute force method
    private void bruteForceV2(boolean experiment, int size, long startTime) {
        long endTime;
        double duration = 0.0;
        
        for (int i = 1; i < size+1; i++) {            
            Combinations comb = new Combinations(size,i);
            Iterator<int[]> litr = comb.iterator();
            while(litr.hasNext()) {
                int[] element = litr.next();
                ArrayList<Integer> indices = new ArrayList<Integer>();
                for (int comb1 : element) {
                    indices.add(comb1+1);
                }                
                if (indices.size()>0 && this.simulator != null) {  //brute force is limited to simulation only
                    boolean accepted = this.simulator.invokeOperationV2(indices);
                    if (accepted) {
                        for (int j = 0; j < simulator.getCurrentSuccessfulPathIndices().size(); j++) {
                            if (indices.equals(simulator.getCurrentSuccessfulPathIndices().get(j))) {
                                stats[j] = abs(stats[j]) + 1; //record the occurences
                                break;
                            }
                        }
                    }
                    if (totalnubmerOfAttempts % logInterval == 0) {
                        endTime = System.currentTimeMillis();
                        duration = (endTime - startTime) / 1000;  //divide by 1000000 to get milliseconds
                        recordStats(experiment, knownSuccessPath.size(), true, duration);
                    }
                    totalnubmerOfAttempts++;
                }
            }
        }
    }
    
    //what about the Brute force method, this version works incredibly better, don't know why
    private void bruteForceV1(boolean experiment, ArrayList<Parameter> parameters, int start, long startTime) {
        long endTime;
        double duration = 0.0;
        //Combinations comb = new Combinations(10,5);
        
        for(int i = start; i < parameters.size(); ++i ){
            paramPath.add(parameters.get(i));
            //try the service with output            
            if (this.simulator != null) {  //brute force is limited to simulation only
                boolean accepted = this.simulator.invokeOperation(paramPath);
                if (accepted) {
                    ArrayList<Integer> indices = new ArrayList<Integer>();
                    for (Parameter parameter : paramPath) {
                        indices.add(parameter.getSimpleIndex());
                    }
                    
                    for (int j = 0; j < simulator.getCurrentSuccessfulPathIndices().size(); j++) {
                        if (indices.equals(simulator.getCurrentSuccessfulPathIndices().get(j))) {
                            stats[j] = abs(stats[j]) + 1; //record the occurences
                            break;
                        }
                    }
                }
            }
            
            if (totalnubmerOfAttempts % 100000000 == 0) {
                endTime = System.currentTimeMillis();
                duration = (endTime - startTime) / 1000;  //divide by 1000000 to get milliseconds
                recordStats(experiment, knownSuccessPath.size(), true, duration);
            }
            totalnubmerOfAttempts++;
            
            if ( i < parameters.size() )
                bruteForceV1(experiment, parameters, i + 1, startTime);            
            paramPath.remove(paramPath.size()-1);
        }

    }

    private TreeNode newDFS(Random rg, ArrayList<Parameter> parameters, int index, TreeNode tree, Distribution prior) {
        // base case
        int totalSize = parameters.size() - 1;  //one for "null"
        if (index >= totalSize) {
            int paramSize = paramPath.size();
            Parameter lastParameter = (Parameter) paramPath.get(paramSize - 1);
            if (lastParameter.getName().equals("OMEGA")) {
                paramPath.remove(paramSize - 1);
            }
            if (paramPath.isEmpty()) {
                return null;
            }
            
            if (debug.equals("TRUE")) {
                System.out.println("Parameters to try.....................");
                for (Parameter param : paramPath) {
                    System.out.print(param.getSimpleIndex()+",");
                }
                System.out.println();
            }
            if (this.simulator != null) {
                if (paramPath.containsAll(knownSuccessPath) && paramPath.size()>knownSuccessPath.size()) {
                    subtypeCounter++;
                    //System.out.println("Subtype number: "+ subtypeCounter);
                }
                
                boolean accepted = this.simulator.invokeOperation(paramPath);
                if (accepted) {
                    success = true;
                    ArrayList<Integer> indices = new ArrayList<Integer>();
                    for (Parameter parameter : paramPath) {
                        indices.add(parameter.getSimpleIndex());
                    }
                    for (int i = 0; i < simulator.getCurrentSuccessfulPathIndices().size(); i++) {
                        ArrayList<ArrayList<Integer>> differences = new ArrayList<ArrayList<Integer>>();
                        if (indices.equals(simulator.getCurrentSuccessfulPathIndices().get(i))) {                            
                            stats[i] = abs(stats[i]) + 1; //record the occurences                            
                            if (!visited[i]) {
                                newSuccess = true;
                                visited[i] =true;
                            }
                            /*
                            if (i==0 && stats[i] ==1) {
                                newSuccess = true;
                            } else {
                                int counter =0;
                                for (int j = 0; j < stats.length; j++) {
                                    if (abs(stats[j])>0) {
                                        counter++;
                                    }
                                }
                                if (counter==2) {
                                    secondSuccess = true; //second success ever
                                } else if (counter==3) {
                                    thirdSuccess = true; //second success ever
                                }
                            }
                            */
                            //once we found more than two path, we then calculate the variance dynamically
                            //compare the current one with the previous one
                            if (previousFoundIndex != null && previousFoundIndex != i && newSuccess) {
                                int biggerSize = simulator.getCurrentSuccessfulPathIndices().get(previousFoundIndex).size();
                                if (biggerSize>indices.size()) {
                                    for (int j = 0; j < simulator.getCurrentSuccessfulPathIndices().get(previousFoundIndex).size(); j++) {
                                        ArrayList<Integer> twoNumbers = new ArrayList<Integer>();                                        
                                        twoNumbers.add(simulator.getCurrentSuccessfulPathIndices().get(previousFoundIndex).get(j));
                                        if (j < indices.size())
                                            twoNumbers.add(indices.get(j));
                                        else
                                            twoNumbers.add(0);                                    
                                        differences.add(twoNumbers);
                                    }                                    
                                } else {
                                    for (int j = 0; j < indices.size(); j++) {
                                        ArrayList<Integer> twoNumbers = new ArrayList<Integer>();                                        
                                        twoNumbers.add(indices.get(j));
                                        if (j < simulator.getCurrentSuccessfulPathIndices().get(previousFoundIndex).size())
                                            twoNumbers.add(simulator.getCurrentSuccessfulPathIndices().get(previousFoundIndex).get(j));
                                        else
                                            twoNumbers.add(0);                                    
                                        differences.add(twoNumbers);
                                    }                                    
                                }                                
                                double tempVariance = calculateNewVariance(differences);
                                if (tempVariance != 0) {
                                    transitionKernelVarianceWithinGroup = tempVariance;
                                }
                                newSuccess = true;
                            }
                            previousFoundIndex = i;
                            break;
                        }
                    }
                    // create tree node
                    TreeNode tn = new TreeNode(lastParameter.getSimpleIndex(), totalSize, rg);
                    return tn;
                }
            } else if (this.service != null) {
                String responseMessage = null;
                /*
                 ArrayList<String> requestResponse = service.invokeServiceOperation(operation.getName(), paramPath);
                 if (requestResponse != null && requestResponse.size() == 2) {
                 responseMessage = requestResponse.get(1);
                 }
                 System.out.println(requestResponse.get(0));
                 System.out.println(requestResponse.get(1));
                 */
                //if (!responseMessage.contains("ERROR")) //record the path (combination) as accepted combination
                if (responseMessage != null && responseMessage.contains("successfully")) //record the path (combination) as accepted combination
                //there may be many such combinations
                {
                    // create tree node
                    TreeNode tn = new TreeNode(lastParameter.getSimpleIndex(), totalSize, rg);
                    return tn;
                    //Combination combination = new Combination(minimumParameters, requestResponse.get(0), responseMessage);
                    //operation.setAccecptedParameterSets(combination);
                    //book.addPath(minimumPath);
                    //list = updateList(responseMessage, path, list, max);
                }
            }
            return null;
        }

        int i = 0;

        int s = (totalSize + 1) - (index + 1);
        // zero tjhe prior
        prior.zero();
        // for each probability value
        for (int j = 0; j < s; j++) {
            prior.setProb(j, 1.0);//(double)(s-j));
        }
        prior.normalise();
        //randome number, treat the tree as the uniform distribution for now        
        if (tree != null) {
            i = index + 1 + tree.getDistribution().sampleWithTransition(transitionKernelVarianceWithinGroup);
        } else {
            i = index + 1 + prior.sampleWithTransition(uniformTransitionKernalVariance);
        }

        if (i==1) {
            System.out.println("wiered................");
        }
        
        int indexOfChild = i - index - 1; //current one - previous one

        // add parameter to list
        Parameter parameter = (Parameter) parameters.get(i);
        paramPath.add(parameter);
        TreeNode result;
        // the importance weight variable
        double w;
        // get importance weight
        if (tree == null) {
            w = (double) (totalSize - i);
        } else {
            w = tree.getDistribution().getImportanceWeight(indexOfChild, null, weightMinTol);
        }
        // if (w >= 1) //we don't wanna end up having negative weight
        //     w = Math.log(w);
        // recurse
        if (tree != null) {
            result = newDFS(rg, parameters, i, tree.getChildren()[indexOfChild], prior);
        } else {
            result = newDFS(rg, parameters, i, null, prior);
        }

        // remove parameter from list         
        paramPath.remove(parameters.get(i));
                
        if (result != null) {
            // if the tree is null
            if (tree == null) {
                // create tree node
                TreeNode tn = new TreeNode(index, totalSize, rg);
                if ((!(result.children.length == 1 && result.children[0]==null && i== totalSize))) {
                // add the result child                
                    tn.getChildren()[indexOfChild] = result;
                    //tn.updatePastDistribution();
                    // update the distribution
                    tn.getDistribution().updatePDF(indexOfChild, w);
                }

                //if the probability of current attribute (indexofChild) given the previous one (index) is greater than the threshold
                // we record it in the markov blanket - a table
                if (i != totalSize) { //we don't record markov blanket for OMEGA
                    Parameter currentParameter = parameters.get(i);
                    double currentProbability = tn.getDistribution().probability(indexOfChild);
                    if (currentProbability > markovBlanketThreshHold) {  //to test this                    
                        int givenIndex = parameters.get(index).getSimpleIndex();
                        Integer originalFrequency = (Integer) currentParameter.getMarkovBlanket().get(givenIndex);
                        if ((originalFrequency == null)) // if the new probability is greater, then replace the old one
                        {
                            currentParameter.getMarkovBlanket().put(givenIndex, 1);
                        } else {
                            currentParameter.getMarkovBlanket().put(givenIndex, originalFrequency + 1);
                        }
                    }
                }
                return tn;
            }

            if (tree.getChildren()[indexOfChild] == null && (!(result.children.length == 1 && result.children[0]==null && i== totalSize))) {
                // add the result child
                tree.getChildren()[indexOfChild] = result;
            }
            if ((!(result.children.length == 1 && result.children[0]==null && i== totalSize))) {
                tree.updatePastDistribution();
                // update the distribution
                tree.getDistribution().updatePDF(indexOfChild, w);
            }

            //if the probability of current attribute (indexofChild) given the previous one (index) is greater than the threshold
            // we record it in the markov blanket - a table
            if (i != totalSize) { //we don't record markov blanket for OMEGA
                Parameter currentParameter = parameters.get(i);
                double currentProbability = tree.getDistribution().probability(indexOfChild);
                if (currentProbability > markovBlanketThreshHold) {  //to test this
                    Integer givenIndex = parameters.get(index).getSimpleIndex();
                    Integer originalFrequency = (Integer) currentParameter.getMarkovBlanket().get(givenIndex);
                    if ((originalFrequency == null)) // if the new probability is greater, then replace the old one
                    {
                        currentParameter.getMarkovBlanket().put(givenIndex, 1);
                    } //System.out.println(parameters.get(i).getName()+" "+ key + " "+ parameters.get(i).getMarkovBlanket().get(key));
                    else {
                        currentParameter.getMarkovBlanket().put(givenIndex, originalFrequency + 1);
                    }
                }
            }
            return tree;
        }

        return result;
    }

    private TreeNode initialiseParamPath(int totalSize, TreeNode tree, int i, Random rg) {
        TreeNode tempTree = tree;
        if (i < knownSuccessPath.size()) {
            int index = knownSuccessPath.get(i).getSimpleIndex();
            int indexForPrevious = 0;
            //if (i > 0) {// exclude the first element, which is null
            //indexForPrevious = index - knownSuccessPath.get(i - 1).getSimpleIndex();
            if (i > 0) //indexForPrevious = knownSuccessPath.get(i - 1).getSimpleIndex();
            {
                indexForPrevious = index - knownSuccessPath.get(i - 1).getSimpleIndex() - 1;
            }
            tempTree = new TreeNode(index, totalSize, rg);
            if (knownSuccessPath.get(i).equals(minimumParameters.get(minimumParameters.size() - 1))) // set where minimum parameters stop in the known success path
            {
                initialTree.setLatestNode(tempTree);
            }
            tree.getChildren()[indexForPrevious] = tempTree;
            tree.getDistribution().updatePDF(indexForPrevious, initialParameterProbabilityFactor * tree.getDistribution().getSize()); //weight 1.5 will make the indexForPrevious 3 times of others
            //tree.getDistribution().updatePDFwithNeighbours(indexForPrevious, 1.5, 5); //weight 1.5 will make the indexForPrevious 3 times of others
            //maybe we should make the neighbours highly likely by updating the distribution?
            // }
            if (minimumParameters.contains(knownSuccessPath.get(i))) //only add the minimum parameters
            {
                paramPath.add(knownSuccessPath.get(i));
            }
            i = i + 1;
            tempTree = initialiseParamPath(totalSize, tempTree, i, rg);
        }
        return tempTree;
    }

    private void recordStats(boolean experiment, int height, boolean normal, double duration) {
        String thingsToPrint = null;
        DateTime now = new DateTime();
        java.util.Date date = now.toDate();
        if (bruteForce.equals("TRUE"))
            thingsToPrint = "\nTEST--------------------------BRUTE FORCE ------------------------------- Time: " + date.toString() + "\n";            
        else 
            thingsToPrint = "\nTEST-------------------------------------------------------------- Time: " + date.toString() + "\n";
        

        if (experiment) {
            if (normal) {
                thingsToPrint = thingsToPrint + "Normal distribution-------------------------------------------------------------------\n";
            } else {
                thingsToPrint = thingsToPrint + "Uniform distribution-------------------------------------------------------------------\n";
            }
        } else {
            thingsToPrint = thingsToPrint + "Real Service--" + service.getServiceName() + "-------------------------------------------\n";
        }

        thingsToPrint = thingsToPrint + "The statistics for the height " + height + " are as follows: \n";        
        thingsToPrint = thingsToPrint + "All groups are : \n";
        for (Group group : allGroups) {
            thingsToPrint = thingsToPrint + group.getGroupNumber() + " [";
            for (Parameter param : group.getParameters()) {
                thingsToPrint = thingsToPrint + param.getSimpleIndex() + ",";
            }
            thingsToPrint = thingsToPrint + "],";
        }
        
        thingsToPrint = thingsToPrint + "\nThe known group path is : \n";
        for (Group group : knownGroups) {
            thingsToPrint = thingsToPrint + group.getGroupNumber() + " [";
            for (Parameter param : group.getParameters()) {
                thingsToPrint = thingsToPrint + param.getSimpleIndex() + ",";
            }
            thingsToPrint = thingsToPrint + "],";
        }
        
        thingsToPrint = thingsToPrint + "\nThe known path is : \n";
        for (Parameter param: knownSuccessPath) {
            thingsToPrint = thingsToPrint + param.getSimpleIndex() + ",";
        }
        
        thingsToPrint = thingsToPrint + "\n the occurences are: \n";
        int counter = 0;
        long hits = 0;
        String identifiedIndicies = "[";
        //for (int k = 0; k < simulator.getCurrentSuccessfulPathIndices().size(); k++) {
        for (int k = 0; k < stats.length; k++) {
            thingsToPrint = thingsToPrint + "The " + (k + 1) + " th combination : "
                    + simulator.getCurrentSuccessfulPathIndices().get(k) + ", occurences: " + stats[k] + "\n";
            if (abs(stats[k]) > 0) {
                hits = hits + abs(stats[k]);
                identifiedIndicies = identifiedIndicies + (k+1)+",";
                counter = counter + 1;
            }
            //stats[k] = 0;  //reset;  //why reset
        }        
        identifiedIndicies = identifiedIndicies+"]";
        
        DecimalFormat df = new DecimalFormat("####0.00");
        double successPercentage = ((double) counter / (double) stats.length) * 100;
        double hitPercentage = 0;
        if (totalnubmerOfAttempts != 0) {
            hitPercentage = ((double) hits / (double) totalnubmerOfAttempts) * 100;
        }

        thingsToPrint = thingsToPrint + "the percentage of the succeeful paths is: " + counter + "/" + stats.length + "=" + df.format(successPercentage)
                + "% the total time taken: " + duration + " seconds.\n";
        thingsToPrint = thingsToPrint + "the identified indicies are: " + identifiedIndicies+ "\n";
        thingsToPrint = thingsToPrint + "the percentage of the hits is: " + hits + "/" + totalnubmerOfAttempts + "=" + df.format(hitPercentage) + "%\n";
        thingsToPrint = thingsToPrint + "the configuration parameters for this run are: \n";
        thingsToPrint = thingsToPrint + "total number of attemps: " + totalnubmerOfAttempts + " Transition kernel variance for Group: "
                + transitionKernelVarianceForGroup + " Transition kernel variance within Group: " + transitionKernelVarianceWithinGroup;
        thingsToPrint = thingsToPrint + " Uniform variance: " + uniformTransitionKernalVariance + " Weight minimual tol: " + weightMinTol + " \n";
        thingsToPrint = thingsToPrint + "Intial group probability factor: " + initialGroupProbabilityFactor
                + " Initial parameter factor " + initialParameterProbabilityFactor + " deviation for Experiments data: " + experimentsValueDeviation + "\n";
        thingsToPrint = thingsToPrint +"Subtype number: "+ subtypeCounter+"\n";
        
        if (logFileName != null) //overwrite the log file
        {
            writeFile("output/" + logFileName, thingsToPrint, true);
        } else {
            writeFile("output/montecarlo.stats", thingsToPrint, true);
        }
    }

    private TreeNode getNode(TreeNode tree, int index) {
        TreeNode node = null;

        int childCount = tree.getChildren().length;

        for (int i = 0; i < childCount; i++) {
            TreeNode childNode = (TreeNode) tree.getChildren()[i];
            if (childNode != null) {
                if (childNode.getReferenceNumber() == index) {
                    return childNode;
                } else if (childNode.getChildren().length > 0) {
                    getNode(childNode, index);
                }
            }
        }
        return node;
    }

    private void searchThePath(boolean experiment, ArrayList<Parameter> parameters) {
        long endTime, startTime;
        double duration = 0.0;
        startTime = System.currentTimeMillis();
        Random randomGenerator = new Random(System.currentTimeMillis());

        ArrayList<Parameter> allParameters = new ArrayList<Parameter>();
        int index = 0;
        if (!bruteForce.equals("TRUE")) {
            Parameter initialOne = new Parameter();
            initialOne.setName("null");
            initialOne.setSimpleIndex(index);
            allParameters.add(initialOne);
        }
        
        //ArrayList<Parameter> inputParameters = parameters;
        for (Parameter parameter : parameters) {
            index = index + 1;  // change the index to start with 1 in order to suit the legacy code
            parameter.setSimpleIndex(index);
            if (parameter.isCompulsory() && minimumParameters.size() < 4) {
                minimumParameters.add(parameter);
            }
            allParameters.add(parameter);
        }
        if (!bruteForce.equals("TRUE")) {
            Parameter omega = new Parameter();
            omega.setName("OMEGA");
            omega.setSimpleIndex(index);
            allParameters.add(omega);
        }
        for (Parameter parameter : knownSuccessPath) {
            Group group = parameter.getGroupBelongTo();
            if (!knownGroups.contains(group)) {
                knownGroups.add(group);
            }
        }

        for (Parameter parameter : minimumParameters) {
            Group group = parameter.getGroupBelongTo();
            if (!minimumGroups.contains(group)) {
                //We should only take groups whose every parameter is in the minumParameters
                //TODO further check if every parameter in the same group appears in the minimumParameters
                minimumGroups.add(group);
            };
        }
        Distribution prior =null;
                
        if (!bruteForce.equals("TRUE")) {
            //GroupTreeNode treeForGroups = new GroupTreeNode(0, numberOfGroups, randomGenerator);
            //treeForGroups.initialise(knownGroups, knownSuccessPath, numberOfGroups, 0, randomGenerator);        
            initialTree = new TreeNode(0, allParameters.size() - 1, randomGenerator);
            initialiseParamPath(allParameters.size() - 1, initialTree, 0, randomGenerator);

            if (debug.equals("TRUE")) {
                String thingsToPrint = "Known groups:\n";
                for (Group group : knownGroups) {
                    thingsToPrint = thingsToPrint + group.getGroupNumber() + ",";
                }
                thingsToPrint = thingsToPrint + "\n" + "Known parameters:\n";
                for (Parameter param : knownSuccessPath) {
                    thingsToPrint = thingsToPrint + param.getSimpleIndex() + ",";
                }
                System.out.print(thingsToPrint + "\n");
                thingsToPrint = thingsToPrint + "\n" + initialTree.print();
                writeFile("output/treeGenerated.stats", thingsToPrint, false);
            }

            prior = new Distribution(allParameters.size(), randomGenerator);
            
            ArrayList<Integer> values = new ArrayList<Integer>();
            for (Parameter parametersToTry1 : knownSuccessPath) {
                values.add(parametersToTry1.getSimpleIndex());
            }
            searchMethodGenerated.add(values); //add the knownpath                    
                    
            if (searchMethoGenerateExpereimentData.equals("TRUE")) {
                for (int j = 0; j < totalnubmerOfAttempts; j++) { //generate the expiriment data first 
                    if (searchMethoGenerateExpereimentDataCounter >= totalNumberofPaths - 1) {
                        break;
                    }
                    for (Parameter parameter : minimumParameters) {
                        if (!paramPath.contains(parameter))
                            paramPath.add(parameter);
                    }                    
                    
                    //generateExperimentData(randomGenerator, allParameters, minimumParameters.get(minimumParameters.size() - 1).getSimpleIndex(), initialTree.getLatestNode(), prior); //start with index 1, as 0 is null
                    generateExperimentData(randomGenerator, allParameters, minimumParameters.get(minimumParameters.size() - 1).getSimpleIndex(), initialTree.getLatestNode(), prior); //start with index 1, as 0 is null
                    paramPath = new ArrayList<Parameter>();
                }
                simulator.setCurrentSuccessfulPathIndices(searchMethodGenerated);
            }
        }

        totalnubmerOfAttempts = 0;

        //for (int j = 0; j < totalnubmerOfAttempts; j++) {
        while (true) {  //make it run infinitely
            if (bruteForce.equals("TRUE")) //do brute force search - this is for evaluation purpose only
            {
                paramPath = new ArrayList<Parameter>();
                bruteForceV2(experiment, allParameters.size(), startTime);
            } else {
                for (Parameter parameter : minimumParameters) {
                    if (!paramPath.contains(parameter))
                        paramPath.add(parameter);
                }
                newDFS(randomGenerator, allParameters, minimumParameters.get(minimumParameters.size() - 1).getSimpleIndex(), initialTree.getLatestNode(), prior); //start with index 1, as 0 is null            
                
                if (!moderate) {  //only moderate once
                    for (int k = 0; k < stats.length; k++) {
                        if (stats[k]>1000000) {
                            transitionKernelVarianceWithinGroup =1;
                            moderate = true;
                            break;
                        }                    
                        if (stats[k]>500000) {
                            transitionKernelVarianceWithinGroup =0.75;
                            moderate = true;                            
                            break;
                        }                    
                        if (stats[k]>100000) {
                            transitionKernelVarianceWithinGroup =0.5;
                            moderate = true;                            
                            break;
                        }
                    }
                }
                
                if (debug.equals("TRUE")) {
                    System.out.println("The " + totalnubmerOfAttempts + " th " + " attempts, successful: " + success);
                }
                //double firstSuccessTransitionKernelVarianceWithinGroup = 0;
                //double fistFailureTransitionKernelVarianceWithinGroup = 0;
                //boolean previousStatus = false;
                //if (secondSuccess || thirdSuccess || newSuccess) { // if success, keep record of the transition kernel variance
                if (newSuccess) { // if success, keep record of the transition kernel variance
                    endTime = System.currentTimeMillis();
                    duration = (endTime - startTime) / 1000;
                    recordStats(experiment, knownSuccessPath.size(), true, duration);
                    //transitionKernelVarianceWithinGroup =0.625;
                    //firstSuccessTransitionKernelVarianceWithinGroup = transitionKernelVarianceWithinGroup;
                    //previousStatus = true;
                    //} else if (previousStatus){
                    //fistFailureTransitionKernelVarianceWithinGroup = transitionKernelVarianceWithinGroup;                    
                }
                /*
                if (secondSuccess) {
                    secondTransitionKernelVariance = transitionKernelVarianceWithinGroup;
                }
                if (thirdSuccess) {
                    thirdTransitionKernelVariance = transitionKernelVarianceWithinGroup;
                    transitionKernelVarianceWithinGroup = (thirdTransitionKernelVariance-secondTransitionKernelVariance)/2+secondTransitionKernelVariance;
                }
                */
                
                if (debug.equals("TRUE") && success) {
                    String thingsToPrint = "RESULT " + totalnubmerOfAttempts + "------------SUCCESSFUL-----------------------------------\n" + initialTree.print();
                    writeFile("output/treeGenerated.stats", thingsToPrint, true);
                }
                success = false;
                if (totalnubmerOfAttempts % logInterval == 0) {
                    endTime = System.currentTimeMillis();
                    duration = (endTime - startTime) / 1000;  //divide by 1000000 to get milliseconds
                    recordStats(experiment, knownSuccessPath.size(), true, duration);
                    //transitionKernelVarianceWithinGroup =0.625;
                    
                    /*
                    if (!thirdSuccess && transitionKernelVarianceWithinGroup<3) { //raise the variance after the first success, otherwise the search method sticks to the known path all the time
                       double maxtransition = transitionKernelVarianceWithinGroup+transitionKernelVarianceWithinGroup+0.25;
                       if (maxtransition>3)
                           transitionKernelVarianceWithinGroup = 3;
                       else
                           transitionKernelVarianceWithinGroup = maxtransition;
                       //transitionKernelVarianceWithinGroup = 2;
                    }
                    
                    */
                    
                    /*
                    if (!thirdSuccess && transitionKernelVarianceWithinGroup<10) { //raise the variance after the first success, otherwise the search method sticks to the known path all the time
                       double maxtransition = transitionKernelVarianceWithinGroup+transitionKernelVarianceWithinGroup*5;
                       if (maxtransition>10)
                           transitionKernelVarianceWithinGroup = 10;
                       else
                           transitionKernelVarianceWithinGroup = maxtransition;
                       //transitionKernelVarianceWithinGroup = 2;
                    }
                    */
                    //transitionKernelVarianceWithinGroup = transitionKernelVarianceWithinGroup+transitionKernelVarianceWithinGroup*2;
                }
                /*
                if (totalnubmerOfAttempts % 100000000 == 0) {
                    transitionKernelVarianceWithinGroup = transitionKernelVarianceWithinGroup+transitionKernelVarianceWithinGroup*2;
                }
                */
                //secondSuccess = false;
                newSuccess = false;                
                totalnubmerOfAttempts++;
                paramPath = new ArrayList<Parameter>();
            }
        }
    }

    private void discoverSuccessfulPath(boolean experiment) {
        if (experiment) {
            simulator.generateSuccessfulPaths(true);  //normal distribution
            ArrayList<ArrayList<ArrayList<Parameter>>> experiments = simulator.getSuccessfulPathIndices();
            for (ArrayList<ArrayList<Parameter>> experiment2 : experiments) {
                ArrayList<ArrayList<Integer>> successfulIndices = new ArrayList<ArrayList<Integer>>();
                for (ArrayList<Parameter> experiment1 : experiment2) {
                    ArrayList<Integer> integers = new ArrayList<Integer>();
                    for (Parameter experiment11 : experiment1) {
                        integers.add(experiment11.getSimpleIndex());
                    }
                    successfulIndices.add(integers);
                }
                simulator.setCurrentSuccessfulPathIndices(successfulIndices);
                knownSuccessPath = experiment2.get(0);
                allGroups = simulator.getGroups();
                //if (realService.equals("TRUE"))
                //    simulator.setCurrentSuccessfulPathIndices(null); //using the static realService list                        
                searchThePath(experiment, simulator.getParams());
                resetPaths();
            }
        } else {
            simulator.setCurrentSuccessfulPathIndices(null); //using the static realService list
            knownSuccessPath = operation.getKnownPath(null);
            allGroups = operation.getGroups();
            searchThePath(experiment, operation.getSimpleInputParameterList());
        }

    }

    private Book discoverSuccessfulPath() {
        //ArrayList<Parameter> minimumParameters = operation.getCompulsoryInputParameterList();        
        ArrayList<Parameter> allParameters = new ArrayList<Parameter>();

        int index = 0;
        Parameter initialOne = new Parameter();
        initialOne.setName("null");
        initialOne.setSimpleIndex(index);
        allParameters.add(initialOne);
        Random randomGenerator = new Random(System.currentTimeMillis());

        ArrayList<Parameter> inputParameters = operation.getSimpleInputParameterList();
        for (Parameter parameter : inputParameters) {
            index = index + 1;
            parameter.setSimpleIndex(index);
            if (parameter.isCompulsory()) {
                minimumParameters.add(parameter);
                knownSuccessPath.add(parameter);
            }
            if (parameter.getSimpleIndex() == 13) {
                knownSuccessPath.add(parameter);
            } else if (parameter.getSimpleIndex() == 20) {
                knownSuccessPath.add(parameter);
            } else if (parameter.getSimpleIndex() == 32) {
                knownSuccessPath.add(parameter);
            }
            allParameters.add(parameter);
        }

        index = index + 1;
        Parameter omega = new Parameter();
        omega.setName("OMEGA");
        omega.setSimpleIndex(index);
        allParameters.add(omega);

        initialTree = new TreeNode(0, allParameters.size() - 1, randomGenerator);
        TreeNode latestNode = initialiseParamPath(allParameters.size() - 1, initialTree, 0, randomGenerator);
        Distribution prior = new Distribution(allParameters.size(), randomGenerator);
        for (int j = 0; j < 1000; j++) {
            newDFS(randomGenerator, allParameters, minimumParameters.get(minimumParameters.size() - 1).getSimpleIndex(), initialTree.getLatestNode(), prior); //start with index 1, as 0 is null
        }

        /*
         System.out.println("the occurences are: ");
         for (int i = 0; i < stats.length; i++) {
         System.out.println("the " + (i + 1) + " th combination : " + Arrays.toString(ServiceSimulator.successfulPathIndices[i]) + ", occurences: " + stats[i]);
         }
         */
        //for (ArrayList<Parameter> parameters : paths) {
        //    for (Parameter parameter : parameters) {
        //        System.out.print(parameter.getName() + ",");
        //    }
        //    System.out.println("-----------------");
        //}
        //ArrayList<Parameter> specialStrings = formSpecialStrings(minimumParameters);

        /*add all associated complex parameters
         Parameter parentParameter = chosenParameter.getParentParameter();
         while (parentParameter.getParameterUniqueIDinTree() != 0 && parentParameter != null && !path.contains(parentParameter)) {
         path.add(parentParameter);
         parentParameter = parentParameter.getParentParameter();
         }
         path.add(chosenParameter);
         */
        //ArrayList<Parameter> results = new ArrayList<Parameter>();
        //dfs(specialStrings, specialStrings.get(0), 4, results, 50);
        //for (Parameter parameter : results) {
        //    System.out.println(parameter.getName()+",");
        // }
        /*
         int pathNumber = 1;
         double power = pow(minimumParameters.size(),2)-1;
         while (pathNumber < power) {
            
         ArrayList<Parameter> results = new ArrayList<Parameter>();
         results = dfs(specialStrings, specialStrings.get(0), 10, results, pathNumber);
         pathCounter =0;
         //Parameter specialOne = new Parameter();
         //specialOne.setName("OMEGA");
         //results.add(specialOne);

         //System.out.println("level: " + depth + " postion " + counter + " value: "+ parameter.getName());

         System.out.println("path "+ pathNumber+"--------");
         for (Parameter parameter : results) {
         System.out.print(parameter.getName()+",");
         }
         System.out.println("path "+ pathNumber+"--------");
         pathNumber = pathNumber+1;
            
         }
         */
        Book book = new Book();
        /*
         ArrayList<String> requestResponse = service.invokeServiceOperation(operation.getName(), minimumParameters);
        
         if (requestResponse != null && requestResponse.size() == 2) {
         responseMessage = requestResponse.get(1);
         }
         //if (!responseMessage.contains("ERROR")) //record the path (combination) as accepted combination
         if (responseMessage != null && responseMessage.contains("successfully")) //record the path (combination) as accepted combination
         //there may be many such combinations
         {
         Combination combination = new Combination(minimumParameters, requestResponse.get(0), responseMessage);
         operation.setAccecptedParameterSets(combination);
         book.addPath(minimumPath);
         //list = updateList(responseMessage, path, list, max);
         } 
         */
        return book;
    }

    private void searchForParameterUnderGroupWithoutUpdateTree(Random rg, ArrayList<Parameter> parametersUnderGroup, int index, TreeNode treeForParameters, Distribution priorForParameters, int lowerIndex, int upperIndex) {
        // base case
        if (index >= upperIndex) {

            int paramSize = parametersPathUnderGroupPath.size();
            Parameter lastParameter = (Parameter) parametersPathUnderGroupPath.get(paramSize - 1);
            if (lastParameter.getName().equals("OMEGA")) {
                parametersPathUnderGroupPath.remove(paramSize - 1);
            }
            return;
        }

        int i = 0;

        //System.out.println("index is: " + index + "  i is: " + i);
        //do {
        int s = upperIndex - (index + 1);
        // zero tjhe prior
        priorForParameters.zero();
        // for each probability value
        for (int j = 0; j < s; j++) {
            priorForParameters.setProb(j, 1.0);//(double)(s-j));
        }
        // normalise the prior
        priorForParameters.normalise();
        //randome number, treat the tree as the uniform distribution for now        
        if (treeForParameters != null) {
            i = index + 1 + treeForParameters.getDistribution().sampleWithTransition(transitionKernelVarianceWithinGroup);
        } else {
            i = index + 1 + priorForParameters.sampleWithTransition(uniformTransitionKernalVariance);
        }

        //} while (parameterIndicies.contains(i));
        //System.out.println("index is: " + index + "  i is: " + i);
        int indexOfChild = i - index - 1; //current one - previous one

        //System.out.println(i);
        // for ever parameter
        //for (int i = index; i < parameters.size(); i++) {
        // add parameter to list
        Parameter parameter = null;
        if (i - lowerIndex >= parametersUnderGroup.size()) {
            parameter = parametersUnderGroup.get(parametersUnderGroup.size() - 1);
        } else {
            parameter = parametersUnderGroup.get(i - lowerIndex);
        }

        parametersPathUnderGroupPath.add(parameter);
        // the importance weight variable
        double w;
        // get importance weight
        if (treeForParameters == null) {
            w = (double) (upperIndex - 1 - i);
        } else {
            w = treeForParameters.getDistribution().getImportanceWeight(indexOfChild, null, weightMinTol);
        }

        // if (w >= 1) //we don't wanna end up having negative weight
        //     w = Math.log(w);
        // recurse
        if (treeForParameters != null) {
            searchForParameterUnderGroupWithoutUpdateTree(rg, parametersUnderGroup, i,
                    treeForParameters.getChildren()[indexOfChild], priorForParameters, lowerIndex, upperIndex);
        } else {
            searchForParameterUnderGroupWithoutUpdateTree(rg, parametersUnderGroup, i,
                    null, priorForParameters, lowerIndex, upperIndex);
        }
    }

    //private TreeNode searchForParameterUnderGroup(Random rg, ArrayList<Parameter> parametersUnderGroup, int index, TreeNode treeForParameters, Distribution priorForParameters, int lowerIndex, int upperIndex) {
    private TreeNode searchForParameterUnderGroup(Random rg, ArrayList<Parameter> parametersUnderGroup, int index, TreeNode treeForParameters, Distribution priorForParameters, int baseLine) {
        int totalSize = parametersUnderGroup.size() - 1; //one for null
        TreeNode result;
        //int totalSize = parametersUnderGroup.size()-1;
        // base case
        if ((index - baseLine) >= totalSize) {
            int paramSize = parametersPathUnderGroupPath.size();
            Parameter lastParameter = (Parameter) parametersPathUnderGroupPath.get(paramSize - 1);
            if (lastParameter.getName().equals("OMEGA")) {
                parametersPathUnderGroupPath.remove(paramSize - 1);
            }
            if (parametersPathUnderGroupPath.isEmpty()) {
                return null;
            }

            // we should really try these parameters here
            //reference should be the simple index of each parameter
            if (lastParameter.getSimpleIndex() > totalSize + baseLine) {
                System.out.print("wrong.................also the reference number is wrong............................");
            }
            TreeNode tn = new TreeNode(lastParameter.getSimpleIndex(), baseLine + totalSize, rg);  //should have zero child when it is OMEGA
            //should not set the reference of OMEGA, as it is incorrect for group

            //if (lastParameter.getSimpleIndex()==upperIndex)
            //    return null; //is this right?
            return tn;
        }

        int i = 0;

        //System.out.println("index is: " + index + "  i is: " + i);
        //do {
        int s = totalSize - (index - baseLine);
        // zero tjhe prior
        priorForParameters.zero();
        // for each probability value
        for (int j = 0; j < s; j++) {
            priorForParameters.setProb(j, 1.0);//(double)(s-j));
        }
        int sample = 0;
        // normalise the prior
        priorForParameters.normalise();
        //randome number, treat the tree as the uniform distribution for now                
        if (treeForParameters != null) {
            sample = treeForParameters.getDistribution().sampleWithTransition(transitionKernelVarianceWithinGroup);
            i = index + 1 + sample;
        } else {
            sample = priorForParameters.sampleWithTransition(uniformTransitionKernalVarianceGroupParameter);
            i = index + 1 + sample;
        }

        int indexOfChild = i - index - 1; //current one - previous one

        if (i - baseLine >= parametersUnderGroup.size()) {
            System.out.println("WRONG.............................");
        }

        //System.out.println(i);
        // for ever parameter
        //for (int i = index; i < parameters.size(); i++) {
        // add parameter to list
        Parameter parameter = parametersUnderGroup.get(i - baseLine);

        parametersPathUnderGroupPath.add(parameter);
        // the importance weight variable
        double w;
        // get importance weight
        if (treeForParameters == null) {
            w = (double) (totalSize - (i - baseLine));
        } else {
            w = treeForParameters.getDistribution().getImportanceWeight(indexOfChild, null, weightMinTol);
        }

        // if (w >= 1) //we don't wanna end up having negative weight
        //     w = Math.log(w);
        // recurse
        if (treeForParameters != null) {
            result = searchForParameterUnderGroup(rg, parametersUnderGroup, i,
                    treeForParameters.getChildren()[indexOfChild], priorForParameters, baseLine);
        } else {
            result = searchForParameterUnderGroup(rg, parametersUnderGroup, i,
                    null, priorForParameters, baseLine);
        }

        // remove parameter from list         
        //paramPath.remove(parameters.get(i));
        if (result != null) {
            // if the tree is null
            if (treeForParameters == null) {
                // create tree node
                TreeNode tn = new TreeNode(index, baseLine + totalSize, rg);
                // add the result child
                tn.getChildren()[indexOfChild] = result;
                //tn.updatePastDistribution();
                // update the distribution                
                tn.getDistribution().updatePDF(indexOfChild, w);

                //if the probability of current attribute (indexofChild) given the previous one (index) is greater than the threshold
                // we record it in the markov blanket - a table
                if (i - baseLine != totalSize - 1) { //we don't record markov blanket for OMEGA
                    Parameter currentParameter = parametersUnderGroup.get(i - baseLine);
                    double currentProbability = tn.getDistribution().probability(indexOfChild);
                    if (currentProbability > markovBlanketThreshHold) {  //to test this                    
                        int givenIndex = parametersUnderGroup.get(index - baseLine).getSimpleIndex();
                        Integer originalFrequency = (Integer) currentParameter.getMarkovBlanket().get(givenIndex);
                        if ((originalFrequency == null)) // if the new probability is greater, then replace the old one
                        {
                            currentParameter.getMarkovBlanket().put(givenIndex, 1);
                        } else {
                            currentParameter.getMarkovBlanket().put(givenIndex, originalFrequency + 1);
                        }
                    }
                }

                //tn.getDistribution().updatePDFwithNeighbours(indexOfChild, w, 5);
                //System.out.println("update the "+indexOfChild+" th child of tree (" + tn + ") with tree "+ result);
                // return
                return tn;
            }

            //if (treeForParameters.getChildren()[indexOfChild] == null) {
            // add the result child
            treeForParameters.getChildren()[indexOfChild] = result;
            //}
            treeForParameters.updatePastDistribution();
            // update the distribution
            treeForParameters.getDistribution().updatePDF(indexOfChild, w);

            //if the probability of current attribute (indexofChild) given the previous one (index) is greater than the threshold
            // we record it in the markov blanket - a table
            if (i - baseLine != totalSize - 1) { //we don't record markov blanket for OMEGA
                Parameter currentParameter = parametersUnderGroup.get(i - baseLine);
                double currentProbability = treeForParameters.getDistribution().probability(indexOfChild);
                if (currentProbability > markovBlanketThreshHold) { //to test this
                    Integer givenIndex = parametersUnderGroup.get(index - baseLine).getSimpleIndex();
                    Integer originalFrequency = (Integer) currentParameter.getMarkovBlanket().get(givenIndex);
                    if ((originalFrequency == null)) // if the new probability is greater, then replace the old one
                    {
                        currentParameter.getMarkovBlanket().put(givenIndex, 1);
                    } //System.out.println(parameters.get(i).getName()+" "+ key + " "+ parameters.get(i).getMarkovBlanket().get(key));
                    else {
                        currentParameter.getMarkovBlanket().put(givenIndex, originalFrequency + 1);
                    }
                }
            }
            //tree.getDistribution().updatePDFwithNeighbours(indexOfChild, w, 5);
            //System.out.println("update the "+indexOfChild+" th child of tree (" + tree + ") with tree "+ result);
            // return
            return treeForParameters;
        }

        return result;
        //}

    }

    private void resetPaths() {
        knownGroups = new ArrayList<Group>();
        allGroups = new ArrayList<Group>();
        minimumGroups = new ArrayList<Group>();
        knownSuccessPath = new ArrayList<Parameter>();        
        minimumParameters = new ArrayList<Parameter>();
        groupPath = new ArrayList<Group>();
        parametersPathUnderGroupPath = new ArrayList<Parameter>();
    }

}
