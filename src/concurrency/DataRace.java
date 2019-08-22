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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("boxing")
public class DataRace {

    private static final int THREADS = 10;
    private static final int ITERATIONS = 1000;

    private static class Data {

        private long value;

        public long increment() {
            return ++ value;
        }

        public long get() {
            return value;
        }
    }

    static class DataRaceCheck implements Callable<Long> {

        final Data data;

        long bugs;

        private DataRaceCheck(Data data) {
            this.data = data;
        }

        @Override
        public Long call() throws Exception {
            long count = 0;
            long oldValue = 0;
            long newValue = 0;
            
            while(count < ITERATIONS) {
                oldValue = data.get();
                newValue = data.increment();
                if(newValue < oldValue) {
                    bugs ++;
                }
                count++;
            }
            return newValue;
        }

    }

    public static void main(String[] args) throws Exception {
        Data data = new Data();
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        Map<Integer, DataRaceCheck> commands = new LinkedHashMap<>();
        for (int i = 0; i < THREADS; i++) {
            commands.put(i, new DataRaceCheck(data));
        }
        pool.invokeAll(commands.values(), 10, TimeUnit.SECONDS);
        commands.forEach(DataRace::print);
        System.out.println("Final result: " + data.get());
    }

    static void print(Integer i, DataRaceCheck d) {
        System.out.println("Thread [" + i + "] \t saw " + d.bugs + "\t bugs");
    }
}
