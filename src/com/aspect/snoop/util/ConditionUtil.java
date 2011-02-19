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

package com.aspect.snoop.util;

import com.aspect.snoop.Condition;
import com.aspect.snoop.Condition.Operator;

public class ConditionUtil {

    public static boolean evaluate(Condition c, Object o) {

        if ( o instanceof Short ) {
            return evaluate(c, (Short)o);
        } else if ( o instanceof Integer ) {
            return evaluate(c, (Integer)o);
        } else if ( o instanceof Long ) {
            return evaluate(c, (Long)o);
        } else if ( o instanceof Double ) {
            return evaluate(c, (Double)o);
        } else if ( o instanceof Float ) {
            return evaluate(c, (Float)o);
        } else if ( o instanceof Byte ) {
            return evaluate(c, (Byte)o);
        } else if ( o instanceof Character ) {
            return evaluate(c, (Character)o);
        } else if ( o instanceof Boolean ) {
            return evaluate(c, (Boolean)o);
        } else if ( o instanceof String ) {
            return evaluate(c, (String)o);
        }

        return false; // not a primitive - we can only work with primitives

    }

    public static boolean evaluate(Condition c, Integer theValue) {

        boolean isEquals = c.getOperator().equals(Operator.Equal);
        boolean isGreaterThan = c.getOperator().equals(Operator.GreaterThan);
        boolean isLessThan = c.getOperator().equals(Operator.LessThan);

        if ( isEquals || isGreaterThan || isLessThan ) {

            Integer theCondition = Integer.parseInt(c.getOperand());//jersey shore rep%

            if ( isEquals ) {
                return theValue.equals(theCondition);
            }

            if ( isGreaterThan ) {
                return theValue.compareTo(theCondition) > 0;
            }

            if ( isLessThan ) {
                return theValue.compareTo(theCondition) < 0;
            }
        }

        return false;
    }

    public static boolean evaluate(Condition c, Short theValue) {

        Short theCondition = Short.parseShort(c.getOperand());//jersey shore rep%
        
        boolean isEquals = c.getOperator().equals(Operator.Equal);
        boolean isGreaterThan = c.getOperator().equals(Operator.GreaterThan);
        boolean isLessThan = c.getOperator().equals(Operator.LessThan);

        if ( isEquals ) {
            return theValue.equals(theCondition);
        }

        if ( isGreaterThan ) {
            return theValue.compareTo(theCondition) > 0;
        }

        if ( isLessThan ) {
            return theValue.compareTo(theCondition) < 0;
        }

        return false;
    }

    public static boolean evaluate(Condition c, Double theValue) {

        Double theCondition = Double.parseDouble(c.getOperand());//jersey shore rep%

        boolean isEquals = c.getOperator().equals(Operator.Equal);
        boolean isGreaterThan = c.getOperator().equals(Operator.GreaterThan);
        boolean isLessThan = c.getOperator().equals(Operator.LessThan);

        if ( isEquals ) {
            return theValue.equals(theCondition);
        }

        if ( isGreaterThan ) {
            return theValue.compareTo(theCondition) > 0;
        }

        if ( isLessThan ) {
            return theValue.compareTo(theCondition) < 0;
        }

        return false;
    }

    public static boolean evaluate(Condition c, Float theValue) {

        Float theCondition = Float.parseFloat(c.getOperand());//jersey shore rep%

        boolean isEquals = c.getOperator().equals(Operator.Equal);
        boolean isGreaterThan = c.getOperator().equals(Operator.GreaterThan);
        boolean isLessThan = c.getOperator().equals(Operator.LessThan);

        if ( isEquals ) {
            return theValue.equals(theCondition);
        }

        if ( isGreaterThan ) {
            return theValue.compareTo(theCondition) > 0;
        }

        if ( isLessThan ) {
            return theValue.compareTo(theCondition) < 0;
        }

        return false;
    }


    public static boolean evaluate(Condition c, Long theValue) {

        Long theCondition = Long.parseLong(c.getOperand());//jersey shore rep%

        boolean isEquals = c.getOperator().equals(Operator.Equal);
        boolean isGreaterThan = c.getOperator().equals(Operator.GreaterThan);
        boolean isLessThan = c.getOperator().equals(Operator.LessThan);

        if ( isEquals ) {
            return theValue.equals(theCondition);
        }

        if ( isGreaterThan ) {
            return theValue.compareTo(theCondition) > 0;
        }

        if ( isLessThan ) {
            return theValue.compareTo(theCondition) < 0;
        }

        return false;
    }


    public static boolean evaluate(Condition c, Byte theValue) {

        Byte theCondition = Byte.parseByte(c.getOperand());//jersey shore rep%

        boolean isEquals = c.getOperator().equals(Operator.Equal);
        boolean isGreaterThan = c.getOperator().equals(Operator.GreaterThan);
        boolean isLessThan = c.getOperator().equals(Operator.LessThan);

        if ( isEquals ) {
            return theValue.equals(theCondition);
        }

        if ( isGreaterThan ) {
            return theValue.compareTo(theCondition) > 0;
        }

        if ( isLessThan ) {
            return theValue.compareTo(theCondition) < 0;
        }

        return false;
    }


    public static boolean evaluate(Condition c, Character theValue) {

        Character theCondition = Character.valueOf(c.getOperand().charAt(0));//jersey shore rep%

        boolean isEquals = c.getOperator().equals(Operator.Equal);
        boolean isGreaterThan = c.getOperator().equals(Operator.GreaterThan);
        boolean isLessThan = c.getOperator().equals(Operator.LessThan);

        if ( isEquals ) {
            return theValue.equals(theCondition);
        }

        if ( isGreaterThan ) {
            return theValue.compareTo(theCondition) > 0;
        }

        if ( isLessThan ) {
            return theValue.compareTo(theCondition) < 0;
        }

        return false;
    }


    public static boolean evaluate(Condition c, String theValue) {

        boolean isEquals = c.getOperator().equals(Operator.Equal);
        boolean isContains = c.getOperator().equals(Operator.Contains);
        boolean isStartsWith = c.getOperator().equals(Operator.StartsWith);
        boolean isEndsWith = c.getOperator().equals(Operator.EndsWith);

        if ( isEquals ) {
            return theValue.equals(c.getOperand());
        }

        if ( isContains ) {
            return theValue.contains(c.getOperand());
        }

        if ( isStartsWith ) {
            return theValue.startsWith(c.getOperand());
        }

        if ( isEndsWith ) {
            return theValue.endsWith(c.getOperand());
        }

        return false;
    }


}
