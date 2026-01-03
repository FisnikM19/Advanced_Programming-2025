package labs.lab7.additional_tuesday;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * TODO: Additional Requirement ->
 * -> After finishing the concurrent processing of all texts, implement an additional 'Callable' task
 * that aggregates the results from all 'Counter' objects and calculates the total number of lines, words, and characters for all texts combined.
 *
 * The task must be executed using the same 'ExecutorService', return a single 'Counter' with 'textId = -1',
 * and the result must be printed after the individual statistics.
 */

public class TextCounter {

    // Result holder
    public static class Counter {
        public final int textId;
        public final int lines;
        public final int words;
        public final int chars;

        public Counter(int textId, int lines, int words, int chars) {
            this.textId = textId;
            this.lines = lines;
            this.words = words;
            this.chars = chars;
        }

        @Override
        public String toString() {
            return "Counter{" +
                    "textId=" + textId +
                    ", lines=" + lines +
                    ", words=" + words +
                    ", chars=" + chars +
                    '}';
        }


    }


    public static Callable<Counter> getTextCounter(int textId, String text) {
        //TODO
        // Return a callable using lamba expression
        // The computation happens inside the callable, not before
        return () -> {
            // Count lines: split by newline and count
            String[] linesArray = text.split("\n", -1);
            int lineCount = linesArray.length;

            // Count words: split by whitespace and count non-empty strings
            String[] wordsArray = text.split("\\s+");
            int wordCount = 0;
            for (String word: wordsArray) {
                if (!word.isEmpty()) {
                    wordCount++;
                }
            }

            // Count characters: simply the length of the text
            int charCount = text.length();

            // Return a new Counter object with computed statistics
            return new Counter(textId, lineCount, wordCount, charCount);
        };
    }

    // TODO NEW: Aggregation Callable that sums all the counters
    public static Callable<Counter> getAggregationTask(List<Counter> counters) {
        return () -> {
            int totalLines = 0;
            int totalWords = 0;
            int totalChars = 0;

            for (Counter counter: counters) {
                totalLines += counter.lines;
                totalWords += counter.words;
                totalChars += counter.chars;
            }

            return new Counter(-1, totalLines, totalWords, totalChars);
        };
    }



    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();       // number of texts
        sc.nextLine();              // consume newline

        List<Callable<Counter>> tasks = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int textId = sc.nextInt();
            sc.nextLine();          // consume newline

            int lines = sc.nextInt();   // number of lines for this text
            sc.nextLine();              // consume newline

            StringBuilder text = new StringBuilder();
            for (int j = 0; j < lines; j++) {
                text.append(sc.nextLine());
                if (j < lines - 1) {
                    text.append("\n");
                }
            }

            //TODO add a Callable<Counter> for each text read in the tasks list
            Callable<Counter> task = getTextCounter(textId, text.toString());
            tasks.add(task);

        }

        ExecutorService executor =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


        //TODO invoke All tasks on the executor and create a List<Future<?>>
        List<Future<Counter>> futures = executor.invokeAll(tasks);

        List<Counter> results = new ArrayList<>();

        //TODO extract results from the List<Future>
        for (Future<Counter> future: futures) {
            Counter counter = future.get();
            results.add(counter);
        }

        // Remove this, we have call the shutdown() at the very end
//        executor.shutdown();


        // Sorting by textId (important concept!)
        results.sort(Comparator.comparingInt(c -> c.textId));

        // Output (optional for debugging / demonstration)
        for (Counter c : results) {
            System.out.printf(
                    "%d %d %d %d%n",
                    c.textId, c.lines, c.words, c.chars
            );
        }

        // TODO NEW: Execute aggregation task using the same ExecutorService
        Callable<Counter> aggregationTask = getAggregationTask(results);
        Future<Counter> aggregationFuture = executor.submit(aggregationTask);
        Counter totalCounter = aggregationFuture.get();

        // Print the aggregated result
        System.out.println("==== Additional task ==== ");
        System.out.printf(
                "%d %d %d %d%n",
                totalCounter.textId, totalCounter.lines, totalCounter.words, totalCounter.chars
        );

        executor.shutdown();
    }
}

