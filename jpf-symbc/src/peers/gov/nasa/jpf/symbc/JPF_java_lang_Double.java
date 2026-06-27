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

import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.symbc.numeric.RawDoubleBitsExpression;
import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;

/**
 * MJI NativePeer class for java.lang.Double with symbolic raw-bit preservation.
 */
public class JPF_java_lang_Double extends NativePeer {
  @MJI
  public long doubleToLongBits__D__J(MJIEnv env, int rcls, double v0) {
    return Double.doubleToLongBits(v0);
  }

  @MJI
  public long doubleToRawLongBits__D__J(MJIEnv env, int rcls, double v0) {
    Object[] attrs = env.getArgAttributes();
    if (SymbolicInstructionFactory.fp && attrs != null && attrs.length > 0 && attrs[0] instanceof RealExpression) {
      env.setReturnAttribute(new RawDoubleBitsExpression((RealExpression) attrs[0]));
    }
    return Double.doubleToRawLongBits(v0);
  }

  @MJI
  public double longBitsToDouble__J__D(MJIEnv env, int rcls, long v0) {
    return Double.longBitsToDouble(v0);
  }

  @MJI
  public int toString__D__Ljava_lang_String_2(MJIEnv env, int objref, double d) {
    return env.newString(Double.toString(d));
  }

  @MJI
  public boolean isInfinite__D__Z(MJIEnv env, int rcls, double v) {
    return Double.isInfinite(v);
  }

  @MJI
  public boolean isNaN__D__Z(MJIEnv env, int rcls, double v) {
    return Double.isNaN(v);
  }
}
