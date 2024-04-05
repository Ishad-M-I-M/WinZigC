package io.cse.winzigc;

public class Token {
  public String type;
  public final String value;

  public Token(String type, String value) {
    this.type = type;
    this.value = value;
  }
}
