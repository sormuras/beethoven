package de.sormuras.beethoven;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
public @interface U {

  @U int NUMBER = 4711;

  String USE = "@" + U.class.getCanonicalName();

  List<Annotation> SINGLETON = List.of(Annotation.annotation(U.class));
}