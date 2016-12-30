/**
 * A class used to store data on type of puzzle board.
 * A reference is assigned to the PuzzleNode's puzzleData static member.
 *
 * There are no getters or setters since the class is meant to be created once
 * for each puzzle solving algorithm instance
 *
 * @author Pierre-André Léger
 * @version COMP 472 - Fall 2016
 */
public class PuzzleData {

    /**
     * The goal state as an array of integers (blank is 0)
     */
    public final int[] GOAL;
    /**
     * The N-dimension of the puzzle-N
     */
    public final int N;
    /**
     * The total number of tiles (redundant but makes code easier to read)
     */
    public final int SIZE;
    /**
     * A 2-D jagged array where the first index corresponds to a
     * blank position and the second an array of positions where the blank can move
     * (swap with).
     */
    public final int[][] MOVES;
    /**
     * An array of duples where the first index denotes a tile number (blank is 0) and
     * the second is a duple with matrix-type coordinates. Use to calculate Manhattan-distance.
     */
    public final int[][] GOALINDEXES;

    /**
     * Default behavior for constructor is a puzzle8 whose solution is with the blank in center
     */
    PuzzleData() {
        this("1238B4765");
    }

    /**
     * Alternate constructor for Searches.Data object.
     * Will convert argument to int[] array and pass it to main constructor.
     * @param goal The goal state as a String (blank is upper or lower case 'b')
     */
    PuzzleData(String goal) {
        this(PuzzleData.convert(goal));
    }
    // Helper method for the String constructor
    private static int[] convert(String goal) {
        int[] state = new int[goal.length()];
        for (int i=0; i<goal.length(); i++) {
            char c = goal.charAt(i);
            if (c == 'B' || c == 'b') {
                state[i] = 0;
            } else {
                state[i] = Character.getNumericValue(c);
            }
        }
        return state;
    }

    /**
     * Main constructor for Searches objects.
     *
     * @param goal The goal state as an array of integers (blank is 0)
     */
    PuzzleData(int[] goal) {
        // Set basic properties
        this.GOAL = goal.clone();
        this.N = (int)(Math.sqrt(goal.length));
        this.SIZE = this.N * this.N;

        // Calculate MOVES - the lower case int[][] moves is used for construction purposes
        int[][] moves = new int[SIZE][];
        int nbMoves;
        int[] move = new int[4];
        for (int i=0; i<SIZE; i++) {
            nbMoves = 0;
            move[0] = -1;
            move[1] = -1;
            move[2] = -1;
            move[3] = -1;

            // Graph.Test for lateral moves
            if ( i % N != N-1 ) {
                nbMoves++;
                move[0] = (i+1);
            }
            if ( i % N != 0 ) {
                nbMoves++;
                move[1] = (i-1);
            }

            // Graph.Test for vertical moves
            if ( (i+N) < SIZE ) {
                nbMoves++;
                move[2] = (i+N);
            }
            if ( (i-N) >= 0 ) {
                nbMoves++;
                move[3] = (i-N);
            }

            // Assign to array
            moves[i] = new int[nbMoves];
            int j = 0;
            for (int k=0; k<4; k++) {
                if (move[k] != -1) {
                    moves[i][j] = move[k];
                    j++;
                }
            }
        }
        this.MOVES = moves;

        // Calculate GOALINDEXES - lower case identifier used for construction
        int[][] goalIndexes = new int[SIZE][2];
        int index = 0;
        for (int i=0; i<(SIZE); i++) {
            for (int j=0; j<(SIZE); j++) {
                if (GOAL[j] == i) index = j;
            }
            goalIndexes[i][0] = index / N;
            goalIndexes[i][1] = index % N;
        }
        this.GOALINDEXES = goalIndexes;
    }
}
