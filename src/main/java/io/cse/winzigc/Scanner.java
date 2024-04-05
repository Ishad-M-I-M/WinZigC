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
        token = new Token("NewLine", String.valueOf(this.nextChar));
        this.setNextChar();
      } else if (this.nextChar == '.') {
        this.setNextChar();
        if (this.nextChar != '.') {
          token = new Token("Dot", ".");
        } else {
          token = new Token("Dots", "..");
          this.setNextChar();
        }
      } else if (this.nextChar == ':') {
        this.setNextChar();
        if (this.nextChar != '=') {
          token = new Token("Colon", ":");
        } else {
          this.setNextChar();
          if (this.nextChar != ':') {
            token = new Token("Assignment", ":=");
          } else {
            token = new Token("Swap", ":=:");
            this.setNextChar();
          }
        }
      } else if (this.nextChar == '<') {
        this.setNextChar();
        if (this.nextChar == '=') {
          token = new Token("LessEquals", "<=");
          this.setNextChar();
        } else if (this.nextChar == '>') {
          token = new Token("NotEqual", "<>");
          this.setNextChar();
        } else {
          token = new Token("Less", "<");
        }
      } else if (this.nextChar == '>') {
        this.setNextChar();
        if (this.nextChar != '=') {
          token = new Token("Greater", ">");
        } else {
          token = new Token("GreaterEquals", ">=");
          this.setNextChar();
        }
      } else if (this.nextChar == '=') {
        token = new Token("Equals", String.valueOf(this.nextChar));
        this.setNextChar();
      } else if (this.nextChar == ';') {
        token = new Token("SemiColon", String.valueOf(this.nextChar));
        this.setNextChar();
      } else if (this.nextChar == ',') {
        token = new Token("Comma", String.valueOf(this.nextChar));
        this.setNextChar();
      } else if (this.nextChar == '(') {
        token = new Token("OpenBracket", String.valueOf(this.nextChar));
        this.setNextChar();
      } else if (this.nextChar == ')') {
        token = new Token("CloseBracket", String.valueOf(this.nextChar));
        this.setNextChar();
      } else if (this.nextChar == '+') {
        token = new Token("Plus", String.valueOf(this.nextChar));
        this.setNextChar();
      } else if (this.nextChar == '-') {
        token = new Token("Minus", String.valueOf(this.nextChar));
        this.setNextChar();
      } else if (this.nextChar == '*') {
        token = new Token("Multiply", String.valueOf(this.nextChar));
        this.setNextChar();
      } else if (this.nextChar == '/') {
        token = new Token("Divide", String.valueOf(this.nextChar));
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
    return new Token("Identifier", value);
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
    return new Token("Int", value);
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
    return new Token("WhiteSpace", value);
  }

  private Token scanChar() throws IOException {
    StringBuilder builder = new StringBuilder();
    builder.append(this.nextChar);
    this.setNextChar();
    if (this.nextChar == '\'') throw new IOException("Invalid token");
    builder.append(this.nextChar);
    this.setNextChar();
    if (this.nextChar != '\'') throw new IOException("Invalid token");
    String value = builder.toString();
    return new Token("Char", value);
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
    return new Token("String", value);
  }

  private Token scanLineComment() throws IOException {
    StringBuilder builder = new StringBuilder();
    do {
      builder.append(this.nextChar);
      this.setNextChar();
    } while (this.nextChar != '\n');
    String value = builder.toString();
    return new Token("Comment", value);
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
    return new Token("Comment", value);
  }

  private void setNextChar() throws IOException {
    int c_int = stream.read();
    if (c_int < 0) this.nextChar = 0;
    else this.nextChar = (char) c_int;
  }

  public static boolean test() {
    return true;
  }
}
