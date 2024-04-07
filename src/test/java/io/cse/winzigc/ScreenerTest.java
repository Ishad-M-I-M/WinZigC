package io.cse.winzigc;

import java.util.ArrayList;
import java.util.Arrays;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ScreenerTest {
  @Test(groups = "screener")
  void testKeywordDetection() {
    for (var keywordEntry : Screener.specialIdentifiers.entrySet()) {
      var tokens =
          Screener.screen(
              new ArrayList<>(
                  Arrays.asList(new Token(TokenType.IDENTIFIER, keywordEntry.getKey()))));
      var result = tokens.get(0);
      Assert.assertEquals(result.type, keywordEntry.getValue());
      Assert.assertEquals(result.value, keywordEntry.getKey());
    }
  }

  @Test(groups = "screener")
  void testRemoveUnnecessaryTokens() {
    var unnecessaryTokens =
        new ArrayList<Token>(
            Arrays.asList(
                new Token(TokenType.COMMENT, "# test comment"),
                new Token(TokenType.WHITESPACE, " "),
                new Token(TokenType.NEWLINE, "\n")));

    var tokens = Screener.screen(unnecessaryTokens);
    Assert.assertEquals(tokens.size(), 0);
  }
}
