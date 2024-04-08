package io.cse.winzigc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
      new Object[] {new Tree("i"), new ArrayList<>(List.of("i(0)"))},
      new Object[] {
        new Tree("not", new ArrayList<>(List.of(new Tree("i")))),
        new ArrayList<>(Arrays.asList("not(1)", ". i(0)"))
      },
      new Object[] {
        new Tree("and", new ArrayList<>(Arrays.asList(new Tree("i"), new Tree("j")))),
        new ArrayList<>(Arrays.asList("and(2)", ". i(0)", ". j(0)"))
      },
      new Object[] {
        new Tree(
            "and",
            new ArrayList<>(
                Arrays.asList(
                    new Tree("not", new ArrayList<>(List.of(new Tree("k")))), new Tree("j")))),
        new ArrayList<>(Arrays.asList("and(2)", ". not(1)", ". . k(0)", ". j(0)"))
      }
    };
  }
}
