package io.cse.winzigc;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ScannerTest {
  @Test(groups = "scanner")
  void testScan() {
    Assert.assertTrue(Scanner.test());
  }
}