package uss;

public class Enterprise implements java.util.function.Supplier<String> {

  private final String text;

  private final Number number;

  public Enterprise(String text, Number number) {
    this.text = text;
    this.number = number;
  }

  @Override
  public String get() {
    return text + '-' + number;
  }
}
