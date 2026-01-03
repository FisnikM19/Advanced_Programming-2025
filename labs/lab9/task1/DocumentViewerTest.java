package labs.lab9.task1;

import java.util.*;

class DocumentViewer {

    Map<String, List<String>> documentsMap;

    public DocumentViewer() {
        documentsMap = new LinkedHashMap<>();
    }

    public void addDocument(String id, String text) {
        if (!documentsMap.containsKey(id)) {
            documentsMap.computeIfAbsent(id, k -> new ArrayList<>()).add(text);
        } else {
            List<String> texts = documentsMap.get(id);
            texts.add(text);
            documentsMap.put(id, texts);
        }
    }

    public void enableLineNumbers(String id) {

        if (documentsMap.containsKey(id)) {
            List<String> lines = documentsMap.get(id);
            List<String> numberedLines = new ArrayList<>();

            for (int i = 0; i < lines.size(); i++) {
                numberedLines.add((i+1) + ": " + lines.get(i));
            }

            documentsMap.put(id, numberedLines);
        }
    }

    public void enableWordCount(String id) {
        List<String> texts = documentsMap.get(id);
        int totalWords = 0;
        for (String text: texts) {
            String[] parts = text.split("\\s+");
            totalWords += parts.length;
        }

        String newLine = "Words: " + totalWords;
        texts.add(newLine);

        documentsMap.put(id, texts);
    }

    public void enableRedaction(String id, List<String> forbiddenWords) {
        if (documentsMap.containsKey(id)) {
            List<String> texts = documentsMap.get(id);
            List<String> refactoredLines = new ArrayList<>();

            for (String text: texts) {
                String[] parts = text.split("\\s+");

                boolean flag = false;
                for (int i = 0; i < parts.length; i++) {
                    if (forbiddenWords.contains(parts[i]) || forbiddenWords.contains(parts[i].toLowerCase())) {
                        parts[i] = "*";
                        flag = true;
                    }
                }

                if (flag) {
                    String newText = "";
                    for (int i = 0; i < parts.length; i++) {
                        newText += parts[i] + " ";
                    }
                    refactoredLines.add(newText);
                } else {
                    refactoredLines.add(text);
                }
            }
            documentsMap.put(id, refactoredLines);
        }
    }

    public void display(String id) {

        System.out.println("=== Document " + id + " ===");
        List<String> texts = documentsMap.get(id);
        for (String text: texts) {
            System.out.println(text);
        }
    }

}

public class DocumentViewerTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        DocumentViewer documentViewer = new DocumentViewer();

        for (int i = 0; i < n; i++) {
            String id = sc.next();
            int numLines = sc.nextInt();
            sc.nextLine();

            for (int j = 0; j < numLines; j++) {
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
                    List<String> forbiddenWords = new ArrayList<>();
                    for (int i = 2; i < parts.length; i++) {
                        forbiddenWords.add(parts[i]);
                    }
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
