interface I {
}

public class A<U> {

  class B<V, W> {

    class C<X, Y, Z> {
    }
  }

  @com.github.sormuras.beethoven.Counter.Mark
  A.B.C raw;

  @com.github.sormuras.beethoven.Counter.Mark
  A<I>.B<I, I>.C<I, I, I> parametered;
}
