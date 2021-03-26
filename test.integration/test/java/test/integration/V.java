package test.integration;

import static java.util.Collections.singletonList;

import com.github.sormuras.beethoven.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
public @interface V {

  String USE = "@" + V.class.getCanonicalName();

  List<Annotation> SINGLETON = singletonList(Annotation.annotation(V.class));
}
