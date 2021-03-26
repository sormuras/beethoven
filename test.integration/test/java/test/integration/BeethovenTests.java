package test.integration;

import com.github.sormuras.beethoven.Beethoven;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BeethovenTests {

  static class PrintStreamFacade extends PrintStream {
    List<String> strings = new ArrayList<>();

    PrintStreamFacade(OutputStream out) {
      super(out, true);
    }

    @Override
    public void print(String line) {
      strings.add(line);
    }

    @Override
    public void println(String line) {
      strings.add(line);
    }
  }

  @Test
  void main() {
    PrintStream out = System.out;
    PrintStreamFacade facade = new PrintStreamFacade(out);
    System.setOut(facade);
    Beethoven.main();
    System.setOut(out);
    String text = String.join("\n", facade.strings);
    Assertions.assertTrue(text.contains(Beethoven.VERSION));
  }
}
