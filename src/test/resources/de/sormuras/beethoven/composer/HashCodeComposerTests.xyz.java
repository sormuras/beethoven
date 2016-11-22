package test;

import java.util.Objects;

public class Xyz {

  String x;

  boolean y;

  Thread.State z;

  @Override
  public int hashCode() {
    return Objects.hash(x, y, z);
  }
}
