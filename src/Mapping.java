import jdk.swing.interop.SwingInterOpUtils;
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
import java.util.function.Supplier;

public class Mapping {
    private GraphIterator<TalNode, DefaultEdge> talInputIterator;

    private Graph<TalNode, DefaultEdge> talInputGraph;
    private Graph<PentNode, DefaultEdge> pentOutputGraph;
    private boolean outputPentVerticesCreated = false;
    private boolean isOutputPentEdgesCreated = false;
    // Initialize static type dictionary.
    private static Map<String, String> mappingDict = new HashMap<String, String>();
    static {
        // One-to-one.
        mappingDict.put("tJoin", "MergeJoin");
        // mappingDict.put("", "SelectValues");
        // mappingDict.put("", "FilterRows");
        // mappingDict.put("tRowGenerator", "");
        // mappingDict.put("tLogRow", "");
        mappingDict.put("tSortRow", "SortRows");
        mappingDict.put("tFileInputDelimited", "CsvInput");
        mappingDict.put("tAggregateSortedRow", "GroupBy");
        mappingDict.put("tFileOutputDelimited", "TextFileOutput");
        // One-to-two.
        // mappingDict.put("
        // tMap", "MergeJoin_FilterRows");
        // mappingDict.put("a", "A");
        // mappingDict.put("a", "A");
    };

    // Constructors. Overloaded.
    Mapping() { }
    Mapping(Graph<TalNode, DefaultEdge> inputTalGraph) {
        talInputGraph = inputTalGraph;
        // Instantiate Iterator.
        instantiateTalTopologicalIterator();
    }

    // Muhammed is working on this.
    public Graph<PentNode, DefaultEdge> getOutputGraph() {
        return pentOutputGraph;
    }

    public void map() {
        createVerticesOnlyPentGraph();
        try {
            if (!outputPentVerticesCreated) {
                throw new ExceptionInInitializerError("Output pent nodes not created.");
            } else {
                appendEdgesToPentGraph();
            }
        } catch (ExceptionInInitializerError e) {
             System.out.println(e.toString());
            }
        // Generate PentNodes.
        // while (talInputIterator.hasNext()) {
        //     System.out.println(((talInputIterator.next())).getType());
        //     System.out.println("Node translated.");
        // }
        // instantiateTalTopologicalIterator();
        // Fill in
        // while (talInputIterator.hasNext()) {
        //     System.out.println(((talInputIterator.next())).getType());
        //     System.out.println("Node translated.");
        // }
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

    public Graph convertNode (TalNode tNode, String type) {
        Graph<PentNode, DefaultEdge> pGraph = createGraph();

        if (type.contains("_")){
            String [] arr = type.split("_");
            PentNode previousNode = null;
            for (int i=0; i< arr.length ;i++){
                PentNode node = createSinglePentNode(tNode, arr[i], i);
                if (i == 0) {
                    tNode.setHeadPentNode(node);
                }
                else if ((i== arr.length - 1)) {
                    tNode.setTailPentNode(node);
                }
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
            PentNode node = createSinglePentNode(tNode,type, 0);
            pGraph.addVertex(node);
            // Add head and tail
            tNode.setHeadPentNode(node);
            tNode.setTailPentNode(node);
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
                    pNode = new CSVInputNode(name, type);
                }
                break;
            case "TextFileOutput":
                try {
                    pNode = new TextOutputNode(name, type, tNode.getSimpleInfo().get("FILENAME")/*"filename55"*/);
                    ((TextOutputNode) pNode).setSeparator(tNode.getSimpleInfo().get("FIELDSEPARATOR").split("/*")[1]);
                    ((TextOutputNode) pNode).setEnclosure(tNode.getSimpleInfo().get("TEXT_ENCLOSURE").split("/*")[1]);
                    //                System.out.println(((TextOutputNode) pNode).getSeparator() + ((TextOutputNode) pNode).getEnclosure());
                    // NO FIELD DATA AVAILABLE TO TRANSFER
                } catch (NullPointerException | IndexOutOfBoundsException invalidData){
                    System.out.println("Error occured in making penaho nodes due to invalid or insufficient data");
                    pNode = new TextOutputNode(name, type);
                }
                break;
            case "SelectValues":
                pNode = new SelectValuesNode(name, type);
                ((SelectValuesNode) pNode).addField("Field 1", "Field One");
                break;
            case "SortRows":
                pNode = new SortNode(name, type);
                try {
                    HashMap<String, ArrayList<String>> selectTable = tNode.getTableInfo().get(0);
//                System.out.println(selectTable);
                    ArrayList<String> column = selectTable.get("COLNAME");
                    ArrayList<String> order = selectTable.get("ORDER");

                    for (int i=0 ; i< column.size() ; i++){
                        String ascending = order.get(i).replace("asc", "Y").replace("desc", "N");
//                    order.get(i) = order.get(i).replace("asc", "Y");
//                    order.get(i).replace("desc", "N");
                    /*System.out.println(column.get(i));
                    System.out.println(ascending);
                    System.out.println("----------");*/
                        ((SortNode) pNode).addField(column.get(i), ascending,"N");
                    }
                } catch (NullPointerException | IndexOutOfBoundsException invalidData){
                    System.out.println("Error occured in making penaho nodes due to invalid or insufficient data");
                }
                break;
            case "MergeJoin":
                try {

                    pNode = new MergeNode(name, type, "DUMMY: joinType", "DUMMY: step1", "DUMMY: step2", "DUMMY: key1", "DUMMY: key2");
                    /*((TextOutputNode) pNode).setSeparator(tNode.getSimpleInfo().get("FIELDSEPARATOR").split("/*")[1]);
                    ((TextOutputNode) pNode).setEnclosure(tNode.getSimpleInfo().get("TEXT_ENCLOSURE").split("/*")[1]);
//                */

                    if (tNode.getSimpleInfo().get("USE_INNER_JOIN").equals("true")){
                        String joinType = "INNER";
                    } else {
                        String joinType = "FULL OUTER";
                    }

//                    HashMap<String, ArrayList<String>> groupBys = tNode.getTableInfo().get(0);
                    HashMap<String, ArrayList<String>> keys = tNode.getTableInfo().get(1);
                    ArrayList<String> inputCols = keys.get("INPUT_COLUMN");
                    ArrayList<String> lookupCols = keys.get("LOOKUP_COLUMN");
//                    System.out.println(keys);

                    for (int i = 0; i < inputCols.size(); i++){
                        ((MergeNode) pNode).addKey_1(inputCols.get(i));
                        ((MergeNode) pNode).addKey_1(lookupCols.get(i));
                    }
                    int num = 1;
                    for (TalNode t : Graphs.predecessorListOf(talInputGraph, tNode)){
                        System.out.println(t.getTailPentNode().getName());
                        String step = "step"+num;
                        System.out.println(step);
                        pNode.getSimpleInfo().put(step,t.getTailPentNode().getName());
                    }


                } catch (NullPointerException | IndexOutOfBoundsException invalidData){
                    System.out.println("Error occured in making penaho nodes due to invalid or insufficient data");
                    pNode = new MergeNode(name, type);
                }
                break;
            case "GroupBy":
                pNode = new GroupByNode(name, type);
                try {
                    HashMap<String, ArrayList<String>> groupBys = tNode.getTableInfo().get(0);
                    HashMap<String, ArrayList<String>> operations = tNode.getTableInfo().get(1);
                    /*System.out.println(groupBys);
                    System.out.println(operations);*/
                    ArrayList<String> inputs = groupBys.get("INPUT_COLUMN");

                    ArrayList<String> outputs = operations.get("OUTPUT_COLUMN");
                    ArrayList<String> inputCol = operations.get("INPUT_COLUMN");
                    ArrayList<String > ignoreNull = operations.get("IGNORE_NULL");
                    ArrayList<String> function = operations.get("FUNCTION");

                    for (String s : inputs){
                        ((GroupByNode) pNode).addFieldToGroupBy(s);
                    }

                    for (int i = 0; i < outputs.size() ; i++){
                        String pentFunc = function.get(i);
                        pentFunc = function.get(i).replace("sum", "SUM");
                        pentFunc = function.get(i).replace("count(distinct)", "COUNT_DISTINCT");
                        pentFunc = function.get(i).replace("min", "MIN");
                        pentFunc = function.get(i).replace("max", "MAX");
                        pentFunc = function.get(i).replace("avg", "AVERAGE");
                        if (ignoreNull.get(i).equals("true")){
                            pentFunc = function.get(i).replace("first", "FIRST").replace("last", "LAST");
                            pentFunc = function.get(i).replace("count", "COUNT_ALL");

                        } else{
                            pentFunc = function.get(i).replace("first", "FIRST_INCL_NULL").replace("last", "LAST_INCL_NULL");
                            pentFunc = function.get(i).replace("count", "COUNT_ANY");

                        }

                        ((GroupByNode) pNode).addAggregateField(outputs.get(i), inputCol.get(0), pentFunc); // different names
                    }

//                System.out.println(((GroupByNode) pNode).getFields().get(0).groupByFieldInfo);
                } catch (NullPointerException | IndexOutOfBoundsException invalidData){
                    System.out.println("Error occured in making penaho nodes due to invalid or insufficient data");
                    pNode = new SortNode(name, type);
                }
                break;
            case "FilterRows":
                pNode = new FilterNode(name, type);
                ((FilterNode) pNode).addCondition("Y","Field 1", "<", "Field 2");
                break;
            case "Dummy":
                pNode = new PentNode(name, "Dummy");
                break;
            default:
                pNode = new PentNode(name,type);
        }
        pNode.setxLoc(tNode.getPosX());
        pNode.setyLoc(tNode.getPosY());
        if (nameTag != 0){
            String newName = tNode.getName() + "(" + Integer.toString(nameTag) + ")";
//            System.out.println(newName);
            pNode.setName(newName);
        }
//        System.out.println("returning node : " + pNode.getName());
        return pNode;
    }

    private Graph<PentNode, DefaultEdge> createGraph() {
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

    private void createVerticesOnlyPentGraph() {
        pentOutputGraph = createGraph();
        instantiateTalTopologicalIterator();
        while (talInputIterator.hasNext()){
            TalNode node = (TalNode) talInputIterator.next();
            if (mappingDict.containsKey(node.getType())){
                Graph<PentNode, DefaultEdge> tNodeGraph = convertNode(node, /*"CsvInput_TextFileOutput"*/ mappingDict.get(node.getType()));
                // WriteXMLFile writer = new WriteXMLFile(convertNode(node, "CsvInput_TextFileOutput" /*mappingDict.get(node.getType()*/)));
                Graphs.addGraph(pentOutputGraph, tNodeGraph);
                // System.out.println(finalPentGraph);
            }
        }
        outputPentVerticesCreated = true;
    }
    private void appendEdgesToPentGraph() {
        instantiateTalTopologicalIterator();
        while (talInputIterator.hasNext()){
            TalNode node = (TalNode) talInputIterator.next();
            List<TalNode> predecessors = Graphs.predecessorListOf(talInputGraph, node);
            for (TalNode pred : predecessors) {
                pentOutputGraph.addEdge(pred.getTailPentNode(), node.getHeadPentNode());
            }
        }
    }
    public void iterate (Iterator it){
        Graph<PentNode, DefaultEdge> finalPentGraph = createGraph();
        while (it.hasNext()){
            TalNode node = (TalNode) it.next();
            if (mappingDict.containsKey(node.getType())){
                Graph<PentNode, DefaultEdge> tNodeGraph = convertNode(node, /*"CsvInput_TextFileOutput"*/ mappingDict.get(node.getType()));
                // WriteXMLFile writer = new WriteXMLFile(convertNode(node, "CsvInput_TextFileOutput" /*mappingDict.get(node.getType()*/)));
                Graphs.addGraph(finalPentGraph, tNodeGraph);
                // System.out.println(finalPentGraph);
            }
        }
        WriteXMLFile writer = new WriteXMLFile(finalPentGraph);
    }
}
