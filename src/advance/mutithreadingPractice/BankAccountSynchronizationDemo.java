package advance.mutithreadingPractice;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BankAccountSynchronizationDemo {
    public static void main(String[] args) throws InterruptedException {
        // Create accounts
        UnsafeBankAccount unsafeAccount = new UnsafeBankAccount(1000);
        SafeBankAccount safeAccount = new SafeBankAccount(1000);

        int numOperations = 500;

        // Run operations on unsafe account
        System.out.println("Running operations on UNSAFE account...");
        runOperations(unsafeAccount, numOperations);

        // Run operations on safe account
        System.out.println("\nRunning operations on SAFE account...");
        runOperations(safeAccount, numOperations);
    }

    private static void runOperations(BankAccount account, int numOperations) throws InterruptedException {
        // Use a thread pool with 10 threads
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Record start balance
        double startBalance = account.getBalance();
        System.out.println("Starting balance: $" + startBalance);

        // Perform equal number of credits and debits of same amount
        // This should result in the same end balance if thread-safe
        for (int i = 0; i < numOperations; i++) {
            // Half operations are credits, half are debits, of the same amount
            boolean isCredit = i % 2 == 0;
            double amount = 100;

            executor.submit(() -> {
                if (isCredit) {
                    account.credit(amount);
                } else {
                    account.debit(amount);
                }

                // Occasionally check balance (20% of operations)
//                if (Math.random() < 0.2) {
//                    account.getBalance();
//                }
            });
        }

        // Shutdown executor and wait for completion
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // Check final balance
        double endBalance = account.getBalance();
        System.out.println("Final balance: $" + endBalance);
    }
}

// interface for both account types
interface BankAccount {
    void credit(double amount);
    boolean debit(double amount);
    double getBalance();
}

class UnsafeBankAccount implements BankAccount {
    private double balance;

    public UnsafeBankAccount(double initialBalance) {
        this.balance = initialBalance;
    }

    @Override
    public void credit(double amount) {
        if (amount <= 0) return;

        double currentBalance = balance;

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Update balance - this operation is not atomic
        balance = currentBalance + amount;
    }

    @Override
    public boolean debit(double amount) {
        if (amount <= 0) return false;

        double currentBalance = balance;

        // Check if sufficient funds
        if (currentBalance >= amount) {
            // Simulate some processing time to increase chance of race condition
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Update balance - this operation is not atomic
            balance = currentBalance - amount;
            return true;
        }

        return false;
    }

    @Override
    public double getBalance() {
        return balance;
    }
}

class SafeBankAccount implements BankAccount {
    private double balance;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public SafeBankAccount(double initialBalance) {
        this.balance = initialBalance;
    }

    @Override
    public void credit(double amount) {
        if (amount <= 0) return;

        // Acquire write lock for update
        lock.writeLock().lock();
        try {
            double currentBalance = balance;

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Update balance - operation is protected by write lock
            balance = currentBalance + amount;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean debit(double amount) {
        if (amount <= 0) return false;

        // Acquire write lock for update
        lock.writeLock().lock();
        try {
            // Check if sufficient funds
            if (balance >= amount) {
                double currentBalance = balance;

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // Update balance - operation is protected by write lock
                balance = currentBalance - amount;
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public double getBalance() {
        // Acquire read lock for reading
        lock.readLock().lock();
        try {
            // Multiple threads can read simultaneously
            return balance;
        } finally {
            lock.readLock().unlock();
        }
    }
}