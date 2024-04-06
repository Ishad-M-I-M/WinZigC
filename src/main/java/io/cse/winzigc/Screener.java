package io.cse.winzigc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Screener {
  public static ArrayList<Token> screen(ArrayList<Token> input) {
    Map<String, TokenType> specialIdentifiers = Map.ofEntries(
            Map.entry("program", TokenType.PROGRAM),
            Map.entry("var", TokenType.VAR),
            Map.entry("const", TokenType.CONST),
            Map.entry("type", TokenType.TYPE),
            Map.entry("function", TokenType.FUNCTION),
            Map.entry("return", TokenType.RETURN),
            Map.entry("begin", TokenType.BEGIN),
            Map.entry("end", TokenType.END),
            Map.entry("output", TokenType.OUTPUT),
            Map.entry("if", TokenType.IF),
            Map.entry("then", TokenType.THEN),
            Map.entry("else", TokenType.ELSE),
            Map.entry("while", TokenType.WHILE),
            Map.entry("do", TokenType.DO),
            Map.entry("case", TokenType.CASE),
            Map.entry("of", TokenType.OF),
            Map.entry("otherwise", TokenType.OTHERWISE),
            Map.entry("repeat", TokenType.REPEAT),
            Map.entry("for", TokenType.FOR),
            Map.entry("until", TokenType.UNTIL),
            Map.entry("loop", TokenType.LOOP),
            Map.entry("pool", TokenType.POOL),
            Map.entry("exit", TokenType.EXIT),
            Map.entry("mod", TokenType.MOD),
            Map.entry("and", TokenType.AND),
            Map.entry("or", TokenType.OR),
            Map.entry("not", TokenType.NOT),
            Map.entry("read", TokenType.READ),
            Map.entry("succ", TokenType.SUCC),
            Map.entry("pred", TokenType.PRED),
            Map.entry("chr", TokenType.CHR),
            Map.entry("ord", TokenType.ORD),
            Map.entry("eof", TokenType.EOF)
    );

    ArrayList<Token> output = new ArrayList<>(input.size());
    input.forEach(
        token -> {
          if (token.type.equals(TokenType.COMMENT)
              || token.type.equals(TokenType.WHITESPACE)
              || token.type.equals(TokenType.NEWLINE)) return;
          if (token.type.equals(TokenType.IDENTIFIER)
              && specialIdentifiers.containsKey(token.value)) {
            token.type = specialIdentifiers.get(token.value);
          }
          output.add(token);
        });
    return output;
  }
}
