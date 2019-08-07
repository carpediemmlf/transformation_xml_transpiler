import org.jgrapht.Graph;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TreeEditDistanceHandler {

    public static float distance(Graph G, Graph H){

        // Converting to a format that RTED can use
        String GBracket = graphToBracket(G);
        String HBracket = graphToBracket(H);

        // Building some necessary stuff
        String outputString = null;

        try {
            Process proc = Runtime.getRuntime().exec("java -jar RTED_v1.jar " + GBracket + " " + HBracket);

            // Read the output from the
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            outputString = stdInput.readLine();
        }
        catch (Exception e) {
            System.out.println("RTED did not run correctly.");
        }

        return Float.parseFloat(outputString);
    }

    private static String graphToBracket(Graph G){

        // G.vertexSet();

        return "test";
    }
}
