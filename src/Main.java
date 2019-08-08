import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Main {
    public static void main (String [] args){
        PentahoToGraph penToGraph = new PentahoToGraph("pentaho_input_output.ktr"/*"testToGraph.ktr"*/);
//        penToGraph.getGraph();
        WriteXMLFile write = new WriteXMLFile(penToGraph.getGraph());
//        write.addStep_inputCSV();
//        write.addStep_outputText();
//        write.addHop("tFileInputDelimited_1","tFileOutputDelimited_1");



    }
}
