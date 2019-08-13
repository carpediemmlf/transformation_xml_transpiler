import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.generate.*;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.io.*;
import org.jgrapht.traverse.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.util.*;
import java.net.MalformedURLException;

public class Mapping {
    private GraphIterator<XMLNode, DefaultEdge> talInputIterator;

    // Initialize static type dictionary.
    private static Map<String, String> mappingDict = new HashMap<String, String>();
    static {
        // One-to-one.
        // mappingDict.put("", "MergeNode");
        // mappingDict.put("", "SelectValuesNode");
        // mappingDict.put("", "FilterNode");
        mappingDict.put("Sort", "SortNode");
        mappingDict.put("Input", "CSVInputNode");
        mappingDict.put("AggregateSort", "GroupByNode");
        mappingDict.put("Output", "TextOutputNode");
        // One-to-two.
        // mappingDict.put("a", "A_D");
        // mappingDict.put("a", "A");
        // mappingDict.put("a", "A");
    };
    private List<String> inputNodeTypes = new List<String>() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<String> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean add(String s) {

            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends String> c) {
            return false;
        }

        @Override
        public boolean addAll(int index, Collection<? extends String> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public String get(int index) {
            return null;
        }

        @Override
        public String set(int index, String element) {
            return null;
        }

        @Override
        public void add(int index, String element) {

        }

        @Override
        public String remove(int index) {
            return null;
        }

        @Override
        public int indexOf(Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }

        @Override
        public ListIterator<String> listIterator() {
            return null;
        }

        @Override
        public ListIterator<String> listIterator(int index) {
            return null;
        }

        @Override
        public List<String> subList(int fromIndex, int toIndex) {
            return null;
        }
    };
    private List<String> outputNodeTypes = new List<String>() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<String> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean add(String s) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends String> c) {
            return false;
        }

        @Override
        public boolean addAll(int index, Collection<? extends String> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String get(int index) {
            return null;
        }

        @Override
        public String set(int index, String element) {
            return null;
        }

        @Override
        public void add(int index, String element) {

        }

        @Override
        public String remove(int index) {
            return null;
        }

        @Override
        public int indexOf(Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }

        @Override
        public ListIterator<String> listIterator() {
            return null;
        }

        @Override
        public ListIterator<String> listIterator(int index) {
            return null;
        }

        @Override
        public List<String> subList(int fromIndex, int toIndex) {
            return null;
        }
    };

    private Graph<XMLNode, DefaultEdge> talInputGraph;
    private Graph<XMLNode, DefaultEdge> pentOutputGraph;

    // Constructors. Overloaded.
    public void Mapping() { }
    public void Mapping(Graph<XMLNode, DefaultEdge> inputTalGraph) {

        talInputGraph = inputTalGraph;
        // Instantiate Iterator.
        instantiateTalTopologicalIterator();
    }

    // Muhammed is working on this.
    private void map() {

    }
    public Graph makePentahoGraph (Graph<TalNode, DefaultEdge> tGraph){
        Graph<PentNode, DefaultEdge> pGraph = createGraph();

        Iterator tGraphIt = tGraph.vertexSet().iterator();
        while (tGraphIt.hasNext()){
            TalNode tNode = (TalNode) tGraphIt.next();
            String name = tNode.getName();
            String type = tNode.getType();
            System.out.println(name +":"+ type);

            PentNode pNode;

            switch (mappingDict.get(type)){
                case "CsvInput":
                    pNode = new CSVInputNode(name, type,"DUMMY: filename");
                    ((CSVInputNode) pNode).addField("Field 1");
                    break;
                case "TextFileOutput":
                    pNode = new TextOutputNode(name, type, "filename55");
                    ((TextOutputNode) pNode).addField("Out field");
                    break;
                case "SelectValues":
                    pNode = new SelectValuesNode(name, type);
                    ((SelectValuesNode) pNode).addField("Field 1", "Field One");
                    break;
                case "SortRows":
                    pNode = new SortNode(name, type);
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
            pGraph.addVertex(pNode);

        }

        return pGraph;
    }


    // use helper classes to define how vertices should be rendered,
    // adhering to the DOT language restrictions

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
        talInputIterator = new TopologicalOrderIterator<XMLNode, DefaultEdge>(talInputGraph);
    }


    // talend section to pentnodes in graph

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
}
