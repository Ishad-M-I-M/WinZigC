package io.cse.winzigc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ScannerTest {
  @Test(groups = "scanner", dataProvider = "SingleTokens")
  void testSingleTokens(String input, Token token) throws IOException {
    InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    Scanner scanner = new Scanner(is);
    ArrayList<Token> tokens = scanner.scan();
    Assert.assertEquals(tokens.size(), 1);
    Token result = tokens.get(0);
    Assert.assertEquals(result.type, token.type);
    Assert.assertEquals(result.value, token.value);
  }

  @DataProvider(name = "SingleTokens")
  Object[][] singleTokens() {
    return new Object[][] {
      new Object[] {"hello", new Token(TokenType.IDENTIFIER, "hello")},
      new Object[] {"123", new Token(TokenType.INT, "123")},
      new Object[] {" ", new Token(TokenType.WHITESPACE, " ")}, // Single space
      new Object[] {"    ", new Token(TokenType.WHITESPACE, "    ")}, // Spaces and tabs
      new Object[] {"'a'", new Token(TokenType.CHAR, "'a'")},
      new Object[] {"\"Hello 123\"", new Token(TokenType.STRING, "\"Hello 123\"")},
      new Object[] {"{ 123 \n \t }", new Token(TokenType.COMMENT, "{ 123 \n \t }")},
      new Object[] {"\n", new Token(TokenType.NEWLINE, "\n")},
      new Object[] {".", new Token(TokenType.DOT, ".")},
      new Object[] {"..", new Token(TokenType.DOTS, "..")},
      new Object[] {":", new Token(TokenType.COLON, ":")},
      new Object[] {":=", new Token(TokenType.ASSIGNMENT, ":=")},
      new Object[] {":=:", new Token(TokenType.SWAP, ":=:")},
      new Object[] {"<=", new Token(TokenType.LESS_EQUALS, "<=")},
      new Object[] {"<>", new Token(TokenType.NOT_EQUALS, "<>")},
      new Object[] {"<", new Token(TokenType.LESS, "<")},
      new Object[] {">=", new Token(TokenType.GREATER_EQUALS, ">=")},
      new Object[] {">", new Token(TokenType.GREATER, ">")},
      new Object[] {"=", new Token(TokenType.EQUALS, "=")},
      new Object[] {";", new Token(TokenType.SEMICOLON, ";")},
      new Object[] {",", new Token(TokenType.COMMA, ",")},
      new Object[] {"(", new Token(TokenType.OPEN_BRACKET, "(")},
      new Object[] {")", new Token(TokenType.CLOSE_BRACKET, ")")},
      new Object[] {"+", new Token(TokenType.PLUS, "+")},
      new Object[] {"-", new Token(TokenType.MINUS, "-")},
      new Object[] {"*", new Token(TokenType.MULTIPLY, "*")},
      new Object[] {"/", new Token(TokenType.DIVIDE, "/")}
    };
  }

  @Test(groups = "scanner", dataProvider = "InvalidTokens")
  void testInvalidTokens(String input) throws IOException {
    InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    Scanner scanner = new Scanner(is);
    try {
      scanner.scan();
      Assert.fail("Invalid token is included");
    } catch (IOException e) {
      Assert.assertEquals(e.getMessage(), "Invalid token");
    }
  }

  @DataProvider(name = "InvalidTokens")
  Object[] invalidTokens() {
    return new Object[] {
      "'ab'", // Two character wrapped by single quotes
      "''", // Empty character wrapped by single quotes
      "'", // One single quote
      "\"", // One double quote
      "$", // Special character
      "abc { def" // Unterminated block comment
    };
  }

  @Test(groups = "scanner")
  void testLineComment() throws IOException {
    String[] comments = {"# hello $% \n", "# hello # $% { \013 abc \f\t  "};
    for (String comment : comments) {
      InputStream is = new ByteArrayInputStream(comment.getBytes(StandardCharsets.UTF_8));
      Scanner scanner = new Scanner(is);
      ArrayList<Token> tokens = scanner.scan();
      Assert.assertEquals(tokens.size(), 1 + (comment.contains("\n") ? 1 : 0));
      Assert.assertEquals(tokens.get(0).type, TokenType.COMMENT);
      Assert.assertEquals(tokens.get(0).value, comment.replace("\n", ""));
    }
  }
}
