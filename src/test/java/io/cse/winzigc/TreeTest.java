package io.cse.winzigc;

import java.util.ArrayList;
import java.util.Arrays;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TreeTest {
  @Test(groups = "tree", dataProvider = "Trees")
  void testGetTree(Tree tree, ArrayList<String> parsedTree) {
    ArrayList<String> output = tree.getTree();
    Assert.assertEquals(output.size(), parsedTree.size());
    for (int i = 0; i < output.size(); i++) {
      Assert.assertEquals(output.get(i), parsedTree.get(i));
    }
  }

  @DataProvider(name = "Trees")
  Object[][] testTrees() {
    return new Object[][] {
      new Object[] {new Tree("i"), new ArrayList<>(Arrays.asList("i"))},
      new Object[] {
        new Tree("not", new ArrayList<>(Arrays.asList(new Tree("i")))),
        new ArrayList<>(Arrays.asList("not", ". i"))
      },
      new Object[] {
        new Tree("and", new ArrayList<>(Arrays.asList(new Tree("i"), new Tree("j")))),
        new ArrayList<>(Arrays.asList("and", ". i", ". j"))
      },
      new Object[] {
        new Tree(
            "and",
            new ArrayList<>(Arrays.asList(new Tree("not", new ArrayList<>(Arrays.asList(new Tree("k")))), new Tree("j")))),
        new ArrayList<>(Arrays.asList("and", ". not", ". . k",". j"))
      }
    };
  }
}
