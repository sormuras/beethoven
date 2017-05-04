import static com.github.forax.pro.Pro.*

//set("pro.loglevel", "debug")
//set("compiler.verbose", true)

set("resolver.dependencies", list(
  "junit.jupiter.api=org.junit.jupiter:junit-jupiter-api:5.0.0-M4",
  "junit.platform.commons=org.junit.platform:junit-platform-commons:1.0.0-M4",
  "opentest4j=org.opentest4j:opentest4j:1.0.0-M2"
));

set("compiler.rawArguments", list("-encoding", "UTF8"))

run("resolver", "modulefixer", "compiler") // , "tester")

// set("pro.loglevel", "debug")
// run("formatter")

/exit
