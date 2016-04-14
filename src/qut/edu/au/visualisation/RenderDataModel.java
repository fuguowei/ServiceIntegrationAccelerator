/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.visualisation;

// Import the basic graphics classes.
import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.WsdlInterfaceFactory;
import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlContext;
import com.eviware.soapui.settings.WsdlSettings;
import com.eviware.soapui.support.SoapUIException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.wsdl.BindingOperation;
import org.apache.xmlbeans.XmlException;
import qut.edu.au.TreeExampleInputParameters;
import qut.edu.au.Utility;
import qut.edu.au.analysis.AnalysisStats;
import qut.edu.au.analysis.ParameterAnalysis;

public class RenderDataModel {

    // Create a constructor method
    public RenderDataModel() {
        // All we do is call JFrame's constructor.
    }

    public static void main(String arg[]) {

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
            iface = WsdlInterfaceFactory.importWsdl(project, "Openshipping/OpenShipService_v7.wsdl", true)[0];
            //iface = WsdlInterfaceFactory.importWsdl(project, "SAP/PurchaseOrder.wsdl", true)[0];
            //iface = WsdlInterfaceFactory.importWsdl(project, "Track/TrackService_v8.wsdl", true)[0];
        } catch (SoapUIException ex) {
            Logger.getLogger(TreeExampleInputParameters.class.getName()).log(Level.SEVERE, null, ex);
        }

        // get "Help" operation
        WsdlOperation operation = (WsdlOperation) iface.getOperationByName("createOpenShipment");        
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
        DefaultMutableTreeNode root = null;
        HashMap hashMap = null;
        String xmlContent = null;
        param.setIface(iface);
        param.setWsdlContext(wsdlContext);

        //First time, call it to get the parameters only        
        String serviceName = iface.getName().replace("ServiceSoapBinding", "");
        try {
            AnalysisStats.resetStats();
            hashMap = param.buildSoapMessageFromInput(bindingOperation, true, false, null,null,serviceName);
        } catch (Exception ex) {
            Logger.getLogger(TreeExampleInputParameters.class.getName()).log(Level.SEVERE, null, ex);
        }

        Iterator<DefaultMutableTreeNode> keySetIterator = hashMap.keySet().iterator();

        while (keySetIterator.hasNext()) {
            root = keySetIterator.next();
            xmlContent = (String) hashMap.get(root);
        }

        System.out.println("Global_total_number_of_parameters: " + AnalysisStats.Global_total_number_of_parameters);
        System.out.println("Global_total_number_of_comlex_parameters: " + AnalysisStats.Global_total_number_of_complex_parameters);

        java.util.List<String> graphStrings = new ArrayList<String>();
        graphStrings = Utility.traverseTreeGetPair(root, graphStrings);
        generateGraph(graphStrings);
    }

    private static void generateGraph(java.util.List<String> graphStrings) {

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
        File out = new File("/tmp/out." + type);   // Linux
//      File out = new File("c:/eclipse.ws/graphviz-java-api/out." + type);    // Windows
        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);

    }

}
