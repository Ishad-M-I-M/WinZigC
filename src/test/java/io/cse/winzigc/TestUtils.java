package io.cse.winzigc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class TestUtils {
  private static final Path resourceDir = Path.of("src", "test", "resources");

  public static InputStream readProgramAsStream(String program) throws IOException {
    var file = new File(resourceDir.resolve("winzig_test_programs").resolve(program).toString());
    return new FileInputStream(file);
  }
}
