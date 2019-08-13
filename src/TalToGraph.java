import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class TalToGraph {

    private Document document;
    Graph<TalNode, DefaultEdge> graph;

    public TalToGraph(String talendXMLName){

        try {
            InputStream inputStream = new FileInputStream(new File(talendXMLName));

            DocumentBuilderFactory Tfactory = DocumentBuilderFactory.newDefaultInstance();
            DocumentBuilder Tbuilder = Tfactory.newDocumentBuilder();

            document = Tbuilder.parse(inputStream);

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

    private Graph<TalNode, DefaultEdge> createGraph(){
        return GraphTypeBuilder.<TalNode,DefaultEdge>directed().allowingMultipleEdges(true).allowingSelfLoops(false).edgeClass(DefaultEdge.class).weighted(false).buildGraph();
    }

    public void addVertices(){
        NodeList vertices = document.getElementsByTagName("node");
        for (int i = 0; i < vertices.getLength(); i++){
            TalNode vertex;
            // call method, passing through xml step, and takes in the data
            String vertexName = vertices.item(i).getChildNodes().item(1).getAttributes().getNamedItem("value").getNodeValue();
            String vertexType = vertices.item(i).getAttributes().getNamedItem("componentName").getNodeValue();
            vertex = new TalNode(vertexName, vertexType);
            // System.out.println(vertexName + "  "+ vertexType);
            for (int j = 0; j < vertices.item(i).getChildNodes().getLength(); j++){
                Node nodeInfo = vertices.item(i).getChildNodes().item(j);
                if (nodeInfo.getNodeType() != 3){
                    try {
                        String field = nodeInfo.getAttributes().getNamedItem("field").getNodeValue();
                        if (!field.equals("TABLE")){
                            String key = nodeInfo.getAttributes().getNamedItem("name").getNodeValue();
                            String data = nodeInfo.getAttributes().getNamedItem("value").getNodeValue();
                            vertex.addSimpleInfo(key, data);
                        } else {
//                            System.out.println(nodeInfo.getAttributes().getNamedItem("name").getNodeValue());

                            HashMap<String, ArrayList<String>> table = vertex.addTable(nodeInfo.getAttributes().getNamedItem("name").getNodeValue());

                            for (int k = 1; k < nodeInfo.getChildNodes().getLength(); k=k+2){
                                String elementRef = nodeInfo.getChildNodes().item(k).getAttributes().getNamedItem("elementRef").getNodeValue();
                                String value = nodeInfo.getChildNodes().item(k).getAttributes().getNamedItem("value").getNodeValue();

                                if (table.containsKey(elementRef)){
                                    table.get(elementRef).add(value);
                                }
                                else {
                                    vertex.addTableInfo(table, elementRef, value);
                                }
                            }
                        }
//                        System.out.println(key +" = "+ data);

                    }catch (NullPointerException NP){
                        System.out.println("Null pointer in data extraction");
                    }
                }
            }

//            System.out.println(vertex.getSimpleInfo());

//            TalNode vertex = new TalNode(vertexName, vertexType);
            vertex.printTable();

            // ERROR CAUGHT FOR METADATA NODES
            graph.addVertex(vertex);
        }
    }

    public void addEdges() {
        //NodeList edges = document.getElementsByTagName("connection");
        //System.out.println(edges.toString());
        // for (int i = 0; i < edges.getLength(); i++){
        //  Node linkNode = edges.item(i);
        NodeList edges = document.getElementsByTagName("connection");
        for (int i = 0; i < edges.getLength(); i++) {
            Node linkNode = edges.item(i);
            if (linkNode.getNodeType() == Node.ELEMENT_NODE) {
                Element linkElement = (Element) linkNode;
                String sourceName = linkElement.getAttribute("source");
                String targetName = linkElement.getAttribute("target");


                // System.out.println("Connection: " + linkElement.getAttribute("label"));
                // call method, passing through xml step, and takes in the data
                // String sourceName = edges.item(i).getAttributes().getNamedItem("source").getNodeName();


                TalNode source = null;
                TalNode target = null;

                Set vertexSet = graph.vertexSet();
                Iterator vertexIterator = vertexSet.iterator();

                while (vertexIterator.hasNext()) {
                    TalNode vertex = (TalNode) vertexIterator.next();
                    if (vertex.getName().equals(sourceName)) {
                        // System.out.println("vertex: " + vertex.getName());
                        // System.out.println("source: " + sourceName);
                        // System.out.println("target: " + targetName);
                        source = vertex;
                    }
                    if (vertex.getName().equals(targetName)) {
                        target = vertex;
                    }
                }
                if (source != null & target != null) {
                    graph.addEdge(source, target);
                } else {
                    System.out.println("Nodes not found in graph");
                }
            }
        }
    }
    public Graph<TalNode, DefaultEdge> getGraph(){
        return graph;
    }
}




















