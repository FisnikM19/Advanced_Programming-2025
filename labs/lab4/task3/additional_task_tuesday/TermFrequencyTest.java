package labs.lab4.task3.additional_task_tuesday;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

/** TODO:
 Implement the method :
 public Map<Integer, List<String>> byFrequency()
 which should return a map where the key is the number of occurrences of the words in the text,
 and the value is a list of words that appear exactly that many times.
 The lists of words must be sorted alphabetically, and the frequencies must be sorted in descending order.

 Implement the method:
 public Set<String> stopWordsUsed()
 which should return a set of all stop-words that actually appeared in the text (even though they are not counted in the statistics).

 Implement the method:
 public String longestWord()
 which should return the longest word that appears in the text.
 If multiple words have the same length, return the lexicographically smallest one.
 */


class TermFrequency {

    private Map<String, Integer> wordFrequency;
    private int totalWords;
    private Set<String> stopWordsUsed; //TODO: we added this attribute

    public TermFrequency(InputStream inputStream, String[] stopWords) {
        this.wordFrequency = new HashMap<>();
        this.totalWords = 0;
        this.stopWordsUsed = new HashSet<>();

        // Convert stopWords array to a Set for O(1) lookup
        Set<String> stopWordsSet = new HashSet<>();
        for (String word: stopWords) {
            stopWordsSet.add(word.toLowerCase());
        }

        // Read and process the input
        Scanner sc = new Scanner(inputStream);

        while (sc.hasNext()) {
            String word = sc.next().toLowerCase().replaceAll("[,.]", ""); // Remove commas and periods
            //TODO:

            if (word.isEmpty()) continue;

            // If it is a stop word, record it
            if (stopWordsSet.contains(word)) {
                stopWordsUsed.add(word);
                continue; // skip counting it
            }

            // Regular word counting
            totalWords++;
            wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);

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
        List<Entry<String, Integer>> entries = new ArrayList<>(wordFrequency.entrySet());

        // Sort by frequency (descending), then alphabetically (ascending)
        entries.sort(Comparator.comparing(Entry<String, Integer>::getValue, Comparator.reverseOrder())
                .thenComparing(Entry::getKey));

        // Extract the top k words
        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(k, entries.size()); i++) {
            result.add(entries.get(i).getKey());
        }

        return result;
    }

    //TODO:First Method
    public Map<Integer, List<String>> byFrequency() {
        // TreeMap to sort keys (frequencies) in descending order
        Map<Integer, List<String>> result = new TreeMap<>(Comparator.reverseOrder());

        // Go through all word-frequency pairs
        for (Map.Entry<String, Integer> entry: wordFrequency.entrySet()) {
            String word = entry.getKey();
            Integer freq = entry.getValue();

            // Insert word into the list for its frequency
            result.putIfAbsent(freq, new ArrayList<>());
            result.get(freq).add(word);
        }

        // Sort each list alphabetically
        for (List<String> words: result.values()) {
            Collections.sort(words);
        }

        return result;
    }

    //TODO:Second Method
    public Set<String> stopWordsUsed() {
        return new HashSet<>(stopWordsUsed);
    }

    //TODO:Third Method
    public String longestWord() {
        return wordFrequency.keySet()
                .stream()
                .max(Comparator
                        .comparingInt(String::length)   // longest first
                        .thenComparing(Comparator.naturalOrder())   // lexicographically smallest
                ).orElse("");   // in case map is empty
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

        //TODO: Testing additional methods
        System.out.println(tf.byFrequency());
        System.out.println(tf.stopWordsUsed());
        System.out.println("Longest word: " + tf.longestWord());
    }
}

