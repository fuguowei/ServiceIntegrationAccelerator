package qut.edu.au;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.WsdlInterfaceFactory;
import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.WsdlRequest;
import com.eviware.soapui.impl.wsdl.WsdlSubmit;
import com.eviware.soapui.impl.wsdl.WsdlSubmitContext;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlContext;
import com.eviware.soapui.model.iface.Operation;
import com.eviware.soapui.model.iface.Request;
import com.eviware.soapui.model.iface.Response;
import com.eviware.soapui.settings.WsdlSettings;
import com.eviware.soapui.support.SoapUIException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.wsdl.BindingOperation;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.xmlbeans.XmlException;
import static qut.edu.au.Utility.readFile;
import static qut.edu.au.Utility.writeFile;
import qut.edu.au.analysis.AnalysisStats;
import qut.edu.au.analysis.ParameterAnalysis;
import qut.edu.au.tree.DepthFirstTreeIterator;
import qut.edu.au.tree.Node;
import qut.edu.au.tree.Tree;

public class TreeExampleInputParameters extends JFrame {

    private JTree jtree;

    public TreeExampleInputParameters() throws ParserConfigurationException {
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
        try {
            iface = WsdlInterfaceFactory.importWsdl(project, "TestData/ES/Fedex/OpenShipService_v7.wsdl", true)[0];

            //iface = WsdlInterfaceFactory.importWsdl(project, "InternetOfService/AWSECommerceService.wsdl", true)[0];
            //iface = WsdlInterfaceFactory.importWsdl(project, "SAP/PurchaseOrder.wsdl", true)[0];
            //iface = WsdlInterfaceFactory.importWsdl(project, "Track/TrackService_v8.wsdl", true)[0];
        } catch (SoapUIException ex) {
            Logger.getLogger(TreeExampleInputParameters.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Operation[] operations = iface.getAllOperations();
        // get "Help" operation
        WsdlOperation operation = (WsdlOperation) iface.getOperationByName("addPackagesToOpenShipment");
        //WsdlOperation operation = (WsdlOperation) iface.getOperationByName("confirmOpenShipment");        
        //WsdlOperation operation = (WsdlOperation) iface.getOperationByName("deleteOpenShipment");        
        //WsdlOperation operation = (WsdlOperation) iface.getOperationByName("deletePackagesFromOpenShipment");        
        //WsdlOperation operation = (WsdlOperation) iface.getOperationByName("modifyOpenShipment");        
        //WsdlOperation operation = (WsdlOperation) iface.getOperationByName("validateOpenShipment");

        //WsdlOperation operation = (WsdlOperation) iface.getOperationByName("addPackagesToOpenShipment");              
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
        ArrayList<DefaultMutableTreeNode> nodesInput = null, nodesOutput = null;
        //DefaultMutableTreeNode root = null;
        HashMap hashMap = null;
        String xmlContent = null;
        param.setIface(iface);
        param.setWsdlContext(wsdlContext);

        //First time, call it to get the parameters only   
        String serviceName = iface.getName().replace("ServiceSoapBinding", "");
        System.out.println("ServiceName............." + serviceName);
        try {
            AnalysisStats.resetStats();
            hashMap = param.buildSoapMessageFromInput(bindingOperation, true, false, null, null, serviceName);
            //bindingOperation.getOperation()
        } catch (Exception ex) {
            Logger.getLogger(TreeExampleInputParameters.class.getName()).log(Level.SEVERE, null, ex);
        }

        Iterator<ArrayList<DefaultMutableTreeNode>> keySetIterator = hashMap.keySet().iterator();

        while (keySetIterator.hasNext()) {
            nodesInput = keySetIterator.next();
            xmlContent = (String) hashMap.get(nodesInput);
        }

        System.out.println("Global_total_number_of_parameters: " + AnalysisStats.Global_total_number_of_parameters);
        System.out.println("Global_total_number_of_comlex_parameters: " + AnalysisStats.Global_total_number_of_complex_parameters);

        //writeFile("Track/generatedContent.xml", xmlContent);
        //System.out.print(xmlContent);
        //create the tree by passing in the root node
        for (DefaultMutableTreeNode root : nodesInput) {

            jtree = new JTree(root);
            for (int i = 0; i < jtree.getRowCount(); i++) {
                jtree.expandRow(i);
            }
            add(jtree);
            add(new JScrollPane(jtree));

            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setTitle("FedEx OpenShipping Service");
            this.pack();
            this.setVisible(true);
            
            /*
            try {
                Utility.writeFile("output/treeStructure.xml",Utility.TreetoXml(jtree.getModel()),false);
                //System.out.print(Utility.TreetoXml(jtree.getModel()));
            } catch (TransformerException ex) {
                Logger.getLogger(TreeExampleInputParameters.class.getName()).log(Level.SEVERE, null, ex);
            }
            */

            // really invoke the service
            Tree tree = new Tree();
            tree.addNode("Track");
            AnalysisStats.listofPreliminary.remove("Key");
            AnalysisStats.listofPreliminary.remove("Password");
            AnalysisStats.listofPreliminary.remove("AccountNumber");
            AnalysisStats.listofPreliminary.remove("MeterNumber");
            AnalysisStats.listofPreliminary.remove("ServiceId");
            AnalysisStats.listofPreliminary.remove("Major");
            AnalysisStats.listofPreliminary.remove("Minor");
            AnalysisStats.listofPreliminary.remove(8);

            //Utility.generateTree(AnalysisStats.listofPreliminary,tree,0,"Track");        
            Utility.generateTree(AnalysisStats.listofPreliminary, tree, 0, "Track");
        //tree.display("Track");

            //Iterator<Node> depthIterator = new DepthFirstTreeIterator(("Track")
            //Iterator<Node> depthIterator = tree.iterator("Track",bindingOperation,param,operation);
            //Iterator<Node> depthIterator = tree.iterator("Track",bindingOperation,param,operation);
            //Second time, really call it       
//        WsdlRequest request = operation.addNewRequest("My request");
//        request.setRequestContent(operation.createRequest(true));
//        if (xmlContent != null) {
//            request.setRequestContent(xmlContent);
//
//            WsdlSubmit submit =null;
//            try {
//                submit = (WsdlSubmit) request.submit(new WsdlSubmitContext(request), false);
//            } catch (Request.SubmitException ex) {
//                Logger.getLogger(TreeExample.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            // wait for the response
//            Response response = submit.getResponse();
//            //	print the response
//            String content = response.getContentAsString();
//            System.out.print(content);
//        }
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new TreeExampleInputParameters();

                } catch (ParserConfigurationException ex) {
                    Logger.getLogger(TreeExampleInputParameters.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}
