// load and open Bach.java
Path bachPath = Paths.get("target")
Path bachJava = bachPath.resolve("Bach.java");
if (Files.notExists(bachJava)) {
  URL bachURL = new URL("https://raw.githubusercontent.com/sormuras/bach/master/bach/Bach.java");
  Files.createDirectories(bachPath);
  try (InputStream in = bachURL.openStream()) {
    Files.copy(in, bachJava, StandardCopyOption.REPLACE_EXISTING);
  }
}
/open target/Bach.java

// use it
Bach.Builder builder = Bach.builder()
builder.override(Folder.SOURCE, Paths.get("modules"))
builder.override(Folder.DEPENDENCIES, Paths.get("dependencies"))
Bach bach = builder.bach()
bach.compile()
bach.run("de.sormuras.beethoven", "de.sormuras.beethoven.Beethoven")

/exit
