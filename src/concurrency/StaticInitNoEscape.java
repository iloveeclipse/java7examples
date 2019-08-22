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

public class StaticInitNoEscape {

    public final static boolean done;

    static {
        check();
        done = someValue();
    }

    static boolean someValue() {
        return true;
    }

    private static void check() {
        Thread t = new Thread(() -> System.out.println(done));
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(done);
    }

}
