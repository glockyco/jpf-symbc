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

package org.apache.commons.math3.util;

public class FastMath {
  public static double abs(double value) {
    return (value < 0.0) ? -value : value;
  }

  public static float abs(float value) {
    return (value < 0.0f) ? -value : value;
  }

  public static int abs(int value) {
    return (value < 0) ? -value : value;
  }

  public static long abs(long value) {
    return (value < 0L) ? -value : value;
  }

  public static double min(double left, double right) {
    return (left <= right) ? left : right;
  }

  public static double max(double left, double right) {
    return (left >= right) ? left : right;
  }

  public static int toIntExact(long value) {
    if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
      throw new ArithmeticException("integer overflow");
    }
    return (int) value;
  }
}
