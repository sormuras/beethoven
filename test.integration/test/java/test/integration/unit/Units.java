package test.integration.unit;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import com.github.sormuras.beethoven.type.ClassType;
import com.github.sormuras.beethoven.type.Type;
import com.github.sormuras.beethoven.unit.ClassDeclaration;
import com.github.sormuras.beethoven.unit.CompilationUnit;
import com.github.sormuras.beethoven.unit.Initializer;
import com.github.sormuras.beethoven.unit.NormalClassDeclaration;
import com.github.sormuras.beethoven.unit.TypeParameter;
import java.util.List;
import javax.lang.model.element.Modifier;
import test.integration.Counter;

public interface Units {

  static List<Type> nTimesI(int n) {
    ClassType i = ClassType.type("", "I");
    if (n == 0) {
      return singletonList(i);
    }
    if (n == 1) {
      return asList(i, i);
    }
    if (n == 2) {
      return asList(i, i, i);
    }
    return emptyList();
  }

  static CompilationUnit abc() {
    CompilationUnit unit = new CompilationUnit();
    unit.declareInterface("I");
    NormalClassDeclaration a = unit.declareClass("A");
    a.addModifier(Modifier.PUBLIC);
    a.addTypeParameter(TypeParameter.of("U"));
    a.declareField(ClassType.type("", "A", "B", "C"), "raw").addAnnotation(Counter.Mark.class);
    a.declareField(ClassType.type("", "A", "B", "C").parameterized(Units::nTimesI), "parametered")
        .addAnnotation(Counter.Mark.class);
    NormalClassDeclaration b = a.declareClass("B");
    b.addTypeParameter(TypeParameter.of("V"));
    b.addTypeParameter(TypeParameter.of("W"));
    NormalClassDeclaration c = b.declareClass("C");
    c.addTypeParameter(TypeParameter.of("X"));
    c.addTypeParameter(TypeParameter.of("Y"));
    c.addTypeParameter(TypeParameter.of("Z"));
    return unit;
  }

  static CompilationUnit simple() {
    CompilationUnit unit = new CompilationUnit();

    ClassDeclaration alpha = unit.declareClass("Alpha");
    alpha.declareClass("Removed");
    alpha.declareInitializer(true);
    alpha.getDeclarations().clear();
    alpha.getInitializers().clear();

    ClassDeclaration beta = unit.declareClass("Beta");
    beta.declareInitializer(true).add(l -> l.add("// init of ").add(beta.getName()).newline());

    ClassDeclaration gamma = unit.declareClass("Gamma");
    gamma.addModifier(Modifier.PUBLIC);
    ClassDeclaration ray = gamma.declareClass("Ray");
    Initializer rayInit = ray.declareInitializer(false);

    ClassDeclaration xxx = rayInit.declareLocalEnum("XXX");
    rayInit.add(xxx.getName() + ".class.getName();");
    rayInit.declareLocalClass("ZZZ");

    return unit;
  }
}
