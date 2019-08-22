/*******************************************************************************
 * Copyright (c) 2019 Andrey Loskutov. All rights reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *     Andrey Loskutov - initial API and implementation
 *******************************************************************************/
package concurrency;

import java.lang.reflect.Field;

public class ConstructorEscape {

    public final boolean done;

    public ConstructorEscape() {
        super();
        check(this);
        done = true;
    }

    @Override
    public String toString() {
        return "Final field is set to: " + done;
    }

    private static void check(ConstructorEscape c) {
        Thread t = new Thread(() -> System.out.println(c));
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        ConstructorEscape c = new ConstructorEscape();
        System.out.println(c);
        neverDoThisAtHome(c);
        System.out.println(c);
    }















    private static void neverDoThisAtHome(ConstructorEscape c) throws Exception {
        Field field = c.getClass().getField("done");
        field.setAccessible(true);
        field.set(c, Boolean.FALSE);
    }

}
