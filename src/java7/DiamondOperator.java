/*******************************************************************************
 * Copyright (c) 2012 Andrey Loskutov. All rights reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *     Andrey Loskutov - initial API and implementation
 *******************************************************************************/
package java7;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class DiamondOperator {
    Map<Class<Number>, Map<String, List<Number>>> map;
    {
        // old way
        map = new HashMap<Class<Number>, Map<String,List<Number>>>();

        // "diamond" operator
        map = new HashMap<>();

        // 2 warnings:
        // "References to generic type should be parameterized",
        // "Expression needs unchecked conversion"
        map = new HashMap();
    }

}
