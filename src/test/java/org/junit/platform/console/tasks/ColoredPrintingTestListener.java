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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

/** @since 1.0 */
class ColoredPrintingTestListener implements TestExecutionListener {

  private final PrintWriter out;
  private final boolean disableAnsiColors;
  private final Theme theme;
  private final boolean verbose;
  private final Deque<Frame> frames;
  private final String[] verticals;
  private long executionStartedNanos;

  ColoredPrintingTestListener(PrintWriter out, boolean disableAnsiColors) {
    this(out, disableAnsiColors, Theme.valueOf(Charset.defaultCharset()), false);
  }

  ColoredPrintingTestListener(
      PrintWriter out, boolean disableAnsiColors, Theme theme, boolean verbose) {
    this.out = out;
    this.disableAnsiColors = disableAnsiColors;
    this.theme = theme;
    this.verbose = verbose;
    this.frames = new ArrayDeque<>();
    // create and populate vertical indentation lookup table
    this.verticals = new String[50];
    this.verticals[0] = "";
    this.verticals[1] = "";
    for (int i = 2; i < verticals.length; i++) {
      verticals[i] = verticals[i - 1] + theme.vertical();
    }
  }

  @Override
  public void testPlanExecutionStarted(TestPlan testPlan) {
    frames.push(new Frame(testPlan.toString()));
    if (verbose) {
      long tests = testPlan.countTestIdentifiers(TestIdentifier::isTest);
      printf(NONE, "Test plan execution started. Number of static tests: ");
      printf(BLUE, "%d%n", tests);
      printf(BLUE, "%s%n", theme.root());
    }
  }

  @Override
  public void testPlanExecutionFinished(TestPlan testPlan) {
    Preconditions.condition(
        frames.size() == 1, () -> "Stack should contain single item, but it has: " + frames);
    Frame frame = frames.pop();
    if (verbose) {
      long tests = testPlan.countTestIdentifiers(TestIdentifier::isTest);
      printf(NONE, "Test plan execution finished. Number of all tests: ");
      printf(BLUE, "%d%n", tests);
    }
  }

  @Override
  public void executionStarted(TestIdentifier testIdentifier) {
    if (testIdentifier.isContainer()) {
      printVerticals();
      printf(NONE, theme.entry());
      printf(BLUE, " %s", testIdentifier.getDisplayName());
      printf(NONE, "%n"); // "started%n"
      frames.push(new Frame(testIdentifier.getUniqueId()));
      return;
    }
    if (verbose) {
      printVerticals();
      printf(NONE, theme.entry());
      printf(NONE, " %s", testIdentifier.getDisplayName());
      printf(NONE, "%n"); // "started%n"
    }
  }

  @Override
  public void executionFinished(
      TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
    if (testIdentifier.isContainer()) {
      frames.pop();
      return;
    }
    Color color = determineColor(testExecutionResult.getStatus());
    if (verbose) {
      printVerticals();
      printf(NONE, theme.spacing());
      printf(color, "%s%n", testExecutionResult.getStatus());
      printVerticals();
      printf(NONE, "%n");
    } else {
      printVerticals();
      printf(NONE, theme.entry());
      printf(color, " %s", testIdentifier.getDisplayName());
      printf(NONE, "%n");
    }
  }

  @Override
  public void executionSkipped(TestIdentifier testIdentifier, String reason) {
    printVerticals();
    printf(NONE, theme.entry());
    if (verbose) {
      printf(NONE, " %s not executed%n", testIdentifier.getDisplayName());
      printVerticals();
      printMessage(YELLOW, theme.spacing() + "reason: " + reason);
      printVerticals();
      printf(YELLOW, "  SKIPPED%n");
    } else {
      printf(YELLOW, " %s ", testIdentifier.getDisplayName());
      printMessage(NONE, reason);
    }
  }

  @Override
  public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry entry) {
    if (verbose) {
      printVerticals();
      printMessage(PURPLE, theme.spacing() + "reports: " + entry.toString());
    }
  }

  private Color determineColor(TestExecutionResult.Status status) {
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

  private void printf(Color color, String message, Object... args) {
    if (disableAnsiColors || color == NONE) {
      out.printf(message, args);
    } else {
      // Use string concatenation to avoid ANSI disruption on console
      out.printf(color + message + NONE, args);
    }
    out.flush();
  }

  private void printVerticals() {
    printf(NONE, verticals());
  }

  private String verticals() {
    return verticals[frames.size()];
  }

  private void printMessage(Color color, String message) {
    String[] lines = message.split("\r\n|\n|\r");
    printf(color, lines[0]);
    if (lines.length > 1) {
      String delimiter = System.lineSeparator() + verticals() + theme.spacing();
      for (int i = 1; i < lines.length; i++) {
        printf(NONE, delimiter);
        printf(color, lines[i]);
      }
    }
    out.println();
    out.flush();
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

  enum Theme {
    ASCII(".", "| ", "+", "    "),
    UTF_8(".", "│  ", "├─", "    ");

    private final String[] tiles;

    Theme(String... tiles) {
      this.tiles = tiles;
    }

    String root() {
      return tiles[0];
    }

    String vertical() {
      return tiles[1];
    }

    String entry() {
      return tiles[2];
    }

    String spacing() {
      return tiles[3];
    }

    static Theme valueOf(Charset charset) {
      if (StandardCharsets.UTF_8.equals(charset)) {
        return UTF_8;
      }
      return ASCII;
    }
  }

  class Frame {
    private final String uniqueId;
    private final long creationNanos;
    private int numberOfAborted;
    private int numberOfSkipped;
    private int numberOfFailed;
    private int numberOfSuccessful;
    private int numberOfStarted;

    private Frame(String uniqueId) {
      this.uniqueId = uniqueId;
      this.creationNanos = System.nanoTime();
    }
  }
}
