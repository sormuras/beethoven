/*
 * Copyright 2015-2016 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.junit.platform.console.tasks;

import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

/** @since 1.0 */
class ColoredPrintingTestListener implements TestExecutionListener {

  enum Tile {
    CONTAINER_BEGIN(" +--", "┌─"),

    CONTAINER_END(" +--", "└─"),

    REPORT("r"),

    SKIP("x"),

    TEST("o", "»"),

    VERTICAL(" | ", "│");

    String[] set;

    Tile(String... set) {
      this.set = set;
    }
  }

  private Deque<TestIdentifier> deque = new ArrayDeque<>();
  private Optional<TestIdentifier> justFinished = Optional.empty();
  private int tileSet = 0;
  private final PrintWriter out;

  ColoredPrintingTestListener(PrintWriter out, boolean disableAnsiColors) {
    this.out = out;
  }

  @Override
  public void testPlanExecutionStarted(TestPlan testPlan) {
    out.printf(
        "Test plan execution started. Number of static tests: %d%n",
        testPlan.countTestIdentifiers(TestIdentifier::isTest));
  }

  @Override
  public void testPlanExecutionFinished(TestPlan testPlan) {
    out.println("Test plan execution finished.");
  }

  @Override
  public void dynamicTestRegistered(TestIdentifier id) {
    // ignored in tree printing
  }

  @Override
  public void executionSkipped(TestIdentifier id, String reason) {
    deque.push(id);
    out.printf("%s %s skipped due: %s%n", indent(Tile.SKIP), id.getDisplayName(), reason);
    deque.pop();
  }

  boolean inline = false;

  @Override
  public void executionStarted(TestIdentifier id) {
    if (inline) {
      out.printf("%n");
      inline = false;
    }
    if (deque.size() == 1 && justFinished.isPresent()) {
      out.printf("%s%n", indent(Tile.VERTICAL));
    }
    deque.push(id);
    Tile tile = id.isContainer() ? Tile.CONTAINER_BEGIN : Tile.TEST;
    out.printf("%s %s", indent(tile), id.getDisplayName());
    inline = true;
  }

  @Override
  public void executionFinished(TestIdentifier id, TestExecutionResult testExecutionResult) {
    if (inline) {
      out.printf("%n");
    } else if (id.isContainer()) {
      out.printf("%s %s%n", indent(Tile.CONTAINER_END), id.getDisplayName());
      out.flush();
    }
    testExecutionResult.getThrowable().ifPresent(t -> out.printf("Exception: %s%n", t));
    justFinished = Optional.of(deque.pop());
    inline = false;
  }

  @Override
  public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry entry) {
    if (inline) {
      inline = false;
      out.printf(" reports%n");
    }
    out.printf("%s %s%n", indent(Tile.REPORT), entry.toString());
  }

  private String indent(Tile tile) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < deque.size() - 1; i++) {
      builder.append(Tile.VERTICAL.set[tileSet]);
    }
    builder.append(tile.set[tileSet]);
    return builder.toString();
  }
}
