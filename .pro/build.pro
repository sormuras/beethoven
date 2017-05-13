import static com.github.forax.pro.Pro.*

set("convention.javaModuleDependencyPath", path("../dependencies"))

set("resolver.remoteRepositories", list(
  // uri("https://oss.sonatype.org/content/repositories/snapshots")
  uri("https://jitpack.io")
))
set("resolver.dependencies", list(
  // junit platform
  "org.junit.jupiter.api=org.junit.jupiter:junit-jupiter-api:5.0.0-M4",
  "org.junit.platform.commons=org.junit.platform:junit-platform-commons:1.0.0-M4",
  "org.opentest4j=org.opentest4j:opentest4j:1.0.0-M2",
  // jqwik test engine
  "org.junit.platform.engine=org.junit.platform:junit-platform-engine:1.0.0-M4",
  "com.github.jlink.jqwik=com.github.jlink:jqwik:0.3.0"
))

set("compiler.lint", "all,-exports")
set("compiler.rawArguments", list("-encoding", "UTF8"))

set("packager.moduleMetadata", list(
  "de.sormuras.beethoven@1.0-SNAPSHOT",
  "de.sormuras.beethoven.type@1.0-SNAPSHOT",
  "de.sormuras.beethoven.unit@1.0-SNAPSHOT",
  "de.sormuras.beethoven.composer@1.0-SNAPSHOT"
))

set("runner.module", "de.sormuras.beethoven/de.sormuras.beethoven.Beethoven")

//
// TODO refactor "prepare.pro" script into a "preparer" plugin
//
/open prepare.pro

prepare(location("../modules"))

run("resolver", "modulefixer", "compiler", "packager", "tester", "runner")

/exit
