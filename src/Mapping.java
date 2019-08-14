import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.generate.*;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.io.*;
import org.jgrapht.traverse.*;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.util.*;
import java.net.MalformedURLException;

public class Mapping {
    private GraphIterator<TalNode, DefaultEdge> talInputIterator;

    // Initialize static type dictionary.
    private static Map<String, String> mappingDict = new HashMap<String, String>();
    static {
        // One-to-one.
        // mappingDict.put("", "MergeNode");
        // mappingDict.put("", "SelectValuesNode");
        // mappingDict.put("", "FilterNode");
        mappingDict.put("tLogRow", "");
        mappingDict.put("tSortRow", "SortRows");
        mappingDict.put("tFileInputDelimited", "CsvInput");
        mappingDict.put("tAggregateSortedRow", "GroupByNode");
        mappingDict.put("tFileOutputDelimited", "TextFileOutput");
        // One-to-two.
        // mappingDict.put("a", "A_D");
        // mappingDict.put("a", "A");
        // mappingDict.put("a", "A");
    };


    private Graph<TalNode, DefaultEdge> talInputGraph;
    private Graph<PentNode, DefaultEdge> pentOutputGraph;

    // Constructors. Overloaded.
    Mapping() { }
    Mapping(Graph<TalNode, DefaultEdge> inputTalGraph) {

        talInputGraph = inputTalGraph;
        // Instantiate Iterator.
        instantiateTalTopologicalIterator();
    }

    // Muhammed is working on this.
    public void map() {
        while (talInputIterator.hasNext()) {
            System.out.println(((talInputIterator.next())).getType());
        }
    }

    // Use helper classes to define how vertices should be rendered,
    // Adhering to the DOT language restrictions

    public void pentToDot(Graph<PentNode, DefaultEdge> pentGraph, String fileName) {
        ComponentNameProvider<PentNode> vertexIdProvider = new ComponentNameProvider<PentNode>() {
            public String getName(PentNode pentNode) {
                return pentNode.getName().replaceAll(" ", "_");
            }
        };
        ComponentNameProvider<PentNode> vertexLabelProvider = new ComponentNameProvider<PentNode>() {
            public String getName(PentNode pentNode) {
                return pentNode.getType().replaceAll(" ", "_");
            }
        };

        GraphExporter<PentNode, DefaultEdge> exporter =
                new DOTExporter<PentNode, DefaultEdge>(vertexIdProvider, vertexLabelProvider, null);

        Writer writer = new StringWriter();
        try {
            // writeStringToNewFile("", fileName);
            // FileWriter fw = null;
            // File file = new File(fileName);
            // fw = new FileWriter(fileName);
            exporter.exportGraph(pentGraph, writer);
            writeStringToNewFile(writer.toString(), fileName);

        } catch (IOException | ExportException e) {
            e.printStackTrace();
        }
    }

    public void talToDot(Graph<TalNode, DefaultEdge> talGraph, String fileName) {
        ComponentNameProvider<TalNode> vertexIdProvider = new ComponentNameProvider<TalNode>() {
            public String getName(TalNode talNode) {
                return talNode.getName().replaceAll(" ", "_"); // .toLowerCase()
            }
        };
        ComponentNameProvider<TalNode> vertexLabelProvider = new ComponentNameProvider<TalNode>() {
            public String getName(TalNode talNode) {
                return talNode.getType().replaceAll(" ", "_");
            }
        };

        GraphExporter<TalNode, DefaultEdge> exporter =
                new DOTExporter<TalNode, DefaultEdge>(vertexIdProvider, vertexLabelProvider, null);

        Writer writer = new StringWriter();
        try {
            // writeStringToNewFile("", fileName);
            // FileWriter fw = null;
            // File file = new File(fileName);
            // fw = new FileWriter(fileName);
            exporter.exportGraph(talGraph, writer);
            writeStringToNewFile(writer.toString(), fileName);

        } catch (IOException | ExportException e) {
            e.printStackTrace();
        }
    }


    // Helper methods.
    public static void writeStringToNewFile(String str, String fileName)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(str);
        writer.close();
    }

    private void instantiateTalTopologicalIterator() {
        talInputIterator = new TopologicalOrderIterator<TalNode, DefaultEdge>(talInputGraph);
    }


    // talend section to pentnodes in graph

    public Graph convertNode (TalNode tNode, String type){
        Graph<PentNode, DefaultEdge> pGraph = createGraph();

        if (type.contains("_")){
            String [] arr = type.split("_");
            PentNode previousNode = null;
            for (int i=0; i< arr.length ;i++){
                PentNode node = createSinglePentNode(tNode, arr[i], i);
                pGraph.addVertex(node);
                if (i != 0){
                    try{
                        pGraph.addEdge(previousNode, node);
                    } catch (NullPointerException NP){
                        System.out.println("Error: Created null node!");
                    }
                }
                previousNode = node;
            }
        }
        else {
            pGraph.addVertex(createSinglePentNode(tNode,type, 0));
        }
        System.out.println(pGraph);
        return pGraph;
    }

// Assumes certain data is provided, ie filename, sparatores ect
    public PentNode createSinglePentNode (TalNode tNode, String type, int nameTag) {
        PentNode pNode = null;   // Possibly worth netting whole switch in try catch
        String name = tNode.getName();
        switch (type){
            case "CsvInput":
                try{
                    pNode = new CSVInputNode(name, type, tNode.getSimpleInfo().get("FILENAME")/*"DUMMY: filename"*/);
//                System.out.println(pNode.getSimpleInfo().get("filename"));
//                tNode.getSimpleInfo().remove("FIELDSEPARATOR");
                    ((CSVInputNode) pNode).setSeparator(tNode.getSimpleInfo().get("FIELDSEPARATOR").split("/*")[1]);
                    ((CSVInputNode) pNode).setEnclosure(tNode.getSimpleInfo().get("TEXT_ENCLOSURE").split("/*")[1]);
//                System.out.println(((CSVInputNode) pNode).getSeparator() + ((CSVInputNode) pNode).getEnclosure());

                    HashMap<String, ArrayList<String>> selectTable = tNode.getTableInfo().get(0);
                    ArrayList<String> fields = selectTable.get("SCHEMA_COLUMN");

                    for (String field : fields){
                        ((CSVInputNode) pNode).addField(field);
                    }
                } catch (NullPointerException | IndexOutOfBoundsException invalidData){
                    System.out.println("Error occured in making penaho nodes due to invalid or insufficient data");
                }
                break;
            case "TextFileOutput":
                pNode = new TextOutputNode(name, type, tNode.getSimpleInfo().get("FILENAME")/*"filename55"*/);
                ((TextOutputNode) pNode).setSeparator(tNode.getSimpleInfo().get("FIELDSEPARATOR").split("/*")[1]);
                ((TextOutputNode) pNode).setEnclosure(tNode.getSimpleInfo().get("TEXT_ENCLOSURE").split("/*")[1]);
//                System.out.println(((TextOutputNode) pNode).getSeparator() + ((TextOutputNode) pNode).getEnclosure());
                // NO FIELD DATA AVAILABLE TO TRANSFER
                break;
            case "SelectValues":
                pNode = new SelectValuesNode(name, type);
                ((SelectValuesNode) pNode).addField("Field 1", "Field One");
                break;
            case "SortRows":
                pNode = new SortNode(name, type);

                HashMap<String, ArrayList<String>> selectTable = tNode.getTableInfo().get(0);
//                System.out.println(selectTable);
                ArrayList<String> column = selectTable.get("COLNAME");
                ArrayList<String> order = selectTable.get("ORDER");

                for (int i=0 ; i< column.size() ; i++){
                    String ascending = order.get(i).replace("asc", "Y").replace("desc", "N");
//                    order.get(i) = order.get(i).replace("asc", "Y");
//                    order.get(i).replace("desc", "N");
                    System.out.println(column.get(i));
                    System.out.println(ascending);
                    System.out.println("----------");
                    ((SortNode) pNode).addField(column.get(i), ascending,"N");
                }

                ((SortNode) pNode).addField("Field 2", "Y", "N");
                break;
            case "MergeJoin":
                pNode = new MergeNode(name, type, "DUMMY: joinType", "DUMMY: step1", "DUMMY: step2", "DUMMY: key1", "DUMMY: key2");
                break;
            case "GroupBy":
                pNode = new GroupByNode(name, type);
                ((GroupByNode) pNode).addFieldToGroupBy("Dummy field1");
                ((GroupByNode) pNode).addAggregateField("Dummy Agrregate", "Dummy subject", "dummy type");
                break;
            case "FilterRows":
                pNode = new FilterNode(name, type);
                ((FilterNode) pNode).addCondition("Y","Field 1", "<", "Field 2");
                break;
            default:
                pNode = new PentNode(name,type);
        }
        if (nameTag != 0){
            String newName = tNode.getName() + "(" + Integer.toString(nameTag) + ")";
//            System.out.println(newName);
            pNode.setName(newName);
        }
//        System.out.println("returning node : " + pNode.getName());
        return pNode;
    }

    private Graph<PentNode, DefaultEdge> createGraph(){
        return GraphTypeBuilder.<PentNode,DefaultEdge>directed().allowingMultipleEdges(true).allowingSelfLoops(false).edgeClass(DefaultEdge.class).weighted(false).buildGraph();
    }

    /*
    public static Graph<URI, DefaultEdge> createHrefGraph() throws URISyntaxException {
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
     */

    public void iterate (Iterator it){
        while (it.hasNext()){
            TalNode node = (TalNode) it.next();
            if (mappingDict.containsKey(node.getType())){
                convertNode(node, /*"CsvInput_TextFileOutput"*/ mappingDict.get(node.getType()));
//            WriteXMLFile writer = new WriteXMLFile(convertNode(node, "CsvInput_TextFileOutput" /*mappingDict.get(node.getType()*/)));
            }

        }
    }
}
