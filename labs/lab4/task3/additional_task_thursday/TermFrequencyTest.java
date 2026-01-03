package labs.lab4.task3.additional_task_thursday;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * TODO:
 * -- Method 1 --
 * public Map<Character, List<String>> groupByFirstLetter()
 * Implement a method that returns a map where:
 * - key → the first letter of the words
 * - value → a list of all words that start with that letter
 * The lists must be sorted alphabetically.
 *
 * -- Method 2 --
 * public Map<String, Integer> countPrefixes(int prefixLength)
 * Implement a method that returns a map where:
 * - key → a prefix of the given length
 * - value → the number of words in the text that start with that prefix
 * Words that are shorter than the prefix length must be ignored.
 *
 * -- Method 3 --
 * public Map<Integer, Set<String>> invertIndex()
 * Implement a method that creates an inverse map of the frequency map, such that:
 * - key → the frequency
 * - value → a set of words that have that frequency
 * The sets must be alphabetically sorted.
 */

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

    //TODO: Method 1
    public Map<Character, List<String>> groupByFirstLetter() {

        return wordFrequency.keySet().stream()
                .collect(Collectors.groupingBy(
                        c -> c.charAt(0),
                        Collectors.toCollection(ArrayList::new)
                ));
    }

    //TODO: Method 2
    public Map<String, Integer> countPrefixes(int prefixLength) {
        Map<String, Integer> map = new HashMap<>();

        for (String s: wordFrequency.keySet()) {
            if (s.length() < prefixLength) {
                continue;
            }

            map.merge(s.substring(0, prefixLength), wordFrequency.get(s), Integer::sum);
        }

        return map;
    }

    //TODO: Method 3
    public  Map<Integer, Set<String>> invertIndex() {

        return wordFrequency.keySet().stream()
                .collect(Collectors.groupingBy(
                        e -> wordFrequency.get(e),
                        Collectors.toCollection(TreeSet::new)
                ));
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
        // Test additional methods:
        System.out.println(tf.groupByFirstLetter()); // method 1
        System.out.println(tf.countPrefixes(3)); // method 2
        System.out.println(tf.invertIndex()); // method 3
    }
}

