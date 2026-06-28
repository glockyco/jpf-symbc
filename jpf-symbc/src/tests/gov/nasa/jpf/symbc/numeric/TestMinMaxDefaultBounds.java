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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests that the default lower/upper bounds for symbolic double variables are
 * correct without requiring a JPF run or a populated Config.
 *
 * The null guards added to getVarMinDouble/getVarMaxDouble allow these methods
 * to be called before collectMinMaxInformation, falling back to the static
 * minDouble/maxDouble defaults.
 */
public class TestMinMaxDefaultBounds {

    private static final String UNCONFIGURED = "no_such_var_xyz";

    @Test
    public void defaultMinDoubleIsNegativeMaxValue() {
        double min = MinMax.getVarMinDouble(UNCONFIGURED);
        assertEquals("default minDouble must be -Double.MAX_VALUE so negatives are in range",
                -Double.MAX_VALUE, min, 0.0);
    }

    @Test
    public void defaultMaxDoubleIsPositiveMaxValue() {
        double max = MinMax.getVarMaxDouble(UNCONFIGURED);
        assertEquals("default maxDouble must be Double.MAX_VALUE",
                Double.MAX_VALUE, max, 0.0);
    }

    @Test
    public void defaultRangeCoversNegatives() {
        double min = MinMax.getVarMinDouble(UNCONFIGURED);
        assertTrue("default lower bound must be negative", min < 0.0);
    }

    @Test
    public void defaultRangeIsOrdered() {
        double min = MinMax.getVarMinDouble(UNCONFIGURED);
        double max = MinMax.getVarMaxDouble(UNCONFIGURED);
        assertTrue("minDouble < maxDouble must hold for any symbolic real", min < max);
    }
}
