package test.integration.composer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.sormuras.beethoven.Name;
import com.github.sormuras.beethoven.composer.ConstructorComposer;
import com.github.sormuras.beethoven.composer.EqualsComposer;
import com.github.sormuras.beethoven.composer.HashCodeComposer;
import com.github.sormuras.beethoven.composer.ImportsComposer;
import com.github.sormuras.beethoven.composer.PropertyComposer;
import com.github.sormuras.beethoven.composer.ToStringComposer;
import test.integration.Tests;
import com.github.sormuras.beethoven.unit.ClassDeclaration;
import com.github.sormuras.beethoven.unit.CompilationUnit;
import java.lang.Thread.State;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;

class ComposerTests {

  @Test
  void properties() throws Exception {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("pool");
    ClassDeclaration car = unit.declareClass("Car");
    car.setModifiers(Modifier.PUBLIC);
    new PropertyComposer()
        .setType(String.class)
        .setName("name")
        .setSetterAvailable(false)
        .setFieldFinal(true)
        .apply(car);
    new PropertyComposer().setType(Number.class).setName("gear").apply(car);
    new PropertyComposer()
        .setType(State.class)
        .setName("state")
        .setSetterRequiresNonNullValue(true)
        .setSetterReturnsThis(true)
        .setFieldInitializer(listing -> listing.add(Name.cast(State.NEW)))
        .apply(car);

    new ConstructorComposer().apply(car);
    new EqualsComposer().apply(car);
    new HashCodeComposer().apply(car);
    new ToStringComposer().apply(car);

    new ImportsComposer().apply(unit);

    Tests.assertEquals(getClass(), "properties", unit);
    Class<?> carClass = unit.compile();
    Object beetle =
        carClass
            .getConstructor(String.class, Number.class, State.class)
            .newInstance("Beetle", 53, State.RUNNABLE);
    assertEquals("Car[name=Beetle, gear=53, state=RUNNABLE]", beetle.toString());
  }
}
