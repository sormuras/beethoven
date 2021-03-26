package test.integration.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.Name;
import com.github.sormuras.beethoven.unit.Block;
import com.github.sormuras.beethoven.unit.MethodDeclaration;
import com.github.sormuras.beethoven.unit.UnitTool;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;

class UnitToolTests {

  @Test
  void override() {
    MethodDeclaration base = new MethodDeclaration();
    base.addAnnotation(Override.class);
    base.addAnnotation(Annotation.annotation(Name.name("Reply"), 42));
    base.setReturnType(Object.class);
    base.setName("method");
    base.declareParameter(String.class, "text");
    MethodDeclaration over = UnitTool.override(base, false);
    over.addModifier(Modifier.DEFAULT);
    over.setBody(new Block());
    assertEquals("@Override\ndefault Object method(String text) {\n}\n", over.list("\n"));
    over = UnitTool.override(base, true);
    over.setBody(new Block());
    assertEquals("@Reply(42)\n@Override\nObject method(String text) {\n}\n", over.list("\n"));
  }
}
