package com.github.sormuras.beethoven;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
public @interface V {

  String USE = "@" + V.class.getCanonicalName();

  List<Annotation> SINGLETON = List.of(Annotation.annotation(V.class));
}
