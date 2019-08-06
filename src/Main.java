
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.generate.*;
import org.jgrapht.io.*;
import org.jgrapht.traverse.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.util.*;
import java.net.MalformedURLException;

public class Main {
    public static void main  (String [] args) throws URISyntaxException, ExportException, IOException, ParserConfigurationException, SAXException {

        // --------------------------------------------
        // Parser practice = new Parser();

        // practice.printTesting();
        // ---------------------------------------------


        /*

        // create a graph based on URI objects
        Graph<URI, DefaultEdge> hrefGraph = createHrefGraph();

        // ----------------------------------------------
        // use helper classes to define how vertices should be rendered,
        // adhering to the DOT language restrictions
        ComponentNameProvider<URI> vertexIdProvider = new ComponentNameProvider<URI>()
        {
            public String getName(URI uri)
            {
                return uri.getHost().replace('.', '_');
            }
        };
        ComponentNameProvider<URI> vertexLabelProvider = new ComponentNameProvider<URI>()
        {
            public String getName(URI uri)
            {
                return uri.toString();
            }
        };

        GraphExporter<URI, DefaultEdge> exporter =
                new DOTExporter<>(vertexIdProvider, vertexLabelProvider, null);
        Writer writer = new StringWriter();
        exporter.exportGraph(hrefGraph, writer);
        System.out.println(writer.toString());

        // Write to a .dot file.
        String fileName = "exampleGraphviz.dot";
        writeToNewFile(writer.toString(), fileName);

        // -----------------------------------------------



         */



        ReadXMLFile.test("talend_input_output.item");
    }

    private static Graph<URI, DefaultEdge> createHrefGraph() throws URISyntaxException {
        Graph<URI, DefaultEdge> g = new DefaultDirectedGraph(DefaultEdge.class);
        URI google = new URI("http://www.google.com");
        URI wikipedia = new URI("http://www.wikipedia.org");
        URI jgrapht = new URI("http://www.jgrapht.org");
        g.addVertex(google);
        g.addVertex(wikipedia);
        g.addVertex(jgrapht);
        g.addEdge(jgrapht, wikipedia);
        g.addEdge(google, jgrapht);
        g.addEdge(google, wikipedia);
        g.addEdge(wikipedia, google);
        return g;
    }

    private static void writeToNewFile(String str, String fileName)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(str);

        writer.close();
    }
}
