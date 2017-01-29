package readme;

import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.unit.ClassDeclaration;
import de.sormuras.beethoven.unit.CompilationUnit;
import de.sormuras.beethoven.unit.MethodDeclaration;

public class Script {

  public static void main(String[] args) throws Exception {
    CompilationUnit unit = CompilationUnit.of("beethoven");
    ClassDeclaration hello = unit.declareClass("Script", PUBLIC);
    MethodDeclaration main = hello.declareMethod(void.class, "main", PUBLIC, STATIC);

    main.addStatement(
        "{{N // out}}.println({{S}} + {{N // join}}({{S}}, {{#getName // of parameter}}))",
        Name.reflect(System.class, "out"),
        "Symphony ",
        Name.reflect(String.class, "join"),
        " // ",
        main.declareParameter(String[].class, "arguments").setVariable(true));

    unit.list(System.out);
    unit.launch("no.9", "The Choral", "d-Moll op. 125");
  }
}
