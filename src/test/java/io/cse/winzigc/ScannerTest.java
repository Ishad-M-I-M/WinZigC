package io.cse.winzigc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ScannerTest {
  @Test(groups = "scanner", dataProvider = "SingleTokens")
  void testSingleTokens(String input, Token token) throws IOException {
    InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    var scanner = new Scanner(is);
    var tokens = scanner.scan();
    Assert.assertEquals(tokens.size(), 1);
    var result = tokens.get(0);
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
    var scanner = new Scanner(is);
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
      "'ab'" // Two character wrapped by single quotes
    };
  }
}
