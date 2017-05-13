import java.nio.file.*
import java.nio.file.attribute.*
import java.util.*

void moveModuleInfo(String module) {
  Path path = Paths.get("src", "test", "java", module);
  if (!Files.exists(path)) return;
  Path pathSource = path.resolve("module-info.test");
  if (!Files.exists(pathSource)) return;
  try {
    Files.move(pathSource, path.resolve("module-info.java"));
  }
  catch(IOException e) {
    throw new Error("Moving module-info failed: " + module, e);
  }
}

void copyTree(Path source, Path target) {
  if (!Files.exists(source)) {
    return;
  }
  try {
     Files.createDirectories(target);
     Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
         new SimpleFileVisitor<Path>() {

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

// modules/<name>/main/<type> -> src/main/<type>/<name>
// modules/<name>/test/<type> -> src/test/<type>/<name>
// module-info.test -> module-info.java
void prepareModule(Path modules, String module) {
  copyTree(modules.resolve(module + "/main/java"), Paths.get("src", "main", "java", module));
  copyTree(modules.resolve(module + "/main/resources"), Paths.get("src", "main", "resources", module));
  copyTree(modules.resolve(module + "/test/java"), Paths.get("src", "test", "java", module));
  copyTree(modules.resolve(module + "/test/resources"), Paths.get("src", "test", "resources", module));
  moveModuleInfo(module);
}

void prepare(Path modules) {
  try {
    // delete src directory
    if (Files.exists(Paths.get("src"))) {
      Files.walk(Paths.get("src")).map(Path::toFile).filter(File::exists).sorted((o1, o2) -> -o1.compareTo(o2)).forEach(File::delete);
    }
    for (File directory : modules.toFile().listFiles(File::isDirectory)) {
      prepareModule(modules, directory.getName());
    }
  }
  catch(Exception e) {
    e.printStackTrace();
    throw new Error("Preparation of " + modules + " failed", e);
  }
}
