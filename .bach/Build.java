import java.net.URI;
import java.nio.file.Paths;
import java.util.logging.Level;

public class Build {

  public static void main(String... args) throws Exception {
    Bach.builder()
        .log(Level.FINE)
        .override(Folder.SOURCE, Paths.get("modules"))
        .override(Folder.DEPENDENCIES, Paths.get("dependencies"))
      .build()
        // JUnit Jupiter API
        .load("org.junit.jupiter.api", URI.create("http://central.maven.org/maven2/org/junit/jupiter/junit-jupiter-api/5.0.0-M4/junit-jupiter-api-5.0.0-M4.jar"))
        .load("org.junit.platform.commons", URI.create("http://central.maven.org/maven2/org/junit/platform/junit-platform-commons/1.0.0-M4/junit-platform-commons-1.0.0-M4.jar"))
        .load("org.opentest4j", URI.create("http://central.maven.org/maven2/org/opentest4j/opentest4j/1.0.0-M2/opentest4j-1.0.0-M2.jar"))
        // JQwik Test Engine
        .load("com.github.jlink.jqwik", URI.create("https://jitpack.io/com/github/jlink/jqwik/0.3.0/jqwik-0.3.0.jar"))
        .load("org.junit.platform.engine", URI.create("http://central.maven.org/maven2/org/junit/platform/junit-platform-engine/1.0.0-M4/junit-platform-engine-1.0.0-M4.jar"))
        //
        //
        //
        .format()
        .compile()
        .run("de.sormuras.beethoven", "de.sormuras.beethoven.Beethoven")
        .test("--classpath", "dependencies/com.github.jlink.jqwik.jar");
  }

}
