package io.cse.winzigc;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

class Scanner {
  private final InputStream stream;
  private char nextChar;

  public Scanner(InputStream stream) throws IOException {
    this.stream = stream;
    int c_int = stream.read();
    if (c_int < 0) throw new EOFException("Empty stream");
    this.nextChar = (char) c_int;
  }

  public ArrayList<Token> scan() throws IOException {
    ArrayList<Token> tokens = new ArrayList<>();
    while (this.nextChar > 0) {
      Token token;
      String nextCharStr = String.valueOf(this.nextChar);
      if (nextCharStr.matches("[A-Za-z_]")) {
        token = this.scanIdentifier();
      } else if (nextCharStr.matches("[0-9]")) {
        token = this.scanInt();
      } else if (" \f\t\013".contains(nextCharStr)) {
        token = this.scanWhiteSpace();
      } else if (this.nextChar == '\'') {
        token = this.scanChar();
      } else if (this.nextChar == '"') {
        token = this.scanString();
      } else if (this.nextChar == '#') {
        token = this.scanLineComment();
      } else if (this.nextChar == '{') {
        token = this.scanBlockComment();
      } else if (this.nextChar == '\n') {
        token = new Token(TokenType.NEWLINE, String.valueOf(this.nextChar));
        this.setNextChar();
      } else if (this.nextChar == '.') {
        this.setNextChar();
        if (this.nextChar != '.') {
          token = new Token(TokenType.DOT, ".");
        } else {
          token = new Token(TokenType.DOTS, "..");
          this.setNextChar();
        }
      } else if (this.nextChar == ':') {
        this.setNextChar();
        if (this.nextChar != '=') {
          token = new Token(TokenType.COLON, ":");
        } else {
          this.setNextChar();
          if (this.nextChar != ':') {
            token = new Token(TokenType.ASSIGNMENT, ":=");
          } else {
            token = new Token(TokenType.SWAP, ":=:");
            this.setNextChar();
          }
        }
      } else if (this.nextChar == '<') {
        this.setNextChar();
        if (this.nextChar == '=') {
          token = new Token(TokenType.LESS_EQUALS, "<=");
          this.setNextChar();
        } else if (this.nextChar == '>') {
          token = new Token(TokenType.NOT_EQUALS, "<>");
          this.setNextChar();
        } else {
          token = new Token(TokenType.LESS, "<");
        }
      } else if (this.nextChar == '>') {
        this.setNextChar();
        if (this.nextChar != '=') {
          token = new Token(TokenType.GREATER, ">");
        } else {
          token = new Token(TokenType.GREATER_EQUALS, ">=");
          this.setNextChar();
        }
      } else if (this.nextChar == '=') {
        token = new Token(TokenType.EQUALS, String.valueOf(this.nextChar));
        this.setNextChar();
      } else if (this.nextChar == ';') {
        token = new Token(TokenType.SEMICOLON, String.valueOf(this.nextChar));
        this.setNextChar();
      } else if (this.nextChar == ',') {
        token = new Token(TokenType.COMMA, String.valueOf(this.nextChar));
        this.setNextChar();
      } else if (this.nextChar == '(') {
        token = new Token(TokenType.OPEN_BRACKET, String.valueOf(this.nextChar));
        this.setNextChar();
      } else if (this.nextChar == ')') {
        token = new Token(TokenType.CLOSE_BRACKET, String.valueOf(this.nextChar));
        this.setNextChar();
      } else if (this.nextChar == '+') {
        token = new Token(TokenType.PLUS, String.valueOf(this.nextChar));
        this.setNextChar();
      } else if (this.nextChar == '-') {
        token = new Token(TokenType.MINUS, String.valueOf(this.nextChar));
        this.setNextChar();
      } else if (this.nextChar == '*') {
        token = new Token(TokenType.MULTIPLY, String.valueOf(this.nextChar));
        this.setNextChar();
      } else if (this.nextChar == '/') {
        token = new Token(TokenType.DIVIDE, String.valueOf(this.nextChar));
        this.setNextChar();
      } else throw new IOException("Invalid token");
      tokens.add(token);
    }
    return tokens;
  }

  private Token scanIdentifier() throws IOException {
    StringBuilder builder = new StringBuilder();
    String nextCharStr;
    do {
      builder.append(this.nextChar);
      this.setNextChar();
      nextCharStr = String.valueOf(this.nextChar);
    } while (nextCharStr.matches("[A-Za-z0-9_]"));
    String value = builder.toString();
    return new Token(TokenType.IDENTIFIER, value);
  }

  private Token scanInt() throws IOException {
    StringBuilder builder = new StringBuilder();
    String nextCharStr;
    do {
      builder.append(this.nextChar);
      this.setNextChar();
      nextCharStr = String.valueOf(this.nextChar);
    } while (nextCharStr.matches("[0-9]"));
    String value = builder.toString();
    return new Token(TokenType.INT, value);
  }

  private Token scanWhiteSpace() throws IOException {
    StringBuilder builder = new StringBuilder();
    String nextCharStr;
    do {
      builder.append(this.nextChar);
      this.setNextChar();
      nextCharStr = String.valueOf(this.nextChar);
    } while (" \f\t\013".contains(nextCharStr));
    String value = builder.toString();
    return new Token(TokenType.WHITESPACE, value);
  }

  private Token scanChar() throws IOException {
    StringBuilder builder = new StringBuilder();
    builder.append(this.nextChar);
    this.setNextChar();
    if (this.nextChar == '\'') throw new IOException("Invalid token");
    builder.append(this.nextChar);
    this.setNextChar();
    builder.append(this.nextChar);
    if (this.nextChar != '\'') throw new IOException("Invalid token");
    String value = builder.toString();
    this.setNextChar();
    return new Token(TokenType.CHAR, value);
  }

  private Token scanString() throws IOException {
    StringBuilder builder = new StringBuilder();
    do {
      builder.append(this.nextChar);
      this.setNextChar();
    } while (this.nextChar != '"');
    builder.append(this.nextChar);
    this.setNextChar();
    String value = builder.toString();
    return new Token(TokenType.STRING, value);
  }

  private Token scanLineComment() throws IOException {
    StringBuilder builder = new StringBuilder();
    do {
      builder.append(this.nextChar);
      this.setNextChar();
    } while (this.nextChar != '\n');
    String value = builder.toString();
    return new Token(TokenType.COMMENT, value);
  }

  private Token scanBlockComment() throws IOException {
    StringBuilder builder = new StringBuilder();
    do {
      builder.append(this.nextChar);
      this.setNextChar();
    } while (this.nextChar != '}');
    builder.append(this.nextChar);
    this.setNextChar();
    String value = builder.toString();
    return new Token(TokenType.COMMENT, value);
  }

  private void setNextChar() throws IOException {
    int c_int = stream.read();
    if (c_int < 0) this.nextChar = 0;
    else this.nextChar = (char) c_int;
  }
}
