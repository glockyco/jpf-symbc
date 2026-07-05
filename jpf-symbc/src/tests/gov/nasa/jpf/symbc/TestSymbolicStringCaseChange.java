/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * Symbolic Pathfinder (jpf-symbc) is licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0.
 */

package gov.nasa.jpf.symbc;

import gov.nasa.jpf.vm.Verify;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** Regression test for symbolic {@code String.toLowerCase()} and {@code toUpperCase()}. */
public class TestSymbolicStringCaseChange extends InvokeTest {

  private static final String LOWER_METHOD =
      "+symbolic.method=gov.nasa.jpf.symbc.TestSymbolicStringCaseChange.lowerBranch(sym)";
  private static final String UPPER_METHOD =
      "+symbolic.method=gov.nasa.jpf.symbc.TestSymbolicStringCaseChange.upperBranch(sym)";

  private static String[] jpfArgs(String method) {
    return new String[] {
        INSN_FACTORY,
        method,
        "+classpath=" + System.getProperty("java.class.path"),
        "+symbolic.collect_constraints=true",
        "+symbolic.optimizechoices=false",
        "+symbolic.strings=true",
        "+symbolic.dp=z3",
        "+symbolic.string_dp_timeout_ms=3000"
    };
  }

  public static void main(String[] args) {
    runTestsOfThisClass(args);
  }

  @Test
  public void lowerCaseResultRemainsSymbolic() {
    if (!isJPFRun()) {
      Verify.resetCounter(0);
      Verify.resetCounter(1);
    }

    if (verifyNoPropertyViolation(jpfArgs(LOWER_METHOD))) {
      lowerBranch("FOO");
    } else {
      assertEquals("seed FOO lowercases to foo", 1, Verify.getCounter(0));
      assertEquals("false branch must not be explored on the concrete path", 0, Verify.getCounter(1));
    }
  }

  @Test
  public void upperCaseResultRemainsSymbolic() {
    if (!isJPFRun()) {
      Verify.resetCounter(0);
      Verify.resetCounter(1);
    }

    if (verifyNoPropertyViolation(jpfArgs(UPPER_METHOD))) {
      upperBranch("foo");
    } else {
      assertEquals("seed foo uppercases to FOO", 1, Verify.getCounter(0));
      assertEquals("false branch must not be explored on the concrete path", 0, Verify.getCounter(1));
    }
  }

  public static void lowerBranch(String s) {
    if (s.toLowerCase().equals("foo")) {
      Verify.incrementCounter(0);
    } else {
      Verify.incrementCounter(1);
    }
  }

  public static void upperBranch(String s) {
    if (s.toUpperCase().equals("FOO")) {
      Verify.incrementCounter(0);
    } else {
      Verify.incrementCounter(1);
    }
  }
}
