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

import com.aspect.snoop.Condition.Operator;
import java.util.HashMap;

public class ModelUIUtil {

    private static HashMap<String,Operator> operatorMap;
    private static HashMap<Operator,String> descriptionMap;

    static {       

        operatorMap = new HashMap<String,Operator>();
        operatorMap.put("equals", Operator.Equal);
        operatorMap.put("contains", Operator.Contains);
        operatorMap.put("starts with", Operator.StartsWith);
        operatorMap.put("ends with", Operator.EndsWith);
        operatorMap.put("greater than", Operator.GreaterThan);
        operatorMap.put("less than", Operator.LessThan);

        descriptionMap = new HashMap<Operator,String>();
        descriptionMap.put(Operator.Equal, "equal");
        descriptionMap.put(Operator.Contains,"contains");
        descriptionMap.put(Operator.StartsWith,"starts with");
        descriptionMap.put(Operator.EndsWith,"ends with");
        descriptionMap.put(Operator.GreaterThan,"greater than");
        descriptionMap.put(Operator.LessThan,"less than");
    }

    public static Operator getOperatorByDescription(String description) {
        return operatorMap.get(description);
    }

    public static Object getDescriptionByOperator(Operator operator) {
        return descriptionMap.get(operator);
    }

}
