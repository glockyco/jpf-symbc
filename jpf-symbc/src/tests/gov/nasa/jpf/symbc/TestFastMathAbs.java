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

import org.apache.commons.math3.util.FastMath;
import org.junit.Test;

public class TestFastMathAbs extends InvokeTest {
  private static final String SYM_METHOD = "+symbolic.method=gov.nasa.jpf.symbc.TestFastMathAbs.testAbs(sym)";
  private static final String COMPAT_METHOD = "+symbolic.method=gov.nasa.jpf.symbc.TestFastMathAbs.testCompatibleJarvisMethods(sym#sym#sym)";
  private static final String[] JPF_ARGS = {INSN_FACTORY, SYM_METHOD, COMPAT_METHOD};

  public static void main(String[] args) {
    runTestsOfThisClass(args);
  }

  @Test
  public void mainTest() {
    if (verifyNoPropertyViolation(JPF_ARGS)) {
      testAbs(-2.0);
      testCompatibleJarvisMethods(1.0, 2.0, 0L);
    }
  }

  public static void testAbs(double value) {
    if (FastMath.abs(value) > 1.0) {
      System.out.println("outside");
    } else {
      System.out.println("inside");
    }
  }

  public static void testCompatibleJarvisMethods(double left, double right, long value) {
    if (FastMath.min(left, right) == FastMath.max(left, right)) {
      System.out.println("equal");
    }
    try {
      if (FastMath.toIntExact(value) == 0) {
        System.out.println("zero");
      }
    } catch (ArithmeticException e) {
      // value is outside int range; expected on overflow paths
    }
  }
}
