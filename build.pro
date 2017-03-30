import static com.github.forax.pro.Pro.*;

// set("pro.loglevel", "debug")

//set("resolver.remoteRepositories", list(
//    uri("https://oss.sonatype.org/content/repositories/snapshots")
//))

set("resolver.dependencies", list(
     "org.junit.jupiter.api=org.junit.jupiter:junit-jupiter-api:5.0.0-M3",
     "org.junit.platform.commons=org.junit.platform:junit-platform-commons:1.0.0-M3",
     "org.opentest4j=org.opentest4j:opentest4j:1.0.0-M1"
//     "org.junit.jupiter.api=org.junit.jupiter:junit-jupiter-api:5.0.0-SNAPSHOT",
//     "org.junit.platform.commons=org.junit.platform:junit-platform-commons:1.0.0-SNAPSHOT",
//     "org.opentest4j=org.opentest4j:opentest4j:1.0.0-SNAPSHOT"
))

set("compiler.rawArguments", list(
    "-encoding", "UTF8"
))

run("resolver", "modulefixer", "compiler", "packager")

/exit
