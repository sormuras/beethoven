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

  private final PrintWriter out;
  private Deque<TestIdentifier> testIdentifiers = new ArrayDeque<>();

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
  public void dynamicTestRegistered(TestIdentifier testIdentifier) {
    // ignored in tree printing
  }

  @Override
  public void executionSkipped(TestIdentifier testIdentifier, String reason) {
    testIdentifiers.push(testIdentifier);
    out.printf(
        "%s %s skipped due to: %s %n", indent("-X-"), testIdentifier.getDisplayName(), reason);
    testIdentifiers.pop();
    if (testIdentifiers.size() == 1) {
      out.println(" | ");
    }
  }

  boolean inline = false;

  @Override
  public void executionStarted(TestIdentifier testIdentifier) {
    if (inline) {
      out.printf("%n");
      inline = false;
    }
    testIdentifiers.push(testIdentifier);
    String pointer = testIdentifier.isContainer() ? " ┌─" : " » ";
    out.printf("%s %s", indent(pointer), testIdentifier.getDisplayName());
    inline = true;
  }

  @Override
  public void executionFinished(
      TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
    if (inline) {
      out.printf("%n");
    } else if (testIdentifier.isContainer()) {
      out.printf("%s %s%n", indent(" └─"), testIdentifier.getDisplayName());
    }
    testIdentifiers.pop();
    testExecutionResult.getThrowable().ifPresent(t -> out.printf("Exception: %s%n", t));
    if (testIdentifiers.size() == 1) {
      out.println(" │ ");
    }
    inline = false;
  }

  @Override
  public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry entry) {
    if (inline) {
      inline = false;
      out.printf(" reports%n");
    }
    out.printf("%s %s%n", indent("   "), entry.toString());
  }

  private String indent(String pointer) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < testIdentifiers.size() - 1; i++) {
      builder.append(" │ ");
    }
    builder.append(pointer);
    return builder.toString();
  }
}
