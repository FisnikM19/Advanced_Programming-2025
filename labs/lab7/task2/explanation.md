## Understanding the Requirements
1. **Create Callables:** Each callable should invoke `Api.get()` with the **requestId** and **parameter**
2. **Submit tasks:** Submit all callables to the executor and collect the futures
3. **Handle timeouts:** Use `Future.get(timeout, unit)` to wait for results with a maximum timeout
4. **Handle failures:** If a task times out, create a failed `ApiResult` instead

## Key Concepts
- `Future.get(long timeout, TimeUnit unit)`: Waits for result with a timeout
- `TimeoutException`: Thrown when `get()` exceeds the timeout
- `executor.submit()`: Submits a single callable and returns a `Future`

## Explanation of the Solution
### 1. Creating Callables
```java
Callable<ApiResult> task = () -> Api.get(requestId, parameter); 
tasks.add(task);
```
- Lambda expression that calls `Api.get()` with the captured `requestId` and `parameter`
- Each callable is added to the tasks list

### 2. Submitting Tasks
```java
for (Callable<ApiResult> task : tasks) {
    Future<ApiResult> future = executor.submit(task);
    futures.add(future);
}
```
- Use `executor.submit()` to submit each callable individually
- Each submission returns a `Future<ApiResult>` that we collect

### 3. Handling Timeouts
```java
try {
    ApiResult result = future.get(timeoutMillis, TimeUnit.MILLISECONDS);
    results.add(result);
} catch (TimeoutException e) {
    // Create failed result for timeout
    ApiResult failedResult = new ApiResult(requestId, false, "TIMEOUT");
    results.add(failedResult);
    future.cancel(true);
}
```
- `future.get(200, TimeUnit.MILLISECONDS)` waits max 200ms for each result
- If timeout occurs, we create a failed `ApiResult` with `success = false`
- `future.cancel(true)` attempts to interrupt the running task

## How the Timeout Works

Given that each API call sleeps for `parameter * 100` milliseconds:
- **parameter = 1**: 100ms delay → **COMPLETES** (< 200ms timeout)
- **parameter = 2**: 200ms delay → **COMPLETES** (= 200ms timeout)
- **parameter = 3**: 300ms delay → **TIMES OUT** (> 200ms timeout)
- **parameter = 5**: 500ms delay → **TIMES OUT** (> 200ms timeout)

## Example Input/Output

**Input:**
```
4
1
2
3
1
```

**Output:**
```
1 OK VALUE_1
2 OK VALUE_2
3 FAILED TIMEOUT
4 OK VALUE_1
```

### Explanation:
- Request 1: 100ms delay → OK
- Request 2: 200ms delay → OK (just within timeout)
- Request 3: 300ms delay → FAILED (exceeds 200ms timeout)
- Request 4: 100ms delay → OK