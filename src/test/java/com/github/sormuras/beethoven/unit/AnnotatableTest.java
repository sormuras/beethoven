package com.github.sormuras.beethoven.unit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sormuras.beethoven.Listable;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

class AnnotatableTest {

  @Test
  void annotatableExtendsListable() {
    assertTrue(Listable.class.isAssignableFrom(Annotatable.class));
  }

  void annotatable(Annotatable annotatable) {
    assertFalse(annotatable.isAnnotated());
    annotatable.addAnnotation(Deprecated.class);
    assertTrue(annotatable.isAnnotated());
  }

  void annotatable(Class<? extends Annotatable> type) {
    try {
      annotatable(type.getConstructor().newInstance());
    } catch (Exception e) {
      throw new AssertionError("Unexpected!", e);
    }
  }

  @TestFactory
  Stream<DynamicTest> annotatables() {
    List<Class<? extends Annotatable>> annotatableClasses = new ArrayList<>();
    new FastClasspathScanner()
        .matchSubclassesOf(
            Annotatable.class,
            type -> {
              if (!Modifier.isAbstract(type.getModifiers())) {
                annotatableClasses.add(type);
              }
            })
        .scan();
    Function<Class<?>, String> displayName = name -> "annotatable(" + name.getSimpleName() + ")";
    return DynamicTest.stream(annotatableClasses.iterator(), displayName, this::annotatable);
  }
}
