package unit;

import static java.lang.System.out;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Launch {

  public static void main(String[] args) throws IOException {
    Files.write(Paths.get(args[0]), String.join("\n", args).getBytes());
  }
}
