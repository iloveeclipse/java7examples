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


public class FloatingNumbers {

    public static void main(String[] args) {
        System.out.println(0 + 0.1);
        System.out.println(0.1 + 0.1);
        System.out.println(0.1 + 0.1 + 0.1);


        System.out.println("---");

        System.out.println(3 * 0.1);
        System.out.println(3 * (0.3 / 3.0));

        System.out.println("---");

        System.out.println(9 * 0.1);
        System.out.println(9 * (0.3 / 3.0));
    }

}
