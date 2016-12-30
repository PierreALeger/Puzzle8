
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

/**
 * Main.java.
 * Minimal user interface is provided
 * Created by Pierre-André on 2016-10-04.
 */
public class Main {

    public static void main(String[] args) {


        Scanner keyboard = new Scanner(System.in);
        int selection;
        String again;
        long timer;
        while (true) {
            System.out.print("" +
                    "Please select from the following algorithms\n" +
                    "1 - Romania path to Bucharest\n" +
                    "2 - N-puzzle\n" +
                    "Selection -- ");
            selection = keyboard.nextInt();
            System.out.println();
            switch (selection) {
                case 1:
                    System.out.print("Please enter a starting city (destination is Bucharest) -- ");
                    String start = keyboard.next();
                    System.out.println();
                    System.out.print("Please select from the following generic searches\n" +
                            "1 - Depth First\n" +
                            "2 - Breadth First\n" +
                            "3 - Best First\n" +
                            "4 - A*\n" +
                            "Selection -- ");
                    selection = keyboard.nextInt()-1;
                    System.out.println();

                    Hashtable<String, GraphNode> nodes = GraphMaker.make();

                    SearchAlgoGraph searchAlgoGraph = new SearchAlgoGraph(nodes);

                    GraphNode startNode = nodes.get(start);
                    GraphNode goal = nodes.get("Bucharest");

                    System.out.println(searchAlgoGraph.search(startNode, goal, SearchType.values()[selection]));
                    System.out.println("Using " + SearchType.values()[selection] + ".");

                    break;
                case 2:
                    PuzzleQuery query = new PuzzleQuery();
                    SearchAlgo<PuzzleNode, String> searchAlgo = null;
                    ArrayList<PuzzleNode> path = null;
                    query.setPuzzleAttributes();
                    PuzzleNode.puzzleData = query.getPuzzleData();
                    while (true) {
                        System.out.println("START NODE\n" + query.getStart());
                        query.setSearchAttributes();
                        searchAlgo =
                                new SearchAlgo<>(
                                        query.getStart(), query.getType(), query.getComparator());

                        timer = System.currentTimeMillis();

                        path = searchAlgo.search();

                        timer = System.currentTimeMillis() - timer;

                        for (PuzzleNode item : path) {
                            System.out.println(item.toString(query.getHeuristic()));
                        }
                        System.out.println("Nodes generated - " + searchAlgo.getTotalGenerated());
                        System.out.println("Computing time - " + timer + " ms\n");
                        System.out.print("" +
                                "Do another search using the same start node (y/n) ? ");
                        again = keyboard.next();
                        System.out.println();
                        if (again.equalsIgnoreCase("y")) continue;
                        else break;
                    }
                    break;
                default:
                    break;
            }

            System.out.print("" +
                    "Do another search (y/n) ? ");
            again = keyboard.next();
            System.out.println();
            if (again.equalsIgnoreCase("y")) continue;
            else break;
        }

        System.out.println("\n\nExiting... Have a nice day");
    }
}
