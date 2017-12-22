import java.util.*;
import java.lang.String;

public class test {
  public static void main(String[] args) {

    Node node = new Node(5);

    node.children = new Node[2];


    node.children[0] = new Node(4);
    node.children[1] = new Node(3);

    node.children[0].children = new Node[3];
    node.children[1].children = new Node[2];

    node.children[0].children[0] = new Node(2);
    node.children[0].children[1] = new Node(1);
    node.children[0].children[2] = new Node(0);
    node.children[1].children[0] = new Node(-1);
    node.children[1].children[1] = new Node(-2);

    ArrayList<Node> nodes = node.assemble(node);
    for (int i = 0; i < nodes.size(); i++) {
      System.out.println(nodes.get(i).value);
    }
    node.children[1].children[1].value = -10;

    for (int i = 0; i < nodes.size(); i++) {
      System.out.println(nodes.get(i).value);
    }

  }
}


class Node {
  int value;
  Node[] children = null;

  public Node(int value) {
    this.value = value;
  }

  public ArrayList<Node> assemble(Node node) {
    ArrayList<Node> assembled = new ArrayList<Node>();
    assembled.add(node);
    if (children == null) return assembled;
    for (int i = 0; i < children.length; i++) {
      assembled.addAll(children[i].assemble(children[i]));
    }
    return assembled;
  }

}
