# de.sormuras.beethoven

deleted: ScriptTests.java
deleted: Counter.java

deleted: from AnnotatedTests.java

  void test(Supplier<? extends Annotated> supplier) {
    Annotated annotated = supplier.get();
    assertEquals(annotated, annotated);
    assertEquals(annotated, supplier.get());
    assertFalse(annotated.isAnnotated());
    assertNotNull(annotated.getDescription());
    assertNotNull(annotated.toString());
  }

  @TestFactory
  Stream<DynamicTest> primitives() {
    List<Primitive> primitives = Arrays.asList(Primitive.values());
    return DynamicTest.stream(primitives.iterator(), Primitive::name, p -> test(p::build));
  }

  @TestFactory
  List<DynamicTest> types() {
    return Arrays.asList(
        // type
        dynamicTest("VoidType", () -> test(VoidType::instance)),
        dynamicTest("WildcardType", () -> test(WildcardType::wildcard)),
        dynamicTest("TypeVariable", () -> test(() -> TypeVariable.variable("T"))),
        dynamicTest("ArrayType.Dimension", () -> test(() -> ArrayType.dimensions(1).get(0))),
        dynamicTest("ClassType.Simple", () -> test(() -> ClassType.simple("S"))),
        // unit
        dynamicTest("PackageDeclaration", () -> test(PackageDeclaration::new)),
        dynamicTest("ConstantDeclaration", () -> test(ConstantDeclaration::new)),
        dynamicTest("AnnotationElement", () -> test(AnnotationElement::new)),
        dynamicTest("AnnotationDeclaration", () -> test(AnnotationDeclaration::new)));
  }

# de.sormuras.beethoven.type

deleted: from TypeTests.java

  private <A extends Annotatable> A mark(A annotatable) {
    annotatable.addAnnotation(Counter.Mark.class);
    return annotatable;
  }

  private void primitives(Counter counter) {
    assertEquals(9, counter.types.size());
    assertEquals(Type.type(boolean.class), counter.types.get("field1"));
    assertEquals(Type.type(byte.class), counter.types.get("field2"));
    assertEquals(Type.type(char.class), counter.types.get("field3"));
    assertEquals(Type.type(double.class), counter.types.get("field4"));
    assertEquals(Type.type(float.class), counter.types.get("field5"));
    assertEquals(Type.type(int.class), counter.types.get("field6"));
    assertEquals(Type.type(long.class), counter.types.get("field7"));
    assertEquals(Type.type(short.class), counter.types.get("field8"));
    assertEquals(Type.type(void.class), counter.types.get("noop"));
  }

  @Test
  void primitivesFromCompilationUnit() throws Exception {
    CompilationUnit unit = CompilationUnit.of("test");
    ClassDeclaration type = unit.declareClass("PrimitiveFields");
    mark(type.declareField(boolean.class, "field1"));
    mark(type.declareField(byte.class, "field2"));
    mark(type.declareField(char.class, "field3"));
    mark(type.declareField(double.class, "field4"));
    mark(type.declareField(float.class, "field5"));
    mark(type.declareField(int.class, "field6"));
    mark(type.declareField(long.class, "field7"));
    mark(type.declareField(short.class, "field8"));
    MethodDeclaration noop = type.declareMethod(void.class, "noop");
    noop.setBody(new Block());
    noop.addAnnotation(Counter.Mark.class);

    Counter counter = new Counter();
    Compilation.compile(
        null, emptyList(), singletonList(counter), singletonList(unit.toJavaFileObject()));
    primitives(counter);
  }

  @Test
  void primitivesFromFile() {
    String charContent = Tests.load(TypeTests.class, "primitives");
    JavaFileObject source = Compilation.source(URI.create("test/Primitives.java"), charContent);
    Counter counter = new Counter();
    Compilation.compile(
        getClass().getClassLoader(), emptyList(), singletonList(counter), singletonList(source));
    primitives(counter);
    // Tree tree = counter.trees.get("field1");
    // Type type = tree.accept(new Type.Trees.TypeTreeVisitor(), null);
    // System.out.print(tree + " -> " + type);
  }

  @Test
  void rootAnnotation() {
    CompilationUnit unit = CompilationUnit.of("test");

    Annotation annotation = Annotation.annotation(All.class);
    annotation.addObject("o", Annotation.annotation(Target.class, ElementType.TYPE));
    annotation.addObject("p", 4711);
    annotation.addObject("r", Double.class);
    annotation.addObject("r", Float.class);

    NormalClassDeclaration type = unit.declareClass("Root");
    type.addAnnotation(annotation);
    type.addTypeParameter(TypeParameter.of("X"));
    mark(type.declareField(TypeVariable.variable("X"), "i"));

    Counter counter = new Counter();
    Compilation.compile(
        null, emptyList(), singletonList(counter), singletonList(unit.toJavaFileObject()));
    assertEquals(1, counter.annotations.size());
    assertEquals(annotation.list(), counter.annotations.get(0).list());
  }

# de.sormuras.beethoven.unit

deleted: from CompilationUnitTests.java

  @Test
  void processed() throws Exception {
    CompilationUnit unit = CompilationUnit.of("test");
    ClassDeclaration enterprise = unit.declareClass("Class");
    enterprise.addModifier(Modifier.PUBLIC);
    enterprise.declareField(Object.class, "field1").addAnnotation(Counter.Mark.class);
    enterprise
        .declareField(
            ClassType.type(Comparable.class)
                .parameterized(i -> singletonList(WildcardType.wildcard())),
            "field2")
        .addAnnotation(Counter.Mark.class);
    enterprise
        .declareField(
            ClassType.type(Map.Entry.class)
                .parameterized(
                    i ->
                        i == 0
                            ? emptyList()
                            : asList(
                                WildcardType.supertype(String.class),
                                WildcardType.extend(Runnable.class))),
            "field3")
        .addAnnotation(Counter.Mark.class);
    enterprise.declareField(int[].class, "field4").addAnnotation(Counter.Mark.class);
    enterprise.declareField(int[][][].class, "field5").addAnnotation(Counter.Mark.class);
    enterprise.declareField(String[][].class, "field6").addAnnotation(Counter.Mark.class);
    Helper.assertEquals(getClass(), "processed", unit);
    Counter counter = new Counter();
    Compilation.compile(
        null, emptyList(), singletonList(counter), singletonList(unit.toJavaFileObject()));
    assertEquals(6, counter.marked.size());
    Assertions.assertEquals(
        "java.util.Map.Entry<? super String, ? extends Runnable>",
        counter.types.get("field3").list());
    Assertions.assertEquals("int[]", counter.types.get("field4").list());
    Assertions.assertEquals("int[][][]", counter.types.get("field5").list());
    Assertions.assertEquals("String[][]", counter.types.get("field6").list());
  }

  @Test
  void abc() throws Exception {
    CompilationUnit unit = Units.abc();
    Helper.assertEquals(getClass(), "abc", unit);

    Counter counter = new Counter();
    Compilation.compile(
        null, emptyList(), singletonList(counter), singletonList(unit.toJavaFileObject()));
    assertEquals(2, counter.marked.size());
    Assertions.assertEquals("A.B.C", counter.types.get("raw").list());
    Assertions.assertEquals("A<I>.B<I, I>.C<I, I, I>", counter.types.get("parametered").list());
  }

  @Test
  void launch() throws IOException {
    Name out = Name.name(System.class, "out");

    Path tempFilePath = Files.createTempFile("beethoven-launch-", ".java");

    CompilationUnit unit = CompilationUnit.of("unit");
    unit.getImportDeclarations().addSingleStaticImport(out);

    ClassDeclaration symphony = unit.declareClass("Launch", Modifier.PUBLIC);
    MethodDeclaration main =
        symphony.declareMethod(void.class, "main", Modifier.PUBLIC, Modifier.STATIC);
    main.addThrows(IOException.class);
    MethodParameter args = main.declareParameter(String[].class, "args");
    main.addStatement(
        listing ->
            listing
                .add(Name.name(Files.class))
                .add(".write(")
                .add(Name.name(Paths.class))
                .add(".get(")
                .add(args.getName())
                .add("[0])")
                .add(", ")
                .add(Name.name(String.class))
                .add(".join(")
                .add(Listable.escape("\n"))
                .add(", ")
                .add(args.getName())
                .add(").getBytes()")
                .add(")"));

    new ImportsComposer().apply(unit);
    String listed = unit.list(System.lineSeparator());
    Helper.assertEquals(getClass(), "launch", listed);

    unit.launch(tempFilePath.toString(), "Second,", "Third line.");
    List<String> lines = Files.readAllLines(tempFilePath);
    assertEquals(tempFilePath.toString(), lines.get(0));
    assertEquals("Second,", lines.get(1));
    assertEquals("Third line.", lines.get(2));
  }
