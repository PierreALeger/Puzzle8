import java.util.*;

/**
 * SearchAlgo.java
 *
 * An instance of SearchAlgo is created for every new search.<br>
 * Closed lists are implemented using a hash set, open lists are either priority queues or double
 * ended queues.
 *
 */
public class SearchAlgo<E extends Node<E, T>, T>{

    private E startState;
    private HashSet<T> closedList;
    private ArrayDeque<E> openListDeck;
    private PriorityQueue<E> openListPQ;
    private SearchType type;
    private Comparator<E> comparator;



    SearchAlgo(E start, SearchType type, Comparator<E> comparator) {
        this.startState = start;
        this.closedList = new HashSet<>();
        this.openListDeck = new ArrayDeque<>();
        this.openListPQ = new PriorityQueue<>(comparator);
        this.type = type;
        this.comparator = comparator;
    }

    public int getTotalGenerated() {
        return closedList.size();
    }

    ArrayList<E> search() {

        E current = null;
        ArrayList<E> children;
        switch (this.type) {
            case DEPTH:
            case BREADTH:

                openListDeck.addFirst(this.startState);

                while (true) {
                    current = openListDeck.removeFirst();
                    closedList.add(current.getTag());

                    if (current.isGoal()) break;

                    children = current.getChild();
                    for (E item : children) {
                        if (closedList.add(item.getTag())) {
                            if (this.type == SearchType.DEPTH) openListDeck.addFirst(item);
                            else openListDeck.addLast(item);
                        }
                    }
                }
                break;

            case BEST:
            case ASTAR:

                openListPQ.add(startState);

                while (true) {

                    current = openListPQ.poll();
                    closedList.add(current.getTag());

                    if (current.isGoal()) break;

                    children = current.getChild();
                    if (children.isEmpty()) break;
                    for (E item : children) {
                        if (closedList.add(item.getTag())) {
                            openListPQ.add(item);
                        }
                    }
                }
                break;
            default:
                break;
        }

        ArrayList<E> path = new ArrayList<>();
        while (current != null) {
            path.add(0, current);
            current = current.getParent();
        }
        path.trimToSize();

        return path;
    }

}
