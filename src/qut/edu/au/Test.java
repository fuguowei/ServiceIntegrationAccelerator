/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import static com.google.common.base.Predicates.in;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import de.wdilab.coma.insert.InsertParser;
import de.wdilab.coma.insert.metadata.CSVParser;
import de.wdilab.coma.insert.metadata.OWLParser_V3;
import de.wdilab.coma.insert.metadata.SQLParser;
import de.wdilab.coma.insert.metadata.XDRParser;
import de.wdilab.coma.insert.metadata.XSDParser;
import de.wdilab.coma.matching.Resolution;
import de.wdilab.coma.matching.Strategy;
import de.wdilab.coma.matching.Workflow;
import de.wdilab.coma.matching.execution.ExecWorkflow;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.Source;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import qut.edu.au.entities.BusinessEntity;
import qut.edu.au.entities.EntityPair;
import qut.edu.au.introspection.TreeNode;
import qut.edu.au.services.Group;
import qut.edu.au.services.Operation;
import qut.edu.au.services.Parameter;
import qut.edu.au.services.Service;
import org.apache.commons.math3.util.Combinations;
import org.simmetrics.StringMetric;
import static org.simmetrics.builders.StringMetricBuilder.with;
import org.simmetrics.metrics.CosineSimilarity;
import org.simmetrics.metrics.Levenshtein;
import org.simmetrics.simplifiers.Simplifiers;
import org.simmetrics.tokenizers.Tokenizers;

/**
 *
 * @author sih
 */
public class Test {

    private static Random rng = new Random();

    private static String a = "ShippingLabel";
    private static String b = "DropoffType";

    /**
     * @param node
     * @param args
     */
    public static void main(String[] args) {

        // TODO Auto-generated method stub  
        /*
         ArrayList<EntityPair> entityPairs = new ArrayList<EntityPair>();
         BusinessEntity enity1 = new BusinessEntity("Shipper");
         BusinessEntity enity2 = new BusinessEntity("Recipient");
         BusinessEntity enity3 = new BusinessEntity("line");

         BusinessEntity enity4 = new BusinessEntity("Shipper");
         BusinessEntity enity5 = new BusinessEntity("Recipient");

         EntityPair pair1 = new EntityPair(enity1, enity2);
         EntityPair pair2 = new EntityPair(enity1, enity3);
         EntityPair pair3 = new EntityPair(enity4, enity5);

         entityPairs.add(pair1);
         entityPairs.add(pair2);

         if (entityPairs.contains(pair3)) {
         System.out.println("true------");
         }
         */
        /*
         for (int i = 0; i < 100; i++) {
         int val = (int) Math.round(rng.nextGaussian()*15+500);
         System.out.print(val+",");
         }
         */

        /*
         ArrayList<Integer> list1= new ArrayList<Integer>();
         list1.add(1);
         list1.add(2);
         list1.add(3);
        
         ArrayList<Integer> list2= new ArrayList<Integer>();
         list2.add(1);
         list2.add(2);
         list2.add(3);
        
         list2.clear();
         System.out.println(list1.size()+ " "+ list2.size());
         */
        // initial a Map
        /*
         Service service = new Service("TestData/ES/Fedex/ShipService_v15.wsdl");
         Operation operation = service.getOperation("processShipment");
         //operation.getKnownPath(null)
         for (Group group : operation.getGroups()) {
         System.out.println("Group: "+ group.getGroupNumber() + " Parameters are: ");
         for (Parameter parameter : group.getParameters()) {
         System.out.println(parameter.getName()+"," + "("+parameter.getGroupBelongTo().getGroupNumber()+")");
         }
         System.out.println("---------------------");
         }
         */
        /*
         int counter = 0;
         for (int i = 1; i < 11; i++) {
         Combinations comb  = new Combinations(10,i);

         Iterator<int[]> litr = comb.iterator();
         while(litr.hasNext()) {
         int[] element = litr.next();
         for (int comb1 : element) {
         System.out.print((comb1+1)+",");
         counter++;
         }
         System.out.println();
         }        
         System.out.println();
         System.out.println();
            
         }
         System.out.println("TOTOAL NUBMER: "+ counter);
        
         */
        String fileSrc = null, fileTrg = null;

        Graph graphSrc = loadGraph(fileSrc, null);
        Graph graphTrg = loadGraph(fileTrg, null);
        ExecWorkflow exec = new ExecWorkflow();
        Strategy strategy = new Strategy(Strategy.COMA_OPT);
        if (graphSrc.getSource().getType() == Source.TYPE_ONTOLOGY
                || graphTrg.getSource().getType() == Source.TYPE_ONTOLOGY) {
            strategy.setResolution(new Resolution(Resolution.RES1_NODES));
        } else {
            graphSrc = graphSrc.getGraph(Graph.PREP_SIMPLIFIED);
            graphTrg = graphTrg.getGraph(Graph.PREP_SIMPLIFIED);
        }
        Workflow workflow = new Workflow();

        workflow.setSource(graphSrc);
        workflow.setTarget(graphTrg);
        workflow.setBegin(strategy);
        MatchResult[] results = exec.execute(workflow);
        if (results == null) {
            System.err.println("COMA_API.matchModelsDefault results unexpected null");
            //return null;
        }
        if (results.length > 1) {
            System.err.println("COMA_API.matchModelsDefault results unexpected more than one, only first one returned");
        }
        System.out.print(results[0]);

        //String a = "RequestedShipment";
        //String b = "ShipmentRequest";
        //StringMetric metric = new Levenshtein();
        //StringMetric metric = new Levenshtein();
        //System.out.println("example00:    "+example00()); // 0.7812
        //System.out.println("example01:    "+example01()); // 0.7812
        //System.out.println("example02:    "+example02()); // 0.7812
        //System.out.println("example03:    "+example03()); // 0.7812
        //System.out.println("example04:    "+example04()); // 0.7812
        //System.out.println("example05:    "+example05()); // 0.7812
        //System.out.println("example06:    "+example06()); // 0.7812
        //System.out.println("example07:    "+example07()); // 0.7812
        /*
         LinkedHashMap<Parameter, ArrayList<Parameter>> groups = new LinkedHashMap<Parameter, ArrayList<Parameter>>();
         //ArrayList<Parameter> children = new ArrayList<Parameter>();
         //Parameter previousParent = operation.getSimpleInputParameterList().get(0).getParentParameter();
        
        
         for (Parameter temParameter : operation.getComplexInputParameterList()) {
         if (temParameter.getParameterUniqueIDinTree()==1)
         temParameter.setLevel(1);
         else
         temParameter.setLevel(temParameter.getParentParameter().getLevel()+1);
         for (Parameter parameter : operation.getComplexInputParameterList()) {
         if (parameter.getParentParameter().equals(temParameter)) {
         temParameter.addChild(parameter);
         }
         }
         }
        

         for (Parameter parameter : operation.getSimpleInputParameterList()) {
         //parameter.setLevel();
         Parameter parent = parameter.getParentParameter();
         for (Parameter temParameter : operation.getComplexInputParameterList())
         if (temParameter.equals(parent))
         temParameter.addChild(parameter);
            
         /*
         int level = 1;
         Parameter parent = parameter.getParentParameter();
         Parameter previous = parent;
         while (parent != null && parent.getParameterUniqueIDinTree() > 1){
         for (Parameter temParameter : operation.getComplexInputParameterList()) {

         if (temParameter.equals(parent)) {
         if (level == 1) {
         temParameter.addChild(parameter);
         } else {
         temParameter.addChild(previous);
         }
         level++;
         temParameter.setLevel(level);
         break;
         }
         }

         previous = parent;
         parent = parent.getParentParameter();
         }
         */
        /*
         if (parent != null && previousParent != null && parent != previousParent) {
         if (groups.get(previousParent) != null) {
         groups.get(previousParent).addAll(children);
         } else {
         groups.put(previousParent, children);
         }
         children = new ArrayList<Parameter>();
         previousParent = parent;
         }
         children.add(parameter);
         */
        //System.out.println("Parameter: "+ parameter.getName()+ "Level"+ parameter.getLevel());
    /*
        
         }
        
         for (Parameter parameter : operation.getComplexInputParameterList()) {
         //System.out.println("Parameter: "+ parameter.getName()+ " Level "+ parameter.getLevel()+" children : ");            
         if (parameter.getChildren()!=null) {
         ArrayList<Parameter> simpleChildren = new ArrayList<Parameter>();
         for (Parameter child : parameter.getChildren()) {
         if (!child.isComplex())
         simpleChildren.add(child);
         System.out.print(child.getName()+",");
         }
         }
         System.out.println();
         }
        
         //Parameter parent = operation.getSimpleInputParameterList().get(0).getParentParameter();
         ArrayList<Parameter> group = new ArrayList<Parameter>();        
         Parameter previousParent = operation.getSimpleInputParameterList().get(0).getParentParameter();        
         for (Parameter parameter : operation.getSimpleInputParameterList()) {
         Parameter currentParent = parameter.getParentParameter();
         if (currentParent.equals(previousParent))
         group.add(parameter);
         else {  // a new group
         if (group.size()>1) {
         //try this group
                    
         }
         }
         previousParent = currentParent;
         }
        
        
        

         // more elegant way
         for (Map.Entry<Parameter, ArrayList<Parameter>> entry : groups.entrySet()) {
         Parameter parent = (Parameter) entry.getKey();
         System.out.println("Parent : " + parent.getName() + " Children : ");
         ArrayList<Parameter> childrenUnder = (ArrayList<Parameter>) entry.getValue();
         int counter = 1;
         for (Parameter child : childrenUnder) {
         System.out.println("child : " + counter + " " + child.getName());
         counter++;
         }
         }
         */
        System.exit(0);
    }

    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static float example00() {
        StringMetric metric = new Levenshtein();
        return metric.compare(a, b);
    }

    /**
     * Simplification
     *
     * Simplification increases the effectiveness of a metric by removing noise
     * and reducing the dimensionality of the problem. The process maps a a
     * complex string such as to a simpler format. This allows string from
     * different sources to be compared in the same form.
     *
     * The Simplifiers utility class contains a collection of common, useful
     * simplifiers. For a custom simplifier you can implement the Simplifier
     * interface.
     */
    public static float example01() {

        StringMetric metric
                = with(new Levenshtein())
                .simplify(Simplifiers.removeDiacritics())
                .build();

        return metric.compare(a, b); // 1.0000
    }

    /**
     * Simplifiers can also be chained.
     */
    public static float example02() {

        StringMetric metric
                = with(new CosineSimilarity<String>())
                .simplify(Simplifiers.toLowerCase(Locale.ENGLISH))
                .simplify(Simplifiers.replaceNonWord())
                .tokenize(Tokenizers.whitespace())
                .build();

        /*
         StringMetric metric = 
         with(new Levenshtein())
         .simplify(Simplifiers.removeDiacritics())
         .simplify(Simplifiers.toLowerCase())
         .build();
         */
        return metric.compare(a, b); // 1.0000
    }

    /**
     * Tokenization
     *
     * A metric can be used to measure the similarity between strings. However
     * not all metrics can operate on strings directly. Some operate on lists,
     * sets or multisets. To compare strings with a metric that works on a
     * collection a tokenizer is required. Tokenization cuts up a string into
     * parts.
     *
     * Example:
     *
     * `chilperic ii son of childeric ii`
     *
     * By splitting on whitespace is tokenized into:
     *
     * `[chilperic, ii, son, of, childeric, ii]`
     *
     * The choice of the tokenizer can influence the effectiveness of a metric.
     * For example when comparing individual words a q-gram tokenizer will be
     * more effective while a whitespace tokenizer will be more effective when
     * comparing documents.
     *
     * The Tokenizers utility class contains a collection of common, useful
     * tokenizers. For a custom tokenizer you can implement the Tokenizer
     * interface. Though it is recommended that you extend the
     * AbstractTokenizer.
     */
    public static float example03() {

        StringMetric metric
                = with(new CosineSimilarity<String>())
                .tokenize(Tokenizers.whitespace())
                .build();

        return metric.compare(a, b); // 0.7777
    }

    /**
     * Tokenizers can also be chained.
     *
     * `chilperic ii son of childeric ii`
     *
     * By splitting on whitespace is tokenized into:
     *
     * `[chilperic, ii, son, of, childeric, ii]`
     *
     * After using a q-gram with a q of 2:
     *
     * `[ch,hi,il,il,lp,pe,er,ri,ic, ii, so,on, of, ch,hi,il,ld,de,er,ri,ic,
     * ii]`
     *
     */
    public static float example04() {

        StringMetric metric
                = with(new CosineSimilarity<String>())
                .tokenize(Tokenizers.whitespace())
                .tokenize(Tokenizers.qGram(3))
                .build();

        return metric.compare(a, b); // 0.8292
    }

    /**
     * Tokens can be filtered to avoid comparing strings on common but otherwise
     * low information words. Tokens can be filtered after any tokenization step
     * and filters can be applied repeatedly.
     *
     * A filter can be implemented by implementing a the {@link Predicate}
     * interface. By chaining predicates more complicated filters can be build.
     *
     */
    public static float example05() {
        Set<String> commonWords = Sets.newHashSet("it", "is");
        Set<String> otherCommonWords = Sets.newHashSet("a");

        StringMetric metric
                = with(new CosineSimilarity<String>())
                .simplify(Simplifiers.toLowerCase())
                .simplify(Simplifiers.removeNonWord())
                .tokenize(Tokenizers.whitespace())
                .filter(Predicates.not(in(commonWords)))
                .filter(Predicates.not(in(otherCommonWords)))
                .tokenize(Tokenizers.qGram(3))
                .build();

        return metric.compare(a, b); // 0.6902
    }

    /**
     * Tokens can be transformed to a simpler form. This may be used to reduce
     * the possible token space. Tokens can be transformed after any
     * tokenization step and the transformation can be applied repeatedly.
     *
     * A transformation can be implemented by implementing a the Function
     * interface.
     */
    public static float example06() {

        Function<String, String> reverse = new Function<String, String>() {

            @Override
            public String apply(String input) {
                return new StringBuilder(input).reverse().toString();
            }

        };

        StringMetric metric
                = with(new CosineSimilarity<String>())
                .simplify(Simplifiers.toLowerCase())
                .simplify(Simplifiers.removeNonWord())
                .tokenize(Tokenizers.whitespace())
                .transform(reverse)
                .tokenize(Tokenizers.qGram(3))
                .build();

        return metric.compare(a, b); // 0.6902
    }

    /**
     * Tokenization and simplification can be expensive operations. To avoid
     * executing expensive operations repeatedly, intermediate results can be
     * cached. Note that Caching itself also has a non-trivial cost. Base your
     * decision on metrics!
     */
    public static float example07() {

        Cache<String, String> stringCache
                = CacheBuilder.newBuilder()
                .maximumSize(2)
                .build();

        Cache<String, Multiset<String>> tokenCache
                = CacheBuilder.newBuilder()
                .maximumSize(2)
                .build();

        StringMetric metric
                = with(new CosineSimilarity<String>())
                .simplify(Simplifiers.toLowerCase())
                .simplify(Simplifiers.removeNonWord())
                .cacheStrings(stringCache)
                .tokenize(Tokenizers.qGram(3))
                .cacheTokens(tokenCache)
                .build();

        return metric.compare(a, b); // 0.6902
    }

    public static Graph loadGraph(String file, String name) {
        if (file == null) {
            System.out.println("COMA_API.loadGraph Error file is null");
            return null;
        }
        boolean insertDB = false;
        String filetype = file.toLowerCase();
        filetype = filetype.substring(filetype.lastIndexOf("."));
        InsertParser par = null;
        if (filetype.equals(InsertParser.XSD)) {
            par = new XSDParser(insertDB);
        } else if (filetype.equals(InsertParser.XDR)) {
            par = new XDRParser(insertDB);
        } else if (filetype.equals(InsertParser.CSV)) {
            par = new CSVParser(insertDB);
        } else if (filetype.equals(InsertParser.SQL)) {
            par = new SQLParser(insertDB);
        } else if (filetype.equals(InsertParser.OWL) || filetype.equals(InsertParser.RDF)) {
            par = new OWLParser_V3(insertDB);
        }

        if (par == null) {
            System.out.println("COMA_API.loadGraph Error filetype not recognized");
            return null;
        }

        par.parseSingleSource(file);
        Graph graph = par.getGraph();
        return graph;
    }

}
