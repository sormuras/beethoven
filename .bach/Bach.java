/*
 * Copyright 2017 Christian Stein
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

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
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;

import static java.util.Objects.requireNonNull;

/**
 * Java Shell Builder.
 *
 * @noinspection WeakerAccess, unused
 */
public class Bach {

  public static void main(String... args) throws Exception {
    Bach bach = new Bach(Level.FINE);
    bach.set(Bach.Folder.DEPENDENCIES, Paths.get("dependencies"));
    bach.clean();
    bach.prepare(Paths.get("modules"));
    bach.compile();
  }

  private final JavaCompiler javac;
  private final EnumMap<Folder, Path> folders;
  private final StandardStreams standardStreams;
  private final Log log;
  private final Util util;

  public Bach() {
    this(Level.INFO);
  }

  public Bach(Level initialLevel) {
    this.util = new Util();
    this.javac = requireNonNull(ToolProvider.getSystemJavaCompiler(), "java compiler not available");
    this.folders = Folder.defaultFolders();
    this.standardStreams = new StandardStreams();
    this.log = new Log().level(initialLevel).tag("init");
    log.info("%s initialized%n", getClass());
    log.log(Level.CONFIG, "level=%s%n", initialLevel);
    log.log(Level.CONFIG, "pwd=`%s`%n", Paths.get(".").toAbsolutePath().normalize());
    log.log(Level.CONFIG, "folder %s%n", folders.entrySet());
  }

  public void set(Folder folder, Path path) {
    folders.put(folder, path);
  }

  public void set(Level level) {
    log.level(level);
  }

  public void clean() throws IOException {
    log.tag("clean");
    util.cleanTree(folders.get(Folder.TARGET), false);
  }

  public void prepare(Path modules, String module) {
    log.log(Level.CONFIG, "module %s%n", module);
    Path target = folders.get(Folder.TARGET).resolve("prepared").resolve("module-source-path");
    Path preparedMain = target.resolve("main/java");
    Path preparedTest = target.resolve("test/java");
    util.copyTree(modules.resolve(module + "/main/java"), preparedMain.resolve(module));
    util.copyTree(modules.resolve(module + "/main/resources"), target.resolve("main/resources/" + module));
    util.copyTree(modules.resolve(module + "/test/java"), preparedTest.resolve(module));
    util.copyTree(modules.resolve(module + "/test/resources"), target.resolve("test/resources/" + module));
    // TODO Util.moveModuleInfo(module);
    folders.put(Folder.SOURCE_MAIN_JAVA, preparedMain);
    folders.put(Folder.SOURCE_TEST_JAVA, preparedTest);
  }

  public void prepare(Path modules) throws IOException {
    log.tag("prepare").info("preparing modules in `%s`%n", modules.toAbsolutePath());
    List<String> names = new ArrayList<>();
    Files.find(modules, 1, (path, attr) -> Files.isDirectory(path))
        .filter(path -> !modules.equals(path))
        .map(path -> modules.relativize(path).toString())
        .peek(names::add)
        .forEach(module -> prepare(modules, module));
    log.info("prepared module %s%n", names);
  }

  public int compile() throws IOException {
    log.tag("compile").log(Level.CONFIG, "folder %s%n", folders.entrySet());
    Path compiled = folders.get(Folder.TARGET).resolve("compiled");
    util.cleanTree(compiled, true);
    compile(folders.get(Folder.SOURCE_MAIN_JAVA), compiled.resolve("main/exploded"));
    // TODO compile(folders.get(Folder.SOURCE_TEST_JAVA), compiled.resolve("test/exploded"));
    return 0;
  }

  public int compile(Path moduleSourcePath, Path destinationPath) throws IOException {
    List<String> arguments = new ArrayList<>();
    if (log.threshold <= Level.FINEST.intValue()) {
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
    int[] count = {0};
    Files.walk(moduleSourcePath)
        .map(Path::toString)
        .filter(name -> name.endsWith(".java"))
        .peek(name -> count[0]++)
        .forEach(arguments::add);
    // compile
    int code = javac.run(standardStreams.in, standardStreams.out, standardStreams.err, arguments.toArray(new String[0]));
    log.info("%d java files processed%n", count[0]);
    return code;
  }

  class Log {
    int threshold;
    String tag;

    Log level(Level level) {
      this.threshold = level.intValue();
      return this;
    }

    Log tag(String tag) {
      this.tag = tag;
      info("%n");
      return this;
    }

    private void printContext(Level level) {
      standardStreams.out.printf("%7s ", tag);
      if (threshold < Level.INFO.intValue()) {
        standardStreams.out.printf("%6s| ", level.getName().toLowerCase());
      }
    }

    void log(Level level, String format, Object... args) {
      if (level.intValue() < threshold) {
        return;
      }
      if (args.length == 1 && args[0] instanceof Collection) {
        for (Object arg : (Iterable<?>) args[0]) {
          printContext(level);
          standardStreams.out.printf(format, arg);
        }
        return;
      }
      printContext(level);
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

  class Util {

    void deleteIfExists(Path path) {
      try {
        Files.deleteIfExists(path);
      } catch (IOException e) {
        throw new AssertionError("should not happen", e);
      }
    }

    void cleanTree(Path root, boolean keepRoot) throws IOException {
      if (Files.notExists(root)) {
        if (keepRoot) {
          Files.createDirectories(root);
        }
        return;
      }
      Files.walk(root)
          .filter(p -> !(keepRoot && root.equals(p)))
          .sorted((p, q) -> -p.compareTo(q))
          .forEach(this::deleteIfExists);
      log.log(Level.FINE, "deleted tree `%s`%n", root);
    }

    void copyTree(Path source, Path target) {
      if (!Files.exists(source)) {
        return;
      }
      log.log(Level.FINE, "copy `%s` to `%s`%n", source, target);
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
      } catch (IOException e) {
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
