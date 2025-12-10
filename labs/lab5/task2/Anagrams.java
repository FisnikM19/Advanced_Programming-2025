package labs.lab5.task2;

import java.io.InputStream;
import java.util.*;

public class Anagrams {

    public static void main(String[] args) {
        findAll(System.in);
    }

    public static void findAll(InputStream inputStream) {
        // Vasiod kod ovde
        Scanner sc = new Scanner(inputStream);

        Map<String, List<String>> groups = new LinkedHashMap<>();

        while (sc.hasNextLine()) {
            String word = sc.nextLine();
            char[] chars = word.toCharArray();
            Arrays.sort(chars);

            String key = new String(chars);
            groups.putIfAbsent(key, new ArrayList<>());
            groups.get(key).add(word);
        }

        for (List<String> group: groups.values()) {
            if (group.size() >= 5) {
                Collections.sort(group);
                System.out.println(String.join(" ", group));
            }
        }


    }
}

