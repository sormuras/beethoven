import static com.github.forax.pro.Pro.*;

set("pro.loglevel", "debug")
set("compiler.verbose", false)

set("compiler.rawArguments", list("-encoding", "UTF8"))

run("compiler", "junitconsole")

/exit
