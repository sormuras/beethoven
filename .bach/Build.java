import java.nio.file.Paths;
import java.util.logging.Level;

public class Build {

  public static void main(String... args) throws Exception {
    Bach.builder()
        .log(Level.FINE)
        .override(Folder.SOURCE, Paths.get("modules"))
        .override(Folder.DEPENDENCIES, Paths.get("dependencies"))
      .build()
        .format()
        .compile()
        .run("de.sormuras.beethoven", "de.sormuras.beethoven.Beethoven");
  }

}
