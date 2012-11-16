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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

// Java must be started with -Xmx4000m -Xms500m argument
public class ForkJoinAPI {

    static final char[] CHARS = "0123456789".toCharArray();

    static int threshold = 10;

    static volatile int forkCount;

    @SuppressWarnings("boxing")
    public static void main(String[] args) {
        List<String> randomStrings = createRandomStrings(100, 2);
        println(randomStrings);
        int parallelism = Runtime.getRuntime().availableProcessors();
        List<Integer> indices = findAll(randomStrings, "42", parallelism);
        System.out.println(indices);
        System.out.println("Parallelism: " + parallelism + ", forks: " + forkCount);


        int listSize = 20000000;
        System.out.println("Creating " + listSize + " random strings...");
        randomStrings = createRandomStrings(listSize, 6);

        System.out.println("Warming up...");
        String key = "123456";
        findAll(randomStrings, key, parallelism);
        findBruteForce(randomStrings, key, 0);
        findAll(randomStrings, key, parallelism);
        findBruteForce(randomStrings, key, 0);

        forkCount = 0;
        threshold = listSize / parallelism;
        System.out.println("Searching with threshold " + threshold + "...");

        long start = System.currentTimeMillis();
        indices = findAll(randomStrings, key, parallelism);
        long stop1 = System.currentTimeMillis();

        List<Integer> indices2 = findBruteForce(randomStrings, key, 0);
        long stop2 = System.currentTimeMillis();

        forkCount = 0;
        List<Integer> indices3 = findAll(randomStrings, key, 1);
        long stop3 = System.currentTimeMillis();

        long singleThread = stop3 - stop2;
        long bruteForce = stop2 - stop1;
        long parallel = stop1 - start;

        System.out.println("Parallelism: " + parallelism + ", forks: " + forkCount + ", " + parallel + " ms");
        System.out.println("Parallelism: " + 1 + ", forks: " + forkCount + ", "+ singleThread + " ms");
        System.out.println("Brute force:" + bruteForce + " ms");

        System.out.printf("Overhead: %1$4.1f %% %n", ((double)singleThread / bruteForce * 100d - 100));
        System.out.printf("Speedup:  %1$4.1f %% %n", ((double)bruteForce / parallel * 100d - 100));

        System.out.println("Results OK?");
        System.out.println("Found: " + indices.size());
        System.out.println("Found: " + indices2.size());
        System.out.println("Found: " + indices3.size());


    }

    static List<Integer> findAll(List<String> list, String key, int parallelism) {
        ForkJoinPool pool = new ForkJoinPool(parallelism);
        FindTask task = new FindTask(list, key, 0);
        return pool.invoke(task);
    }

    @SuppressWarnings("serial")
    static class FindTask extends RecursiveTask<List<Integer>> {

        private final List<String> list;

        private final String key;

        private final int start;

        public FindTask(List<String> list, String key, int start) {
            super();
            forkCount++;
            this.list = list;
            this.key = key;
            this.start = start;
        }

        @Override
        protected List<Integer> compute() {
            if (list.size() <= threshold) {
                return findBruteForce(list, key, start);
            }

            int mid = list.size() / 2;
            FindTask find1 = new FindTask(list.subList(0, mid), key, start);
            FindTask find2 = new FindTask(list.subList(mid, list.size()), key, mid
                    + start);
            invokeAll(find1, find2);
            List<Integer> result;
            try {
                result = find1.get();
                result.addAll(find2.get());
                return result;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
        }

    }

    @SuppressWarnings("boxing")
    static List<Integer> findBruteForce(List<String> list, String key, int start) {
        List<Integer> set = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (key.equals(list.get(i))) {
                set.add(start + i);
            }
        }
        return set;
    }

    private static void println(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.print(list.get(i) + " ");
            if ((i + 1) % 10 == 0) {
                System.out.println(" " + (i + 1));
            }
        }

    }

    public static List<String> createRandomStrings(int listSize, int charsCount) {
        List<String> list = new ArrayList<>(listSize);
        Random r = new Random();
        StringBuilder sb = new StringBuilder(charsCount);
        for (int i = 0; i < listSize; i++) {
            for (int j = 0; j < charsCount; j++) {
                sb.append(CHARS[r.nextInt(CHARS.length)]);
            }
            list.add(sb.toString());
            sb.setLength(0);
        }
        return list;
    }

}
