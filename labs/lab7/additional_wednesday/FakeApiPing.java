package labs.lab7.additional_wednesday;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * TODO: Additional requirement of task 2 =>
 * It is necessary to implement a mechanism that limits the number of API calls executed in parallel.
 * Upon detection of the first API call that does not finish within the allowed time, all other active and unfinished calls must be cancelled.
 * API calls that are cancelled must be marked as 'unsuccessful' "(FAILED)".
 */

public class FakeApiPing {

    // Result holder
    public static class ApiResult {
        public final int requestId;
        public final boolean success;
        public final String value;

        public ApiResult(int requestId, boolean success, String value) {
            this.requestId = requestId;
            this.success = success;
            this.value = value;
        }

        @Override
        public String toString() {
            return "ApiResult{" +
                    "requestId=" + requestId +
                    ", success=" + success +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public static class Api {
        public static ApiResult get(int requestId, int parameter) throws InterruptedException {
            long delayMillis = parameter * 100L;
            Thread.sleep(delayMillis);

            String response = "VALUE_" + parameter;
            return new ApiResult(requestId, true, response);
        }
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt(); // number of API calls
        int maxParallelCalls = sc.nextInt(); //TODO NEW: we added this

        List<Callable<ApiResult>> tasks = new ArrayList<>();
        List<Integer> requestIds = new ArrayList<>(); //TODO NEW: we added this

        for (int i = 0; i < n; i++) {
            int parameter = sc.nextInt();

            // requestId is the loop index + 1
            int requestId = i+1;
            requestIds.add(requestId); //TODO NEW: we added this

            //TODO add a Callable that invokes the API get method in the tasks list
            Callable<ApiResult> task = () -> Api.get(requestId, parameter);
            tasks.add(task);
        }

        ExecutorService executor = Executors.newFixedThreadPool(maxParallelCalls); //TODO NEW: we added maxParallelCalls as argument

        List<Future<ApiResult>> futures = new ArrayList<>();
        //TODO submit all callables to the executor and get the Futures
        for (Callable<ApiResult> task: tasks) {
            Future<ApiResult> future = executor.submit(task);
            futures.add(future);
        }

        List<ApiResult> results = new ArrayList<>();
        long timeoutMillis = 200;

        //TODO NEW: Use a flag array to track which futures have been processed
        boolean[] processed = new boolean[futures.size()];
        boolean timeoutDetected = false;
        long startTime = System.currentTimeMillis();
        long deadline = startTime + timeoutMillis;

        //TODO NEW: // Keep checking futures until timeout or all are done
        while (System.currentTimeMillis() < deadline && !timeoutDetected) {
            for (int i = 0; i < futures.size(); i++) {
                if (processed[i]) continue;

                Future<ApiResult> future = futures.get(i);
                int requestId = requestIds.get(i);

                // Check if this future is done (without blocking)
                if (future.isDone()) {
                    try {
                        ApiResult result = future.get(); // Won't block since isDone() is true
                        results.add(result);
                        processed[i] = true;
                    } catch (CancellationException e) {
                        ApiResult failedResult = new ApiResult(requestId, false, "CANCELLED");
                        results.add(failedResult);
                        processed[i] = true;
                    } catch (ExecutionException e) {
                        ApiResult failedResult = new ApiResult(requestId, false, "ERROR");
                        results.add(failedResult);
                        processed[i] = true;
                    }
                }
            }

            // Small sleep to avoid busy waiting
            Thread.sleep(10);
        }

        // Check if timeout occurred
        if (System.currentTimeMillis() >= deadline) {
            timeoutDetected = true;
        }

        //TODO NEW: Handle any remaining unprocessed futures
        for (int i = 0; i < futures.size(); i++) {
            if (!processed[i]) {
                Future<ApiResult> future = futures.get(i);
                int requestId = requestIds.get(i);

                if (timeoutDetected) {
                    // Cancel and mark as timeout or cancelled
                    if (future.isDone()) {
                        // Completed just after deadline
                        try {
                            ApiResult result = future.get();
                            results.add(result);
                        } catch (Exception e) {
                            ApiResult failedResult = new ApiResult(requestId, false, "CANCELLED");
                            results.add(failedResult);
                        }
                    } else {
                        // Still running - this is the timeout or needs cancellation
                        future.cancel(true);
                        // First unfinished task gets TIMEOUT, others get CANCELLED
                        boolean isFirstTimeout = results.stream()
                                .noneMatch(r -> "TIMEOUT".equals(r.value));
                        String status = isFirstTimeout ? "TIMEOUT" : "CANCELLED";
                        ApiResult failedResult = new ApiResult(requestId, false, status);
                        results.add(failedResult);
                    }
                }
            }
        }

        executor.shutdownNow(); //TODO NEW: Force shutdown to interrupt any running tasks

        // Sorting by requestId
        results.sort(Comparator.comparingInt(r -> r.requestId));

        // Output
        for (ApiResult r : results) {
            System.out.printf(
                    "%d %s %s%n",
                    r.requestId,
                    r.success ? "OK" : "FAILED",
                    r.value
            );
        }
    }
}

