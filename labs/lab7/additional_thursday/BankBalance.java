package labs.lab7.additional_thursday;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TODO: == Additional requirement ==
 * Extend the program so that, in addition to printing the final balance,
 * it also prints a deterministic log of all operations, showing whether each operation succeeded or failed.
 *
 * Each operation must be logged exactly once, and the output must be ordered by operationId,
 * regardless of the order in which the tasks actually execute.
 *
 * Constraints:
 * - The solution must not rely on execution order or thread scheduling.
 * - The output must always be deterministic for the same input.
 * - All shared data structures used for logging must be thread-safe.
 */

/*
Try this input:
1000
5
withdraw 300
withdraw 500
withdraw 300
deposit 400
withdraw 200
 */

public class BankBalance {

    // Shared bank account
    public static class BankAccount {
        private int balance;

        // Add a lock to synchronize access
        private final ReentrantLock lock = new ReentrantLock();

        public BankAccount(int initialBalance) {
            this.balance = initialBalance;
        }

        public boolean deposit(int amount) {
            lock.lock(); // Acquire the lock
            try {
                balance += amount;
                return true;
            } finally {
                lock.unlock(); // Always release the lock
            }
        }

        public boolean withdraw(int amount) {
            lock.lock(); // Acquire the lock

            try {
                if (balance >= amount) {
                    balance -= amount;
                    return true;
                }
                return false;
            } finally {
                lock.unlock(); // Always release the lock
            }

        }

        public int getBalance() {
            lock.lock(); // Even reading needs synchronization
            try {
                return balance;
            } finally {
                lock.unlock();
            }
        }
    }


    // Operation result
    public static class OperationResult {
        public final int operationId;
        public final String operationType;//TODO
        public final int amount;//TODO
        public final boolean success;

        public OperationResult(int operationId, String operationType, int amount, boolean success) {
            this.operationId = operationId;
            this.operationType = operationType;
            this.amount = amount;
            this.success = success;
        }
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        int initialBalance = sc.nextInt();
        int n = sc.nextInt(); // number of operations

        BankAccount account = new BankAccount(initialBalance);

        List<Callable<OperationResult>> tasks = new ArrayList<>();

        //TODO: Store operation details for task creation
        List<OperationInfo> operationInfos = new ArrayList<>();

        long lockTimeoutMs = 100; // max time to wait for the lock

        for (int i = 0; i < n; i++) {
            String type = sc.next();
            int amount = sc.nextInt();
            operationInfos.add(new OperationInfo(i + 1, type, amount));
        }

        //TODO: Create tasks with captured operation details
        for (OperationInfo info: operationInfos) {
            tasks.add(() -> {
               Thread.sleep(1000);
               boolean success;
               if (info.type.equals("deposit")) {
                   success = account.deposit(info.amount);
               } else {
                   success = account.withdraw(info.amount);
               }
               return new OperationResult(info.operationId, info.type, info.amount, success);
            });
        }

        ExecutorService executor = Executors.newFixedThreadPool(4);

        // Concurrent way:
//        List<Future<OperationResult>> futures = executor.invokeAll(tasks);
//        List<OperationResult> results = new ArrayList<>();
//        for (Future<OperationResult> f : futures) {
//            results.add(f.get());
//        }

        //TODO: Sequential way:
        List<OperationResult> results = new ArrayList<>();
        for (Callable<OperationResult> task: tasks) {
            Future<OperationResult> future = executor.submit(task);
            results.add(future.get()); // Blocks until this task completes
        }

        executor.shutdown();

        //TODO: Sort results by operationId to ensure deterministic output
        results.sort(Comparator.comparingInt(r -> r.operationId));

        // Print deterministic log of all operations
        System.out.println("OPERATION_LOG:");
        for (OperationResult result: results) {
            String status = result.success ? "SUCCESS" : "FAILED";
            System.out.println("Operation " + result.operationId + ": " +
                    result.operationType.toUpperCase() + " " +
                    result.amount + " - " + status);
        }

        // Deterministic final balance
        System.out.println("FINAL_BALANCE " + account.getBalance());
    }

    //TODO: Helper class to store operation information
    private static class OperationInfo {
        final int operationId;
        final String type;
        final int amount;

        OperationInfo(int operationId, String type, int amount) {
            this.operationId = operationId;
            this.type = type;
            this.amount = amount;
        }
    }

}