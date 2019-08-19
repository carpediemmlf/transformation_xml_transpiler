package com.hitachivantara.talendtopentaho.treeeditdistance;

import com.hitachivantara.talendtopentaho.io.*;
import com.hitachivantara.talendtopentaho.nodetype.*;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static org.jgrapht.Graphs.neighborListOf;

public class TreeEditDistanceHandler {

    public static float distance(Graph<PentNode, DefaultEdge> directedG, Graph<PentNode, DefaultEdge> directedH){

        float minDist = -1.0f;
        boolean first = true;

        Graph<PentNode, DefaultEdge> H = new AsUndirectedGraph(directedH);
        Graph<PentNode, DefaultEdge> G = new AsUndirectedGraph(directedG);

        // Choose an arbitrary valid root for H
        PentNode rootH = null;
        Iterator<PentNode> rootIterH = directedH.vertexSet().iterator();
        while (rootIterH.hasNext()) {
            PentNode vertex = rootIterH.next();
            if (directedH.outDegreeOf(vertex) == 0) {
                rootH = vertex;
                break;
            }
        }

        // Choose an arbitrary valid order for H
        List<List<PentNode>> orderH = new ArrayList<List<PentNode>>();
        BreadthFirstIterator<PentNode, DefaultEdge> splitIterH = new BreadthFirstIterator(H, rootH);
        while (splitIterH.hasNext()) {
            PentNode vertex = splitIterH.next();
            List<PentNode> childList = neighborListOf(H,vertex);
            childList.remove(splitIterH.getParent(vertex));

            if (childList.size()>1) {
                orderH.add(childList);
            }
        }

        // Identify roots for G
        List<PentNode> rootListG = new ArrayList<PentNode>();
        Iterator<PentNode> rootIterG = directedG.vertexSet().iterator();
        while (rootIterG.hasNext()) {
            PentNode vertex = rootIterG.next();
            if (directedG.outDegreeOf(vertex) == 0) {
                rootListG.add(vertex);
            }
        }

        // Loop through possible roots for G
        for (int i = 0; i < rootListG.size(); i++) {

            // Identify all splits
            List<List<PentNode>> splitList = new ArrayList<List<PentNode>>();
            BreadthFirstIterator<PentNode, DefaultEdge> splitIter = new BreadthFirstIterator(G, rootListG.get(i));
            while (splitIter.hasNext()) {
                PentNode vertex = splitIter.next();
                List<PentNode> childList = neighborListOf(G,vertex);
                childList.remove(splitIter.getParent(vertex));

                if (childList.size()>1) {
                    splitList.add(childList);
                }
            }

            // Handle graphs with no splits
            if (splitList.isEmpty()) {
                List<List<PentNode>> orderG = new ArrayList<List<PentNode>>();
                return distanceString(graphToBracket(G, rootListG.get(i), orderG), graphToBracket(H, rootH, orderH));
            }

            // Identify all orderings
            List<List<List<PentNode>>> tempList = new ArrayList<List<List<PentNode>>>();
            for (int j = 0; j < splitList.size(); j++) {
                tempList.add(listPermutations(splitList.get(j)));
            }
            List<List<List<PentNode>>> orderListG = cartesianProduct(tempList);

            // Loop through possible orderings for G
            for (int j = 0; j < orderListG.size(); j++) {
                // Run RTED and save if minimum
                float newDist = distanceString(graphToBracket(G, rootListG.get(i), orderListG.get(j)), graphToBracket(H, rootH, orderH));
                if (first || newDist < minDist) {
                    minDist = newDist;
                    first = false;
                }
            }
        }

        return minDist;
    }

    private static float distanceString(String G, String H){

        // Building some necessary stuff
        String outputString = null;

        try {
            Process proc = Runtime.getRuntime().exec("java -jar RTED_v1.jar -t " + G + " " + H);

            // Read the output from the
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            outputString = stdInput.readLine();
        }
        catch (Exception e) {
            System.out.println("RTED did not run correctly.");
        }

        return Float.parseFloat(outputString);
    }

    private static String graphToBracket(Graph<PentNode, DefaultEdge> G, PentNode root, List<List<PentNode>> orderList){
        return vertexToBracket(G, root, null, orderList);
    }

    private static String vertexToBracket(Graph<PentNode, DefaultEdge> G, PentNode vertex, PentNode parent, List<List<PentNode>> orderList){

        List<PentNode> childList = neighborListOf(G,vertex);
        childList.remove(parent);

        if (childList.isEmpty()) {
            return "{" + vertex.getType() + "}";
        } else if (childList.size() == 1) {
            return "{" + vertex.getType() + vertexToBracket(G,childList.get(0),vertex,orderList) + "}";
        } else {
            String output = "{" + vertex.getType();

            for (int i = 0; i < orderList.size(); i++) {
                if (new HashSet<>(childList).equals(new HashSet<>(orderList.get(i)))) {
                    for (int j = 0; j < orderList.get(i).size(); j++) {
                        output = output + vertexToBracket(G,orderList.get(i).get(j),vertex,orderList);
                    }
                }
            }
            return output + "}";
        }
    }

    // Adapted from https://stackoverflow.com/questions/24460480/permutation-of-an-arraylist-of-numbers-using-recursion
    private static List<List<PentNode>> listPermutations(List<PentNode> list) {

        if (list.size() == 0) {
            List<List<PentNode>> result = new ArrayList<List<PentNode>>();
            result.add(new ArrayList<PentNode>());
            return result;
        }

        List<List<PentNode>> returnMe = new ArrayList<List<PentNode>>();

        PentNode firstElement = list.remove(0);

        List<List<PentNode>> recursiveReturn = listPermutations(list);
        for (List<PentNode> li : recursiveReturn) {

            for (int index = 0; index <= li.size(); index++) {
                List<PentNode> temp = new ArrayList<PentNode>(li);
                temp.add(index, firstElement);
                returnMe.add(temp);
            }

        }
        return returnMe;
    }

    // Adapted from https://stackoverflow.com/questions/714108/cartesian-product-of-arbitrary-sets-in-java
    private static <T> List<List<T>> cartesianProduct(List<List<T>> lists) {
        List<List<T>> resultLists = new ArrayList<List<T>>();
        if (lists.size() == 0) {
            resultLists.add(new ArrayList<T>());
            return resultLists;
        } else {
            List<T> firstList = lists.get(0);
            List<List<T>> remainingLists = cartesianProduct(lists.subList(1, lists.size()));
            for (T condition : firstList) {
                for (List<T> remainingList : remainingLists) {
                    ArrayList<T> resultList = new ArrayList<T>();
                    resultList.add(condition);
                    resultList.addAll(remainingList);
                    resultLists.add(resultList);
                }
            }
        }
        return resultLists;
    }
}
