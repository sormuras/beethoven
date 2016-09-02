package com.github.sormuras.beethoven;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
public @interface U {

  @U int NUMBER = 4711;

  String USE = "@" + U.class.getCanonicalName();

  List<Annotation> SINGLETON = Collections.singletonList(Annotation.annotation(U.class));
}
