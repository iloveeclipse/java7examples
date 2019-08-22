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

public class ClassInitIsSynchronized {

    static void check(String name) {
        Thread t = new Thread(() -> {
            try {
                Class.forName(name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(new B());
    }


}

class A {
    static {
        ClassInitIsSynchronized.check(A.class.getName().replace('A', 'B'));
    }
}

class B {
    static {
        ClassInitIsSynchronized.check(B.class.getName().replace('B', 'A'));
    }
}
