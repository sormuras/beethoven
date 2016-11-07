module M @ 1.0 {
  requires A @ >= 2.0;
  requires B for compilation, reflection;

  requires service S1;
  requires optional service S2;

  provides MI @ 4.0;
  provides service MS with C;
  exports ME;
  permits MF;
  class MMain;

  view N {
    provides NI @ 1.0;
    provides service NS with D;
    exports NE;
    permits MF;
    class NMain;
  }
}
