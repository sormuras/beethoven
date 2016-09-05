package com.github.sormuras.beethoven;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Local @{@link javax.annotation.Generated} replacement due to JDK9.
 *
 * @see <a href="https://bugs.openjdk.java.net/browse/JDK-8152842">DK-8152842</a>
 */
@Retention(RetentionPolicy.SOURCE)
@Target({PACKAGE, TYPE, ANNOTATION_TYPE, METHOD, CONSTRUCTOR, FIELD, LOCAL_VARIABLE, PARAMETER})
public @interface Generated {
  /**
   * The value element MUST have the name of the code generator. The recommended convention is to
   * use the fully qualified name of the code generator. For example: com.acme.generator.CodeGen.
   */
  String[] value();

  /** Date when the source was generated. */
  String date() default "";

  /**
   * A place holder for any comments that the code generator may want to include in the generated
   * code.
   */
  String comments() default "";
}
