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
        // tMap", "MergeJoin_FilterRows");
        // mappingDict.put("a", "A_B)");
        // mappingDict.put("a", "A_B");
    };

    // Constructors. Overloaded.
    Mapping() { }
    Mapping(Graph<TalNode, DefaultEdge> inputTalGraph) {
        talInputGraph = inputTalGraph;
        // Instantiate Iterator.
        instantiateTalTopologicalIterator();
    }

    public void map() {
        createVerticesOnlyPentGraph();
        try {
            if (!outputPentVerticesCreated) {
                throw new ExceptionInInitializerError("Output pent nodes not created.");
            } else {
                appendEdgesToPentGraph();
                System.out.println(getOutputGraph());
            }
        } catch (ExceptionInInitializerError e) {
            System.out.println(e.toString());
        }
    }

    public Graph<PentNode, DefaultEdge> getOutputGraph() {
        return pentOutputGraph;
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
            exporter.exportGraph(talGraph, writer);
            writeStringToNewFile(writer.toString(), fileName);

        } catch (IOException | ExportException e) {
            e.printStackTrace();
        }
    }


    // Creates empty outputGraph.
    // Iterates through talend nodes and creates equivalent pentaho nodes (in an edgeless graph)
    // Adds those to the initial outputGraph
    private void createVerticesOnlyPentGraph() {
        pentOutputGraph = createGraph();
        instantiateTalTopologicalIterator();
        while (talInputIterator.hasNext()){
            TalNode node = (TalNode) talInputIterator.next();
            // If statement to convert any undefined type into Dummy
            if (mappingDict.containsKey(node.getType())){
                Graph<PentNode, DefaultEdge> tNodeGraph = convertNode(node, mappingDict.get(node.getType()));
                Graphs.addGraph(pentOutputGraph, tNodeGraph);
            } else{
                Graph<PentNode, DefaultEdge> tNodeGraph = convertNode(node,"Dummy");
                Graphs.addGraph(pentOutputGraph, tNodeGraph);
            }
        }
        outputPentVerticesCreated = true;
    }

// Lingfa - please describe
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

// Helper methods.
    private static void writeStringToNewFile(String str, String fileName)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(str);
        writer.close();
    }

    private void instantiateTalTopologicalIterator() {
        talInputIterator = new TopologicalOrderIterator<TalNode, DefaultEdge>(talInputGraph);
    }

// talend section to pentnodes in graph

    // Takes in a talend node and the type/ types of pentaho that it is mapped to. (which should be made) - in order
    private Graph convertNode (TalNode tNode, String type) {
        Graph<PentNode, DefaultEdge> pGraph = createGraph();
        /* One to many relation!
         If talend node is mapped to multiple pentaho nodes, they are delimited by "_"
         They are split and in a loop created
         A pointer to the previous node allows edges to be made as nodes are made
         They first and last nodes are set as the head and tail respectively*/
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
        // returns graph corresponding to specific talend node
        return pGraph;
    }

    // Creates single pentNode given talend node and pentaho node type
    // nameTag is used to add a tag to name of nodes part of a one-many, in which they would be mapped the same name
    // Assumes certain data is provided, ie filename, sparatores ect
    private PentNode createSinglePentNode (TalNode tNode, String type, int nameTag) {
        PentNode pNode = null;   // Possibly worth netting whole switch in try catch
        String name = tNode.getName();
        switch (type){
            case "CsvInput":
                try{
                    pNode = new CSVInputNode(name, type, tNode.getSimpleInfo().get("FILENAME"));
                    // set separator and enclosure
                    ((CSVInputNode) pNode).setSeparator(tNode.getSimpleInfo().get("FIELDSEPARATOR").split("/*")[1]);
                    ((CSVInputNode) pNode).setEnclosure(tNode.getSimpleInfo().get("TEXT_ENCLOSURE").split("/*")[1]);
                    // getting field data
                    HashMap<String, ArrayList<String>> selectTable = tNode.getTableInfo().get(0);
                    ArrayList<String> fields = selectTable.get("SCHEMA_COLUMN");
                    // setting fields in penNode
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
                    pNode = new TextOutputNode(name, type, tNode.getSimpleInfo().get("FILENAME"));
                    // set separator and enclosure
                    ((TextOutputNode) pNode).setSeparator(tNode.getSimpleInfo().get("FIELDSEPARATOR").split("/*")[1]);
                    ((TextOutputNode) pNode).setEnclosure(tNode.getSimpleInfo().get("TEXT_ENCLOSURE").split("/*")[1]);

                } catch (NullPointerException | IndexOutOfBoundsException invalidData){
                    System.out.println("Error occured in making penaho nodes due to invalid or insufficient data");
                    pNode = new TextOutputNode(name, type);
                }
                break;
            case "SortRows":
                pNode = new SortNode(name, type);
                try {
                    // getting seocnd level info
                    HashMap<String, ArrayList<String>> selectTable = tNode.getTableInfo().get(0);
                    ArrayList<String> column = selectTable.get("COLNAME");
                    ArrayList<String> order = selectTable.get("ORDER");

                    for (int i=0 ; i< column.size() ; i++){
                        // editing dialect and adding fields
                        String ascending = order.get(i).replace("asc", "Y").replace("desc", "N");
                        ((SortNode) pNode).addField(column.get(i), ascending,"N");
                    }
                } catch (NullPointerException | IndexOutOfBoundsException invalidData){
                    System.out.println("Error occured in making penaho nodes due to invalid or insufficient data");
                }
                break;
            case "MergeJoin":
                pNode = new MergeNode(name, type);
                try {
                    // setting join type based on provied info
                    String joinType;
                    if (tNode.getSimpleInfo().get("USE_INNER_JOIN").equals("true")){
                        joinType = "INNER";
                        ((MergeNode) pNode).setJoin_type(joinType);
                    } else {
                        joinType = "FULL OUTER";
                        ((MergeNode) pNode).setJoin_type(joinType);
                    }
                    // getting key info
                    HashMap<String, ArrayList<String>> keys = tNode.getTableInfo().get(1);
                    ArrayList<String> inputCols = keys.get("INPUT_COLUMN");
                    ArrayList<String> lookupCols = keys.get("LOOKUP_COLUMN");
                    // setting keys
                    for (int i = 0; i < inputCols.size(); i++){
                        ((MergeNode) pNode).addKey_1(inputCols.get(i));
                        ((MergeNode) pNode).addKey_1(lookupCols.get(i));
                    }
                    // Use of edges to assume steps feeding into this step
                    // Assume only 1 and 2, if more, they will be ignored (added to hashmap with step# tag)
                    int num = 1;
                    for (TalNode t : Graphs.predecessorListOf(talInputGraph, tNode)){
                        String step = "step"+num;
                        pNode.getSimpleInfo().put(step,t.getTailPentNode().getName());
                    }
                } catch (NullPointerException | IndexOutOfBoundsException invalidData){
                    System.out.println("Error occured in making penaho nodes due to invalid or insufficient data");
                }
                break;
            case "GroupBy":
                pNode = new GroupByNode(name, type);
                try {
                    // getting second level info
                    HashMap<String, ArrayList<String>> groupBys = tNode.getTableInfo().get(0);
                    HashMap<String, ArrayList<String>> operations = tNode.getTableInfo().get(1);
                    // extracting groupBy info
                    ArrayList<String> inputs = groupBys.get("INPUT_COLUMN");
                    // extracting operation info
                    ArrayList<String> outputs = operations.get("OUTPUT_COLUMN");
                    ArrayList<String> inputCol = operations.get("INPUT_COLUMN");
                    ArrayList<String > ignoreNull = operations.get("IGNORE_NULL");
                    ArrayList<String> function = operations.get("FUNCTION");
                    // setting fields to groupBy
                    for (String s : inputs){
                        ((GroupByNode) pNode).addFieldToGroupBy(s);
                    }
                    // setting aggregate info
                    for (int i = 0; i < outputs.size() ; i++){
                        // mapping talend dialect to pentaho
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
                        // add aggregate
                        ((GroupByNode) pNode).addAggregateField(outputs.get(i), inputCol.get(0), pentFunc);
                    }
                } catch (NullPointerException | IndexOutOfBoundsException invalidData){
                    System.out.println("Error occured in making penaho nodes due to invalid or insufficient data");
                    pNode = new GroupByNode(name, type);
                }
                break;
            case "FilterRows":
                pNode = new FilterNode(name, type);
                ((FilterNode) pNode).addCondition("Y","Field 1", "<", "Field 2");
                break;
            case "SelectValues":
                pNode = new SelectValuesNode(name, type);
                ((SelectValuesNode) pNode).addField("Field 1", "Field One");
                break;
            case "Dummy":
                pNode = new PentNode(name, "Dummy");
                break;
            default:
                pNode = new PentNode(name,type);
        }
        // setting x and y from talend on all pentNodes
        pNode.setxLoc(tNode.getPosX());
        pNode.setyLoc(tNode.getPosY());
        // if not one-to-one, tags added to name
        if (nameTag != 0){
            String newName = tNode.getName() + "(" + Integer.toString(nameTag) + ")";
            pNode.setName(newName);
        }
        return pNode;
    }

    // Creates empty graph of pentNodes
    private Graph<PentNode, DefaultEdge> createGraph() {
        return GraphTypeBuilder.<PentNode,DefaultEdge>directed().allowingMultipleEdges(true).allowingSelfLoops(false).edgeClass(DefaultEdge.class).weighted(false).buildGraph();
    }
}
