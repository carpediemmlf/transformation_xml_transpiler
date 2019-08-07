import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.io.*;
import org.jgrapht.traverse.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.util.*;


public final class Graphing
{
    private Graphing()
    {
    } 

   
    public static void main(String[] args)
            throws URISyntaxException,
            ExportException, IOException, SAXException, ParserConfigurationException {
        Graph<String, DefaultEdge> a = new SimpleGraph<>(DefaultEdge.class);
        ReadXMLFile b= new ReadXMLFile();

        List nodes= b.getVertices("C:\\Users\\prasaha\\Documents\\talend.item");
        for ( Object element : nodes) {
            a.addVertex(element.toString());
        }


        Map mo= b.getEdges("C:\\Users\\prasaha\\Documents\\talend.item");
        String people = joe.toString();
        String x=b.getEdges("C:\\Users\\prasaha\\Documents\\talend.item").toString();

        System.out.println(a);


    }
    }
