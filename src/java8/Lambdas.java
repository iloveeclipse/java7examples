/*******************************************************************************
 * Copyright (c) 2018 Andrey Loskutov. All rights reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *     Andrey Loskutov - initial API and implementation
 *******************************************************************************/
package java8;

import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@SuppressWarnings("boxing")
public class Lambdas {

    public static void main(String[] args) throws Exception {
        // old style
        Runnable helloWorld = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello world");
            }
        };
        helloWorld.run();

        // new style
        Runnable helloLambda = () -> System.out.println("Hello lambda");
        helloLambda.run();

        // non-capturing lambda
        Callable<Integer> meaningOfLife = () -> 42;
        System.out.println(meaningOfLife.call());

        // non-capturing lambda
        Function<Integer, Integer> negateMe = i -> -i;
        int result = negateMe.apply(meaningOfLife.call());
        System.out.println(result);

        // capturing lambda
        Function<Integer, Integer> resultCapture = i -> i - result;
        System.out.println(resultCapture.apply(meaningOfLife.call()));

        // composite function
        Integer question = resultCapture.andThen(negateMe).apply(meaningOfLife.call());
        System.out.println(question);

        // capturing lambda with object references
        AtomicReference<Integer> reference = new AtomicReference<>(result);
        Function<Integer, Integer> referenceCapture = i -> i - reference.get();
        System.out.println(referenceCapture.apply(meaningOfLife.call()));

        // modifying "stateless" lambda
        reference.set(-624);
        System.out.println(referenceCapture.apply(meaningOfLife.call()));

        // Reuse
        HashSet<Callable<?>> lambdas = new HashSet<>();
        lambdas.add(createNonCapturingLambda(1));
        lambdas.add(createNonCapturingLambda(1));
        lambdas.add(createNonCapturingLambda(1));
        System.out.println(lambdas.size());

        lambdas = new HashSet<>();
        lambdas.add(createCapturingLambda(1));
        lambdas.add(createCapturingLambda(1));
        lambdas.add(createCapturingLambda(1));
        System.out.println(lambdas.size());

    }

    static Callable<Integer> createNonCapturingLambda(int i){
        return () -> 42;
    }

    static Callable<Integer> createCapturingLambda(int i){
        return () -> i;
    }

}
