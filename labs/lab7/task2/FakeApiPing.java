package labs.lab7.task2;

import java.util.*;
import java.util.concurrent.*;

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

        List<Callable<ApiResult>> tasks = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int parameter = sc.nextInt();

            // requestId is the loop index
            int requestId = i+1;
            //TODO add a Callable that invokes the API get method in the tasks list
            Callable<ApiResult> task = () -> Api.get(requestId, parameter);
            tasks.add(task);
        }

        ExecutorService executor =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<Future<ApiResult>> futures = new ArrayList<>();
        //TODO submit all callables to the executor and get the Futures
        for (Callable<ApiResult> task: tasks) {
            Future<ApiResult> future = executor.submit(task);
            futures.add(future);
        }

        List<ApiResult> results = new ArrayList<>();

        long timeoutMillis = 200;

        //TODO get the ApiResult from all the futures and allow a max timeout of timeoutMillis
        for (Future<ApiResult> future: futures) {
            try {
                // Try to get the result within the timeout
                ApiResult result = future.get(timeoutMillis, TimeUnit.MILLISECONDS);
                results.add(result);
            } catch (TimeoutException e) {
                // Task did not complete in time - need to determine requestId
                // Since we're iterating through futures in order, we can use the index
                int requestId = results.size() + 1;

                // Create a failed result
                ApiResult failedResult = new ApiResult(requestId, false, "TIMEOUT");
                results.add(failedResult);

                // Cancel the future to stop the task if possible
                future.cancel(true);
            } catch (ExecutionException e) {
                // Task threw an exception
                int requestId = results.size() + 1;
                ApiResult failedResult = new ApiResult(requestId, false, "ERROR");
                results.add(failedResult);
            }
        }

        executor.shutdown();

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

