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

import java.lang.reflect.Field;

@SuppressWarnings("boxing")
public class CatchMultipleExceptions {
    public static void main(String[] args) {
        oldWayCatch();
        java7Catch();
        System.out.println("2 + 2 = " + sum(2, 2));
    }

    static int sum(Integer in1, Integer in2){
        return in1 + in2;
    }

    static void oldWayCatch() {
        try {
            Field value = Integer.class.getDeclaredField("value");
            value.setAccessible(true);
            Integer obj = Integer.valueOf(2);
            value.set(obj, 21);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    static void java7Catch() {
        try {
            Field value = Integer.class.getDeclaredField("value");
            value.setAccessible(true);
            Integer obj = Integer.valueOf(2);
            value.set(obj, 21);
        } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
