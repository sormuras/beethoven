enum Everything {

  A,

  @Deprecated
  B(),

  C(123) {

    public String toString() {
      return "c" + i;
    }
  }
  ;

  int i;

  Everything() {
    this(0);
  }

  Everything(int i) {
    this.i = i;
  }
}
