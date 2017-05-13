package test;

public class Single {

  String text;

  @Override
  public int hashCode() {
    return java.util.Objects.hashCode(text);
  }
}
