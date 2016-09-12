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

import static org.junit.platform.console.tasks.ColoredPrintingTestListener.Color.BLUE;
import static org.junit.platform.console.tasks.ColoredPrintingTestListener.Color.GREEN;
import static org.junit.platform.console.tasks.ColoredPrintingTestListener.Color.NONE;
import static org.junit.platform.console.tasks.ColoredPrintingTestListener.Color.PURPLE;
import static org.junit.platform.console.tasks.ColoredPrintingTestListener.Color.RED;
import static org.junit.platform.console.tasks.ColoredPrintingTestListener.Color.YELLOW;

import java.io.PrintWriter;
import java.util.regex.Pattern;
import org.junit.platform.commons.util.ExceptionUtils;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestExecutionResult.Status;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

/** @since 1.0 */
class ColoredPrintingTestListener implements TestExecutionListener {

  private static final Pattern LINE_START_PATTERN = Pattern.compile("(?m)^");

  static final String INDENTATION = "             ";

  private final PrintWriter out;
  private final boolean disableAnsiColors;

  private int treeLevel = 1;

  ColoredPrintingTestListener(PrintWriter out, boolean disableAnsiColors) {
    this.out = out;
    this.disableAnsiColors = disableAnsiColors;
  }

  @Override
  public void testPlanExecutionStarted(TestPlan testPlan) {
    out.printf(
        "Test execution started. Number of static tests: %d%n",
        testPlan.countTestIdentifiers(TestIdentifier::isTest));
  }

  @Override
  public void testPlanExecutionFinished(TestPlan testPlan) {
    out.println("Test execution finished.");
  }

  @Override
  public void dynamicTestRegistered(TestIdentifier testIdentifier) {
    printlnTestDescriptor(BLUE, "|", testIdentifier);
  }

  @Override
  public void executionSkipped(TestIdentifier testIdentifier, String reason) {
    printlnTestDescriptor(YELLOW, "Skipped:", testIdentifier);
    printlnMessage(YELLOW, "Reason", reason);
  }

  @Override
  public void executionStarted(TestIdentifier testIdentifier) {
    printlnTestDescriptor(NONE, "|", testIdentifier);
    treeLevel++;
  }

  @Override
  public void executionFinished(
      TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
    treeLevel--;
    Color color = determineColor(testExecutionResult.getStatus());
    printlnTestDescriptor(color, "\\", testIdentifier);
    testExecutionResult.getThrowable().ifPresent(t -> printlnException(color, t));
    if (treeLevel == 2) {
      out.println("|");
    }
  }

  @Override
  public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry entry) {
    printlnTestDescriptor(PURPLE, "Reported:", testIdentifier);
    printlnMessage(PURPLE, "Reported values", entry.toString());
  }

  private Color determineColor(Status status) {
    switch (status) {
      case SUCCESSFUL:
        return GREEN;
      case ABORTED:
        return YELLOW;
      case FAILED:
        return RED;
      default:
        return NONE;
    }
  }

  private void printlnTestDescriptor(Color color, String message, TestIdentifier testIdentifier) {
    println(
        color,
        "%s %s (%s)",
        indent(treeLevel, message),
        testIdentifier.getDisplayName(),
        testIdentifier.getUniqueId());
  }

  private void printlnException(Color color, Throwable throwable) {
    printlnMessage(color, "Exception", ExceptionUtils.readStackTrace(throwable));
  }

  private void printlnMessage(Color color, String message, String detail) {
    println(color, INDENTATION + "=> " + message + ": %s", indented(detail));
  }

  private void println(Color color, String format, Object... args) {
    println(color, String.format(format, args));
  }

  private void println(Color color, String message) {
    if (disableAnsiColors) {
      out.println(message);
    } else {
      // Use string concatenation to avoid ANSI disruption on console
      out.println(color + message + NONE);
    }
  }

  private static String indent(int level, String pointer) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < level - 1; i++) {
      builder.append("| ");
    }
    builder.append(pointer);
    return builder.toString();
  }

  /**
   * Indent the given message if it is a multi-line string.
   *
   * <p>{@link #INDENTATION} is used to prefix the start of each new line except the first one.
   *
   * @param message the message to indent
   * @return indented message
   */
  private static String indented(String message) {
    return LINE_START_PATTERN.matcher(message).replaceAll(INDENTATION).trim();
  }

  enum Color {
    NONE(0),

    BLACK(30),

    RED(31),

    GREEN(32),

    YELLOW(33),

    BLUE(34),

    PURPLE(35),

    CYAN(36),

    WHITE(37);

    private final int ansiCode;

    Color(int ansiCode) {
      this.ansiCode = ansiCode;
    }

    @Override
    public String toString() {
      return "\u001B[" + this.ansiCode + "m";
    }
  }
}
