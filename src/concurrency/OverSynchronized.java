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

public class OverSynchronized {
    Object data;

    public OverSynchronized() {
        synchronized (this) {
            data = "NPE will happen if data is not initialized";
        }
    }

    Object getData() {
        return data;
    }

    Object getData1() {
        synchronized (data) {
            return data;
        }
    }

    Object getData2() {
        synchronized (this) {
            return data;
        }
    }

    synchronized Object getData3() {
        return data;
    }

    synchronized Object getDataTwoLocks() {
        synchronized (data) {
            return data;
        }
    }

    synchronized Object getDataTwoLocks1() {
        return getInstance().getData();
    }

    static synchronized Object getDataTwoLocks2() {
        return getInstance().getData1();
    }

    static synchronized Object getDataTwoLocks3() {
        return getInstance().getData2();
    }

    synchronized Object getDataThreeLocks() {
        return OverSynchronized.getDataTwoLocks2();
    }

    static synchronized Object getDataThreeLocks1() {
        return getInstance().getDataTwoLocks();
    }

    static synchronized Object getDataThreeLocks2() {
        return getInstance().getDataTwoLocks();
    }

    static synchronized OverSynchronized getInstance() {
        if(singleton == null) {
            singleton = new OverSynchronized();
        }
        return singleton;
    }

    static OverSynchronized singleton;


    static class DeadlockCheck implements Callable<Long> {

        @SuppressWarnings("boxing")
        @Override
        public Long call() throws Exception {
            long count = 0;
            OverSynchronized overSynchronized = OverSynchronized.getInstance();
            while(count < ITERATIONS) {
                if(count % 2 == 0) {
                    overSynchronized.getDataTwoLocks();
                } else {
                    overSynchronized.getDataTwoLocks1();
                }
                count ++;
            }
            return count;
        }

    }

    private static final int THREADS = 2;
    private static final int ITERATIONS = 10;

    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        Map<Integer, DeadlockCheck> commands = new LinkedHashMap<>();
        for (int i = 0; i < THREADS; i++) {
            commands.put(i, new DeadlockCheck());
        }
        pool.invokeAll(commands.values(), 10, TimeUnit.SECONDS);
    }

}
