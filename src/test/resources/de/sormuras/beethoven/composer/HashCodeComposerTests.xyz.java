package test;

import static java.util.Objects.hash;

public class Xyz {

  String x;

  boolean y;

  Thread.State z;

  @Override
  public int hashCode() {
    return hash(x, y, z);
  }
}
