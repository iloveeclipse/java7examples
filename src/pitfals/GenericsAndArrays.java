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

import static java.util.Arrays.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * http://www.ibm.com/developerworks/java/library/j-jtp01255/index.html
 * http://www.angelikalanger.com/GenericsFAQ/JavaGenericsFAQ.html
 * http://www.angelikalanger.com/Articles/Papers/JavaGenerics/ArraysInJavaGenerics.htm
 */
@SuppressWarnings("boxing")
public class GenericsAndArrays {

    public static void main(String[] args) {
        covariantGenerics();
        covariantArrays();
        covariantArrays2();

        listFunction();
        arrayFunction();
        arrayAndListFunction();
    }

    private static void covariantGenerics() {
        List<Integer> ints = new ArrayList<>();
        List<Number> numbers = new ArrayList<>();
        // numbers = ints; // can't compile that to avoid line below
        numbers.add(new Integer(1));
        numbers.add(new Double(1.0));
        System.out.println(numbers);
    }

    private static void covariantArrays() {
        Integer[] ints = new Integer[2];
        Number[] numbers;
        numbers = ints; // OK!
        numbers[0] = new Integer(1);
        try {
            numbers[1] = new Double(1.0); // runtime exception!
        } catch (ArrayStoreException e) {
            e.printStackTrace();
        }
    }

    private static void covariantArrays2() {
        Function<Integer> func1 = new IntegerListFunction();
        Function<Number> func2 = new NumberListFunction();
//        Function<?>[] functions = new Function<Integer>[]{func1, func2};
        Function<?>[] functions = (Function<Integer>[]) Array.newInstance(Function.class, 2);
        functions[0] = func1;
        functions[1] = func2;
    }



    static void listFunction() {
        List<Integer> input = asList(2, 2);
        Number result = new IntegerListFunction().sum(input);
        System.out.println(input + "+ = " + result);

        // does not work! generics are not covariant!
        // List<Number> is not a supertype of List<Integer>!
        // result = new NumberListFunction().sum(input);

        List<Number> input2 = new ArrayList<Number>(input);
        Number result2 = new NumberListFunction().sum(input2);
        System.out.println(input2 + "+ = " + result2);

        List<Number> input3 = new ArrayList<Number>(asList(2.0, 2.0));
        Number result3 = new NumberListFunction().sum(input3);
        System.out.println(input3 + "+ = " + result3);
    }

    static void arrayFunction() {
        Integer[] input = {2, 2};
        Number result = new IntegerArrayFunction().sum(input);
        System.out.println(Arrays.toString(input) + "+ = " + result);

        // works! array are covariant! Number[] is a supertype of Integer[]
        // result = new NumberArrayFunction().sum(input);

        Number[] input2 = input;
        Number result2 = new NumberArrayFunction().sum(input2);
        System.out.println(Arrays.toString(input2) + "+ = " + result2);

        Number[] input3 = {2.0, 2.0};
        Number result3 = new NumberArrayFunction().sum(input3);
        System.out.println(Arrays.toString(input3) + "+ = " + result3);
    }

    static void arrayAndListFunction() {
        List<Number>[] input = new List[]{asList(2, 2), asList(2, 2)};
        Number result = new NumberArrayAndListFunction().sum(input);
        System.out.println(Arrays.toString(input) + "+ = " + result);

        List<Number[]> input2 = new ArrayList<>();
        Number [] array = {2, 2};
        input2.add(array);
        input2.add(array);
        result = sum(new NumberArrayAndListFunction(), input2);
        System.out.println(toString(input2) + "+ = " + result);

        result = sum(new NumberArrayAndListFunction(), input);
        System.out.println(Arrays.toString(input) + "+ = " + result);
    }


    interface Function<T> {
        // can't "reuse" this method for sum(List<T> input) and sum(T [] input)
        // T sum(Object input);
    }

    interface ListFunction<T> extends Function<T> {
        T sum(List<T> input);
    }

    interface ArrayFunction<T> extends Function<T> {
        T sum(T [] input);
    }

    interface ArrayAndListFunction<T> extends ListFunction<T>, ArrayFunction<T> {
        @Override
        T sum(List<T> input);

        @Override
        T sum(T [] input);
    }

    static class IntegerListFunction implements ListFunction<Integer>{

        @Override
        public Integer sum(List<Integer> input) {
            int i = 0;
            for (Number number : input) {
                i += number.intValue();
            }
            return i;
        }

    }

    static class NumberListFunction implements ListFunction<Number>{

        @Override
        public Number sum(List<Number> input) {
            double i = 0;
            for (Number number : input) {
                i += number.doubleValue();
            }
            return i;
        }
    }

    static class IntegerArrayFunction implements ArrayFunction<Integer>{

        @Override
        public Integer sum(Integer[] input) {
            int i = 0;
            for (Number number : input) {
                i += number.intValue();
            }
            return i;
        }
    }

    static class NumberArrayFunction implements ArrayFunction<Number>{

        @Override
        public Number sum(Number[] input) {
            double i = 0;
            for (Number number : input) {
                i += number.doubleValue();
            }
            return i;
        }
    }

    static class NumberArrayAndListFunction implements ArrayAndListFunction<Number>{

        @Override
        public Number sum(Number[] input) {
            double i = 0;
            for (Number number : input) {
                i += number.doubleValue();
            }
            return i;
        }

        @Override
        public Number sum(List<Number> input) {
            double i = 0;
            for (Number number : input) {
                i += number.doubleValue();
            }
            return i;
        }

        // Type erasure: same signature as method above!
//        public Number sum(List<Number[]> input){
//            List<Number> result = new ArrayList<>();
//            for (int i = 0; i < input.size(); i++) {
//                result.add(sum(input.get(i)));
//            }
//            return sum(result);
//        }

        public Number sum(List<Number>[] input){
            // Runtime exception
            // Number [] result = (Number[]) new Object[input.length];
            Number [] result = new Number[input.length];
            for (int i = 0; i < input.length; i++) {
                result[i] = sum(input[i]);
            }
            return sum(result);
        }

    }


    static <T> T sum(ArrayAndListFunction<T> func, List<T[]> input){
        List<T> result = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            result.add(func.sum(input.get(i)));
        }
        return func.sum(result);
    }

    static <T> T sum(ArrayAndListFunction<T> func, List<T>[] input){
        // this code is bad ...
        T [] result = (T[]) new Object[input.length];
        for (int i = 0; i < input.length; i++) {
            result[i] = func.sum(input[i]);
        }

        // it will fail here...
        return func.sum(result);
    }


    private static String toString(List<Number[]> input) {
        StringBuilder sb = new StringBuilder("[");
        for (Number[] numbers : input) {
            sb.append(Arrays.toString(numbers)).append(", ");
        }
        if(sb.length() > 1) {
            sb.setLength(sb.length() - 2);
        }
        sb.append(']');
        return sb.toString();
    }

}
