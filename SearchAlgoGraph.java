import java.util.*;

/**
 * Created by Pierre-André on 2016-10-03.
 */
class SearchAlgoGraph {

    private Hashtable<String, GraphNode> nodes;

    SearchAlgoGraph(Hashtable<String, GraphNode> nodes) {
        this.nodes = nodes;
    }

    ArrayList<GraphNode> search(GraphNode start, GraphNode goal, SearchType type) {

        SearchTree tree = new SearchTree(new GraphNode(start, null, 0), goal);

        // Closed list is a HashSet object with all tags
        HashSet<String> closedList = new HashSet<>();
        GraphNode current = null;
        Hashtable<String, Integer> children = null;


        // When populating the search tree, we create clones of the original set
        // Uninformed search type do not take into consideration cost, heuristic or accrued cost
        // Informed searches use PriorityQueue ordered by h(n) or g(n) for Best First or A*, respectively

        GraphNode child;

        if (type == SearchType.DEPTH || type == SearchType.BREADTH) {
            ArrayDeque<GraphNode> openList = new ArrayDeque<>();
            openList.addFirst(tree.getRoot());

            while (true) {
                current = openList.removeFirst();
                closedList.add(current.getTag());
                System.out.println(current);

                // Test if goal state
                if (tree.isGoal(current)) break;

                // If not, get children
                children = nodes.get(current.getTag()).getChild();
                Set<String> keys = children.keySet();
                for (String key : keys) {
                    if (closedList.add(key)) {
                        child = nodes.get(key);
                        child = new GraphNode(child, current, nodes.get(current.getTag()).getChild().get(key));
                        if (type== SearchType.DEPTH) openList.addFirst(child);
                        else {
                            openList.addLast(child);
                        }
                    }
                }
            }
        } else if (type == SearchType.BEST || type == SearchType.ASTAR) {

            Comparator<GraphNode> comparator;
            PriorityQueue<GraphNode> openList = null;
            if (type == SearchType.BEST) {
                comparator = new Comparator<GraphNode>() {
                    @Override
                    public int compare(GraphNode o1, GraphNode o2) {
                        return o1.getH_n() - o2.getH_n();
                    }
                };
                openList = new PriorityQueue<>(comparator);
            } else {
                comparator = new Comparator<GraphNode>() {
                    @Override
                    public int compare(GraphNode o1, GraphNode o2) {
                        return o1.getG_n() - o2.getG_n();
                    }
                };
                openList = new PriorityQueue<>(comparator);
            }

            openList.add(tree.getRoot());

            while (true) {
                current = openList.poll();
                closedList.add(current.getTag());
                System.out.println(current);

                if (tree.isGoal(current)) break;

                children = nodes.get(current.getTag()).getChild();
                Set<String> keys = children.keySet();
                for (String key : keys) {
                    if (closedList.add(key)) {
                        child = nodes.get(key);
                        child = new GraphNode(child, current, nodes.get(current.getTag()).getChild().get(key));
                        openList.add(child);
                    }
                }

            }

        }
        ArrayList<GraphNode> path = new ArrayList<>();
        while (current != null) {
            path.add(0, current);
            current = current.getParent();
        }
        return path;
    }





    // Nodes stored in the SearchTree are clones of the original
    // The information stored in the child field is not carried over to the tree
    // In fact, nodes are oblivious to their childs, only a refence to the parent is stored
    // When a solution is found, we traverse upwards through the parents to find the path

    private class SearchTree {

        private GraphNode root;
        private GraphNode goal;

        SearchTree() {
            this(null, null);
        }

        SearchTree(GraphNode root, GraphNode goal) {
            this.root = root;
            this.goal = goal;
        }

        public boolean isGoal(GraphNode graphNode) {
            return (graphNode.equals(goal));
        }

        public GraphNode getRoot() {
            return root;
        }

        public GraphNode getGoal() {
            return goal;
        }
    }
}
