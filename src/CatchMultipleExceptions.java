import java.lang.reflect.Field;

/*******************************************************************************
 * Copyright (c) 2012 Andrey Loskutov. All rights reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *     Andrey Loskutov - initial API and implementation
 *******************************************************************************/
@SuppressWarnings("boxing")
public class CatchMultipleExceptions {
    public static void main(String[] args) {
        oldWayCatch();
        java7Catch();
    }

    static void oldWayCatch() {
        try {
            Field value = Integer.class.getDeclaredField("value");
            value.setAccessible(true);
            Integer obj = new Integer(42);
            System.out.println(obj);
            value.set(obj, -1);
            System.out.println(obj);
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
            Integer obj = new Integer(42);
            System.out.println(obj);
            value.set(obj, -1);
            System.out.println(obj);
        } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
