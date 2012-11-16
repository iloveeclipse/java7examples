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

@SuppressWarnings(value = {"unused", "boxing"})
public class BoxingUnboxing {

    public static void main(String[] args) {
        int i = 0;
        try {
            i += calculate1(42);            // <- NPE here!!!
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        try {
            i += calculate2(42);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            i += calculate1(null);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            i += calculate2(null);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            i += calculate3(null);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    static Integer calculate1(Integer i){
        if(i % 2 != 0) {                    // <- NPE here!
            return i;
        }
        return null;
    }

    static int calculate2(Integer i){
        return i % 2 != 0? i : null;       // <- NPE here!
    }

    static int calculate3(Integer i){
        return i;                          // <- NPE here!
    }

}
