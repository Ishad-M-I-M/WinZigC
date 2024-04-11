package io.cse.winzigc;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ParserTest {
  @Test(groups = "parser", dataProvider = "SamplePrograms")
  void testPrograms(InputStream program, ArrayList<String> expected)
      throws IOException, ParseException {
    Scanner scanner = new Scanner(program);
    ArrayList<Token> tokens = Screener.screen(scanner.scan());
    Parser parser = new Parser(tokens);
    ArrayList<String> actual = parser.parse().getTree();
    Assert.assertEquals(actual.size(), expected.size());
    for (int i = 0; i < actual.size(); i++) {
      Assert.assertEquals(actual.get(i), expected.get(i));
    }
  }

  @DataProvider(name = "SamplePrograms")
  Object[][] samplePrograms() throws IOException {
    ArrayList<Object[]> data = new ArrayList<>();
    for (int i = 1; i <= 15; i++) {
      String program = String.valueOf(i);
      if (i < 10) program = "0" + program;
      data.add(
          new Object[] {
            TestUtils.readProgramAsStream("winzig_" + program),
            TestUtils.getOutput("winzig_" + program + ".tree")
          });
    }
    return data.toArray(new Object[data.size()][]);
  }
}
