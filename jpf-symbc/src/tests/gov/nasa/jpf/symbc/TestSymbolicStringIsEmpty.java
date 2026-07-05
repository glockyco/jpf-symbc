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
 * Regression test for the {@code isEmpty} handler in {@link
 * gov.nasa.jpf.symbc.bytecode.SymbolicStringHandler}. {@code String.isEmpty} was previously
 * unhandled and aborted the search; it is now modeled as the sound, fork-free equality
 * {@code receiver.equals("")}. Under {@code symbolic.collect_constraints} the branch on
 * {@code s.isEmpty()} must follow the concrete seed's branch (symcrete choice selection), exactly
 * like the other boolean String ops -- it must not explore choice 0 (false) first.
 *
 * <p>The empty seed {@code ""} takes the {@code isEmpty}-true branch; a non-empty seed takes the
 * false branch. Only the branch the seed actually takes may be explored.
 */
public class TestSymbolicStringIsEmpty extends InvokeTest {

  private static final String SYM_METHOD =
      "+symbolic.method=gov.nasa.jpf.symbc.TestSymbolicStringIsEmpty.isEmptyBranch(sym)";

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
  public void followsEmptyBranchWhenSeedIsEmpty() {
    if (!isJPFRun()) {
      Verify.resetCounter(0);
      Verify.resetCounter(1);
    }

    if (verifyNoPropertyViolation(JPF_ARGS)) {
      isEmptyBranch("");
    } else {
      assertEquals("empty seed must take the isEmpty-true branch under collect_constraints",
          1, Verify.getCounter(0));
      assertEquals("the isEmpty-false branch must not be explored on the concrete path",
          0, Verify.getCounter(1));
    }
  }

  @Test
  public void followsNonEmptyBranchWhenSeedIsNonEmpty() {
    if (!isJPFRun()) {
      Verify.resetCounter(0);
      Verify.resetCounter(1);
    }

    if (verifyNoPropertyViolation(JPF_ARGS)) {
      isEmptyBranch("x");
    } else {
      assertEquals("non-empty seed must take the isEmpty-false branch under collect_constraints",
          1, Verify.getCounter(1));
      assertEquals("the isEmpty-true branch must not be explored on the concrete path",
          0, Verify.getCounter(0));
    }
  }

  public static void isEmptyBranch(String s) {
    if (s.isEmpty()) {
      Verify.incrementCounter(0);
    } else {
      Verify.incrementCounter(1);
    }
  }
}
