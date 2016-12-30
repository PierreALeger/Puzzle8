import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

/**
 * Created by Pierre-André on 2016-10-05.
 */
public class PuzzleQuery {

    private Comparator<PuzzleNode> comparator;
    private SearchType type;
    private PuzzleNode start;
    private PuzzleData puzzleData;
    private Heuristics heuristic;

    PuzzleQuery() {
        comparator = null;
        type = null;
        start = null;
        puzzleData = null;
        heuristic = null;
    }

    public Comparator<PuzzleNode> getComparator() {
        return comparator;
    }

    public SearchType getType() {
        return type;
    }

    public PuzzleNode getStart() {
        return start;
    }

    public PuzzleData getPuzzleData() {
        return puzzleData;
    }

    public Heuristics getHeuristic() {
        return heuristic;
    }

    public void setPuzzleAttributes() {
        int selection;
        Scanner keyboard = new Scanner(System.in);
        System.out.print("" +
                "Please select the type of N-puzzle\n" +
                "1 - 8-puzzle, center blank solution\n" +
                "2 - 8-puzzle, bottom right blank solution\n" +
                "3 - 15-puzzle\n" +
                "4 - 24-puzzle\n" +
                "Selection -- ");
        selection = keyboard.nextInt();
        System.out.println();

        int[] temp;
        int size;
        switch (selection) {
            case 1:
                this.puzzleData = new PuzzleData("1238B4765");
                break;
            case 2:
                size = 9;
                temp = new int[size];
                for (int i=0; i<size-1; i++) {
                    temp[i] = i+1;
                }
                temp[size-1] = 0;
                this.puzzleData = new PuzzleData(temp);
                break;
            case 3:
                size = 16;
                temp = new int[size];
                for (int i=0; i<size-1; i++) {
                    temp[i] = i+1;
                }
                temp[size-1] = 0;
                this.puzzleData = new PuzzleData(temp);
                break;
            case 4:
                size = 25;
                temp = new int[size];
                for (int i=0; i<size-1; i++) {
                    temp[i] = i+1;
                }
                temp[size-1] = 0;
                this.puzzleData = new PuzzleData(temp);
                break;
            default:
                break;
        }
        PuzzleNode.puzzleData = this.puzzleData;

        System.out.print("" +
                "Enter number of moves for the shuffler.\n" +
                "Alternatively, enter the '#' symbol followed by the String representation of the board.\n" +
                "Use '0' for the blank tile.\n" +
                "Separate characters or numbers by single white space\n" +
                "Input -- ");
        if (keyboard.hasNextInt()) {
            selection = keyboard.nextInt();
            System.out.println();
            this.start = new PuzzleNode(selection);
        } else {
            keyboard.next();
            ArrayList<Integer> board = new ArrayList<>();
            String input = keyboard.nextLine().substring(1);
            Scanner handler = new Scanner(input);
            while (handler.hasNextInt()) {
                board.add(handler.nextInt());
            }
            board.trimToSize();
            int[] boardInt = new int[board.size()];
            for (int i=0; i<boardInt.length; i++) {
                boardInt[i] = board.get(i);
            }
            this.start = new PuzzleNode(boardInt);

        }

    }

    public void setSearchAttributes() {
        int[] selection = new int[2];
        Scanner keyboard = new Scanner(System.in);

        System.out.print("" +
                "Please select from the following search algorithms\n" +
                "1 - Depth first\n" +
                "2 - Breadth first\n" +
                "3 - Best first\n" +
                "4 - A*\n" +
                "Selection -- ");
        selection[0] = keyboard.nextInt() - 1;
        System.out.println();
        this.type = SearchType.values()[selection[0]];
        if (selection[0] > 1) {
            System.out.print("" +
                    "Please select from the following heurisitics\n" +
                    "1 - Manhanttan distance\n" +
                    "2 - Misplaced tiles\n" +
                    "3 - Min(Manhattan distance, Misplaced tiles)\n" +
                    "4 - Nilsson's sequence score (not admissible)\n" +
                    "5 - Linear conflict\n" +
                    "6 - Linear conflict + Manhattan distance\n" +
                    "Selection -- ");
            selection[1] = keyboard.nextInt() - 1;
            System.out.println();
            this.heuristic = Heuristics.values()[selection[1]];

            switch (this.type) {
                case BEST:
                    this.comparator = (new Comparator<PuzzleNode>() {
                        @Override
                        public int compare(PuzzleNode o1, PuzzleNode o2) {
                            return (o1.getH_n().get(heuristic) -
                                    o2.getH_n().get(heuristic));
                        }
                    });
                    break;
                case ASTAR:
                    this.comparator = (new Comparator<PuzzleNode>() {
                        @Override
                        public int compare(PuzzleNode o1, PuzzleNode o2) {
                            int o1H_n = o1.getH_n().get(heuristic);
                            int o2H_n = o2.getH_n().get(heuristic);
                            int o1G_n = o1.getG_n();
                            int o2G_n = o2.getG_n();
                            return (o1H_n + o1G_n) - (o2H_n + o2G_n);
                        }
                    });
                    break;
                default:
                    break;

            }

        } else {
            this.heuristic = null;
            this.comparator = (new Comparator<PuzzleNode>() {
                @Override
                public int compare(PuzzleNode o1, PuzzleNode o2) {
                    return 0;
                }
            });
        }
    }

}
