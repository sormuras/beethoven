package pool;

class Car {

  private final String name;

  private Number gear;

  private Thread.State state = Thread.State.NEW;

  Car(String name, Number gear, Thread.State state) {
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
    this.gear = java.util.Objects.requireNonNull(gear, "Property `gear` requires non `null` values!");
  }

  public Thread.State getState() {
    return state;
  }

  public Car setState(Thread.State state) {
    this.state = java.util.Objects.requireNonNull(state, "Property `state` requires non `null` values!");
    return this;
  }
}
