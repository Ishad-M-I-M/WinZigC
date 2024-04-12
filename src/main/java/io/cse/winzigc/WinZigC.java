package io.cse.winzigc;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;

public class WinZigC {
  public static void main(String[] args) throws IOException, ParseException {
    if (args.length != 2 || args[0].compareTo("-ast") != 0) {
      System.out.println("Invalid input format.");
      System.exit(1);
    }
    File file = new File(args[1]);
    InputStream input = new FileInputStream(file);
    ArrayList<String> output =
        new Parser(Screener.screen((new Scanner(input)).scan())).parse().getTree();
    for (String line : output) {
      System.out.println(line);
    }
  }
}
