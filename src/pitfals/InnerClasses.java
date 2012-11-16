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

import java.lang.reflect.Field;
import java.util.concurrent.Callable;

@SuppressWarnings("rawtypes")
public class InnerClasses {

    public static void main(String[] args) throws Exception {
        final Callable r1 = new Callable() {
            @Override
            public Object call() {
                // no references escaped from static method
                return this;
            }
        };

        Callable r2 = new Callable() {
            @Override
            public Object call() throws Exception {
                r1.hashCode();
                return this;
            }
        };

        Callable r3 = new Callable() {
            @Override
            public Object call() throws Exception {
                return new InnerClasses().createBadTask1().call();
            }
        };

        Callable r4 = new Callable() {
            @Override
            public Object call() throws Exception {
                return new InnerClasses().createBadTask2().call();
            }
        };

        Callable r5 = new Callable() {
            @Override
            public Object call() throws Exception {
                return new InnerClasses().createStaticTask().call();
            }
        };

        // r1 holds no references
        System.out.print("Anonymous class 1:\t");
        doBadThings(r1.call());

        System.out.print("Anonymous class 2:\t");
        // r2 holds reference to r1
        doBadThings(r2.call());

        // r3 returns reference to InnerClasses object
        System.out.print("Anonymous class 3:\t");
        doBadThings(r3.call());

        // r4 returns reference to InnerClasses object
        System.out.print("Inner class:\t\t");
        doBadThings(r4.call());

        // r5 holds no references
        System.out.print("Static inner class:\t");
        doBadThings(r5.call());
    }

    Callable createBadTask1(){
        // r holds reference to InnerClasses
        final Callable r = new Callable() {
            @Override
            public Object call() {
                return this;
            }
        };
        return r;
    }

    Callable createBadTask2(){
        return new BadClass();
    }
    Callable createStaticTask(){
        return new GoodClass();
    }

    /// BadClass holds reference to InnerClasses
    private final class BadClass implements Callable {
        @Override
        public Object call() {
            return this;
        }
    }

    private static final class GoodClass implements Callable {
        @Override
        public Object call() {
            return this;
        }
    }


    static void doBadThings(Object object) {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                System.out.println("Name: " + field.getName() + ", type: '" + field.get(object).getClass().getSimpleName() + "'");
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if(fields.length == 0){
            System.out.println("No reference escaped!");
        }
    }

}
