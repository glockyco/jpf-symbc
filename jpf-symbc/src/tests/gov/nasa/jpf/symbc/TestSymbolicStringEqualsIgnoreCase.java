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
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gov.nasa.jpf.symbc;

import gov.nasa.jpf.vm.Verify;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Regression test for the {@code equalsIgnoreCase} handler in {@link
 * gov.nasa.jpf.symbc.bytecode.SymbolicStringHandler}. It was previously an unimplemented stub that
 * threw {@code RuntimeException} and aborted the search; it now delegates to the shared boolean
 * String path with {@code StringComparator.EQUALSIGNORECASE}. Under
 * {@code symbolic.collect_constraints} the branch on {@code s.equalsIgnoreCase("FOO")} must follow
 * the concrete seed's branch (symcrete choice selection) rather than exploring choice 0 (false)
 * first — the seed {@code "foo"} matches case-insensitively (true branch); {@code "bar"} does not.
 */
public class TestSymbolicStringEqualsIgnoreCase extends InvokeTest {

  private static final String SYM_METHOD =
      "+symbolic.method=gov.nasa.jpf.symbc.TestSymbolicStringEqualsIgnoreCase.equalsIgnoreCaseBranch(sym)";

  private static final String[] JPF_ARGS = {
      INSN_FACTORY,
      SYM_METHOD,
      "+classpath=" + System.getProperty("java.class.path"),
      "+symbolic.collect_constraints=true",
      "+symbolic.optimizechoices=false",
      "+symbolic.strings=true",
      "+symbolic.dp=z3",
      "+symbolic.string_dp_timeout_ms=3000"
  };

  public static void main(String[] args) {
    runTestsOfThisClass(args);
  }

  @Test
  public void followsTrueBranchWhenSeedMatchesIgnoringCase() {
    if (!isJPFRun()) {
      Verify.resetCounter(0);
      Verify.resetCounter(1);
    }

    if (verifyNoPropertyViolation(JPF_ARGS)) {
      equalsIgnoreCaseBranch("foo");
    } else {
      assertEquals("seed \"foo\" matches \"FOO\" ignoring case (true branch)",
          1, Verify.getCounter(0));
      assertEquals("the false branch must not be explored on the concrete path",
          0, Verify.getCounter(1));
    }
  }

  @Test
  public void followsFalseBranchWhenSeedDiffers() {
    if (!isJPFRun()) {
      Verify.resetCounter(0);
      Verify.resetCounter(1);
    }

    if (verifyNoPropertyViolation(JPF_ARGS)) {
      equalsIgnoreCaseBranch("bar");
    } else {
      assertEquals("seed \"bar\" does not match \"FOO\" (false branch)",
          1, Verify.getCounter(1));
      assertEquals("the true branch must not be explored on the concrete path",
          0, Verify.getCounter(0));
    }
  }

  public static void equalsIgnoreCaseBranch(String s) {
    if (s.equalsIgnoreCase("FOO")) {
      Verify.incrementCounter(0);
    } else {
      Verify.incrementCounter(1);
    }
  }
}
