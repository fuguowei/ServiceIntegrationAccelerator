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

public class StatisticsFinding extends JFrame {

    private JTree jtree;

    public StatisticsFinding() throws ParserConfigurationException {
        WsdlProject project = null;
        try {
            project = new WsdlProject();
        } catch (XmlException ex) {
            Logger.getLogger(StatisticsFinding.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StatisticsFinding.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SoapUIException ex) {
            Logger.getLogger(StatisticsFinding.class.getName()).log(Level.SEVERE, null, ex);
        }

        SoapUI.getSettings().setString(WsdlSettings.XML_GENERATION_TYPE_EXAMPLE_VALUE, "true");

        // import amazon wsdl
        WsdlInterface iface = null;
        try {
            iface = WsdlInterfaceFactory.importWsdl(project, "Openshipping/OpenShipService_v7.wsdl", true)[0];
            //iface = WsdlInterfaceFactory.importWsdl(project, "SAP/PurchaseOrder.wsdl", true)[0];
            //iface = WsdlInterfaceFactory.importWsdl(project, "Track/TrackService_v8.wsdl", true)[0];

        } catch (SoapUIException ex) {
            Logger.getLogger(StatisticsFinding.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Operation[] operations = iface.getAllOperations();
        
        int totalNumberofParameters = 0;
        int totalNumberofComplexParameters =0;
        int averageNumberofParameters =0;
        int averageNumberofComplexParameters =0;
        int totalNumberofLevels =0;
        int evarageNumberofLevels =0;
                
        for (int i = 0; i < iface.getOperationCount(); i++) {
            WsdlOperation operation = iface.getOperationAt(i);

            ParameterAnalysis param = new ParameterAnalysis();

            WsdlContext wsdlContext = iface.getWsdlContext();
        //String bindingOperationName = operation.getConfig().getBindingOperationName();

            BindingOperation bindingOperation = null;
            try {
                bindingOperation = operation.findBindingOperation(wsdlContext.getDefinition());
            } catch (Exception ex) {
                Logger.getLogger(StatisticsFinding.class.getName()).log(Level.SEVERE, null, ex);
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
                hashMap = param.buildSoapMessageFromInput(bindingOperation, true, false, null, null,serviceName);
            } catch (Exception ex) {
                Logger.getLogger(StatisticsFinding.class.getName()).log(Level.SEVERE, null, ex);
            }

            Iterator<DefaultMutableTreeNode> keySetIterator = hashMap.keySet().iterator();

            while (keySetIterator.hasNext()) {
                root = keySetIterator.next();
                xmlContent = (String) hashMap.get(root);
            }

            System.out.println("Global_total_number_of_parameters: " + AnalysisStats.Global_total_number_of_parameters);
            System.out.println("Global_total_number_of_comlex_parameters: " + AnalysisStats.Global_total_number_of_complex_parameters);
            totalNumberofComplexParameters = AnalysisStats.Global_total_number_of_complex_parameters + totalNumberofComplexParameters;
            totalNumberofParameters = AnalysisStats.Global_total_number_of_parameters+totalNumberofParameters;
            //totalNumberofLevels 
        }

        System.out.println("the total number of parameters under the service is: ----"+AnalysisStats.Global_total_number_of_parameters);
        System.out.println("the total number of complex parameters under the service is: ----"+AnalysisStats.Global_total_number_of_complex_parameters);
        System.out.println("the total number of levels under the service is: ----"+AnalysisStats.Global_total_number_of_levels);
        System.out.println("the average number of parameters under the service is: ----"+AnalysisStats.Global_total_number_of_parameters/22);
        System.out.println("the average number of complex parameters under the service is: ----"+AnalysisStats.Global_total_number_of_complex_parameters/22);
        System.out.println("the average number of level parameters under the service is: ----"+AnalysisStats.Global_total_number_of_levels/22);

        // get "Help" operation
        //WsdlOperation operation = (WsdlOperation) iface.getOperationByName("createOpenShipment");
        //WsdlOperation operation = (WsdlOperation) iface.getOperationByName("confirmOpenShipment");        
        //WsdlOperation operation = (WsdlOperation) iface.getOperationByName("deleteOpenShipment");        
        //WsdlOperation operation = (WsdlOperation) iface.getOperationByName("deletePackagesFromOpenShipment");        
        //WsdlOperation operation = (WsdlOperation) iface.getOperationByName("modifyOpenShipment");        
        //WsdlOperation operation = (WsdlOperation) iface.getOperationByName("validateOpenShipment");        
        //WsdlOperation operation = (WsdlOperation) iface.getOperationByName("addPackagesToOpenShipment");              
        //WsdlOperation operation = (WsdlOperation) iface.getOperationByName("PurchaseOrderRequest_Out");        
        //WsdlOperation operation = (WsdlOperation) iface.getOperationByName("track");                        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new StatisticsFinding();
                } catch (ParserConfigurationException ex) {
                    Logger.getLogger(StatisticsFinding.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}
