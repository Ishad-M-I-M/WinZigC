package io.cse.winzigc;

public class Token {
  public TokenType type;
  public final String value;

  public Token(TokenType type, String value) {
    this.type = type;
    this.value = value;
  }
}
