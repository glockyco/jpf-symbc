/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * The Java Pathfinder core (jpf-core) platform is licensed under the
 * Apache License, Version 2.0 (the "License"); you may not use this file except
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
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;

/**
 * MJI NativePeer class for java.lang.Boolean library abstraction
 */
public class JPF_java_lang_Boolean extends NativePeer {

  @MJI
  public int valueOf__Z__Ljava_lang_Boolean_2 (MJIEnv env, int clsRef, boolean val) {
    Object[] attrs = env.getArgAttributes();
    if (attrs == null || attrs[0] == null) {
      return env.valueOfBoolean(val);
    }

    int result = env.newObject("java.lang.Boolean");
    env.setBooleanField(result, "value", val);
    env.addFieldAttr(result, "value", attrs[0]);
    return result;
  }
}
