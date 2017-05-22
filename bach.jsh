Path bachJava = Paths.get("target/Bach.java")
if (Files.notExists(bachJava)) {
  URL bachURL = new URL("https://raw.githubusercontent.com/sormuras/bach/master/bach/Bach.java");
  Files.createDirectories(bachJava.getParent());
  try (InputStream in = bachURL.openStream()) {
    Files.copy(in, bachJava, StandardCopyOption.REPLACE_EXISTING);
  }
  System.out.printf("created %s [url=%s]%n", bachJava, bachURL);
}
/open target/Bach.java

{
Bach.builder()
    .override(Folder.SOURCE, Paths.get("modules"))
    .override(Folder.DEPENDENCIES, Paths.get("dependencies"))
  .build()
    .compile()
    .run("de.sormuras.beethoven", "de.sormuras.beethoven.Beethoven");
}

/exit
