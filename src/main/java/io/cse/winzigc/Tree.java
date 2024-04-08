package io.cse.winzigc;

import java.util.ArrayList;

public class Tree {
  private String root;
  private ArrayList<Tree> children;

  public Tree(String value) {
    this.root = value;
    this.children = new ArrayList<>();
  }

  public Tree(String value, ArrayList<Tree> children) {
    this.root = value;
    this.children = children;
  }

  public ArrayList<String> getTree() {
    ArrayList<String> tree = new ArrayList<>();
    tree.add(this.root);
    for (Tree child : this.children) {
      for (String entry : child.getTree()) {
        tree.add(". " + entry);
      }
    }
    return tree;
  }
}
