package labs.lab9.task1;

import java.util.*;
import java.util.stream.Collectors;

class DocumentViewer {

    Map<String, List<String>> documents;
    public DocumentViewer() {
        documents = new HashMap<>();
    }

    public void addDocument(String id, String text) {
        // Alternative 1
//        documents.putIfAbsent(id, new ArrayList<>());
//        documents.get(id).add(text);

        // Alternative 2
        documents.computeIfAbsent(id, k -> new ArrayList<>()).add(text);
    }

    public void enableLineNumbers(String id) {

        if (documents.containsKey(id)) {
            List<String> lines = documents.get(id);
            List<String> numberedLines = new ArrayList<>();

            for (int i = 0; i < lines.size(); i++) {
                numberedLines.add(i+1 + ": " + lines.get(i));
            }

            documents.put(id, numberedLines);
        }
    }

    public void enableWordCount(String id) {
        List<String> lines = documents.get(id);
        if (lines == null || lines.isEmpty()) return;

//        int count = 0;
//        for (String text: lines) {
//            String[] parts = text.split("\\s+");
//            count += parts.length;
//        }

        // Alternative 2
        int count = lines.stream()
                        .mapToInt(line -> line.split("\\s+").length)
                                .sum();

        lines.add("Words: " + count);
    }

    public void enableRedaction(String id, List<String> forbiddenWords) {
        List<String> lines = documents.get(id);
        List<String> newLines = new ArrayList<>();


        for (String line: lines) {
            List<String> words = new ArrayList<>();
            String[] parts = line.split("\\s+");
            for (int i = 0; i < parts.length; i++) {
                if (forbiddenWords.contains(parts[i].toLowerCase()) || forbiddenWords.contains(parts[i]))
                    words.add("*");
                else
                    words.add(parts[i]);
            }
            String l = String.join(" ", words);
            newLines.add(l);
        }
        documents.put(id, newLines);
    }

    public void display(String id) {
        List<String> texts = documents.get(id);

        System.out.printf("=== Document %s ===\n", id);
        for (String text: texts) {
            System.out.println(text);
        }

    }
}

public class DocumentViewerTest {
    public static void main(String[] args) {

        DocumentViewer documentViewer = new DocumentViewer();


        Scanner sc = new Scanner(System.in);
        int numDocs = sc.nextInt();
        sc.nextLine();
        for (int i = 0; i < numDocs; i++) {
            String id = sc.nextLine();
            int rows = sc.nextInt();
            sc.nextLine();
            for (int j = 0; j < rows; j++) {
                String text = sc.nextLine();
                documentViewer.addDocument(id, text);
            }
        }

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.equals("exit")) break;
            String[] parts = line.split("\\s+");
            String decision = parts[0];
            String id = parts[1];

            switch (decision) {
                case "enableLineNumbers":
                    documentViewer.enableLineNumbers(id);
                    break;
                case "enableWordCount":
                    documentViewer.enableWordCount(id);
                    break;
                case "enableRedaction":
                    List<String> forbiddenWords = Arrays.asList(parts).subList(2, parts.length);
                    documentViewer.enableRedaction(id, forbiddenWords);
                    break;
                case "display":
                    documentViewer.display(id);
                    break;
            }
        }
        sc.close();
    }
}
