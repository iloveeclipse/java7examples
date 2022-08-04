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
public class AccountExample {

    private static final long CLIENTS = 4; //Runtime.getRuntime().availableProcessors();
    private static final long INITIAL_AMOUNT = 10_000;

    private static class Bank {

        private long money;

        Bank(long initialAmount) {
            super();
            money = initialAmount;
        }

        public long withdrawOneDollar() {
            return -- money;
        }

        public long getCurrentAmount() {
            return money;
        }

        @Override
        public String toString() {
            return  money + "$";
        }
    }

    static class Client implements Callable<Long> {

        final Bank bank;
        long myMoney;
        long inconsistentState;

        private Client(Bank b) {
            this.bank = b;
        }

        @Override
        public Long call() throws Exception {
            long oldValue = bank.getCurrentAmount();
            while(myMoney < INITIAL_AMOUNT) {
                long newValue = bank.withdrawOneDollar();
                if(newValue >= oldValue) {
                    // Surprise ...
                    inconsistentState ++;
                }
                oldValue = newValue;
                myMoney++;
            }
            return myMoney;
        }
    }

    public static void main(String[] args) throws Exception {
        Bank account = new Bank(CLIENTS * INITIAL_AMOUNT);
        System.out.println("Initial money in the bank: " + account);
        long initialAmount = account.getCurrentAmount();
        ExecutorService pool = Executors.newFixedThreadPool((int) CLIENTS);
        Map<Integer, Client> commands = new LinkedHashMap<>();
        for (int i = 0; i < CLIENTS; i++) {
            commands.put(i, new Client(account));
        }
        pool.invokeAll(commands.values(), 10, TimeUnit.SECONDS);

        commands.forEach(AccountExample::print);
        long withdrawnMoney = commands.values().stream().mapToLong(c -> c.myMoney).sum();
        System.out.println("Clients withdrawn together: " + withdrawnMoney + "$");
        System.out.println("Should be in the bank: " + initialAmount + " - " + withdrawnMoney + " = " + (initialAmount - withdrawnMoney) + "$");
        System.out.println("Final money in the bank: " + account);

        pool.shutdownNow();
    }

    static void print(Integer i, Client d) {
        System.out.println("Client [" + i + "] \t got " + d.myMoney + "$ and saw " + d.inconsistentState + "\t issues");
    }
}
