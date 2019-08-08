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
import java.util.Iterator;
import java.util.Set;

public class TalToGraph {

    private Document document;
    Graph<TalNode, DefaultEdge> graph;

    public TalToGraph(String talendXMLName){

        try {
            // setting up documents to read from and write to, as xml document objects
            InputStream inputStream = new FileInputStream(new File(talendXMLName)); //PROBLEM: when path is translated.xml, graph is NOT drawn

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
            // call method, passing through xml step, and takes in the data
            String vertexName = vertices.item(i).getChildNodes().item(1).getAttributes().getNamedItem("value").getNodeValue();
            String vertexType = vertices.item(i).getChildNodes().item(1).getAttributes().getNamedItem("value").getNodeValue();
            // System.out.println(vertexName + "  "+ vertexType);

            TalNode vertex = new TalNode();
            vertex.setName(vertexName);
            vertex.setType(vertexType);

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




















