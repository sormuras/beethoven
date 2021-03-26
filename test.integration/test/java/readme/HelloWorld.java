package readme;

import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import com.github.sormuras.beethoven.Listable;
import com.github.sormuras.beethoven.Name;
import com.github.sormuras.beethoven.unit.ClassDeclaration;
import com.github.sormuras.beethoven.unit.CompilationUnit;
import com.github.sormuras.beethoven.unit.MethodDeclaration;
import com.github.sormuras.beethoven.unit.MethodParameter;

public class HelloWorld {

  public static void main(String[] args) throws Exception {
    Name out = Name.name(System.class, "out");

    CompilationUnit unit = CompilationUnit.of("beethoven");
    unit.getImportDeclarations().addSingleStaticImport(out);

    ClassDeclaration symphony = unit.declareClass("Symphony", PUBLIC);
    MethodDeclaration main = symphony.declareMethod(void.class, "main", PUBLIC, STATIC);
    MethodParameter strings = main.declareParameter(String[].class, "strings");
    main.addStatement(
        listing ->
            listing
                .add(out)
                .add(".println(")
                .add(Listable.escape("Symphony "))
                .add(" + ")
                .add(Name.name(String.class))
                .add(".join(")
                .add(Listable.escape(" - "))
                .add(", ")
                .add(strings.getName())
                .add("))"));

    unit.list(System.out);
    unit.launch("no.9", "The Choral");
  }
}
