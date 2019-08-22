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

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("boxing")
public class Volatile {

    private static final int CPU = Runtime.getRuntime().availableProcessors();
    private static final int THREADS = (int)(CPU * 1.5);
    private static final long ITERATIONS = 1000_000_000;
    private static final int MAX_RUNS = 10;

    public static class Data {


        public long value;
        public volatile long volValue;

        long getNonVolatile() {
            return value;
        }

        long getVolatile() {
            return volValue;
        }

    }

    static class VolatileCheck implements Callable<Long> {

        final Data data;
        final boolean readVolatile;

        long runs;

        volatile boolean sawRightData;
        volatile boolean sawChange = true;


        private VolatileCheck(Data data, boolean readVolatile) {
            this.data = data;
            this.readVolatile = readVolatile;
        }

        @Override
        public Long call() throws Exception {
            long value = 0;

            while(runs < ITERATIONS) {
                if(readVolatile) {
                    value = data.getVolatile();
                } else {
                    value = data.getNonVolatile();
                }

                if(value == 0) {
                    runs ++;
                } else {
                    sawRightData = true;
                    return value;
                }
            }
            sawChange = false;
            return Long.valueOf(-1);
        }

    }

    public static void main(String[] args) throws Exception {
        AtomicLong good = new AtomicLong();
        AtomicLong seen = new AtomicLong();
        Instant start = Instant.now();
        for (int i = 0; i < MAX_RUNS; i++) {
            changeDataOnce(good, seen);
        }
        long sumGood = good.get();
        long sumSeen = seen.get();

        int total = MAX_RUNS * THREADS;
        int percent = (int)(sumGood / ((double)total / 100));
        long seconds = Duration.between(start, Instant.now()).getSeconds();
        System.out.println("Saw data \t" + sumGood + " x from " + total + " (" + percent + " %)");

        long neverSawAnything = total - sumSeen;
        percent = (int)(neverSawAnything / ((double)total / 100));
        System.out.println("NEVER saw data \t" + neverSawAnything + " x from " + total + " (" + percent + " %)");


        System.out.println("Used " + seconds + " sec");
    }

    private static void changeDataOnce(AtomicLong good, AtomicLong sawChange) throws InterruptedException {
        Data data = new Data();
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        Map<Integer, VolatileCheck> commands = new LinkedHashMap<>();
        boolean readVolatile = false;

        for (int i = 0; i < THREADS; i++) {
            commands.put(i, new VolatileCheck(data, readVolatile));
        }
        commands.values().forEach(c -> pool.submit(c));
        Thread.sleep(5);

        data.value = 42;
        data.volValue = 42;


        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
        good.addAndGet(commands.values().stream().filter(c -> c.sawChange).mapToInt(c -> c.sawRightData? 1 : 0).sum());
        sawChange.addAndGet(commands.values().stream().filter(c -> c.sawChange).count());
    }

}
