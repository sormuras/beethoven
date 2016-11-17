package readme;

import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.unit.ClassDeclaration;
import de.sormuras.beethoven.unit.CompilationUnit;
import de.sormuras.beethoven.unit.MethodDeclaration;
import de.sormuras.beethoven.unit.MethodParameter;
import javax.lang.model.element.Modifier;

public class HelloWorld {

  public static void main(String[] args) throws Exception {
    Name out = Name.name(System.class, "out");

    CompilationUnit unit = CompilationUnit.of("beethoven");
    unit.getImportDeclarations().addSingleStaticImport(out);

    ClassDeclaration symphony = unit.declareClass("Symphony");
    symphony.addModifier(Modifier.PUBLIC);

    MethodParameter parameter = MethodParameter.of(String[].class, "strings");
    MethodDeclaration main = symphony.declareMethod(void.class, "main");
    main.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
    main.addParameter(parameter);
    main.addStatement("{{N}}.println({{S}} + {{#getName}}[0])", out, "Symphony ", parameter);

    System.out.println(unit.list());

    Class<?> hello = unit.compile();
    Object[] arguments = {new String[] {"no.9 - The Choral"}};
    hello.getMethod("main", String[].class).invoke(null, arguments);
  }
}
