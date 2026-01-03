## 1. Why use Executors?
Instead of manually creating threads like this:
```java
Thread t1 = new Thread(() -> { /* do work */ });
t1.start();
```

We use an **ExecutorService** which:
- Manages a **pool of threads** (reuses threads instead of creating new ones each time)
- Handles thread lifecycle automatically
- Makes concurrent programming much easier
```java
ExecutorService executor = Executors.newFixedThreadPool(4);
```
This creates a pool of **4 threads** ready to execute tasks.
<hr>

## 2. Callable vs Runnable
- **Runnable:** A task that doesn't return a result
```java
Runnable task = () -> { System.out.println("Hello"); };
```
- **Callable:** A task that returns a result (this is what we need!)
```java
Callable<String> task = () -> { return "Hello"; };
```
We use `Callable<OperationResult>` because each operation needs to return whether it succeeded or failed.
<hr>

## 3. Future - Getting Results Back
When you submit a `Callable` to an executor, you get a **Future** object:
```java
Future<OperationResult> future = executor.submit(task);
OperationResult result = future.get(); // Blocks until task completes
```
Think of a `Future` as a "promise" - it represents a result that will be available in the future.

Calling `.get()` waits for the task to finish and retrieves the result.
<hr>

## 🔧 The Solution - Step by Step
### Step 1: Enhanced OperationResult Class
```java
public static class OperationResult {
    public final int operationId;
    public final String operationType;  // "deposit" or "withdraw"
    public final int amount;            // how much money
    public final boolean success;       // did it succeed?
}
```
**Why?** We need to store all information about each operation so we can log it later.
The original only had `operationId` and `success`, but we need to know WHAT operation happened.
<hr>

### Step 2: The OperationInfo Helper Class
```java
private static class OperationInfo {
    final int operationId;
    final String type;
    final int amount;
}
```
**Why do we need this?** Here's the problem:
```java
// ❌ WRONG - This won't work:
for (int i = 0; i < n; i++) {
    String type = sc.next();
    int amount = sc.nextInt();
    
    tasks.add(() -> {
        // Problem: which 'type' and 'amount' does this lambda use?
        // All tasks would see the LAST value!
    });
}
```
Lambda expressions capture variables, but if you create multiple lambdas in a loop, they might all reference the same variable.
**Solution:** Store each operation's data in an immutable object first.
```java
// ✅ CORRECT:
List<OperationInfo> operationInfos = new ArrayList<>();

// First, read and store all operations
for (int i = 0; i < n; i++) {
    String type = sc.next();
    int amount = sc.nextInt();
    operationInfos.add(new OperationInfo(i + 1, type, amount));
}

// Then, create tasks using the stored info
for (OperationInfo info : operationInfos) {
    tasks.add(() -> {
        // Now each task has its OWN 'info' object
        boolean success;
        if (info.type.equals("deposit")) {
            success = account.deposit(info.amount);
        } else {
            success = account.withdraw(info.amount);
        }
        return new OperationResult(info.operationId, info.type, info.amount, success);
    });
}
```
<hr>

### Step 3: Sequential Execution (The Key Fix!)
This is the most important part to understand.

**Option A: Concurrent Execution (WRONG for this problem)**
```java
// Submits ALL tasks at once - they run in parallel
List<Future<OperationResult>> futures = executor.invokeAll(tasks);

// Then collect results
List<OperationResult> results = new ArrayList<>();
for (Future<OperationResult> f : futures) {
    results.add(f.get());
}
```

**What happens:**
- All 5 operations start **at the same time** on different threads
- Operation 4 (deposit 400) might finish **before** Operation 3 (withdraw 300)
- The balance changes in unpredictable order
- Operation 3 might succeed when it should fail!

**Timeline (concurrent):**
```
Time 0: Op1, Op2, Op3, Op4, Op5 all start together
Time 1: Op4 completes (deposit 400)
Time 2: Op1 completes (withdraw 300)
Time 3: Op3 completes (withdraw 300) ← Now succeeds because Op4 already added money!
```
**Option B: Sequential Execution (CORRECT)**
```java
List<OperationResult> results = new ArrayList<>();

for (Callable<OperationResult> task : tasks) {
    Future<OperationResult> future = executor.submit(task);
    results.add(future.get()); // ← This blocks! Wait for completion.
}
```

**What happens:**
- Submit Operation 1 → **wait** for it to complete → save result
- Submit Operation 2 → **wait** for it to complete → save result
- Submit Operation 3 → **wait** for it to complete → save result
- etc.

**Timeline (sequential):**
```
Time 0-1: Op1 executes and completes (withdraw 300, balance = 700)
Time 1-2: Op2 executes and completes (withdraw 500, balance = 200)
Time 2-3: Op3 executes and FAILS (withdraw 300, but only 200 available)
Time 3-4: Op4 executes and completes (deposit 400, balance = 600)
Time 4-5: Op5 executes and completes (withdraw 200, balance = 400)
```
<hr>

### Step 4: Sorting for Deterministic Output
```java
results.sort(Comparator.comparingInt(r -> r.operationId));
```
**Why?** Even though we executed sequentially (so results are already in order), this is a safety measure.
The requirement says output must be deterministic "regardless of execution order," so sorting guarantees this.
<hr>

### Step 5: Print the Log
```java
System.out.println("OPERATION_LOG:");
for (OperationResult result : results) {
    String status = result.success ? "SUCCESS" : "FAILED";
    System.out.println("Operation " + result.operationId + ": " + 
                     result.operationType.toUpperCase() + " " + 
                     result.amount + " - " + status);
}
```
Now we have all the information we need to log each operation!

## 🔐 Thread Safety - The ReentrantLock
```java
private final ReentrantLock lock = new ReentrantLock();

public boolean withdraw(int amount) {
    lock.lock();  // Only one thread can enter at a time
    try {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    } finally {
        lock.unlock();  // Always release the lock
    }
}
```

**Why is this needed?**
- Multiple threads might try to access `balance` at the same time
- Without locking: Thread 1 reads balance=100, Thread 2 reads balance=100, both withdraw 100, balance becomes 0 instead of -100 (lost update!)
- With locking: Only one thread can read/modify balance at a time

**Always use try-finally** to ensure the lock is released even if an exception occurs!

---

## 📊 Your Example Traced Through

**Input:**
```
Initial balance: 1000
Op1: withdraw 300
Op2: withdraw 500
Op3: withdraw 300
Op4: deposit 400
Op5: withdraw 200
```

**Execution:**
1. Op1: balance = 1000, withdraw 300 ✓ → balance = 700
2. Op2: balance = 700, withdraw 500 ✓ → balance = 200
3. Op3: balance = 200, withdraw 300 ✗ → **FAILED** (not enough money)
4. Op4: balance = 200, deposit 400 ✓ → balance = 600
5. Op5: balance = 600, withdraw 200 ✓ → balance = 400

**Output:**
```
OPERATION_LOG:
Operation 1: WITHDRAW 300 - SUCCESS
Operation 2: WITHDRAW 500 - SUCCESS
Operation 3: WITHDRAW 300 - FAILED    ← Correctly fails!
Operation 4: DEPOSIT 400 - SUCCESS
Operation 5: WITHDRAW 200 - SUCCESS
FINAL_BALANCE 400
```