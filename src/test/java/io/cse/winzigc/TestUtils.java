package io.cse.winzigc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class TestUtils {
  private static final Path resourceDir = Path.of("src", "test", "resources");

  public static InputStream readProgramAsStream(String program) throws IOException {
    File file = new File(resourceDir.resolve("winzig_test_programs").resolve(program).toString());
    return new FileInputStream(file);
  }

  public static ArrayList<String> getOutput(String program) throws IOException {
    File file =
        new File(resourceDir.resolve("winzig_test_programs").resolve(program ).toString());
    Scanner scan = new Scanner(file);
    ArrayList<String> tree = new ArrayList<>();
    while (scan.hasNextLine()) {
      tree.add(scan.nextLine().replace("\n", ""));
    }
    return tree;
  }
}
