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
package gov.nasa.jpf.symbc.bytecode;

import gov.nasa.jpf.symbc.SymbolicInstructionFactory;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

final class CharPredicateHandler {

    private static final String CHARACTER_IS_WHITESPACE = "java.lang.Character.isWhitespace(C)Z";

    /*
     * Constraint collection follows the concrete path. Pinning the contiguous ASCII run that
     * contains the concrete argument is sound because every character in that run gives the same
     * predicate result, so the recorded interval preserves the branch that was actually executed.
     */
    private static final Run[] ASCII_WHITESPACE_RUNS = {
        new Run(0, 8, false),
        new Run(9, 13, true),
        new Run(14, 27, false),
        new Run(28, 32, true),
        new Run(33, 127, false)
    };

    private CharPredicateHandler() {
    }

    static Instruction handleIfApplicable(INVOKESTATIC invInst, ThreadInfo th, MethodInfo callee) {
        if (!CHARACTER_IS_WHITESPACE.equals(callee.getFullName())) {
            return null;
        }
        if (!SymbolicInstructionFactory.collect_constraints) {
            return null;
        }

        StackFrame sf = th.getModifiableTopFrame();
        Object attr = sf.getOperandAttr();
        if (!(attr instanceof IntegerExpression)) {
            return null;
        }

        int concreteChar = sf.peek();
        if (concreteChar > 127) {
            return null;
        }

        if (!th.isFirstStepInsn()) {
            PCChoiceGenerator cg = new PCChoiceGenerator(1);
            cg.setOffset(invInst.getPosition());
            cg.setMethodName(invInst.getMethodInfo().getFullName());
            th.getVM().setNextChoiceGenerator(cg);
            return invInst;
        }

        return handleAsciiWhitespace(invInst, th, (IntegerExpression) attr, concreteChar);
    }

    private static Instruction handleAsciiWhitespace(
        INVOKESTATIC invInst,
        ThreadInfo th,
        IntegerExpression symChar,
        int concreteChar
    ) {
        ChoiceGenerator<?> cg = th.getVM().getChoiceGenerator();
        assert (cg instanceof PCChoiceGenerator) : "expected PCChoiceGenerator, got: " + cg;

        Run run = findRun(concreteChar);
        ((PCChoiceGenerator) cg).select(run.value ? 1 : 0);

        PathCondition pc = previousPathCondition(cg).make_copy();
        pc._addDet(Comparator.GE, symChar, run.lower);
        pc._addDet(Comparator.LE, symChar, run.upper);
        if (!pc.simplify()) {
            th.getVM().getSystemState().setIgnored(true);
        } else {
            ((PCChoiceGenerator) cg).setCurrentPC(pc);
        }

        StackFrame sf = th.getModifiableTopFrame();
        sf.pop();
        sf.push(run.value ? 1 : 0, false);
        return invInst.getNext(th);
    }

    private static PathCondition previousPathCondition(ChoiceGenerator<?> cg) {
        ChoiceGenerator<?> prevCg = cg.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);
        if (prevCg == null) {
            return new PathCondition();
        }
        PathCondition pc = ((PCChoiceGenerator) prevCg).getCurrentPC();
        assert pc != null;
        return pc;
    }

    private static Run findRun(int concreteChar) {
        for (Run run : ASCII_WHITESPACE_RUNS) {
            if (concreteChar >= run.lower && concreteChar <= run.upper) {
                return run;
            }
        }
        throw new IllegalArgumentException("ASCII character outside modeled run table: " + concreteChar);
    }

    private static final class Run {
        private final int lower;
        private final int upper;
        private final boolean value;

        private Run(int lower, int upper, boolean value) {
            this.lower = lower;
            this.upper = upper;
            this.value = value;
        }
    }
}
