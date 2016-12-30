import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Pierre-André on 2016-10-08.
 */
public class Batcher {

    public static void main(String[] args) {

        /**
         * Quick and dirty code for running batch tests on the blank center puzzle 8
         */

        // ---------------- //
        // ENTER STUFF HERE //

        int iterations = 500;

        int shuffles = 100;

        SearchType[] types = {SearchType.ASTAR, SearchType.ASTAR, SearchType.ASTAR, SearchType.ASTAR, SearchType.BREADTH};

        Heuristics[] heuristics = {Heuristics.SUM_MAN_CON, Heuristics.MANHATTAN, Heuristics.MISPLACED, Heuristics.NILSSONS, Heuristics.MIN_MAN_MIS, null};

        //   STOP   //
        // -------- //

        Comparator<PuzzleNode>[] comparators = new Comparator[types.length];
        for (int i = 0; i < types.length; i++) {
            Heuristics temp = heuristics[i];
            if (types[i] == SearchType.ASTAR) {
                comparators[i] = new Comparator<PuzzleNode>() {
                    @Override
                    public int compare(PuzzleNode o1, PuzzleNode o2) {
                        int o1H_n = o1.getH_n().get(temp);
                        int o2H_n = o2.getH_n().get(temp);
                        int o1G_n = o1.getG_n();
                        int o2G_n = o2.getG_n();
                        if (o1H_n + o1G_n == o2H_n + o2G_n) return o1G_n - o2G_n;
                        return (o1H_n + o1G_n) - (o2H_n + o2G_n);
                    }
                };
            } else if (types[i] == SearchType.BEST){
                comparators[i] = new Comparator<PuzzleNode>() {
                    @Override
                    public int compare(PuzzleNode o1, PuzzleNode o2) {
                        int o1H_n = o1.getH_n().get(temp);
                        int o2H_n = o2.getH_n().get(temp);
                        return o1H_n - o2H_n;
                    }
                };
            }



        }
        PuzzleNode.puzzleData = new PuzzleData();

        PuzzleNode start;
        PuzzleNode last;

        SearchAlgo<PuzzleNode, String>[] searchAlgos = new SearchAlgo[types.length];

        ArrayList<PuzzleNode>[] paths = new ArrayList[types.length];

        int[] costs = new int[types.length];
        int[] nodes = new int[types.length];


        for (int i = 0; i < iterations; i++) {

            start = new PuzzleNode(shuffles);

            for (int j = 0; j < types.length; j++) {
                searchAlgos[j] = new SearchAlgo<>(start, types[j], comparators[j]);
                paths[j] = searchAlgos[j].search();
                last = paths[j].get(paths[j].size()-1);


                costs[j] += last.getG_n();
                nodes[j] += searchAlgos[j].getTotalGenerated();


            }

        }

        for (int i = 0; i < types.length; i++) {
            System.out.println("-----" + types[i] + "-----" + heuristics[i]);
            System.out.println("Avg path cost --- " + (double)costs[i]/iterations);
            System.out.println("Avg node count -- " + (double)nodes[i]/iterations);

        }
    }
}
