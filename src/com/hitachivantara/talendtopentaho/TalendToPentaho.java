package com.hitachivantara.talendtopentaho;

import com.hitachivantara.talendtopentaho.io.*;
import com.hitachivantara.talendtopentaho.nodetype.*;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.ExportException;
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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;


public class TalendToPentaho {
    public static void main (String [] args)
            throws URISyntaxException, ExportException, IOException, ParserConfigurationException, SAXException {
        String inputFileName = args[0];
        TalToGraph talGrapher = new TalToGraph(inputFileName);
        System.out.println(talGrapher.getGraph());
        Mapping mapper = new Mapping(talGrapher.getGraph());
        mapper.map();
        WriteXMLFile writer = new WriteXMLFile(mapper.getOutputGraph());
        writer.writeXML();
    }
}