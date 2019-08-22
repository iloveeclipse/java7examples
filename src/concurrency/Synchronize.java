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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Synchronize {

    long data;

    long getData() {
        return data;
    }

    synchronized void setData(long data) {
        this.data = data;
    }

    private static void modifyAndCheck(Synchronize test) {
        while(true) {
            long old = test.getData();
            if(old != Long.MIN_VALUE && old != Long.MAX_VALUE) {
                System.out.println("Yahoo: " + old + ",\nwrong bits: " + Long.toBinaryString(old));
                break;
            }
            long newValue = old == Long.MIN_VALUE ? Long.MAX_VALUE : Long.MIN_VALUE;
            test.setData(newValue);
        }
    }

    public static void main(String[] args) {
        Synchronize test = new Synchronize();
        test.data = Long.MAX_VALUE;
        int threads = Runtime.getRuntime().availableProcessors() - 2;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            pool.execute(() -> modifyAndCheck(test));
        }
    }

}
