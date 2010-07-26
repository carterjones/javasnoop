/*
 * Copyright, Aspect Security, Inc.
 *
 * This file is part of JavaSnoop.
 *
 * JavaSnoop is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JavaSnoop is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaSnoop.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aspect.snoop;

import java.io.Serializable;

/**
 * This class represents a logical condition that decides whether or not a
 * particular hook should be executed.
 *
 * @author adabirsiaghi
 */
public class Condition implements Cloneable, Serializable {

    public enum Operator {
        Equal,
        Contains,
        LessThan,
        GreaterThan,
        StartsWith,
        EndsWith
    };

    boolean enabled; // whether or not this condition should be checked
    Operator operator; // what kind of test to perform (the 'operator')
    int parameter; // what parameter to test (the 'data')
    String operand; // the value to test against (the 'operand')

    // for instance:
    // you want to test the first parameter of PrintWriter.println(String s);
    // you want to test to see if the word "aspect" is in there. if so, you
    // want the condition to pass. in this case:

    // test = Contains
    // testParameter = $1:String
    // testValue = "aspect"

    // needed for serializaiton
    public Condition() { }

    public Condition(boolean enabled, Operator operator, int parameter, String operand) {
        this.enabled = enabled;
        this.operator = operator;
        this.parameter = parameter;
        this.operand = operand;
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setParameter(int parameter) {
        this.parameter = parameter;
    }

    /**
     * @return the test
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * @param test the test to set
     */
    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    /**
     * @return the testValue
     */
    public String getOperand() {
        return operand;
    }

    /**
     * @param testValue the testValue to set
     */
    public void setOperand(String operand) {
        this.operand = operand;
    }

    public int getParameter() {
        return parameter;
    }
}
