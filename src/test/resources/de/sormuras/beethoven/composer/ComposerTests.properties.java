package pool;

public class Car {

  private final String name;

  private Number gear;

  private Thread.State state = Thread.State.NEW;

  public Car(String name, Number gear, Thread.State state) {
    this.name = name;
    this.gear = gear;
    this.state = state;
  }

  public String getName() {
    return name;
  }

  public Number getGear() {
    return gear;
  }

  public void setGear(Number gear) {
    this.gear = gear;
  }

  public Thread.State getState() {
    return state;
  }

  public Car setState(Thread.State state) {
    this.state = java.util.Objects.requireNonNull(state, "Property `state` requires non `null` values!");
    return this;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    return hashCode() == other.hashCode();
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(name, gear, state);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Car");
    builder.append('[');
    builder.append("name").append('=').append(name);
    builder.append(", ").append("gear").append('=').append(gear);
    builder.append(", ").append("state").append('=').append(state);
    builder.append(']');
    return builder.toString();
  }
}
