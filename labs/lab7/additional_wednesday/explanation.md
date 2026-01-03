## Key Features:

1. Limited Parallel Execution:
    - Added `maxParallelCalls` input parameter
    - Created executor with `Executors.newFixedThreadPool(maxParallelCalls)` to limit concurrent API calls
2. Timeout Detection & Cancellation:
    - Added `timeoutDetected` flag to track when first timeout occurs
    - When a timeout is detected:
      - Mark the timed-out task as `"TIMEOUT"`
      - Cancel that future
      - Cancel **ALL remaining futures** in the list
      - Mark all cancelled tasks as `"CANCELLED"`
      - Break out of the loop
3. Proper Request ID Tracking:
    - Store requestIds in a separate list to accurately identify which task failed
    - This ensures correct mapping even when tasks complete out of order
4. Force Shutdown:
    - Use `executor.shutdownNow()` instead of `shutdown()` to forcefully interrupt any running tasks

## Example Input/Output:

**Input:**
```
5 2 
1 3 2 4 1
```
- 5 API calls
- Max 2 parallel calls
- Parameters: 1, 3, 2, 4, 1 (delays: 100ms, 300ms, 200ms, 400ms, 100ms)

**Expected behavior:**
- Request 3 (300 ms) will timeout at 200ms
- All subsequent unfinished requests will be cancelled

This implementation ensures that once a timeout is detected, no further API calls are allowed to complete, and all pending/running tasks are immediately cancelled.