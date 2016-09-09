@interface Everything {

  String EMPTY_TEXT = "";

  @Deprecated
  float PI = 3.141F;

  double E = Math.E;

  int id();

  @Deprecated
  String date() default "201608032129";

  Class<? extends java.util.Formatter> formatterClass();
}
