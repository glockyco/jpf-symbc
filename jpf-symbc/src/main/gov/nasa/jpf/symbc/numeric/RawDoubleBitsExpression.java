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
package gov.nasa.jpf.symbc.numeric;

import java.util.Map;

/** Integer expression for Double.doubleToRawLongBits(symDouble). */
public class RawDoubleBitsExpression extends IntegerExpression {
  private final RealExpression argument;

  public RawDoubleBitsExpression(RealExpression argument) {
    this.argument = argument;
  }

  public RealExpression getArgument() {
    return argument;
  }

  @Override
  public long solution() {
    return Double.doubleToRawLongBits(argument.solution());
  }

  @Override
  public String stringPC() {
    return "doubleToRawLongBits(" + argument.stringPC() + ")";
  }

  @Override
  public String prefix_notation() {
    return stringPC();
  }

  @Override
  public void getVarsVals(Map<String, Object> varsVals) {
    argument.getVarsVals(varsVals);
  }

  @Override
  public void accept(ConstraintExpressionVisitor visitor) {
    argument.accept(visitor);
  }

  @Override
  public int compareTo(Expression expr) {
    if (expr instanceof RawDoubleBitsExpression) {
      return argument.compareTo(((RawDoubleBitsExpression) expr).argument);
    }
    return getClass().getCanonicalName().compareTo(expr.getClass().getCanonicalName());
  }
}
