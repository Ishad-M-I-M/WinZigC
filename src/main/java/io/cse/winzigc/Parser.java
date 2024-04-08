package io.cse.winzigc;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;

public class Parser {

  private final ArrayList<Token> tokens;
  private final LinkedList<Tree> stack;
  private int index;
  private Token nextToken;

  public Parser(ArrayList<Token> tokens) {
    this.tokens = tokens;
    this.stack = new LinkedList<>();
    this.index = 0;
    this.nextToken = tokens.get(this.index);
  }

  public Tree parse() throws ParseException {
    parseWinZig();
    if (stack.size() != 1)
      throw new ParseException("Stack has " + stack.size() + " elements after parsing", this.index);
    return stack.pop();
  }

  private void parseWinZig() throws ParseException {
    if (nextToken.type != TokenType.PROGRAM)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseIdentifier();
    setNextToken();
    if (nextToken.type != TokenType.COLON)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseConsts();
    parseTypes();
    parseDclns();
    parseSubProgs();
    parseBody();
    parseIdentifier();
    setNextToken();
    if (nextToken.type != TokenType.DOT)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    buildTree("program", 7);
  }

  private void parseIdentifier() throws ParseException {
    setNextToken();
    if (nextToken.type != TokenType.IDENTIFIER)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    stack.push(new Tree(this.nextToken.value));
    buildTree("<identifier>", 1);
  }

  private void parseConsts() throws ParseException {
    setNextToken();
    if (nextToken.type != TokenType.CONST) {
      unsetNextToken();
      stack.push(new Tree("consts"));
      return;
    }
    int prevSize = stack.size();
    parseConstList();
    int newSize = stack.size();
    setNextToken();
    if (nextToken.type != TokenType.SEMICOLON)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    buildTree("consts", newSize - prevSize);
  }

  private void parseConstList() throws ParseException {
    parseConst();
    parseX_1();
  }

  private void parseConst() throws ParseException {
    parseIdentifier();
    setNextToken();
    if (nextToken.type != TokenType.EQUALS)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseConstValue();
    buildTree("const", 2);
  }

  private void parseConstValue() throws ParseException {
    setNextToken();
    if (nextToken.type == TokenType.INT) {
      stack.push(new Tree(nextToken.value));
      buildTree("<integer>", 1);
    } else if (nextToken.type == TokenType.CHAR) {
      stack.push(new Tree(nextToken.value));
      buildTree("<char>", 1);
    } else if (nextToken.type == TokenType.IDENTIFIER) {
      stack.push(new Tree(nextToken.value));
      buildTree("<identifier>", 1);
    } else {
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    }
  }

  private void parseX_1() throws ParseException {
    setNextToken();
    if (nextToken.type == TokenType.COMMA) {
      parseConstList();
    } else {
      unsetNextToken();
    }
  }

  private void parseTypes() throws ParseException {
    setNextToken();
    if (nextToken.type != TokenType.TYPE) {
      unsetNextToken();
      stack.push(new Tree("types"));
      return;
    }
    int prevSize = stack.size();
    parseTypeList();
    int newSize = stack.size();
    buildTree("types", newSize - prevSize);
  }

  private void parseTypeList() throws ParseException {
    parseType();
    setNextToken();
    if (nextToken.type != TokenType.SEMICOLON)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseX_10();
  }

  private void parseX_10() throws ParseException {
    try {
      setNextToken();
      if (nextToken.type != TokenType.IDENTIFIER) return;
    } catch (ParseException ignored) {
      return;
    } finally {
      unsetNextToken();
    }
    parseType();
    setNextToken();
    if (nextToken.type != TokenType.SEMICOLON)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseX_10();
  }

  private void parseType() throws ParseException {
    parseIdentifier();
    setNextToken();
    if (nextToken.type != TokenType.EQUALS)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseLitList();
    buildTree("type", 2);
  }

  private void parseLitList() throws ParseException {
    setNextToken();
    if (nextToken.type != TokenType.OPEN_BRACKET)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    int prevSize = stack.size();
    parseNameList();
    int newSize = stack.size();
    setNextToken();
    if (nextToken.type != TokenType.CLOSE_BRACKET)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    buildTree("lit", newSize - prevSize);
  }

  private void parseNameList() throws ParseException {
    parseIdentifier();
    parseX_2();
  }

  private void parseX_2() throws ParseException {
    setNextToken();
    if (nextToken.type != TokenType.COMMA) {
      unsetNextToken();
      return;
    }
    parseNameList();
  }

  private void parseDclns() throws ParseException {
    setNextToken();
    if (nextToken.type != TokenType.VAR) {
      unsetNextToken();
      stack.push(new Tree("dclns"));
      return;
    }
    int prevSize = stack.size();
    parseDclnList();
    int newSize = stack.size();
    buildTree("dclns", newSize - prevSize);
  }

  private void parseDclnList() throws ParseException {
    parseDcln();
    setNextToken();
    if (nextToken.type != TokenType.SEMICOLON)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseX_11();
  }

  private void parseX_11() throws ParseException {
    try {
      setNextToken();
      if (nextToken.type != TokenType.IDENTIFIER) return;
    } catch (ParseException ignored) {
      return;
    } finally {
      unsetNextToken();
    }
    parseDcln();
    setNextToken();
    if (nextToken.type != TokenType.SEMICOLON)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseX_11();
  }

  private void parseDcln() throws ParseException {
    int prevSize = stack.size();
    parseNameList();
    setNextToken();
    if (nextToken.type != TokenType.COLON)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseIdentifier();
    int newSize = stack.size();
    buildTree("var", newSize - prevSize);
  }

  private void parseSubProgs() throws ParseException {
    try {
      setNextToken();
      if (nextToken.type != TokenType.FUNCTION) return;
    } catch (ParseException ignored) {
      return;
    } finally {
      unsetNextToken();
      buildTree("subprogs", 0);
    }
    int prevSize = stack.size();
    parseFcn();
    parseSubProgs();
    int newSize = stack.size();
    buildTree("subprogs", newSize - prevSize);
  }

  private void parseFcn() throws ParseException {
    // TODO: Implement
  }

  private void parseBody() throws ParseException {
    setNextToken();
    if (nextToken.type != TokenType.BEGIN)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    int prevSize = stack.size();
    parseStatementList();
    int newSize = stack.size();
    setNextToken();
    if (nextToken.type != TokenType.END)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    buildTree("block", newSize - prevSize);
  }

  private void parseStatementList() throws ParseException {
    // TODO: Implement
  }

  private void buildTree(String name, int nItems) throws ParseException {
    if (stack.size() < nItems)
      throw new ParseException(
          "Stack has only " + stack.size() + " elements for " + name, this.index);
    ArrayList<Tree> children = new ArrayList<>(nItems);
    for (int ind = nItems - 1; ind >= 0; ind--) {
      Tree child = stack.pop();
      children.add(ind, child);
    }
    Tree parent = new Tree(name, children);
    stack.push(parent);
  }

  private void setNextToken() throws ParseException {
    this.index++;
    if (index >= tokens.size()) throw new ParseException("Unexpected end of tokens", this.index);
    this.nextToken = this.tokens.get(this.index);
  }

  private void unsetNextToken() throws ParseException {
    this.index--;
    if (index >= tokens.size()) throw new ParseException("Unexpected end of tokens", this.index);
    this.nextToken = this.tokens.get(this.index);
  }
}
