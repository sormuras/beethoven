package de.sormuras.beethoven.unit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.sormuras.beethoven.Listable;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.SubclassMatchProcessor;
import java.lang.*;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

class AnnotatableTests {

  class Annotatables implements SubclassMatchProcessor<Annotatable> {

    List<Annotatable> annotatables = new ArrayList<>();

    @Override
    public void processMatch(java.lang.Class<? extends Annotatable> type) {
      if (Modifier.isAbstract(type.getModifiers())) {
        return;
      }
      try {
        annotatables.add(type.getConstructor().newInstance());
      } catch (Exception e) {
        throw new AssertionError("Unexpected!", e);
      }
    }

    Iterator<Annotatable> iterator() {
      new FastClasspathScanner().matchSubclassesOf(Annotatable.class, this).scan();
      return annotatables.iterator();
    }
  }

  @Test
  void annotatableExtendsListable() {
    assertTrue(Listable.class.isAssignableFrom(Annotatable.class));
  }

  void annotatable(Annotatable annotatable) {
    assertFalse(annotatable.isAnnotated());
    annotatable.addAnnotation(Deprecated.class);
    assertTrue(annotatable.isAnnotated());
  }

  @TestFactory
  Stream<DynamicTest> annotatables() {
    Function<Annotatable, String> name = a -> "annotatable(" + a.getClass().getSimpleName() + ")";
    return DynamicTest.stream(new Annotatables().iterator(), name, this::annotatable);
  }
}
