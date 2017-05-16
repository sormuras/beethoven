// no package

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;

import static java.util.Objects.requireNonNull;

/**
 * Java Shell Builder.
 * @noinspection WeakerAccess, unused
 */
public class Bach {

  public static void main(String... args) throws Exception {
    new Bach();
  }

  private final JavaCompiler javac;
  private final EnumMap<Folder, Path> folders;
  private final StandardStreams standardStreams;
  private final Log log;

  public Bach() {
    this(Level.INFO);
  }

  public Bach(Level initialLevel) {
    this.log = new Log().level(requireNonNull(initialLevel, "initial log level must be null"));
    this.javac = requireNonNull(ToolProvider.getSystemJavaCompiler(), "java compiler not available");
    this.folders = Folder.defaultFolders();
    this.standardStreams = new StandardStreams();
    log.info("[init] %s initialized%n", getClass());
    log.log(Level.CONFIG,"[init] folders=%s%n", folders);
  }

  public void set(Folder folder, Path path) {
    folders.put(folder, path);
  }

  public void set(Level level) {
    log.level(level);
  }

  public void clean() throws IOException {
    log.info("[clean]%n");
    Util.cleanTree(folders.get(Folder.TARGET), false);
  }

  // modules/<name>/main/<type> -> src/main/<type>/<name>
  // modules/<name>/test/<type> -> src/test/<type>/<name>
  // module-info.test -> module-info.java
  public void prepare(Path modules, String module) {
    Path target = folders.get(Folder.TARGET).resolve("prepared").resolve("module-source-path");
    Path preparedMain = target.resolve("main/java");
    Path preparedTest = target.resolve("test/java");
    Util.copyTree(modules.resolve(module + "/main/java"), preparedMain.resolve(module));
    Util.copyTree(modules.resolve(module + "/main/resources"), target.resolve("main/resources/" + module));
    Util.copyTree(modules.resolve(module + "/test/java"), preparedTest.resolve(module));
    Util.copyTree(modules.resolve(module + "/test/resources"), target.resolve("test/resources/" + module));
    // TODO Util.moveModuleInfo(module);
    folders.put(Folder.SOURCE_MAIN_JAVA, preparedMain);
    folders.put(Folder.SOURCE_TEST_JAVA, preparedTest);
  }

  public void prepare(Path modules) throws IOException {
    log.info("[prepare] %s%n", modules);
    Files.find(modules, 1, (path, attr) -> Files.isDirectory(path))
        .filter(path -> !modules.equals(path))
        .map(path -> modules.relativize(path).toString())
        .peek(System.out::println)
        .forEach(module -> prepare(modules, module));
  }

  public int compile() throws IOException {
    log.info("[compile]%n");
    log.log(Level.CONFIG,"[compile] folders=%s%n", folders);
    Path target = folders.get(Folder.TARGET);
    // Util.cleanTree(target, true);
    compile(folders.get(Folder.SOURCE_MAIN_JAVA), target.resolve("main/exploded"));
    // TODO merge! compile(folders.get(Folder.SOURCE_TEST_JAVA), target.resolve("test/exploded"));
    return 0;
  }

  public int compile(Path moduleSourcePath, Path destinationPath) throws IOException {
    List<String> arguments = new ArrayList<>();
    if (log.current == Level.FINEST) {
      // output messages about what the compiler is doing
      arguments.add("-verbose");
    }
    // file encoding
    arguments.add("-d");
    arguments.add(destinationPath.toString());
    // specify character encoding used by source files
    arguments.add("-encoding");
    arguments.add("UTF-8");
    // specify where to find application modules
    arguments.add("--module-path");
    arguments.add(folders.get(Folder.DEPENDENCIES).toString());
    // specify where to find input source files for multiple modules
    arguments.add("--module-source-path");
    arguments.add(moduleSourcePath.toString());
    // collect .java source files
    Files.walk(moduleSourcePath)
        .map(Path::toString)
        .filter(name -> name.endsWith(".java"))
        .forEach(arguments::add);
    // compile
    return javac.run(standardStreams.in, standardStreams.out, standardStreams.err, arguments.toArray(new String[0]));
  }

  class Log {
    Level current;
    Log level(Level level) {
      this.current = level;
      return this;
    }
    void log(Level level, String format, Object... args) {
      if (level.intValue() < current.intValue()) {
        return;
      }
      standardStreams.out.printf(format, args);
    }
    void info(String format, Object... args) {
      log(Level.INFO, format, args);
    }
  }

  enum Folder {
    DEPENDENCIES("deps"),
    SOURCE_MAIN_JAVA("src/main/java"),
    SOURCE_TEST_JAVA("src/test/java"),
    TARGET("target/bach");

    static EnumMap<Folder, Path> defaultFolders() {
      EnumMap<Folder, Path> folders = new EnumMap<>(Folder.class);
      for (Folder folder : Folder.values()) {
        folders.put(folder, folder.defaultPath);
      }
      return folders;
    }

    final Path defaultPath;

    Folder(String defaultPath) {
      this.defaultPath = Paths.get(defaultPath);
    }
  }

  interface Util {

    static void deleteIfExists(Path path) {
      try {
        Files.deleteIfExists(path);
      } catch (IOException e) {
        throw new AssertionError("should not happen", e);
      }
    }

    static void cleanTree(Path root, boolean keepRoot) throws IOException {
      if (Files.notExists(root)) {
        if (keepRoot) {
          Files.createDirectories(root);
        }
        return;
      }
      Files.walk(root)
          .filter(p -> !(keepRoot && root.equals(p)))
          .sorted((p, q) -> -p.compareTo(q))
          .forEach(Util::deleteIfExists);
    }

    static void copyTree(Path source, Path target) {
      if (!Files.exists(source)) {
        return;
      }
      try {
        Files.createDirectories(target);
        Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
            new SimpleFileVisitor<>() {
              @Override
              public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetdir = target.resolve(source.relativize(dir));
                try {
                  Files.copy(dir, targetdir);
                } catch (FileAlreadyExistsException e) {
                  if (!Files.isDirectory(targetdir)) {
                    throw e;
                  }
                }
                return FileVisitResult.CONTINUE;
              }
              @Override
              public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)));
                return FileVisitResult.CONTINUE;
              }
            });
      }
      catch(IOException e) {
        throw new Error("Copying " + source + " to " + target + " failed: " + e, e);
      }
    }
  }

  class StandardStreams {
    InputStream in = System.in;
    PrintStream out = System.out;
    PrintStream err = System.err;
  }
}
