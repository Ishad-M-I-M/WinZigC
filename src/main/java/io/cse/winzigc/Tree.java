package io.cse.winzigc;

import java.util.ArrayList;

public class Tree {
  private final String value;
  private final ArrayList<Tree> children;

  public Tree(String value) {
    this.value = value;
    this.children = new ArrayList<>();
  }

  public Tree(String value, ArrayList<Tree> children) {
    this.value = value;
    this.children = children;
  }

  public ArrayList<String> getTree() {
    ArrayList<String> tree = new ArrayList<>();
    int size = 0;
    if (this.children != null) size = this.children.size();
    tree.add(this.value + "(" + size + ")");
    if (this.children == null) return tree;
    for (Tree child : this.children) {
      for (String entry : child.getTree()) {
        tree.add(". " + entry);
      }
    }
    return tree;
  }
}
