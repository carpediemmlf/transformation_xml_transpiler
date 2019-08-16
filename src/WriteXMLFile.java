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
            // setting up documents to read from and write to, as xml document objects
//            InputStream inputStream = new FileInputStream(new File("talend_input_output.item"));
            InputStream inputStreamPenTemplate = new FileInputStream("PentahoTemplates\\EmptyTransformation.ktr");

            DocumentBuilderFactory factoryP = DocumentBuilderFactory.newDefaultInstance();
            DocumentBuilder builderP = factoryP.newDocumentBuilder();

//            document = builderT.parse(inputStream);
            translatedDoc = builderP.parse(inputStreamPenTemplate);

            for (PentNode v: graph.vertexSet()){
                System.out.println("#####################################################################################");
                System.out.println("#######################################         NEW NODE");
                addStep(v);
            }

            for (DefaultEdge e: graph.edgeSet()){
                addHop(graph.getEdgeSource(e).getName(),graph.getEdgeTarget(e).getName());
            }

            writeXML();


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


    //
    // Add to working document

    public Document getTemplateStepDoc (String pathname) throws Exception{
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

    public void addStep_inputCSV (PentNode v){
        try {
            Document documentI = getTemplateStepDoc("csvInputStep");

            // // Add info into step
//            System.out.println(documentI.getFirstChild().getChildNodes().item(1).getTextContent());
            documentI.getFirstChild().getChildNodes().item(1).setTextContent(v.getName());

/*
            documentI.getFirstChild().getChildNodes().item(1).setTextContent(getStepInfo(0).get(0));    // to take straight from talend

*/

//            System.out.println(documentI.getFirstChild().getChildNodes().item(1).getTextContent());
            /*for (int i =0; i < documentI.getChildNodes().item(0).getChildNodes().getLength();i++){
             *//*if ((StepInfo.get(h)).equals(translatedDoc.getChildNodes().item(0).getChildNodes().item(i).getNodeName())){

                }*//*
                System.out.println(documentI.getChildNodes().item(0).getChildNodes().item(i).getNodeName());
            }*/



            // // Insert Step into main template
            Node stepNode = translatedDoc.importNode(documentI.getFirstChild(), true);
//            System.out.println(stepNode.getNodeType());
//            System.out.println(stepNode.getNodeName());
            Node refNode = translatedDoc.getChildNodes().item(0).getChildNodes().item(7);
            translatedDoc.getChildNodes().item(0).insertBefore(stepNode,refNode);

        } catch (Exception TemplateNotFound){
            System.out.println(TemplateNotFound.getMessage());
        }
        //writeXML();
    }

    public void addStep_outputText (PentNode v){
        try {
            Document documentI = getTemplateStepDoc("textOutputStep");

            // // Add info into step
//            System.out.println(documentI.getFirstChild().getChildNodes().item(1).getTextContent());
            documentI.getFirstChild().getChildNodes().item(1).setTextContent(v.getName());
/*

            documentI.getFirstChild().getChildNodes().item(1).setTextContent(getStepInfo(1).get(0));    // to take straight from talend

*/

//            System.out.println(documentI.getFirstChild().getChildNodes().item(1).getTextContent());

            // // Insert Step into main template
            Node stepNode = translatedDoc.importNode(documentI.getFirstChild(), true);
//            System.out.println(stepNode.getNodeType());
//            System.out.println(stepNode.getNodeName());
            //findRefNode();
//            System.out.println(translatedDoc.getChildNodes().item(0).getChildNodes().item(7));
            Node refNode = translatedDoc.getChildNodes().item(0).getChildNodes().item(8);
            translatedDoc.getChildNodes().item(0).insertBefore(stepNode,refNode);

        } catch (Exception TemplateNotFound){
            System.out.println(TemplateNotFound.getMessage());
        }
        //writeXML();
    }

    public void addStep (PentNode vertex){
        try {
            Document documentI = null;
            System.out.println(vertex.getType());
            switch (vertex.getType()){
                case "CsvInput":
                    documentI = getTemplateStepDoc("csvInputStep");
                    for (CSVInputNode.CSVInputField f : ((CSVInputNode) vertex).getFields()){
                        Document fieldDoc = getTemplateStepDoc("InputFieldTemplate");
                        for (int i = 0; i<fieldDoc.getFirstChild().getChildNodes().getLength(); i++){
                            String variable = fieldDoc.getFirstChild().getChildNodes().item(i).getNodeName();
                            if (f.getFieldInfo().containsKey(variable)) {
                                fieldDoc.getFirstChild().getChildNodes().item(i).setTextContent((f.getFieldInfo().get(variable)));
                            }
                        }
//                        System.out.println(fieldDoc.getFirstChild().getChildNodes().item(1).getNodeName());
//                        fieldDoc.getFirstChild().getChildNodes().item(1).setTextContent(f.getName());

                        Node inputNode = documentI.importNode(fieldDoc.getFirstChild(), true);
//                        System.out.println(documentI.getChildNodes().item(0).getChildNodes().item(43));
                        Node refNode = documentI.getChildNodes().item(0).getChildNodes().item(43);
                        refNode.appendChild(inputNode);
                    }
                    break;
                case "TextFileOutput":
                    documentI = getTemplateStepDoc("textOutputStep");

                    NodeList fileNodes = documentI.getElementsByTagName("file").item(0).getChildNodes();
                    for (int i = 0; i< fileNodes.getLength() ;i++){
                        String variable = documentI.getFirstChild().getChildNodes().item(i).getNodeName();
                        if (((TextOutputNode)vertex).getFileInfo().containsKey(variable)){
                            fileNodes.item(i).setTextContent(((TextOutputNode) vertex).getFileInfo().get(variable));
                        }
                    }
                    for (TextOutputNode.TextOutputField f : ((TextOutputNode) vertex).getFields()){
                        Document fieldDoc = getTemplateStepDoc("textOutputFieldTemplate");
                        for (int i = 0; i<fieldDoc.getFirstChild().getChildNodes().getLength(); i++){
                            String variable = fieldDoc.getFirstChild().getChildNodes().item(i).getNodeName();
                            if (f.getFieldInfo().containsKey(variable)) {
                                fieldDoc.getFirstChild().getChildNodes().item(i).setTextContent((f.getFieldInfo().get(variable)));
                            }
                        }
                        Node inputNode = documentI.importNode(fieldDoc.getFirstChild(), true);
//                        System.out.println(documentI.getChildNodes().item(0).getChildNodes().item(43));
                        Node refNode = documentI.getChildNodes().item(0).getChildNodes().item(43);
                        refNode.appendChild(inputNode);
                    }

                    break;
                case "SelectValues":
                    documentI = getTemplateStepDoc("selectValuesStep");
                    for (SelectValuesNode.SelectValuesField f : ((SelectValuesNode) vertex).getFields()){
                        Document fieldDoc = getTemplateStepDoc("SelectValuesFieldTemplate");

                        for (int i = 0; i<fieldDoc.getFirstChild().getChildNodes().getLength(); i++){
                            String variable = fieldDoc.getFirstChild().getChildNodes().item(i).getNodeName();
                            if (f.getFieldInfo().containsKey(variable)) {
                                fieldDoc.getFirstChild().getChildNodes().item(i).setTextContent((f.getFieldInfo().get(variable)));
                            }
                        }

//                        System.out.println(fieldDoc.getFirstChild().getChildNodes().item(3).getNodeName());
/*                        fieldDoc.getFirstChild().getChildNodes().item(1).setTextContent(f.getName());
                        fieldDoc.getFirstChild().getChildNodes().item(3).setTextContent(f.getRename());*/

                        Node stepNode = documentI.importNode(fieldDoc.getFirstChild(), true);
//                        System.out.println(documentI.getChildNodes().item(0).getChildNodes().item(15));
                        Node refNode = documentI.getChildNodes().item(0).getChildNodes().item(15);
                        refNode.appendChild(stepNode);
                    }
                    break;
                case "SortRows":
                    documentI = getTemplateStepDoc("sortStep");
                    for (SortNode.SortField f : ((SortNode) vertex).getFields()){
                        Document fieldDoc = getTemplateStepDoc("SortFieldTemplate");
//                        System.out.println(fieldDoc.getFirstChild().getChildNodes().item(1).getNodeName());

                        for (int i = 0; i<fieldDoc.getFirstChild().getChildNodes().getLength(); i++){
                            String variable = fieldDoc.getFirstChild().getChildNodes().item(i).getNodeName();
                            if (f.getFieldInfo().containsKey(variable)) {
                                fieldDoc.getFirstChild().getChildNodes().item(i).setTextContent((f.getFieldInfo().get(variable)));
                            }
                        }
                        /*fieldDoc.getFirstChild().getChildNodes().item(1).setTextContent(f.getName());
                        fieldDoc.getFirstChild().getChildNodes().item(3).setTextContent(f.getAscending());
                        fieldDoc.getFirstChild().getChildNodes().item(5).setTextContent(f.getCase_sensitive());*/

                        Node stepNode = documentI.importNode(fieldDoc.getFirstChild(), true);
//                        System.out.println(documentI.getChildNodes().item(0).getChildNodes().item(29));
                        Node refNode = documentI.getChildNodes().item(0).getChildNodes().item(29);
                        refNode.appendChild(stepNode);
                    }
                    break;
                case "MergeJoin":
                    documentI = getTemplateStepDoc("mergeStep");
                    /*System.out.println(documentI.getFirstChild().getChildNodes().item(15).getNodeName());
                    System.out.println(documentI.getFirstChild().getChildNodes().item(17).getNodeName());*/
                    MergeNode v = (MergeNode) vertex;
                    /*documentI.getFirstChild().getChildNodes().item(15).setTextContent(v.getJoin_type());
                    documentI.getFirstChild().getChildNodes().item(17).setTextContent(v.getStep1());
                    documentI.getFirstChild().getChildNodes().item(19).setTextContent(v.getStep2());*/
                    for (String keyVal : v.getKey_1()){
                        Document keyDoc = getTemplateStepDoc("MergeKeyTemplate1");
                        keyDoc.getFirstChild().setTextContent(keyVal);
                        Node stepNode = documentI.importNode(keyDoc.getFirstChild(), true);
//                        System.out.println(documentI.getChildNodes().item(0).getChildNodes().item(21));
                        Node refNode = documentI.getChildNodes().item(0).getChildNodes().item(21);
                        refNode.appendChild(stepNode);
                    }
                    for (String keyVal : v.getKey_1()){
                        Document keyDoc = getTemplateStepDoc("MergeKeyTemplate1");
                        keyDoc.getFirstChild().setTextContent(keyVal);
                        Node stepNode = documentI.importNode(keyDoc.getFirstChild(), true);
//                        System.out.println(documentI.getChildNodes().item(0).getChildNodes().item(23));
                        Node refNode = documentI.getChildNodes().item(0).getChildNodes().item(23);
                        refNode.appendChild(stepNode);
                    }
                    /*documentI.getFirstChild().getChildNodes().item(21).appendChild(key1);
                    documentI.getFirstChild().getChildNodes().item(21).appendChild(key1);*/

                    break;
                case "GroupBy":
                    documentI = getTemplateStepDoc("groupStep");
                    for (String f : ((GroupByNode) vertex).getFieldsToGroupBy()){
                        Document groupDoc = getTemplateStepDoc("GroupByGroupTemplate");
//                        System.out.println(groupDoc.getFirstChild().getChildNodes().item(1).getNodeName());
                        groupDoc.getFirstChild().getChildNodes().item(1).setTextContent(f);

                        Node inputNode = documentI.importNode(groupDoc.getFirstChild(), true);
//                        System.out.println(documentI.getChildNodes().item(0).getChildNodes().item(31));
                        Node refNode = documentI.getChildNodes().item(0).getChildNodes().item(31);
                        refNode.appendChild(inputNode);

                    }
                    for (GroupByNode.GroupByField f : ((GroupByNode) vertex).getFields()){
                        Document fieldDoc = getTemplateStepDoc("GroupByFieldTemplate");
                        for (int i = 0; i<fieldDoc.getFirstChild().getChildNodes().getLength(); i++){
                            String variable = fieldDoc.getFirstChild().getChildNodes().item(i).getNodeName();
                            if (f.getGroupByFieldInfo().containsKey(variable)) {
                                fieldDoc.getFirstChild().getChildNodes().item(i).setTextContent((f.getGroupByFieldInfo().get(variable)));
                            }
                        }
                        Node inputNode = documentI.importNode(fieldDoc.getFirstChild(), true);
//                        System.out.println(documentI.getChildNodes().item(0).getChildNodes().item(31));
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
//                        System.out.println(documentI.getChildNodes().item(0).getChildNodes().item(19));
                        Node refNode = documentI.getChildNodes().item(0).getChildNodes().item(19);
                        refNode.appendChild(inputNode);
                    }
                    break;
//                case "Dummy":
                default:
                    documentI = getTemplateStepDoc("DummyTemplate");
//                    System.out.println(vertex.getName());
                    vertex.setType("Dummy");
                    break;

            }

            NodeList nodes = documentI.getFirstChild().getChildNodes();
            for (int i =0; i < nodes.getLength();i++){

//                System.out.println(nodes.item(i).getNodeName());
                String variable = nodes.item(i).getNodeName();
//                System.out.println(nodes.item(i).getChildNodes().getLength());
                if (nodes.item(i).getNodeType()!=3){                        // getting rid of text nodes
                    if (nodes.item(i).getChildNodes().getLength() <= 1) {

                        /*System.out.println(nodes.item(i).getNodeName());
                        System.out.println("yes");*/
                        if (vertex.getSimpleInfo().containsKey(variable)) {
                            System.out.println("  IN IF WHICH CONTAINS INFO");
                            nodes.item(i).setTextContent(vertex.getSimpleInfo().get(variable));
                        }
                    }
                    else{
                        if (nodes.item(i).getNodeName().equals("GUI")){
                            Document guiDoc = getTemplateStepDoc("stepGuiTemplate");
                            System.out.println(guiDoc.getFirstChild().getChildNodes().item(1).getNodeName());
                            System.out.println(guiDoc.getFirstChild().getChildNodes().item(3).getNodeName());
//                            guiDoc.getFirstChild().getChildNodes().item(1).setTextContent();
                        }
                        /*System.out.println(nodes.item(i).getChildNodes().item(1).getNodeName());
                        System.out.println(nodes.item(i).getChildNodes().item(1).getChildNodes().getLength());*/
                        /*System.out.println(nodes.item(i).getNodeName());
                        System.out.println("no");*/
                    }
                }

                /*try{
                    nodes.item(i).setTextContent(v.ge);
                }catch(NullPointerException dataNotThere){
                    System.out.println("Data doesn't exist");
                }*/

            }

            Node stepNode = translatedDoc.importNode(documentI.getFirstChild(), true);
            Node refNode = translatedDoc.getChildNodes().item(0).getChildNodes().item(findRefNode(translatedDoc));
            translatedDoc.getChildNodes().item(0).insertBefore(stepNode,refNode);

//            System.out.println(refNode.getNodeName());


        } catch (Exception TemplateNotFound){
            System.out.println(TemplateNotFound.getMessage());
        }

    }

    public int findRefNode(Document doc){
        for (int n =0 ; n < doc.getFirstChild().getChildNodes().getLength(); n++)
            if ((doc.getFirstChild().getChildNodes().item(n).getNodeName()).equals("step_error_handling")){
                return n;
            }
        return 0;
    }

    public void addHop (String source, String target){
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
//        writeXML();
    }

    //
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



