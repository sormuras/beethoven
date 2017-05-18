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
Bach bach = new Bach(Level.FINE, Layout.IDEA)
bach.set(Folder.SOURCE, Paths.get("modules"))
bach.compile()
bach.run("de.sormuras.beethoven", "de.sormuras.beethoven.Beethoven")

/exit
