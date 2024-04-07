package io.cse.winzigc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ScreenerTest {
  @Test(groups = "screener")
  void testKeywordDetection() {
    for (Map.Entry<String, TokenType> keywordEntry : Screener.specialIdentifiers.entrySet()) {
      ArrayList<Token> tokens =
          Screener.screen(
              new ArrayList<>(List.of(new Token(TokenType.IDENTIFIER, keywordEntry.getKey()))));
      Token result = tokens.get(0);
      Assert.assertEquals(result.type, keywordEntry.getValue());
      Assert.assertEquals(result.value, keywordEntry.getKey());
    }
  }

  @Test(groups = "screener")
  void testRemoveUnnecessaryTokens() {
    ArrayList<Token> unnecessaryTokens =
        new ArrayList<>(
            Arrays.asList(
                new Token(TokenType.COMMENT, "# test comment"),
                new Token(TokenType.WHITESPACE, " "),
                new Token(TokenType.NEWLINE, "\n")));
    //noinspection InstantiationOfUtilityClass
    Screener screener = new Screener();
    ArrayList<Token> tokens = Screener.screen(unnecessaryTokens);
    Assert.assertEquals(tokens.size(), 0);
  }
}
