package com.hitachivantara.talendtopentaho.io;

import com.hitachivantara.talendtopentaho.io.*;
import com.hitachivantara.talendtopentaho.nodetype.*;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Iterator;
import java.util.Set;

public class PentahoToGraph {

    private Document document;
    Graph<PentNode, DefaultEdge> graph;

    public PentahoToGraph(String filename){

        try {
            // setting up documents to read from and write to, as xml document objects
//            InputStream inputStream = new FileInputStream(new File("pentaho_input_output.ktr")); //PROBLEM: when path is translated.xml, graph is NOT drawn
            InputStream inputStream = new FileInputStream(new File(/*"pentahoXML\\" + *//*"PentahoTemplates\\"+*/filename)); //PROBLEM: when path is translated.xml, graph is NOT drawn


            DocumentBuilderFactory Pfactory = DocumentBuilderFactory.newDefaultInstance();
            DocumentBuilder Pbuilder = Pfactory.newDocumentBuilder();

            document = Pbuilder.parse(inputStream);

            graph=createGraph();
            addVertices();
            addEdges();
            System.out.println(graph);

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

    private Graph<PentNode, DefaultEdge> createGraph(){
        return GraphTypeBuilder.<PentNode,DefaultEdge>directed().allowingMultipleEdges(true).allowingSelfLoops(false).edgeClass(DefaultEdge.class).weighted(false).buildGraph();
    }

    private void addVertices(){
        NodeList vertices = document.getElementsByTagName("step");
        for (int i = 0; i < vertices.getLength(); i++){
            // call method, passing through xml step, and takes in the data
            String vertexName = vertices.item(i).getChildNodes().item(1).getTextContent();
            String vertexType = vertices.item(i).getChildNodes().item(3).getTextContent();
//            System.out.println(vertexName + "  "+ vertexType);

            /*PentNode vertex = new PentNode(vertexName, vertexType);
            *//*PentNode vertex = new PentNode();
            vertex.setName(vertexName);
            vertex.setType(vertexType);*/

            PentNode vertex = null;
            switch (vertexType){
                case "CsvInput":
                    vertex = new CSVInputNode(vertexName, vertexType,"DUMMY: filename");
                    ((CSVInputNode) vertex).addField("Field 1");
                    break;
                case "TextFileOutput":
                    vertex = new TextOutputNode(vertexName, vertexType, "filename55");
                    ((TextOutputNode) vertex).addField("Out field");
                    break;
                case "SelectValues":
                    vertex = new SelectValuesNode(vertexName, vertexType);
                    ((SelectValuesNode) vertex).addField("Field 1", "Field One");
                    break;
                case "SortRows":
                    vertex = new SortNode(vertexName, vertexType);
                    ((SortNode) vertex).addField("Field 2", "Y", "N");
                    break;
                case "MergeJoin":
                    vertex = new MergeNode(vertexName, vertexType, "DUMMY: joinType", "DUMMY: step1", "DUMMY: step2", "DUMMY: key1", "DUMMY: key2");
                    break;
                case "GroupBy":
                    vertex = new GroupByNode(vertexName, vertexType);
                    ((GroupByNode) vertex).addFieldToGroupBy("Dummy field1");
                    ((GroupByNode) vertex).addAggregateField("Dummy Agrregate", "Dummy subject", "dummy type");
                    break;
                case "FilterRows":
                    vertex = new FilterNode(vertexName, vertexType);
                    ((FilterNode) vertex).addCondition("Y","Field 1", "<", "Field 2");
                    break;
                default:
                    vertex = new PentNode(vertexName,vertexType);
            }

            graph.addVertex(vertex);

/*
            graph.addVertex(vertex);
*/
        }
    }

    private void addEdges(){
        NodeList edges = document.getElementsByTagName("hop");
        for (int i = 0; i < edges.getLength(); i++){
            // call method, passing through xml step, and takes in the data
            String sourceName = edges.item(i).getChildNodes().item(1).getTextContent();
            String targetName = edges.item(i).getChildNodes().item(3).getTextContent();

            PentNode source = null;
            PentNode target = null;

            Set vertexSet = graph.vertexSet();
            Iterator vertexIterator = vertexSet.iterator();

            while (vertexIterator.hasNext()){
                PentNode vertex = (PentNode) vertexIterator.next();
                if (vertex.getName().equals(sourceName)){
                    source=vertex;
                }
                if (vertex.getName().equals(targetName)){
                    target=vertex;
                }
            }
            if (source!=null & target!=null){
                graph.addEdge(source, target);
            }
            else {
                System.out.println("Nodes not found in graph");
            }
        }
    }

    public Graph<PentNode, DefaultEdge> getGraph(){
        return graph;
    }
}
