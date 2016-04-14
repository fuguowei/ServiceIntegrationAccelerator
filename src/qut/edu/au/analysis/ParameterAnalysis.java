/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.analysis;

/*
 * Copyright 2014 sih.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.support.Constants;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlContext;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlUtils;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlUtils.SoapHeader;
import com.eviware.soapui.model.iface.Interface;
import com.eviware.soapui.model.iface.MessageBuilder;
import com.eviware.soapui.settings.WsdlSettings;
import com.eviware.soapui.support.xml.XmlUtils;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import qut.edu.au.services.Parameter;
import qut.edu.au.Utility;

/**
 *
 * @author sih
 */
public class ParameterAnalysis implements MessageBuilder {

    private final static Logger log = Logger.getLogger(ParameterAnalysis.class);

    //private static DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
    //private static JTree tree;
    private WsdlContext wsdlContext;

    public void setWsdlContext(WsdlContext wsdlContext) {
        this.wsdlContext = wsdlContext;
    }

    public void setIface(WsdlInterface iface) {
        this.iface = iface;
    }
    private WsdlInterface iface;
    private Map<QName, String[]> multiValues = null;

    public Interface getInterface() {
        return iface;
    }

    public HashMap buildSoapMessageFromOutput(BindingOperation bindingOperation, boolean buildOptional,
            boolean alwaysBuildHeaders, List<String> parametersToBeRemoved, String serviceName) throws Exception {
        ArrayList<DefaultMutableTreeNode> nodes = new ArrayList<DefaultMutableTreeNode>();
        String xmlContent = null;
        HashMap<ArrayList<DefaultMutableTreeNode>, String> result = new HashMap<ArrayList<DefaultMutableTreeNode>, String>();
        boolean inputSoapEncoded = WsdlUtils.isInputSoapEncoded(bindingOperation);
        SampleXmlUtility xmlGenerator = new SampleXmlUtility(inputSoapEncoded);
        xmlGenerator.setIgnoreOptional(!buildOptional);
        xmlGenerator.setMultiValues(multiValues);
        XmlObject object = XmlObject.Factory.newInstance();
        XmlCursor cursor = object.newCursor();
        cursor.toNextToken();
        cursor.beginElement(wsdlContext.getSoapVersion().getEnvelopeQName());
        if (inputSoapEncoded) {
            cursor.insertNamespace("xsi", Constants.XSI_NS);
            cursor.insertNamespace("xsd", Constants.XSD_NS);
        }
        cursor.toFirstChild();
        cursor.beginElement(wsdlContext.getSoapVersion().getBodyQName());
        cursor.toFirstChild();
        if (WsdlUtils.isRpc(wsdlContext.getDefinition(), bindingOperation)) {
            buildRpcResponse(bindingOperation, cursor, xmlGenerator, parametersToBeRemoved, serviceName);
        } else {
            nodes = buildDocumentResponse(bindingOperation, cursor, xmlGenerator, parametersToBeRemoved, serviceName);
        }
        if (alwaysBuildHeaders) {
// bindingOutput will be null for one way operations,
// but then we shouldn't be here in the first place???
            BindingOutput bindingOutput = bindingOperation.getBindingOutput();
            if (bindingOutput != null) {
                List<?> extensibilityElements = bindingOutput.getExtensibilityElements();
                List<SoapHeader> soapHeaders = WsdlUtils.getSoapHeaders(extensibilityElements);
                addHeaders(soapHeaders, cursor, xmlGenerator,parametersToBeRemoved, null, serviceName);
            }
        }
        cursor.dispose();
        try {
            StringWriter writer = new StringWriter();
            XmlUtils.serializePretty(object, writer);
            xmlContent = writer.toString();
        } catch (Exception e) {
            SoapUI.logError(e);
            xmlContent = object.xmlText();
        }
        result.put(nodes, xmlContent);
        return result;
    }

    public HashMap buildSoapMessageFromInput(BindingOperation bindingOperation, boolean buildOptional,
            boolean alwaysBuildHeaders, List<String> parametersToBeRemoved, List<Parameter> parametersToTry, String serviceName) throws Exception {
        ArrayList<DefaultMutableTreeNode> nodes = new ArrayList<DefaultMutableTreeNode>();
        boolean inputSoapEncoded = WsdlUtils.isInputSoapEncoded(bindingOperation);
        //List<> result = new ArrayList<>();
        HashMap<ArrayList<DefaultMutableTreeNode>, String> result = new HashMap<ArrayList<DefaultMutableTreeNode>, String>();
        String xmlContent = null;
        SampleXmlUtility xmlGenerator = new SampleXmlUtility(inputSoapEncoded);
        xmlGenerator.setMultiValues(multiValues);
        xmlGenerator.setIgnoreOptional(!buildOptional);
        XmlObject object = XmlObject.Factory.newInstance();
        XmlCursor cursor = object.newCursor();
        cursor.toNextToken();
        cursor.beginElement(wsdlContext.getSoapVersion().getEnvelopeQName());
        if (inputSoapEncoded) {
            cursor.insertNamespace("xsi", Constants.XSI_NS);
            cursor.insertNamespace("xsd", Constants.XSD_NS);
        }
        cursor.toFirstChild();
        QName bodyName = wsdlContext.getSoapVersion().getBodyQName();
        cursor.beginElement(bodyName);
        cursor.toFirstChild();
        if (WsdlUtils.isRpc(wsdlContext.getDefinition(), bindingOperation)) {
            buildRpcRequest(bindingOperation, cursor, xmlGenerator, parametersToBeRemoved, parametersToTry, serviceName);
        } else {
            nodes = buildDocumentRequest(bindingOperation, cursor, xmlGenerator, parametersToBeRemoved, parametersToTry, serviceName);
        }
        if (alwaysBuildHeaders) {
            BindingInput bindingInput = bindingOperation.getBindingInput();
            if (bindingInput != null) {
                List<?> extensibilityElements = bindingInput.getExtensibilityElements();
                List<SoapHeader> soapHeaders = WsdlUtils.getSoapHeaders(extensibilityElements);
                addHeaders(soapHeaders, cursor, xmlGenerator, parametersToBeRemoved, parametersToTry, serviceName);
            }
        }
        cursor.dispose();

        try {
            StringWriter writer = new StringWriter();
            XmlUtils.serializePretty(object, writer);
            xmlContent = writer.toString();
        } catch (Exception e) {
            SoapUI.logError(e);
            xmlContent = object.xmlText();
        }

        result.put(nodes, xmlContent);
        return result;
    }

    private void addHeaders(List<SoapHeader> headers, XmlCursor cursor, SampleXmlUtility xmlGenerator, List<String> parametersToBeRemoved, List<Parameter> parametersToTry, String serviceName) throws Exception {
        // reposition
        cursor.toStartDoc();
        cursor.toChild(wsdlContext.getSoapVersion().getEnvelopeQName());
        cursor.toFirstChild();
        cursor.beginElement(wsdlContext.getSoapVersion().getHeaderQName());
        cursor.toFirstChild();
        for (int i = 0; i < headers.size(); i++) {
            SoapHeader header = headers.get(i);
            Message message = wsdlContext.getDefinition().getMessage(header.getMessage());
            if (message == null) {
                log.error("Missing message for header: " + header.getMessage());
                continue;
            }
            Part part = message.getPart(header.getPart());
            if (part != null) {
                createElementForPart(part, cursor, xmlGenerator, null, parametersToBeRemoved, parametersToTry, serviceName);
            } else {
                log.error("Missing part for header; " + header.getPart());
            }
        }
    }

    public DefaultMutableTreeNode createElementForPart(Part part, XmlCursor cursor, SampleXmlUtility xmlGenerator, DefaultMutableTreeNode originalNode, List<String> parametersToBeRemoved, List<Parameter> parametersToTry, String serviceName) throws Exception {
        QName elementName = part.getElementName();
        QName typeName = part.getTypeName();
        if (elementName != null) {
            cursor.beginElement(elementName);
            if (wsdlContext.hasSchemaTypes()) {
                SchemaGlobalElement elm = wsdlContext.getSchemaTypeLoader().findElement(elementName);
                if (elm != null) {
                    cursor.toFirstChild();
                    originalNode = xmlGenerator.createSampleForType(elm.getType(), cursor, originalNode, null, null, parametersToBeRemoved, parametersToTry, serviceName, null);
                    //XStream xstream = new XStream();
                    //String operationStr = xstream.toXML(originalNode);
                    //System.out.print(operationStr);
                } else {
                    log.error("Could not find element [" + elementName + "] specified in part [" + part.getName() + "]");
                }
            }
            cursor.toParent();
        } else {
            // cursor.beginElement( new QName(
            // wsdlContext.getWsdlDefinition().getTargetNamespace(), part.getName()
            // ));
            cursor.beginElement(part.getName());
            if (typeName != null && wsdlContext.hasSchemaTypes()) {
                SchemaType type = wsdlContext.getSchemaTypeLoader().findType(typeName);
                if (type != null) {
                    cursor.toFirstChild();
                    originalNode = xmlGenerator.createSampleForType(type, cursor, originalNode, null, null, parametersToBeRemoved, parametersToTry, serviceName, null);
                } else {
                    log.error("Could not find type [" + typeName + "] specified in part [" + part.getName() + "]");
                }
            }
            cursor.toParent();
        }
        return originalNode;
    }

    private ArrayList<DefaultMutableTreeNode> buildDocumentRequest(BindingOperation bindingOperation, XmlCursor cursor, SampleXmlUtility xmlGenerator, List<String> parametersToBeRemoved, List<Parameter> parametersToTry, String serviceName)
            throws Exception {
        Part[] parts = WsdlUtils.getInputParts(bindingOperation);
        ArrayList<DefaultMutableTreeNode> nodes = new ArrayList<DefaultMutableTreeNode>();

        for (Part part : parts) {
            if (!WsdlUtils.isAttachmentInputPart(part, bindingOperation)
                    && (part.getElementName() != null || part.getTypeName() != null)) {
                XmlCursor c = cursor.newCursor();
                c.toLastChild();
                Parameter parameter = new Parameter();
                parameter.setName(part.getElementName().getLocalPart());
                if (part.getTypeName() != null)
                    parameter.setType(part.getTypeName().getLocalPart());
                //parameter.setComplex(true);  //by default, the root parameter is complex
                parameter.setParameterUniqueIDinTree(AnalysisStats.parameterUniqueIDinTree);
                DefaultMutableTreeNode partNode = new DefaultMutableTreeNode(parameter);
                DefaultMutableTreeNode updatedNode = createElementForPart(part, c, xmlGenerator, partNode, parametersToBeRemoved, parametersToTry, serviceName);
                nodes.add(updatedNode);
                //root.add(updatedNode);
                c.dispose();
            }
        }
        return nodes;

//        //now really starts generating data
//        for (int i = 0; i < parts.length; i++) {
//            Part part = parts[i];
//            if (!WsdlUtils.isAttachmentInputPart(part, bindingOperation)
//                    && (part.getElementName() != null || part.getTypeName() != null)) {
//                XmlCursor c = cursor.newCursor();
//                c.toLastChild();
//                DefaultMutableTreeNode partNode = new DefaultMutableTreeNode(part.getElementName().getLocalPart());
//                DefaultMutableTreeNode updatedNode = createElementForPart(part, c, xmlGenerator, partNode);
//                root.add(updatedNode);
//                c.dispose();
//            }
//        }
        //tree = new JTree(root);  //this tree stores everything wee need.
        //System.out.print(Utility.TreetoXml(tree.getModel()));
        //System.out.println(traverseTree(root));
        //System.out.print(traverseTree(root));
//        List<String> allParameters = new ArrayList<>();
//        allParameters = traverseTree(root,allParameters);
//        int elementNumber = allParameters.size();
//        String[] strArray = new String[elementNumber];
//        System.out.print("Number of elements:------------------"+elementNumber+"-------------------------");
//        for(int i=0; i<elementNumber;i++) {
//            strArray[i] = allParameters.get(i);
//        }
//        MutableSet<String> set = UnifiedSet.newSetWith(strArray);        
//        MutableSet allCombinations = set.powerSet();
//        MutableSet<UnsortedSetIterable> powerSet = allCombinations.powerSet();        
//
//        Iterator iter = powerSet.iterator();
//        while (iter.hasNext()) {
//          MutableSet<UnsortedSetIterable<String>> nestSet = (MutableSet<UnsortedSetIterable<String>>)iter.next();
//          Iterator iter2 = nestSet.iterator();
//          while (iter2.hasNext()) {
//            System.out.println("--------------------------------");
//            System.out.println("------"+iter2.next()+"------");
//            System.out.println("--------------------------------");
//          }
//        }
        //MutableSet<String> set = UnifiedSet.newSetWith("a1","a2","a3","a4","a5");        
        //MutableSet allCombinations = set.powerSet();
        //System.out.print(allCombinations);
        // for(int i =0; i< powerSet.size(); i++)
        //System.out.println("powerSet = " + allCombinations.powerSet());
        //for (String messagePart : allCombinations)
    }

    private ArrayList<DefaultMutableTreeNode> buildDocumentResponse(BindingOperation bindingOperation, XmlCursor cursor, SampleXmlUtility xmlGenerator, List<String> parametersToBeRemoved, String serviceName)
            throws Exception {
        Part[] parts = WsdlUtils.getOutputParts(bindingOperation);
        ArrayList<DefaultMutableTreeNode> nodes = new ArrayList<DefaultMutableTreeNode>();
        for (int i = 0; i < parts.length; i++) {
            Part part = parts[i];
            if (!WsdlUtils.isAttachmentOutputPart(part, bindingOperation)
                    && (part.getElementName() != null || part.getTypeName() != null)) {
                XmlCursor c = cursor.newCursor();
                c.toLastChild();
                Parameter parameter = new Parameter();
                parameter.setName(part.getElementName().getLocalPart());
                if (part.getTypeName() != null)
                    parameter.setType(part.getTypeName().getLocalPart());
                //parameter.setComplex(true);  //by default, the root parameter is complex
                parameter.setParameterUniqueIDinTree(AnalysisStats.parameterUniqueIDinTree);
                DefaultMutableTreeNode partNode = new DefaultMutableTreeNode(parameter);
                DefaultMutableTreeNode updatedNode = createElementForPart(part, c, xmlGenerator, partNode, parametersToBeRemoved, null, serviceName);
                nodes.add(updatedNode);
                c.dispose();
            }
        }
        return nodes;
    }

//    public ArrayList<ArrayList<Integer>> permute(int[] num) {  //change this to string to permute what's in the JTree
//        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
//
//        //start from an empty list
//        result.add(new ArrayList<Integer>());
//
//        for (int i = 0; i < num.length; i++) {
//            //list of list in current iteration of the array num
//            ArrayList<ArrayList<Integer>> current = new ArrayList<ArrayList<Integer>>();
//
//            for (ArrayList<Integer> l : result) {
//                // # of locations to insert is largest index + 1
//                for (int j = 0; j < l.size() + 1; j++) {
//                    // + add num[i] to different locations
//                    l.add(j, num[i]);
//
//                    ArrayList<Integer> temp = new ArrayList<Integer>(l);
//                    current.add(temp);
//
//				//System.out.println(temp);
//                    // - remove num[i] add
//                    l.remove(j);
//                }
//            }
//
//            result = new ArrayList<ArrayList<Integer>>(current);
//        }
//
//        return result;
//    }
    private void buildRpcRequest(BindingOperation bindingOperation, XmlCursor cursor, SampleXmlUtility xmlGenerator, List<String> parametersToBeRemoved, List<Parameter> parametersToTry, String serviceName)
            throws Exception {
        // rpc requests use the operation name as root element
        String ns = WsdlUtils.getSoapBodyNamespace(bindingOperation.getBindingInput().getExtensibilityElements());
        if (ns == null) {
            ns = WsdlUtils.getTargetNamespace(wsdlContext.getDefinition());
            log.warn("missing namespace on soapbind:body for RPC request, using targetNamespace instead (BP violation)");
        }
        cursor.beginElement(new QName(ns, bindingOperation.getName()));
        if (xmlGenerator.isSoapEnc()) {
            cursor.insertAttributeWithValue(new QName(wsdlContext.getSoapVersion().getEnvelopeNamespace(),
                    "encodingStyle"), wsdlContext.getSoapVersion().getEncodingNamespace());
        }
        Part[] inputParts = WsdlUtils.getInputParts(bindingOperation);
        for (int i = 0; i < inputParts.length; i++) {
            Part part = inputParts[i];
            if (WsdlUtils.isAttachmentInputPart(part, bindingOperation)) {
                if (iface.getSettings().getBoolean(WsdlSettings.ATTACHMENT_PARTS)) {
                    XmlCursor c = cursor.newCursor();
                    c.toLastChild();
                    c.beginElement(part.getName());
                    c.insertAttributeWithValue("href", part.getName() + "Attachment");
                    c.dispose();
                }
            } else {
                if (wsdlContext.hasSchemaTypes()) {
                    QName typeName = part.getTypeName();
                    if (typeName != null) {
                        SchemaType type = wsdlContext.getInterfaceDefinition().findType(typeName);
                        if (type != null) {
                            XmlCursor c = cursor.newCursor();
                            c.toLastChild();
                            c.insertElement(part.getName());
                            c.toPrevToken();
                            xmlGenerator.createSampleForType(type, c, null, null, null, parametersToBeRemoved, parametersToTry, serviceName, null);
                            c.dispose();
                        } else {
                            log.warn("Failed to find type [" + typeName + "]");
                        }
                    } else {
                        SchemaGlobalElement element = wsdlContext.getSchemaTypeLoader().findElement(part.getElementName());
                        if (element != null) {
                            XmlCursor c = cursor.newCursor();
                            c.toLastChild();
                            c.insertElement(element.getName());
                            c.toPrevToken();
                            xmlGenerator.createSampleForType(element.getType(), c, null, null, null, parametersToBeRemoved, parametersToTry, serviceName, null);
                            c.dispose();
                        } else {
                            log.warn("Failed to find element [" + part.getElementName() + "]");
                        }
                    }
                }
            }
        }
    }

    private void buildRpcResponse(BindingOperation bindingOperation, XmlCursor cursor, SampleXmlUtility xmlGenerator, List<String> parametersToBeRemoved, String serviceName)
            throws Exception {
// rpc requests use the operation name as root element
        BindingOutput bindingOutput = bindingOperation.getBindingOutput();
        String ns = bindingOutput == null ? null : WsdlUtils.getSoapBodyNamespace(bindingOutput
                .getExtensibilityElements());
        if (ns == null) {
            ns = WsdlUtils.getTargetNamespace(wsdlContext.getDefinition());
            log.warn("missing namespace on soapbind:body for RPC response, using targetNamespace instead (BP violation)");
        }
        cursor.beginElement(new QName(ns, bindingOperation.getName() + "Response"));
        if (xmlGenerator.isSoapEnc()) {
            cursor.insertAttributeWithValue(new QName(wsdlContext.getSoapVersion().getEnvelopeNamespace(),
                    "encodingStyle"), wsdlContext.getSoapVersion().getEncodingNamespace());
        }
        Part[] inputParts = WsdlUtils.getOutputParts(bindingOperation);
        for (int i = 0; i < inputParts.length; i++) {
            Part part = inputParts[i];
            if (WsdlUtils.isAttachmentOutputPart(part, bindingOperation)) {
                if (iface.getSettings().getBoolean(WsdlSettings.ATTACHMENT_PARTS)) {
                    XmlCursor c = cursor.newCursor();
                    c.toLastChild();
                    c.beginElement(part.getName());
                    c.insertAttributeWithValue("href", part.getName() + "Attachment");
                    c.dispose();
                }
            } else {
                if (wsdlContext.hasSchemaTypes()) {
                    QName typeName = part.getTypeName();
                    if (typeName != null) {
                        SchemaType type = wsdlContext.getInterfaceDefinition().findType(typeName);
                        if (type != null) {
                            XmlCursor c = cursor.newCursor();
                            c.toLastChild();
                            c.insertElement(part.getName());
                            c.toPrevToken();
                            xmlGenerator.createSampleForType(type, c, null, null, null, parametersToBeRemoved, null, serviceName,null);
                            c.dispose();
                        } else {
                            log.warn("Failed to find type [" + typeName + "]");
                        }
                    } else {
                        SchemaGlobalElement element = wsdlContext.getSchemaTypeLoader().findElement(part.getElementName());
                        if (element != null) {
                            XmlCursor c = cursor.newCursor();
                            c.toLastChild();
                            c.insertElement(element.getName());
                            c.toPrevToken();
                            xmlGenerator.createSampleForType(element.getType(), c, null, null, null, parametersToBeRemoved, null, serviceName,null);
                            c.dispose();
                        } else {
                            log.warn("Failed to find element [" + part.getElementName() + "]");
                        }
                    }
                }
            }
        }
    }

}
