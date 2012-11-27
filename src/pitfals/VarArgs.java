/*******************************************************************************
 * Copyright (c) 2012 Andrey Loskutov. All rights reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *     Andrey Loskutov - initial API and implementation
 *******************************************************************************/
package pitfals;

import java.util.Arrays;

public class VarArgs {

    public static void main(String[] args) {

        Object[] result = varargs();
        printArray(result);

        result = varargs(null);
        printArray(result);

        result = varargs((String[])null);
        printArray(result);

        result = varargs((String)null);
        printArray(result);

        result = varargs();
        printArray(result);

        result = varargs(null, null);
        printArray(result);


        result = varargs2(null);
        printArray(result);

        result = varargs2((String)null);
        printArray(result);

        result = varargs2(null, null);
        printArray(result);
    }

    static Object[] varargs(String ... strings) {
        return strings;
    }

    static Object[] varargs2(String s, String ... strings) {
        return strings;
    }

    private static void printArray(Object[] result) {
        if(result != null){
            System.out.print(result.getClass().getName() + " :\t");
        } else {
            System.out.print("Null object :\t\t");
        }
        System.out.println(Arrays.toString(result));
    }

}
