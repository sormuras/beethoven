interface I {
}

public class A<U> {

  class B<V, W> {

    class C<X, Y, Z> {
    }
  }

  @test.integration.Counter.Mark
  A.B.C raw;

  @test.integration.Counter.Mark
  A<I>.B<I, I>.C<I, I, I> parametered;
}
