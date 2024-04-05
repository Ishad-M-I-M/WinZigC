package io.cse.winzigc;

import java.util.ArrayList;
import java.util.Arrays;

public class Screener {
  public static ArrayList<Token> screen(ArrayList<Token> input) {
    String[] specialIdentifiers =
        new String[] {
          "program",
          "var",
          "const",
          "type",
          "function",
          "return",
          "begin",
          "end",
          "output",
          "if",
          "then",
          "else",
          "while",
          "do",
          "case",
          "of",
          "otherwise",
          "repeat",
          "for",
          "until",
          "loop",
          "pool",
          "exit",
          "mod",
          "and",
          "or",
          "not",
          "read",
          "succ",
          "pred",
          "chr",
          "ord",
          "eof"
        };
    ArrayList<Token> output = new ArrayList<>(input.size());
    input.forEach(
        token -> {
          if (token.type.equals("Comment")
              || token.type.equals("WhiteSpace")
              || token.type.equals("NewLine")) return;
          if (token.type.equals("Identifier")
              && Arrays.stream(specialIdentifiers).anyMatch(x -> x.equals(token.value))) {
            token.type = token.value;
          }
          output.add(token);
        });
    return output;
  }
}
