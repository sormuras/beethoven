package test.integration.unit;

import static java.util.Collections.singletonList;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sormuras.beethoven.unit.Modifiable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

class ModifiableTests implements Modifiable {

  private final Set<Modifier> modifiers = new TreeSet<>();

  @Test
  void addModifiers() {
    addModifier(ABSTRACT);
    assertTrue(getModifiers().equals(EnumSet.of(ABSTRACT)));
    assertFalse(isPublic());
    assertFalse(isStatic());
    addModifiers(PUBLIC, STATIC);
    assertTrue(getModifiers().equals(EnumSet.of(ABSTRACT, PUBLIC, STATIC)));
    assertTrue(isPublic());
    assertTrue(isStatic());
    addModifiers(singletonList(FINAL));
    assertTrue(getModifiers().equals(EnumSet.of(ABSTRACT, FINAL, PUBLIC, STATIC)));
    assertTrue(isPublic());
    assertTrue(isStatic());
    setModifiers(Collections.emptySet());
    assertFalse(isPublic());
    assertFalse(isStatic());
    setModifiers(java.lang.reflect.Modifier.STATIC);
    assertTrue(isStatic());
    setModifiers(0);
    assertFalse(isModified());
  }

  @Test
  void addModifiersFails() {
    Modifier invalid = Modifier.VOLATILE;
    Exception e = assertThrows(IllegalArgumentException.class, () -> addModifier(invalid));
    assertTrue(e.getMessage().contains(invalid.toString()));
  }

  @Test
  void emptyOnCreation() {
    assertFalse(isStatic());
    assertTrue(getModifiers().isEmpty());
  }

  @Override
  public Set<Modifier> getModifiers() {
    return modifiers;
  }

  @Override
  public Set<Modifier> getModifierValidationSet() {
    return EnumSet.of(ABSTRACT, FINAL, PUBLIC, STATIC);
  }

  @Override
  public boolean isModified() {
    return !modifiers.isEmpty();
  }

  @Test
  void validationSetDefaultsToAllEnumConstants() {
    Set<Modifier> set = Modifiable.super.getModifierValidationSet();
    assertTrue(set.containsAll(EnumSet.allOf(Modifier.class)));
    assertTrue(set.equals(EnumSet.allOf(Modifier.class)));
  }

  @Test
  void validationThrowsNullPointerException() {
    assertThrows(NullPointerException.class, () -> validateModifiers(null, null));
  }

  @TestFactory
  List<DynamicTest> modifiers() {
    assertEquals("[strictfp]", Modifiable.modifiers(java.lang.reflect.Modifier.STRICT).toString());
    List<DynamicTest> tests = new ArrayList<>();
    Arrays.stream(java.lang.reflect.Modifier.class.getFields())
        .filter(f -> !f.getName().equals("INTERFACE"))
        .filter(f -> !f.getName().equals("STRICT"))
        .forEach(
            f ->
                tests.add(
                    DynamicTest.dynamicTest(
                        f.getName(),
                        () ->
                            assertEquals(
                                "[" + f.getName().toLowerCase() + "]",
                                Modifiable.modifiers(f.getInt(null)).toString()))));

    return tests;
  }
}
