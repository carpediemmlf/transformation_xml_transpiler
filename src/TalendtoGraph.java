import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.io.*;
import org.jgrapht.traverse.*;
import org.jgrapht.util.SupplierUtil;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.Supplier;


public class Grapher {
    public static Graph<String, DefaultEdge> makeGraph(String fileName) {

        Graph<String, DefaultEdge> a = new SimpleGraph<>(DefaultEdge.class);
        ReadXMLFile b = new ReadXMLFile();

        try {
            List nodeType = b.getVertices(fileName);
            Map connector = b.getEdges(fileName);
            System.out.println(connector);
            //String x=b.getEdges("C:\\Users\\prasaha\\Documents\\talend.item").toString();
            String x = connector.keySet().toArray()[0].toString();
            a.addVertex(x);
            a.addEdge(nodeType.get(0).toString(), nodeType.get(1).toString());

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

return a;
    
}
    

            
    public static void printGraph (Graph a){
        Iterator<String> iter = new DepthFirstIterator<>(a);
        while (iter.hasNext()) {
            String vertex = iter.next();
            System.out.println(
                    "Vertex " + vertex + " is connected to: "
                            + a.edgesOf(vertex).toString());
        }

    }

}









