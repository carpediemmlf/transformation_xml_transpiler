
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
    public static void main  (String [] args)
            throws URISyntaxException, ExportException, IOException, ParserConfigurationException, SAXException {

        // --------------------------------------------
        // Parser practice = new Parser();
        // practice.printTesting();
        // ---------------------------------------------

        // create a graph based on URI objects
        Graph<URI, DefaultEdge> hrefGraph = Mapping.createHrefGraph();

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
        Mapping.writeStringToNewFile(writer.toString(), fileName);

        // -----------------------------------------------

        Map<String, String> vertices = ReadXMLFile.getVertices("talend_input_output.item");
        System.out.println(vertices);
    }




}
