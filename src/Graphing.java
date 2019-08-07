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

public final class Graphing
{
    private Graphing() {}

    public static void main(String[] args)
            throws URISyntaxException,
            ExportException, IOException, SAXException, ParserConfigurationException {
        Graph<String, DefaultEdge> a = new SimpleGraph<>(DefaultEdge.class);
        ReadXMLFile b= new ReadXMLFile();

        List nodes= b.getVertices("C:\\Users\\prasaha\\Documents\\talend.item");
        for ( Object element : nodes) {
            a.addVertex(element.toString());
        }

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
        exporter.exportGraph(a, writer);
        System.out.println(writer.toString());

        // Write to a .dot file.
        String fileName = "trish_visualization.dot";
        Mapping.writeStringToNewFile(writer.toString(), fileName);

        // -----------------------------------------------


    }
    }
