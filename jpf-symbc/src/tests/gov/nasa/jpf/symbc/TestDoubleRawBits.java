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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestDoubleRawBits extends InvokeTest {
  private static final String SYM_METHOD = "+symbolic.method=gov.nasa.jpf.symbc.TestDoubleRawBits.rawSignMatchesDoubleSign(sym)";
  private static final String PRECISION_METHOD = "+symbolic.method=gov.nasa.jpf.symbc.TestDoubleRawBits.precisionLikeMaxUlps(sym)";
  private static final String RATIONAL_METHOD = "+symbolic.method=gov.nasa.jpf.symbc.TestDoubleRawBits.rawBitsConcreteFallback(sym)";
  private static final String[] JPF_ARGS = {
      INSN_FACTORY,
      SYM_METHOD,
      "+classpath=" + System.getProperty("java.class.path"),
      "+symbolic.dp=z3bitvector",
      "+symbolic.fp=true",
      "+symbolic.bvlength=64",
      "+symbolic.min_double=-100.0",
      "+symbolic.max_double=100.0"
  };

  private static final String[] RATIONAL_REAL_ARGS = {
      INSN_FACTORY,
      RATIONAL_METHOD,
      "+classpath=" + System.getProperty("java.class.path"),
      "+symbolic.dp=z3",
      "+symbolic.min_double=-100.0",
      "+symbolic.max_double=100.0"
  };

  public static void main(String[] args) {
    runTestsOfThisClass(args);
  }

  @Test
  public void mainTest() {
    if (!isJPFRun()) {
      Verify.resetCounter(0);
    }

    if (verifyNoPropertyViolation(JPF_ARGS)) {
      rawSignMatchesDoubleSign(1.0);
    } else {
      assertEquals("validated sign regions", 2, Verify.getCounter(0));
    }
  }

  @Test
  public void precisionMaxUlpsTest() {
    if (!isJPFRun()) {
      Verify.resetCounter(1);
      Verify.resetCounter(2);
    }

    if (verifyNoPropertyViolation(JPF_ARGS[0], PRECISION_METHOD, JPF_ARGS[2], JPF_ARGS[3],
        JPF_ARGS[4], JPF_ARGS[5], JPF_ARGS[6], JPF_ARGS[7])) {
      precisionLikeMaxUlps(1.0);
    } else {
      assertTrue("ulps in-range path", Verify.getCounter(1) > 0);
      assertTrue("ulps out-of-range path", Verify.getCounter(2) > 0);
    }
  }

  @Test
  public void rationalRealModePreservesSymbolicRawBits() {
    if (isJPFRun()) {
      rawBitsConcreteFallback(1.0);
      return;
    }

    // The native peer records a symbolic double's raw bits (RawDoubleBitsExpression) regardless of
    // the symbolic.fp setting. On the rational-real path there is no bit-precise solver, so the
    // dependent sign-bit branch fails loud -- JPF surfaces the solver's inability to model the bit
    // pattern as an internal exception -- rather than silently concretizing the bits to a single
    // region, which would let an unsound generalization through.
    AssertionError failure = null;
    try {
      verifyNoPropertyViolation(RATIONAL_REAL_ARGS);
    } catch (AssertionError e) {
      failure = e;
    }
    assertTrue("expected the rational-real raw-bits branch to fail loud, but JPF ran clean",
        failure != null);
    assertTrue("expected a JPF internal/solver failure for the unsupported raw-bits term, got: "
            + failure.getMessage(),
        failure.getMessage() != null && failure.getMessage().contains("JPF internal exception"));
  }

  public static void rawBitsConcreteFallback(double value) {
    long rawBits = Double.doubleToRawLongBits(value);
    if ((rawBits & Long.MIN_VALUE) == 0L) {
      Verify.incrementCounter(3);
    }
  }

  public static void rawSignMatchesDoubleSign(double value) {
    long rawBits = Double.doubleToRawLongBits(value);
    boolean negativeRawSign = (rawBits & Long.MIN_VALUE) != 0L;

    if (value > 0.0) {
      if (negativeRawSign) {
        fail("positive double cannot have a negative raw sign bit");
      }
      Verify.incrementCounter(0);
    } else if (value < 0.0) {
      if (!negativeRawSign) {
        fail("negative double cannot have a positive raw sign bit");
      }
      Verify.incrementCounter(0);
    }
  }

  public static boolean precisionLikeMaxUlps(double value) {
    long valueBits = Double.doubleToRawLongBits(value);
    long targetBits = Double.doubleToRawLongBits(1.0);

    boolean isEqual;
    if (((valueBits ^ targetBits) & Long.MIN_VALUE) == 0L) {
      isEqual = Math.abs(valueBits - targetBits) <= 1;
    } else {
      long deltaPlus;
      long deltaMinus;
      if (valueBits < targetBits) {
        deltaPlus = targetBits - 0x0000000000000000L;
        deltaMinus = valueBits - 0x8000000000000000L;
      } else {
        deltaPlus = valueBits - 0x0000000000000000L;
        deltaMinus = targetBits - 0x8000000000000000L;
      }
      isEqual = deltaPlus <= 1 && deltaMinus <= (1 - deltaPlus);
    }

    if (isEqual) {
      Verify.incrementCounter(1);
    } else {
      Verify.incrementCounter(2);
    }
    return isEqual;
  }
}
