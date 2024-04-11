package io.cse.winzigc;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
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
      buildTree("consts", 0);
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
      unsetNextToken();
      parseIdentifier();
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
      buildTree("types", 0);
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
      buildTree("dclns", 0);
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
    setNextToken();
    if (nextToken.type != TokenType.FUNCTION)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseIdentifier();
    setNextToken();
    if (nextToken.type != TokenType.OPEN_BRACKET)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseParams();
    setNextToken();
    if (nextToken.type != TokenType.CLOSE_BRACKET)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    setNextToken();
    if (nextToken.type != TokenType.COLON)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseIdentifier();
    setNextToken();
    if (nextToken.type != TokenType.SEMICOLON)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseConsts();
    parseTypes();
    parseDclns();
    parseBody();
    parseIdentifier();
    setNextToken();
    if (nextToken.type != TokenType.SEMICOLON)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    buildTree("fcn", 8);
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

  private void parseParams() throws ParseException {
    int prevSize = stack.size();
    parseParamList();
    int newSize = stack.size();
    buildTree("params", newSize - prevSize);
  }

  private void parseParamList() throws ParseException {
    setNextToken();
    if (nextToken.type != TokenType.IDENTIFIER) {
      unsetNextToken();
      return;
    }
    unsetNextToken();
    while (true) {
      parseDcln();
      setNextToken();
      if (nextToken.type != TokenType.SEMICOLON) {
        unsetNextToken();
        return;
      }
    }
  }

  private void parseStatementList() throws ParseException {
    while (true) {
      setNextToken();
      if (nextToken.type == TokenType.END
          || nextToken.type == TokenType.UNTIL
          || nextToken.type == TokenType.POOL) {
        unsetNextToken();
        break;
      }
      unsetNextToken();
      parseStatement();
      setNextToken();
      if (nextToken.type != TokenType.SEMICOLON)
        throw new ParseException("Unexpected token " + nextToken.value, this.index);
    }
  }

  private void parseStatement() throws ParseException {
    setNextToken();
    TokenType nextType = nextToken.type;
    unsetNextToken();
    switch (nextType) {
      case IDENTIFIER:
        {
          parseAssignment();
          break;
        }
      case OUTPUT:
        {
          parseOutput();
          break;
        }
      case IF:
        {
          parseIf();
          break;
        }
      case WHILE:
        {
          parseWhile();
          break;
        }
      case REPEAT:
        {
          parseRepeat();
          break;
        }
      case FOR:
        {
          parseFor();
          break;
        }
      case LOOP:
        {
          parseLoop();
          break;
        }
      case CASE:
        {
          parseCase();
          break;
        }
      case READ:
        {
          parseRead();
          break;
        }
      case EXIT:
        {
          parseExit();
          break;
        }
      case RETURN:
        {
          parseReturn();
          break;
        }
      case BEGIN:
        {
          parseBody();
          break;
        }
      default:
        {
          break;
        }
    }
  }

  private void parseAssignment() throws ParseException {
    parseIdentifier();
    setNextToken();
    String nodeType;
    switch (nextToken.type) {
      case ASSIGNMENT:
        {
          nodeType = "assign";
          parseExpression();
          break;
        }
      case SWAP:
        {
          nodeType = "swap";
          parseIdentifier();
          break;
        }
      default:
        {
          throw new ParseException("Unexpected token " + nextToken.value, this.index);
        }
    }
    buildTree(nodeType, 2);
  }

  private void parseExpression() throws ParseException {
    // TODO: implement
    throw new ParseException("Unimplemented", 0);
  }

  private void parseOutput() throws ParseException {
    setNextToken();
    if (nextToken.type != TokenType.OUTPUT)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    setNextToken();
    if (nextToken.type != TokenType.OPEN_BRACKET)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    int prevSize = stack.size();
    parseOutExpList();
    int newSize = stack.size();
    if (nextToken.type != TokenType.CLOSE_BRACKET)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    buildTree("output", newSize - prevSize);
  }

  private void parseOutExpList() throws ParseException {
    parseOutExp();
    setNextToken();
    while (nextToken.type == TokenType.COMMA) {
      parseOutExp();
      setNextToken();
    }
    unsetNextToken();
  }

  private void parseOutExp() throws ParseException {
    setNextToken();
    if (nextToken.type == TokenType.STRING) {
      stack.push(new Tree(nextToken.value));
      buildTree("<string>", 1);
    } else {
      unsetNextToken();
      parseExpression();
    }
  }

  private void parseIf() throws ParseException {
    setNextToken();
    if (nextToken.type != TokenType.IF)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    int prevSize = stack.size();
    parseExpression();
    setNextToken();
    if (nextToken.type != TokenType.THEN)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseStatement();
    parseElse();
    int newSize = stack.size();
    buildTree("if", newSize - prevSize);
  }

  private void parseElse() throws ParseException {
    setNextToken();
    if (nextToken.type == TokenType.ELSE) {
      parseStatement();
    } else {
      unsetNextToken();
    }
  }

  private void parseWhile() throws ParseException {
    setNextToken();
    if (nextToken.type != TokenType.WHILE)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseExpression();
    setNextToken();
    if (nextToken.type != TokenType.DO)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseStatement();
    buildTree("while", 2);
  }

  private void parseRepeat() throws ParseException {
    setNextToken();
    if (nextToken.type != TokenType.REPEAT)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseStatementList();
    setNextToken();
    if (nextToken.type != TokenType.UNTIL)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseExpression();
    buildTree("repeat", 2);
  }

  private void parseFor() throws ParseException {
    setNextToken();
    if (nextToken.type != TokenType.FOR)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    setNextToken();
    if (nextToken.type != TokenType.OPEN_BRACKET)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseForStat();
    setNextToken();
    if (nextToken.type != TokenType.SEMICOLON)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseForExp();
    setNextToken();
    if (nextToken.type != TokenType.SEMICOLON)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseForStat();
    setNextToken();
    if (nextToken.type != TokenType.CLOSE_BRACKET)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseStatement();
    buildTree("for", 4);
  }

  private void parseForStat() throws ParseException {
    setNextToken();
    if (nextToken.type != TokenType.SEMICOLON) {
      unsetNextToken();
      parseAssignment();
    } else {
      unsetNextToken();
      buildTree("<null>", 0);
    }
  }

  private void parseForExp() throws ParseException {
    setNextToken();
    if (nextToken.type != TokenType.SEMICOLON) {
      unsetNextToken();
      parseExpression();
    } else {
      unsetNextToken();
      buildTree("<null>", 0);
    }
  }

  private void parseLoop() throws ParseException {
    setNextToken();
    if (nextToken.type != TokenType.LOOP)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    int prevSize = stack.size();
    parseStatementList();
    int newSize = stack.size();
    setNextToken();
    if (nextToken.type != TokenType.POOL)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    buildTree("loop", newSize - prevSize);
  }

  private void parseCase() throws ParseException {
    // TODO: implement
    throw new ParseException("Unimplemented", 0);
  }

  private void parseRead() throws ParseException {
    setNextToken();
    if (nextToken.type != TokenType.READ)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    setNextToken();
    if (nextToken.type != TokenType.OPEN_BRACKET)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    int prevSize = stack.size();
    parseNameList();
    int newSize = stack.size();
    setNextToken();
    if (nextToken.type != TokenType.CLOSE_BRACKET)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    buildTree("read", newSize - prevSize);
  }

  private void parseExit() throws ParseException {
    setNextToken();
    if (nextToken.type != TokenType.EXIT)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseExpression();
    buildTree("exit", 0);
  }

  private void parseReturn() throws ParseException {
    setNextToken();
    if (nextToken.type != TokenType.RETURN)
      throw new ParseException("Unexpected token " + nextToken.value, this.index);
    parseExpression();
    buildTree("return", 2);
  }

  private void buildTree(String name, int nItems) throws ParseException {
    if (stack.size() < nItems)
      throw new ParseException(
          "Stack has only " + stack.size() + " elements for " + name, this.index);
    ArrayList<Tree> children = new ArrayList<>(Collections.nCopies(nItems, null));
    for (int ind = nItems - 1; ind >= 0; ind--) {
      Tree child = stack.pop();
      children.set(ind, child);
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
