package labs.lab4.task3;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

class TermFrequency {

    private Map<String, Integer> wordFrequency;
    private int totalWords;

    public TermFrequency(InputStream inputStream, String[] stopWords) {
        this.wordFrequency = new HashMap<>();
        this.totalWords = 0;

        // Convert stopWords array to a Set for O(1) lookup
        Set<String> stopWordsSet = new HashSet<>();
        for (String word: stopWords) {
            stopWordsSet.add(word.toLowerCase());
        }

        // Read and process the input
        Scanner sc = new Scanner(inputStream);

        while (sc.hasNext()) {
            String word = sc.next().toLowerCase().replaceAll("[,.]", ""); // Remove commas and periods

            // Skip empty strings and stop words
            if (!word.isEmpty() && !stopWordsSet.contains(word)) {
                totalWords++;
                wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
            }
        }
        sc.close();
    }

    public int countTotal() {
        return totalWords;
    }

    public int countDistinct() {
        return wordFrequency.size();
    }

    public List<String> mostOften(int k) {
        // Create list from the map entries
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(wordFrequency.entrySet());

        // Sort by frequency (descending), then alphabetically (ascending)
        entries.sort(Comparator.comparing(Map.Entry<String, Integer>::getValue, Comparator.reverseOrder())
                .thenComparing(Map.Entry::getKey));

        // Extract the top k words
        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(k, entries.size()); i++) {
            result.add(entries.get(i).getKey());
        }

        return result;
    }

}

public class TermFrequencyTest {
    public static void main(String[] args) throws FileNotFoundException {
        String[] stop = new String[] { "во", "и", "се", "за", "ќе", "да", "од",
                "ги", "е", "со", "не", "тоа", "кои", "до", "го", "или", "дека",
                "што", "на", "а", "но", "кој", "ја" };
        TermFrequency tf = new TermFrequency(System.in,
                stop);
        System.out.println(tf.countTotal());
        System.out.println(tf.countDistinct());
        System.out.println(tf.mostOften(10));
    }
}

