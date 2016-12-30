import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Interface that must be implemented by objects used by SearchAlgo.java class.
 * E is the 'state' class.
 * T is the 'state identifier' class.
 *
 * @author Pierre-André Léger - 40004010
 * @version COMP 472 - Fall 2016
 */

public interface Node<E, T> {

    /**
     * Returns true if this state matches the goal state
     * @return true if this instance matches the goal state
     */
    boolean isGoal();

    /**
     * Returns the unique identifier for current state
     * @return a unique identifer of type T
     */
    T getTag();

    /**
     * Returns an ArrayList consisting of all child elements
     * @return an ArrayList containing all child elements of this state
     */
    ArrayList<E> getChild();

    /**
     * Returns the parent of current state.
     * i.e. the state that generated the current state
     * @return the parent of current state
     */
    E getParent();

    /**
     * Returns an ArrayList containing values for various heuristic functions h(n).
     * Their order or nature is specific to each Node implementation
     * @return an ArrayList containing values for h(n)
     */
    Hashtable<Heuristics,Integer> getH_n();

    /**
     * Returns the accrued cost function f(n) for a give Node object.
     * The nature of how this cost is calculated is implementation-specific
     * @return the value of f(n) for this Node
     */
    int getG_n();


}
