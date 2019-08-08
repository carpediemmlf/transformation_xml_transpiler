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
    private Document document;
    private Document translatedDoc;

    public WriteXMLFile(Graph<PentNode, DefaultEdge> graph) {
        this.graph=graph;

        try {
            // setting up documents to read from and write to, as xml document objects
            InputStream inputStream = new FileInputStream(new File("talend_input_output.item"));
            InputStream inputStreamPenTemplate = new FileInputStream("PentahoTemplates\\EmptyTransformation.ktr");

            DocumentBuilderFactory factoryT = DocumentBuilderFactory.newDefaultInstance();
            DocumentBuilder builderT = factoryT.newDocumentBuilder();
            DocumentBuilderFactory factoryP = DocumentBuilderFactory.newDefaultInstance();
            DocumentBuilder builderP = factoryP.newDocumentBuilder();

            document = builderT.parse(inputStream);
            translatedDoc = builderP.parse(inputStreamPenTemplate);

            for (PentNode v: graph.vertexSet()){
                addStep(v);
            }

            /*for (PentNode v: graph.vertexSet()){
                switch (v.getType()){
                    case "CsvInput":
                        addStep_inputCSV(v);
                        break;
                    case "TextFileOutput":
                        addStep_outputText(v);
                        break;
                }
            }*/

            for (DefaultEdge e: graph.edgeSet()){
                addHop(graph.getEdgeSource(e).getName(),graph.getEdgeTarget(e).getName());
            }


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

    public void addStep (PentNode v){
        try {
            Document documentI = null;
            switch (v.getType()){
                case "CsvInput":
                    documentI = getTemplateStepDoc("csvInputStep");
                    break;
                case "TextFileOutput":
                    documentI = getTemplateStepDoc("textOutputStep");
                    break;
            }

            NodeList nodes = documentI.getFirstChild().getChildNodes();
            for (int i =0; i < nodes.getLength();i++){
//                System.out.println(nodes.item(i).getNodeName());
                String variable = nodes.item(i).getNodeName();
//                System.out.println(nodes.item(i).getChildNodes().getLength());
                if (nodes.item(i).getNodeType()!=3){                        // getting rid of text nodes
                    if (nodes.item(i).getChildNodes().getLength() <= 1) {
                        System.out.println(nodes.item(i).getNodeName());
                        System.out.println("yes");
                        if (v.getSimpleInfo().containsKey(variable)) {
                            System.out.println("  IN IF WHICH CONTAINS INFO");
                            nodes.item(i).setTextContent(v.getSimpleInfo().get(variable));
                        }
                    }
                    else{
//                        System.out.println("no");
                    }
                }
                /*try{
                    nodes.item(i).setTextContent(v.ge);
                }catch(NullPointerException dataNotThere){
                    System.out.println("Data doesn't exist");
                }*/

            }




        } catch (Exception TemplateNotFound){
            System.out.println(TemplateNotFound.getMessage());
        }

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
    // Temporary get method to get step info
    public ArrayList<String> getStepInfo (Integer i){
        ArrayList<String> info = new ArrayList<>();
        NodeList nodes = document.getElementsByTagName("node");

        // adding name to arraylist
        info.add(nodes.item(i).getChildNodes().item(1).getAttributes().getNamedItem("value").getNodeValue());
        // adding type to arraylist
        info.add(nodes.item(i).getAttributes().getNamedItem("componentName").getNodeValue());
        // adding filename to input node
        if (info.get(1).equals("tFileInputDelimited")){
            info.add(nodes.item(i).getChildNodes().item(9).getAttributes().getNamedItem("value").getNodeValue());
        }

       /* for (String s : info){
            System.out.println(s);
        }*/
        return info;
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



