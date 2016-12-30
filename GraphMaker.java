import java.io.File;
import java.util.Hashtable;
import java.util.Scanner;

/**
 * Created by Pierre-André on 2016-10-01.
 */
class GraphMaker<E> {

    /*
     * make() expects to find to text files in the package directory directory.
     *
     * nodes.txt in the following format
     * <Tag> <h_n>
     * <Tag> <h_n>
     * ...
     * <Tag> <h_n>
     * #
     * The character '#' denotes an end file. Do not use it in tag names
     *
     * edges.txt in the following format, listed in a similar fashion
     * <from Tag> <to Tag> <cost>
     * ...
     * #
     * Again, '#' denotes end of file.
     *
     * Graphs are all bidirected in this case
     */

    static Hashtable<String, GraphNode> make() {

        Hashtable<String, GraphNode> nodes = new Hashtable<>();

        Scanner handlerNodes = null;
        try {
            handlerNodes =  new Scanner(new File("./src/nodes.txt"));
        } catch (Exception e) {
            System.out.print("Missed");
            System.exit(1);
        }

        int h;
        GraphNode node;
        String name;

        while (!handlerNodes.hasNext("#")) {
            name = handlerNodes.next();
            h = handlerNodes.nextInt();
            node = new GraphNode(name, h);
            nodes.put(name, node);
            handlerNodes.nextLine();
        }


        handlerNodes.close();
        Scanner handlerEdges = null;
        try {
            handlerEdges =  new Scanner(new File("./src/edges.txt"));
        } catch (Exception e) {
            System.out.print("Missed");
            System.exit(1);
        }

        String from;
        String to;
        int cost;

        while (!handlerEdges.hasNext("#")) {
            from = handlerEdges.next();
            to = handlerEdges.next();
            cost = handlerEdges.nextInt();
            nodes.get(from).addChild(nodes.get(to), cost);
            nodes.get(to).addChild(nodes.get(from), cost);
            handlerEdges.nextLine();
        }

        handlerEdges.close();

        return nodes;

    }
}
