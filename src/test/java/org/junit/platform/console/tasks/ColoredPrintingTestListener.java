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

    REPORT("r", " ├─"),

    SKIP("x", " ├─"),

    TEST("o", " ├─"),

    VERTICAL(" | ", " │ ");

    String[] set;

    Tile(String... set) {
      this.set = set;
    }
  }

  private Deque<TestIdentifier> deque = new ArrayDeque<>();
  private int tileSet = 1;
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

  @Override
  public void executionStarted(TestIdentifier id) {
    out.printf("%s %s%n", indent(Tile.TEST), id.getDisplayName());
    if (id.isContainer()) {
      deque.push(id);
    }
  }

  @Override
  public void executionFinished(TestIdentifier id, TestExecutionResult testExecutionResult) {
    if (id.isContainer()) {
      deque.pop();
      out.flush();
    }
    testExecutionResult.getThrowable().ifPresent(t -> out.printf("Exception: %s%n", t));
  }

  @Override
  public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry entry) {
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
