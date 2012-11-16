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
import java.util.Objects;

public class ObjectsAPI {
    public static void main(String[] args) {
        String [] array1 = {"a", "b", null};
        String [] array2 = {"a", "b", null};

        System.out.println(array1.equals(array2));

        System.out.println(Objects.equals(array1, array2));
        System.out.println(Objects.deepEquals(array1, array2));
        System.out.println(Objects.hash(array1, array2));

        array1[2] = Objects.requireNonNull(System.getProperty("undefined"), "No NULL please!");
    }
}
