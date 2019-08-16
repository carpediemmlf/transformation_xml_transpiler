import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;

public class WriteXMLFile {

    private Graph<PentNode, DefaultEdge> graph;
    private Document translatedDoc;

    public WriteXMLFile(Graph<PentNode, DefaultEdge> graph) {
        this.graph=graph;

        try {
            // created xml document from template of empty pentaho transformation, adds vertices then edges
            InputStream inputStreamPenTemplate = new FileInputStream("PentahoTemplates\\EmptyTransformation.ktr");

            DocumentBuilderFactory factoryP = DocumentBuilderFactory.newDefaultInstance();
            DocumentBuilder builderP = factoryP.newDocumentBuilder();

            translatedDoc = builderP.parse(inputStreamPenTemplate);
            // added vertices
            for (PentNode v: graph.vertexSet()){
                System.out.println("- - NEW NODE");
                addStep(v);
            }
            // adding edges
            for (DefaultEdge e: graph.edgeSet()){
                addHop(graph.getEdgeSource(e).getName(),graph.getEdgeTarget(e).getName());
            }
/*
            writeXML();             // if you want constructor to create xml automatically
*/
        } catch (FileNotFoundException notFound){
            System.out.println("File wasn't found");
        } catch (IOException IoE){
            System.out.println("IoE");
        } catch (SAXException SAX){
            System.out.println("SAX");
        } catch (ParserConfigurationException parse){
            System.out.println("Parse exception");
        }

    }

// Add to working document

    private void addStep (PentNode vertex){
        try {
            // documentI is insert document
            Document documentI = null;
            System.out.println(vertex.getType());
            // Switch statement is to insert the appropriate second layer info depending on type
            switch (vertex.getType()){
                case "CsvInput":    // fields
                    documentI = getTemplateStepDoc("csvInputStep");
                    // loop to add to documentI
                    for (CSVInputNode.CSVInputField f : ((CSVInputNode) vertex).getFields()){
                        Document fieldDoc = getTemplateStepDoc("InputFieldTemplate");
                        // loop to add to fieldDoc
                        // loops through fields, if tag info is stored, it will set node text content to it
                        for (int i = 0; i<fieldDoc.getFirstChild().getChildNodes().getLength(); i++){
                            String variable = fieldDoc.getFirstChild().getChildNodes().item(i).getNodeName();
                            if (f.getFieldInfo().containsKey(variable)) {
                                fieldDoc.getFirstChild().getChildNodes().item(i).setTextContent((f.getFieldInfo().get(variable)));
                            }
                        }
                        // insert back into documentI
                        Node inputNode = documentI.importNode(fieldDoc.getFirstChild(), true);
                        Node refNode = documentI.getChildNodes().item(0).getChildNodes().item(43);
                        refNode.appendChild(inputNode);
                    }
                    break;
                case "TextFileOutput":  //fileInfo //fields
                    documentI = getTemplateStepDoc("textOutputStep");
                    // loops through children of file tag and inserts info contained in pentNode
                    NodeList fileNodes = documentI.getElementsByTagName("file").item(0).getChildNodes();
                    for (int i = 0; i< fileNodes.getLength() ;i++){
                        String variable = documentI.getFirstChild().getChildNodes().item(i).getNodeName();
                        if (((TextOutputNode)vertex).getFileInfo().containsKey(variable)){
                            fileNodes.item(i).setTextContent(((TextOutputNode) vertex).getFileInfo().get(variable));
                        }
                    }
                    // loop to add to documentI
                    for (TextOutputNode.TextOutputField f : ((TextOutputNode) vertex).getFields()){
                        Document fieldDoc = getTemplateStepDoc("textOutputFieldTemplate");
                        // loop to add to fieldDoc
                        // loops through fields, if tag info is stored, it will set node text content to it
                        for (int i = 0; i<fieldDoc.getFirstChild().getChildNodes().getLength(); i++){
                            String variable = fieldDoc.getFirstChild().getChildNodes().item(i).getNodeName();
                            if (f.getFieldInfo().containsKey(variable)) {
                                fieldDoc.getFirstChild().getChildNodes().item(i).setTextContent((f.getFieldInfo().get(variable)));
                            }
                        }
                        // insert back into documentI
                        Node inputNode = documentI.importNode(fieldDoc.getFirstChild(), true);
                        Node refNode = documentI.getChildNodes().item(0).getChildNodes().item(43);
                        refNode.appendChild(inputNode);
                    }

                    break;
                case "SelectValues":
                    documentI = getTemplateStepDoc("selectValuesStep");
                    for (SelectValuesNode.SelectValuesField f : ((SelectValuesNode) vertex).getFields()){
                        Document fieldDoc = getTemplateStepDoc("SelectValuesFieldTemplate");
                        // similar method to insert details
                        for (int i = 0; i<fieldDoc.getFirstChild().getChildNodes().getLength(); i++){
                            String variable = fieldDoc.getFirstChild().getChildNodes().item(i).getNodeName();
                            if (f.getFieldInfo().containsKey(variable)) {
                                fieldDoc.getFirstChild().getChildNodes().item(i).setTextContent((f.getFieldInfo().get(variable)));
                            }
                        }
                        // insert back into documentI
                        Node stepNode = documentI.importNode(fieldDoc.getFirstChild(), true);
                        Node refNode = documentI.getChildNodes().item(0).getChildNodes().item(15);
                        refNode.appendChild(stepNode);
                    }
                    break;
                case "SortRows":
                    documentI = getTemplateStepDoc("sortStep");
                    // transferring details in similar method to above methods
                    for (SortNode.SortField f : ((SortNode) vertex).getFields()){
                        Document fieldDoc = getTemplateStepDoc("SortFieldTemplate");
                        for (int i = 0; i<fieldDoc.getFirstChild().getChildNodes().getLength(); i++){
                            String variable = fieldDoc.getFirstChild().getChildNodes().item(i).getNodeName();
                            if (f.getFieldInfo().containsKey(variable)) {
                                fieldDoc.getFirstChild().getChildNodes().item(i).setTextContent((f.getFieldInfo().get(variable)));
                            }
                        }
                        // insert back into documentI
                        Node inputNode = documentI.importNode(fieldDoc.getFirstChild(), true);
                        Node refNode = documentI.getChildNodes().item(0).getChildNodes().item(29);
                        refNode.appendChild(inputNode);
                    }
                    break;
                case "MergeJoin":
                    documentI = getTemplateStepDoc("mergeStep");
                    // adding keys to documentI
                    for (String keyVal : ((MergeNode) vertex).getKey_1()){
                        Document keyDoc = getTemplateStepDoc("MergeKeyTemplate1");
                        keyDoc.getFirstChild().setTextContent(keyVal);

                        Node keyNode = documentI.importNode(keyDoc.getFirstChild(), true);
                        Node refNode = documentI.getChildNodes().item(0).getChildNodes().item(21);
                        refNode.appendChild(keyNode);
                    }
                    for (String  keyVal : ((MergeNode) vertex).getKey_2()){
                        Document keyDoc = getTemplateStepDoc("MergeKeyTemplate1");
                        keyDoc.getFirstChild().setTextContent(keyVal);

                        Node inputNode = documentI.importNode(keyDoc.getFirstChild(), true);
                        Node refNode = documentI.getChildNodes().item(0).getChildNodes().item(23);
                        refNode.appendChild(inputNode);
                    }

                    break;
                case "GroupBy":
                    documentI = getTemplateStepDoc("groupStep");
                    // adding fields to group by to documentI
                    for (String f : ((GroupByNode) vertex).getFieldsToGroupBy()){
                        Document groupDoc = getTemplateStepDoc("GroupByGroupTemplate");
                        groupDoc.getFirstChild().getChildNodes().item(1).setTextContent(f);

                        Node inputNode = documentI.importNode(groupDoc.getFirstChild(), true);
                        Node refNode = documentI.getChildNodes().item(0).getChildNodes().item(31);
                        refNode.appendChild(inputNode);

                    }
                    // adding aggregate info
                    for (GroupByNode.GroupByField f : ((GroupByNode) vertex).getFields()){
                        Document fieldDoc = getTemplateStepDoc("GroupByFieldTemplate");
                        for (int i = 0; i<fieldDoc.getFirstChild().getChildNodes().getLength(); i++){
                            String variable = fieldDoc.getFirstChild().getChildNodes().item(i).getNodeName();
                            if (f.getGroupByFieldInfo().containsKey(variable)) {
                                fieldDoc.getFirstChild().getChildNodes().item(i).setTextContent((f.getGroupByFieldInfo().get(variable)));
                            }
                        }
                        Node inputNode = documentI.importNode(fieldDoc.getFirstChild(), true);
                        Node refNode = documentI.getChildNodes().item(0).getChildNodes().item(33);
                        refNode.appendChild(inputNode);
                    }

                    break;
                case "FilterRows":
                    documentI = getTemplateStepDoc("filterRowsStep");
                    for (FilterNode.Condition c : ((FilterNode) vertex).getConditons()){
                        Document conditionDoc = getTemplateStepDoc("FilterRowsConditionTemplate");
                        for (int i = 0; i<conditionDoc.getFirstChild().getChildNodes().getLength(); i++){
                            String variable = conditionDoc.getFirstChild().getChildNodes().item(i).getNodeName();
                            if (c.getConditionInfo().containsKey(variable)) {
                                conditionDoc.getFirstChild().getChildNodes().item(i).setTextContent((c.getConditionInfo().get(variable)));
                            }
                        }
                        Node inputNode = documentI.importNode(conditionDoc.getFirstChild(), true);
                        Node refNode = documentI.getChildNodes().item(0).getChildNodes().item(19);
                        refNode.appendChild(inputNode);
                    }
                    break;
                case "Dummy":
                    documentI = getTemplateStepDoc("DummyTemplate");
                    break;
                default:
                    documentI = getTemplateStepDoc("DummyTemplate");
                    vertex.setType("Dummy");
                    break;
            }

            // Iterate through step template and check tag name in hashmap of node if info is in node, write it into xml
            NodeList nodes = documentI.getFirstChild().getChildNodes();
            for (int i =0; i < nodes.getLength();i++){
                // tag name to compare with info hashmap
                String variable = nodes.item(i).getNodeName();
                // removes #text nodes
                if (nodes.item(i).getNodeType()!=3){
                    // only nodes with single layer of info
                    if (nodes.item(i).getChildNodes().getLength() <= 1) {
                        // if tag is hashmap- info is available
                        if (vertex.getSimpleInfo().containsKey(variable)) {
                            nodes.item(i).setTextContent(vertex.getSimpleInfo().get(variable));
                        }
                    }
                    else{
                        // add GUI info
                        if (nodes.item(i).getNodeName().equals("GUI")){
                            Document guiDoc = getTemplateStepDoc("stepGuiTemplate");

                            guiDoc.getFirstChild().getChildNodes().item(1).setTextContent(vertex.getxLoc());
                            guiDoc.getFirstChild().getChildNodes().item(3).setTextContent(vertex.getyLoc());

                            Node guiNode = documentI.importNode(guiDoc.getFirstChild(),true);
                            nodes.item(i).getParentNode().replaceChild(guiNode,nodes.item(i));
                        }
                    }
                }
            }
            // inserting step doc into translatedDoc
            Node stepNode = translatedDoc.importNode(documentI.getFirstChild(), true);
            Node refNode = translatedDoc.getChildNodes().item(0).getChildNodes().item(findRefNode(translatedDoc));
            translatedDoc.getChildNodes().item(0).insertBefore(stepNode,refNode);

        } catch (Exception TemplateNotFound){
            System.out.println(TemplateNotFound.getMessage());
        }

    }
    // Used to find step_error_handling node, as this used to insert a step in the correct location - directly above
    private int findRefNode(Document doc){
        for (int n =0 ; n < doc.getFirstChild().getChildNodes().getLength(); n++)
            if ((doc.getFirstChild().getChildNodes().item(n).getNodeName()).equals("step_error_handling")){
                return n;
            }
        return 0;
    }
    // Add hop to xml doc: translatedDoc
    private void addHop (String source, String target){
        try {
            Document documentI = getTemplateStepDoc("hopTemplate");

            // // Add info into step
            documentI.getFirstChild().getChildNodes().item(1).setTextContent(source);
            documentI.getFirstChild().getChildNodes().item(3).setTextContent(target);

            // // Insert Step into main template
            Node hopNode = translatedDoc.importNode(documentI.getFirstChild(), true);

            translatedDoc.getChildNodes().item(0).getChildNodes().item(5).appendChild(hopNode);

        } catch (Exception TemplateNotFound){
            System.out.println(TemplateNotFound.getMessage());
        }
    }
    // gets appropriate template for xml insertion
    private Document getTemplateStepDoc (String pathname) throws Exception{
        Document documentI;
        try { // setting up template document
            InputStream inputStream = new FileInputStream(new File("PentahoTemplates\\"+pathname));

            DocumentBuilderFactory factoryI = DocumentBuilderFactory.newDefaultInstance();
            DocumentBuilder builderI = factoryI.newDocumentBuilder();

            documentI = builderI.parse(inputStream);

        } catch (FileNotFoundException notFound){
            System.out.println("File wasn't found");
            throw new Exception("Template Not Found");
        } catch (IOException IoE){
            System.out.println("IoE");
            throw new Exception("Template Not Found");
        } catch (SAXException SAX){
            System.out.println("SAX");
            throw new Exception("Template Not Found");
        } catch (ParserConfigurationException parse){
            System.out.println("Parse exception");
            throw new Exception("Template Not Found");
        }
        return documentI;
    }


    // Turn translatedDoc into xml
    public void writeXML (){
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(translatedDoc);
            StreamResult streamResult = new StreamResult(new File("translated.xml"));
            transformer.transform(domSource,streamResult);
        } catch (TransformerConfigurationException TCE){
            System.out.println(TCE.getMessage());
        } catch (TransformerException TE){
            System.out.println(TE.getMessage());
        }
    }

}



