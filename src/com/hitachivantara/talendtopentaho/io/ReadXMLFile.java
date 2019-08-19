package com.hitachivantara.talendtopentaho.io;

import com.hitachivantara.talendtopentaho.io.*;
import com.hitachivantara.talendtopentaho.nodetype.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class ReadXMLFile {

    private HashMap<String, String> myHashMap;


    public HashMap getVertices(String fileName) throws ParserConfigurationException, IOException, SAXException {
        myHashMap = new LinkedHashMap();
        //childHashMap=new LinkedHashMap<>();

        // Read in file located at same level as the root of the project folder.

        File fXmlFile = new File(fileName);

        // Build DOM Document. Throws ParserConfigurationException.

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();


        // Throws IOException and SAXException.

        Document doc = dBuilder.parse(fXmlFile);

        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work

        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("node");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            HashMap map = new LinkedHashMap();
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                System.out.println("Node : " + eElement.getAttribute("componentName"));

                for (int a = 1; a < nNode.getChildNodes().getLength(); a++) {
                    if ((nNode.getChildNodes().item(a).getNodeType() == 1)) {
                        Node childNode = nNode.getChildNodes().item(a);


                        String key = nNode.getChildNodes().item(a).getAttributes().item(1).toString().replaceAll("name=", "");
                        Node value = nNode.getChildNodes().item(a).getAttributes().getNamedItem("value");
                        if (key != null && value != null) {
                            map.put(key, value.toString().replaceAll("value=", ""));


                        }
                        if (childNode.hasChildNodes() && Node.ELEMENT_NODE == nNode.getNodeType()) {
                            HashMap childHashMap = new LinkedHashMap();
                            for (int b = 0; b < childNode.getChildNodes().getLength(); b++) {


                                if (childNode.getChildNodes().item(b).hasAttributes()) {


                                    String key2 = childNode.getChildNodes().item(b).getAttributes().item(0).toString().replaceAll("elementRef=", "");
                                    String value2 = childNode.getChildNodes().item(b).getAttributes().item(1).toString().replaceAll("value=", "");
                                    if (key2 != null && value2 != null) {
                                        childHashMap.put(key2, value2);

                                    }
                                }
                            }
                            System.out.println(childHashMap);
                        }

                    }

                }
                // System.out.println(map);

            }


        }


        return myHashMap;

    }

}
