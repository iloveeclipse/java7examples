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

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
        Callable<Integer> nc1 = createNonCapturingLambda(1);
        lambdas.add(nc1);
        Callable<Integer> nc2 = createNonCapturingLambda(1);
        lambdas.add(nc2);
        System.out.println(lambdas.size());

        Callable<Integer> another42 = () -> 42;
        System.out.println(meaningOfLife == another42);
        System.out.println(meaningOfLife.equals(another42));
        System.out.println(meaningOfLife == nc1);

        lambdas = new HashSet<>();
        lambdas.add(createCapturingLambda(1));
        lambdas.add(createCapturingLambda(1));
        System.out.println(lambdas.size());

        // Constructor reference
        Supplier<LinkedHashSet<String>> supplier = LinkedHashSet::new;
        System.out.println(supplier.get());

        List<Integer> list = Arrays.asList(1, 3, 1, 2);

        LinkedHashSet<Integer> set = list.stream().collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);
        System.out.println(set);

        Supplier<LinkedHashSet<Integer>> supplier2 = LinkedHashSet::new;
        BiConsumer<LinkedHashSet<Integer>, ? super Integer> biConsumer = LinkedHashSet::add;
        BiConsumer<LinkedHashSet<Integer>, LinkedHashSet<Integer>> combiner = LinkedHashSet::addAll;
        list.stream().collect(supplier2, biConsumer, combiner);

        // Good old Java API to do the same
        set = new LinkedHashSet<>(list);

        // Method reference
        Consumer<Object> println = System.out::println;
        list.forEach(println);

        Comparator<Integer> comparator = Integer::compare;
        list.sort(comparator);
        list.forEach(println);

        // this
        System.out.println(new Lambdas().returnMe().call());

        // Scope
        int x = 42;
        Consumer<Integer> c = (i) -> {
            // Lambda expression's local variable x cannot redeclare another local variable defined in an enclosing scope.
//            int x = i;
        };
    }

    static Callable<Integer> createNonCapturingLambda(int i){
        return () -> 42;
    }

    static Callable<Integer> createCapturingLambda(int i){
        return () -> i;
    }


    Callable<Object> returnMe(){
        Callable<Object> returnMe = () -> this;
        return returnMe;
    }

    @Override
    public String toString() {
        return "[Lambdas instance]";
    }
}
