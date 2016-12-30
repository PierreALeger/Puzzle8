import java.util.Hashtable;

/**
 * Created by Pierre-André on 2016-10-01.
 */
class GraphNode {

    /*
     * Each node has an identifier that describes it.
     * Type of identifier is generic, so anything can be used.
     * A hashtable is used to store the GraphNode objects, using the
     * tag of type E as an index. This mirrors the child data structure.
     */

    private String tag;
    private int h_n;
    private int f_n;
    private int g_n;
    private GraphNode parent;
    private Hashtable<String, Integer> child;

    /*
     * Constructors
     *
     * The GraphNode class is "general" purpose, in the sense that it can be used to
     * create a directed graph or to create a search tree.
     * When used to create a graph, the 2 argument constructor is used.
     * For the tree, use the 3 argument constructor
     */


    /*
     * First two constructors used to create the original set of nodes as well as
     * tree nodes for uninformed searches.
     *
     * Next two constructor populate fields for search tree with informed searches
     */


    GraphNode(String tag, int h_n) {
        this(tag, h_n, null, 0);
    }

    GraphNode(GraphNode other, GraphNode parent, int cost) {
        this(other.getTag(), other.getH_n(), parent, cost);
    }
    GraphNode(String tag, int h_n, GraphNode parent, int cost) {
        this.tag = tag;
        this.h_n = h_n;
        this.parent = parent;

        if (parent == null) {
            this.f_n = 0;
        } else {
            this.f_n = this.parent.getF_n() + cost;
        }
        this.g_n = this.f_n + this.h_n;
        this.child = new Hashtable<>();
    }

    /*
     * Getters, Setters
     */

    void addChild(GraphNode child, int cost) {
        this.child.put(child.getTag(), cost);
    }


    int getF_n() {
        return f_n;
    }

    int getG_n() {
        return g_n;
    }

    String getTag() {
        return tag;
    }

    GraphNode getParent() {
        return parent;
    }

    int getH_n() {
        return h_n;
    }

    Hashtable<String, Integer> getChild() {
        return child;
    }

    @Override
    public String toString() {
        return tag;
    }

    @Override
    public boolean equals(Object obj) {

        /*
         * Two Nodes are equal if they have the same String tag
         */

        if (obj==null) return false;
        GraphNode other = null;
        try {
            other = (GraphNode)obj;
        } catch (Exception e) {
            return false;
        }
        if (this.getTag().equalsIgnoreCase(other.getTag())) return true;
        return false;
    }
}
