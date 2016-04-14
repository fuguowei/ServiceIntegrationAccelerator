/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package qut.edu.au.tree;

/**
 *
 * @author sih
 */
import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.impl.wsdl.WsdlRequest;
import com.eviware.soapui.impl.wsdl.WsdlSubmit;
import com.eviware.soapui.impl.wsdl.WsdlSubmitContext;
import com.eviware.soapui.model.iface.Request;
import com.eviware.soapui.model.iface.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.wsdl.BindingOperation;
import qut.edu.au.analysis.AnalysisStats;
import qut.edu.au.analysis.ParameterAnalysis;

/*
 * See URL: http://en.wikipedia.org/wiki/Depth-first_search
 */

public class DepthFirstTreeIterator implements Iterator<Node> {
    private LinkedList<Node> list;
    private static final int ROOT = 0;
    private int depth =2;
    //private HashMap<Integer, ArrayList<String>> levels;
    private List<String> parametersToBeRemoved = new ArrayList<String>();
    private int requestCounter = 0;
    private final static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DepthFirstTreeIterator.class);
    

    public DepthFirstTreeIterator(HashMap<String, Node> tree, String identifier,BindingOperation bindingOperation,ParameterAnalysis param,WsdlOperation operation) {
        list = new LinkedList<Node>();
        //levels = new HashMap<Integer, ArrayList<String>>();

        if (tree.containsKey(identifier)) {
            this.buildList(tree, identifier,ROOT, ROOT,bindingOperation,param,operation);
        }
    }
    
//    private List<ArrayList> getAllnodes(HashMap<String, Node> tree, String identifier,int level,BindingOperation bindingOperation,ParameterAnalysis param,WsdlOperation operation)
//    {
//        ArrayList<ArrayList> listofnodes = new ArrayList<ArrayList>();
//        ArrayList<String> nodes = new ArrayList<String>();
//        list.add(tree.get(identifier));
//        current level 
//        if (level > depth)
//            return listofnodes;
//        
//        ArrayList<String> children = tree.get(identifier).getChildren();
//        for (String child : children) {
//            // Recursive call
//            nodes.add(child);
//            this.getAllnodes(tree, child,level+1,bindingOperation,param,operation);
//        }        
//    }

    private void buildList(HashMap<String, Node> tree, String identifier,int previousLevel, int level,BindingOperation bindingOperation,ParameterAnalysis param,WsdlOperation operation) {
        
        list.add(tree.get(identifier));
        DefaultMutableTreeNode root =null;
        HashMap hashMap =null;
        String xmlContent =null;
                
        System.out.println("Level:"+level+"String: "+identifier);
        //if (level >= depth)
        //   parametersToBeRemoved.remove(parametersToBeRemoved.size()-1);        
        //parametersToBeRemoved.remove(parametersToBeRemoved.size()-1);
        
        if (previousLevel == 0 && level == 1)
            parametersToBeRemoved.remove(0);
                
        if (level == 2)
            if (parametersToBeRemoved.size()>=2)
                parametersToBeRemoved.remove(parametersToBeRemoved.size()-1); 
        
//        if (level >= 2)
//           parametersToBeRemoved.remove(0);
        if (level == 1)
            if (parametersToBeRemoved.size()>=1)
                parametersToBeRemoved.clear();
        
//        if (parametersToBeRemoved.size()>=level)
//            parametersToBeRemoved.remove(parametersToBeRemoved.size()-1); 
        
           
        parametersToBeRemoved.add(identifier);
        try {
            AnalysisStats.resetStats();            
            hashMap  = param.buildSoapMessageFromInput(bindingOperation,true,false,parametersToBeRemoved,null,null);
        } catch (Exception ex) {
            //log.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            log.error(ex);
        }


        Iterator<DefaultMutableTreeNode> keySetIterator = hashMap.keySet().iterator();

        while(keySetIterator.hasNext()){
          root = keySetIterator.next();
          xmlContent = (String)hashMap.get(root);
        }
                
//        if (level == ROOT) {
//            list.add(tree.get(identifier));
//        }
        
        WsdlRequest request = operation.addNewRequest("My request");
        request.setRequestContent(operation.createRequest(true));
        requestCounter = requestCounter +1;
        log.info("------------------------------------REQUEST"+requestCounter+"-------------------------------------\n");
        for (String s : parametersToBeRemoved)
            log.info("Level:  "+level+"  String: "+identifier+ "     Strings to be removed this time:"+s);        
        log.info("------------------------------------REQUEST CONTENT-------------------------------------\n");        
        log.info(xmlContent);
        //writeFile("Track/request"+requestCounter+".xml", xmlContent);
        
        if (xmlContent != null) {
            request.setRequestContent(xmlContent);

            WsdlSubmit submit =null;
            try {
                submit = (WsdlSubmit) request.submit(new WsdlSubmitContext(request), false);
            } catch (Request.SubmitException ex) {
                log.error(ex);
                //log.getLogger(DepthFirstTreeIterator.class.getName()).log(Level.FATAL, null, ex);
            }
            // wait for the response
            Response response = submit.getResponse();
            //	print the response
            String content = response.getContentAsString();            
            //writeFile("Track/response"+requestCounter+".xml", content);
            log.info("------------------------------------RESPONSE"+requestCounter+" CONTENT-------------------------------------\n");                    
            log.info(content);

            //System.out.print(content);
        }
        
        if (level >= depth)
            return;
        
        ArrayList<String> children = tree.get(identifier).getChildren();
        for (String child : children) {
            // Recursive call
            this.buildList(tree, child,level,level+1,bindingOperation,param,operation);
        }
    }

    @Override
    public boolean hasNext() {
        return !list.isEmpty();
    }

    @Override
    public Node next() {
        return list.poll();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}