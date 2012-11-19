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

public class Immutable {

    public static void main(String[] args) {
        String s = "    Hallo  ";
        Object other = s;
        s.trim();
        System.out.println(">" + s + "<");
        System.out.println("same? " + (other == s));

        s = s + 1;
        System.out.println(">" + s + "<");
        System.out.println("same? " + (other == s));

        Integer in = Integer.valueOf(0);
        other = in;
        in = in + 1;
        System.out.println(in);
        System.out.println(other);
        System.out.println("same? " + (other == in));
    }

}
