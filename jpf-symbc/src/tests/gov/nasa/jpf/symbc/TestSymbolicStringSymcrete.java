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
 * Regression test for symcrete (concolic) choice selection in {@link
 * gov.nasa.jpf.symbc.bytecode.SymbolicStringHandler}. Under {@code symbolic.collect_constraints}, a
 * branch on a symbolic String comparison must follow the concrete seed's branch, exactly as the
 * numeric {@code IF_ICMP*} instructions do -- not explore choice 0 (false) first.
 *
 * <p>The seed {@code "foo"} takes the {@code equals}-true branch, so only the true-branch counter
 * may be incremented; the false branch must never be explored on the concrete path. Before the fix
 * the handler always created a two-choice generator and took choice 0 first, so the false branch was
 * captured regardless of the seed.
 */
public class TestSymbolicStringSymcrete extends InvokeTest {

  private static final String SYM_METHOD =
      "+symbolic.method=gov.nasa.jpf.symbc.TestSymbolicStringSymcrete.equalsBranch(sym)";

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
  public void followsConcreteBranch() {
    if (!isJPFRun()) {
      Verify.resetCounter(0);
      Verify.resetCounter(1);
    }

    if (verifyNoPropertyViolation(JPF_ARGS)) {
      equalsBranch("foo");
    } else {
      assertEquals("seed \"foo\" must take the equals-true branch under collect_constraints",
          1, Verify.getCounter(0));
      assertEquals("the equals-false branch must not be explored on the concrete path",
          0, Verify.getCounter(1));
    }
  }

  public static void equalsBranch(String s) {
    if (s.equals("foo")) {
      Verify.incrementCounter(0);
    } else {
      Verify.incrementCounter(1);
    }
  }
}
