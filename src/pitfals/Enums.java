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

public class Enums {
    public static void main(String[] args) {
        printEnum(Numbers.ONE);
        System.out.println(Arrays.toString(Numbers.values()));
        System.out.println(Numbers.valueOf("MANY"));
        System.out.println("---");

        printEnum(CrazyNumbers.ONE);
        System.out.println(Arrays.toString(CrazyNumbers.values()));
        System.out.println(CrazyNumbers.valueOf("MANY"));

    }


    enum Numbers {
        ONE,
        TWO,
        MANY,
    }

    enum CrazyNumbers {
        ONE{
            @Override
            public String toString() {
                return "TWO";
            }
        },

        TWO {
            @Override
            public String toString() {
                return "ONE";
            }
        },

        MANY,
    }

    private static void printEnum(Enum<?> n) {
        System.out.println(n.name());
        System.out.println(n.toString());
        System.out.println(n.ordinal());
        System.out.println(n.getClass());
        System.out.println(n.getDeclaringClass());
        System.out.println(n.valueOf(n.getDeclaringClass(), n.name()));
    }

}
