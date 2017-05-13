@de.sormuras.beethoven.Generated({"https://", "github.com/sormuras/listing"})
package abc.xyz;

import static java.util.Objects.*;
import static java.util.Collections.shuffle;

import abc.*;
import org.junit.jupiter.api.Assertions;

@interface TestAnno {
}

@de.sormuras.beethoven.Generated("An enum for testing")
protected enum TestEnum implements java.io.Serializable {

  A,

  B,

  C
}

interface TestIntf {
}

public final class SimpleClass<S extends @Tag Runnable, T extends S> extends @Tag Thread implements Cloneable, Runnable {

  private volatile int i = 4711;

  @Tag String s = "The Story about \"Ping\"";

  java.util.List<String> l = java.util.Collections.emptyList();

  @Override
  public final void run() {
    System.out.println("Hallo Welt!");
  }

  static <N extends Number> N calc(int i) throws Exception {
    return null;
  }
}
