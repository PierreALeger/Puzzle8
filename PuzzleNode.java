import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

/**
 * Implementation of a Node interface for use with different search algorithms.
 * This implementation allows any type of puzzle-N and implements the following heuristics
 * functions. <br>
 * 1 - Manhattan Distance <br>
 * 2 - Misplaced Tiles <br>
 * 3 - Min(Manhattan, Misplaced)<br>
 * 4 - Nilsson's Sequence Score (not admissible) <br>
 * 5 - Linear Conflict <br>
 * 6 - Linear Conflict + Manhattan Distance <br>
 * The values returned by the different heuristic functions are meant to be used in a Comparator
 * object passed to a PriorityQueue for the creation of the open list.
 * @author Pierre-André Léger - 40004010
 * @version COMP 472 - Fall 2016
 */
public class PuzzleNode implements Node<PuzzleNode, String> {


    public static PuzzleData puzzleData;

    private String tag;
    private int[] state;
    private PuzzleNode parent;
    private int blankIndex;
    private ArrayList<PuzzleNode> child;
    private Hashtable<Heuristics, Integer> h_n;
    private int cost;
    private int g_n;

    /**
     * Default constructor.
     * Creates a random board placement by moving the blank tile 50 times.
     */
    PuzzleNode() {
        this(50);
    }

    /**
     * Constructor.
     * Creates a random board placement by moving the blank tile a given number of times.
     * @param times Number of moves simulated to create random board
     */
    PuzzleNode(int times) {
        this(puzzleData.GOAL);
        shuffle(times);
    }

    /**
     * Constructor.
     * Creates a new board given an array representation. Should be used to create initial state (no parent).
     * @param state Array representation of the board (0 is blank)
     */
    PuzzleNode(int[] state) {
        this(state, null);
    }

    /**
     * Standard constructor.
     * Creates a new board given an array representation and a reference to the parent.
     * @param state Array representation of the board (0 is blank)
     * @param parent A reference to a PuzzleNode parent
     */
    PuzzleNode(int[] state, PuzzleNode parent) {
        // Assign what we can
        this.tag = setTag(state);
        this.state = state.clone();
        this.parent = parent;
        this.cost = 1;
        h_n = new Hashtable<>(6);

        // Find the index for the blank tile
        for (int i=0; i<puzzleData.SIZE; i++) {
            if (state[i] == 0) {
                blankIndex = i;
                break;
            }
        }

        // Assign g(n)
        if (this.parent == null) this.g_n = 0;
        else this.g_n = this.parent.getG_n() + this.cost;

        setHeuristicFunctions();

    }

    @Override
    public boolean isGoal() {
        return Arrays.equals(puzzleData.GOAL, state);
    }

    @Override
    public ArrayList<PuzzleNode> getChild() {
        if (this.child == null) {
            populateChild();
        }
        return this.child;
    }

    @Override
    public PuzzleNode getParent() {
        return parent;
    }

    @Override
    public String getTag() {
        return tag;
    }

    public int getG_n() {
        return g_n;
    }

    @Override
    public Hashtable<Heuristics, Integer> getH_n() {
        return h_n;
    }

    /*
     * The following are setters in disguise. They are only used internally thus kept private
     */

    private void setBlankIndex(int blankIndex) {
        this.blankIndex = blankIndex;
    }

    private void setState(int[] state) {
        this.state = state;
    }

    private void setTag(String tag) {
        this.tag = tag;
    }

    private String setTag(int[] state) {
        char[] temp = new char[puzzleData.SIZE];
        for (int i = 0; i < (puzzleData.SIZE); i++) {
            if (state[i] != 0) {
                temp[i] = (char) (state[i] + '0');
            } else {
                temp[i] = 'B';
            }
        }
        return new String(temp);
    }
    private void setHeuristicFunctions() {
        // Compute h1(n) as the Manhattan distance heuristic
        int h1_n = 0;
        int[] current = new int[2];
        int[] correct = null;
        for (int i=0; i<(puzzleData.SIZE); i++) {
            if (state[i] != puzzleData.GOAL[i] && state[i] != 0) {
                current[0] = i / puzzleData.N;
                current[1] = i % puzzleData.N;
                correct = puzzleData.GOALINDEXES[state[i]];
                h1_n += Math.abs(current[0] - correct[0]);
                h1_n += Math.abs(current[1] - correct[1]);
            }
        }

        // Compute h2(n) as the misplaced tiles heuristic
        int h2_n = 0;
        for (int i=0; i<(puzzleData.SIZE); i++) {
            if (puzzleData.GOAL[i] != state[i]) h2_n++;
        }

        /*
          * Compute h3(n) as the Nilsson's Sequence Score heurisitic\
          * Used only for blank-centered 8-puzzle, will evaluate to 0 otherwise
          * h(n) = P(n) + 3 * S(n)
          * P(n) is the Manhattan Distance
          * S(n) is the sequence score
          *    - increase by 2 for every non-center node not followed by its proper successor
          *    - increase by 1 if center tile is not blank
          *  from  https://heuristicswiki.wikispaces.com/Nilsson%27s+Sequence+Score
          */

        int h3_n = 0 , s_n = 0;
        if (Arrays.equals(puzzleData.GOAL, (new int[] {1,2,3,8,0,4,7,6,5}))) {
            if (blankIndex != 4) s_n += 1;
            // Next index of clockwise neighbor
            int[] neighbor = {1,2,5,0,4,8,3,6,7};
            for (int i=0; i<9; i++){
                if (i!=4) {
                    if (this.state[i] % 8 + 1 != this.state[neighbor[i]])
                        s_n += 2;
                }
            }
            h3_n = 3 * s_n + h1_n;
        }

        /*
         * Compute h4(n) as linear conflict
         * This heuristic is meant to be used with Manhattan distance, even though implementation
         * allows the algorithms to run using this heuristic only (not recommended, does not carry
         * much information by itself). Two numbered tiles are in linear conflict if
         * 1 - They are both in their correct respective final rows or columns
         * 2 - They are 'inversed'. i.e first tile is located to the left of the second one while
         * it should be to right in the goal state (similarly for columns)
         * Add 2 to h4(n) for each pair of tiles that are in linear conflict.
         * If a tile A has to move "over" tile B, then either tile A's path will increase by 2,
         * or tile B will have to 'dodge' tile A, which costs 2 also.
         */

        int h4_n = 0;
        boolean[] foundInRow = {false, false, false};
        boolean[] foundInCol = {false, false, false};
        // Find conflicts for each row and column
        for (int i=0; i<puzzleData.N; i++){                  // -------------------------row index
            for (int j=0; j<puzzleData.N; j++) {             // ---------------------item index (column pos)
                if (state[puzzleData.N*i + j] == 0)          // ---------------------skip blank
                if (puzzleData.GOALINDEXES[state[puzzleData.N*i+j]][0] == i) {    // ---current item in correct row ?
                    for (int k=j+1; k < puzzleData.N; k++) {                      // --------check right of item, try to find conflict
                        if (puzzleData.GOALINDEXES[state[puzzleData.N*i+k]][1] < j) {
                            if (!foundInRow[i]) {
                                h4_n += 2;
                                foundInRow[i] = true;
                            }
                        }
                    }
                }
                if (puzzleData.GOALINDEXES[state[puzzleData.N*i+j]][1] == j) {   // ----current item in correct col ?
                    for (int k=i+1; k < puzzleData.N; k++) {   // ----------------------check below item, try to find conflict
                        if (puzzleData.GOALINDEXES[state[puzzleData.N*k+j]][0] < i) {
                            if (!foundInCol[i]) {
                                h4_n += 2;
                                foundInCol[j] = true;
                            }
                        }
                    }
                }
            }
        }

        // Assign all heuristics to the Hashtable with enum keys
        h_n.put(Heuristics.MANHATTAN, h1_n);
        h_n.put(Heuristics.MISPLACED, h2_n);
        h_n.put(Heuristics.MIN_MAN_MIS, Math.min(h1_n, h2_n));
        h_n.put(Heuristics.NILSSONS, h3_n);
        h_n.put(Heuristics.LINEAR_CON, h4_n);
        h_n.put(Heuristics.SUM_MAN_CON, h1_n + h4_n);
    }

    /*
     * Populates the child ArrayList object.
     * Should only be called once from the getChild() method if child is null.
     */
    private void populateChild() {
        this.child = new ArrayList<>(4);
        int temp;
        int destination;
        int[] childState;
        for (int i=0; i<puzzleData.MOVES[blankIndex].length; i++) {
            destination = puzzleData.MOVES[blankIndex][i];
            temp = state[destination];
            childState = state.clone();
            childState[destination] = 0;
            childState[blankIndex] = temp;
            this.child.add(new PuzzleNode(childState, this));
        }
    }

    /**
     * Shuffles the current board configuration by moving the blank a number of times.
     * A mechanism prevents the blank from moving back and forth to the same state in
     * two consecutive moves only.
     * @param times The number of moves
     */

    private void shuffle(int times) {
        int blankPos = -1;
        for (int i = 0; i < (puzzleData.SIZE); i++) {
            if (puzzleData.GOAL[i] == 0) {
                blankPos = i;
                break;
            }
        }
        int newPos;  // Destination
        int range;   // Number of possible moves
        int temp;    // Temp buffer to store previous tile at destination
        int[] transState = puzzleData.GOAL.clone();
        int previousBlank = -1;

        for (int i = 0; i < times; i++) {
            range = puzzleData.MOVES[blankPos].length;
            newPos = puzzleData.MOVES[blankPos][(int) (Math.random() * range)];
            if (newPos == previousBlank) {
                newPos = puzzleData.MOVES[blankPos][(newPos + 1) % range];
            }
            temp = transState[newPos];
            transState[newPos] = 0;
            transState[blankPos] = temp;
            previousBlank = blankPos;
            blankPos = newPos;
        }
        setBlankIndex(blankPos);
        setState(transState);
        setTag(setTag(transState));
        setHeuristicFunctions();
    }

    /**
     * Two PuzzleNodes are equal if they have the same String tag
     * @return true if both PuzzleNode have the same ID tag
     */
    @Override
    public boolean equals(Object obj) {

        if (obj==null) return false;
        PuzzleNode other = null;
        try {
            other = (PuzzleNode)obj;
        } catch (Exception e) {
            return false;
        }
        if (this.getTag().equalsIgnoreCase(other.getTag())) return true;
        return false;
    }

    @Override
    public String toString() {
        StringBuilder out;
        out = new StringBuilder();
        out.ensureCapacity((puzzleData.SIZE)*4);
        out.append("--------------\n");
        for (int i=0; i<puzzleData.SIZE; i++) {
            if (i!=0 && i%puzzleData.N == 0) out.append("\n");
            out.append(this.state[i] + " ");
        }
        out.append("\n");
        out.append("MANHATTAN -------- h1(n) = " + getH_n().get(Heuristics.MANHATTAN) + "\n");
        out.append("MISPLACED -------- h2(n) = " + getH_n().get(Heuristics.MISPLACED) + "\n");
        out.append("NILSSON ---------- h3(n) = " + getH_n().get(Heuristics.NILSSONS) + "\n");
        out.append("LINEAR CONFLICT--- h4(n) = " + getH_n().get(Heuristics.LINEAR_CON) + "\n");
        out.append("Accrued cost -- g(n) = " + getG_n() + "\n");
        return out.toString();
    }

    public String toString(Heuristics heuristic) {
        StringBuilder out;
        out = new StringBuilder();
        out.ensureCapacity((puzzleData.SIZE)*4);
        out.append("--------------\n");
        for (int i=0; i<puzzleData.SIZE; i++) {
            if (i!=0 && i%puzzleData.N == 0) out.append("\n");
            out.append(this.state[i] + " ");
        }
        out.append("\n");
        if (heuristic != null) {
            out.append(heuristic + " -- h(n) = " + getH_n().get(heuristic) + "\n");
        }
        out.append("Accrued cost -- g(n) = " + getG_n() + "\n");
        return out.toString();
    }


}
