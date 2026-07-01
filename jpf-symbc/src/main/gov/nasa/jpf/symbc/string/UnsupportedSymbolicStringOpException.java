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

package gov.nasa.jpf.symbc.string;

/**
 * Signals that {@link gov.nasa.jpf.symbc.bytecode.SymbolicStringHandler} was asked to model a String
 * operation it does not implement (for example {@code compareTo} or {@code isEmpty}). A typed
 * exception, rather than a bare {@link RuntimeException}, so a consumer can catch it at its own
 * boundary and record a clean per-assertion exclusion instead of treating an unhandled operation as
 * an internal crash.
 */
public class UnsupportedSymbolicStringOpException extends RuntimeException {

    public UnsupportedSymbolicStringOpException(String operation) {
        super("Unsupported symbolic String operation: " + operation);
    }
}
