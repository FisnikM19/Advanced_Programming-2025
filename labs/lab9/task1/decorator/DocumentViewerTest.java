package labs.lab9.task1.decorator;

import java.util.*;

// =======================
// Component
// =======================
interface IDocument {
    List<String> getLines();
}

// =======================
// Concrete Components
// =======================
class Document implements IDocument {

    private List<String> lines;

    public Document(List<String> lines) {
        this.lines = lines;
    }

    @Override
    public List<String> getLines() {
        return new ArrayList<>(lines);
    }
}


// =======================
// Decorator base
// =======================
abstract class DocumentDecorator implements IDocument {

    protected IDocument document;

    public DocumentDecorator(IDocument document) {
        this.document = document;
    }

    @Override
    public List<String> getLines() {
        return document.getLines();
    }
}

// =======================
// Concrete Decorators
// =======================
class LineNumberDecorator extends DocumentDecorator {

    public LineNumberDecorator(IDocument document) {
        super(document);
    }

    @Override
    public List<String> getLines() {
        List<String> lines = document.getLines();
        List<String> numberedLines = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            numberedLines.add((i + 1) + ": " + lines.get(i));
        }
        return numberedLines;
    }
}


class WordCountDecorator extends DocumentDecorator {

    public WordCountDecorator(IDocument document) {
        super(document);
    }

    @Override
    public List<String> getLines() {
        List<String> lines = document.getLines();

        int count = lines.stream()
                .mapToInt(line -> line.split("\\s+").length)
                .sum();

        lines.add("Words: " + count);

        return lines;
    }
}

class RedactionDecorator extends DocumentDecorator {

    private List<String> forbiddenWords;

    public RedactionDecorator(IDocument document, List<String> forbiddenWords) {
        super(document);
        this.forbiddenWords = forbiddenWords;
    }

    @Override
    public List<String> getLines() {
        List<String> lines = document.getLines();
        List<String> redactedLines = new ArrayList<>();

        // Convert to lowercase set for efficient lookup
        Set<String> forbidden = new HashSet<>();
        for (String word: forbiddenWords) {
            forbidden.add(word.toLowerCase());
        }

        for (String line: lines) {
            String[] words = line.split("\\s+");
            for (int i = 0; i < words.length; i++) {
                if (forbidden.contains(words[i].toLowerCase())) {
                    words[i] = "*";
                }
            }
            redactedLines.add(String.join(" ", words));
        }
        return redactedLines;
    }
}

class DocumentViewer {

    private Map<String, List<String>> rawDocuments;
    private Map<String, IDocument> decoratedDocuments;

    public DocumentViewer() {
        rawDocuments = new HashMap<>();
        decoratedDocuments = new HashMap<>();
    }

    public void addDocument(String id, String text) {
        rawDocuments.computeIfAbsent(id, k -> new ArrayList<>()).add(text);
    }

    public void enableLineNumbers(String id) {
        IDocument doc = getOrCreateDocument(id);
        decoratedDocuments.put(id, new LineNumberDecorator(doc));
    }

    public void enableWordCount(String id) {
        IDocument doc = getOrCreateDocument(id);
        decoratedDocuments.put(id, new WordCountDecorator(doc));
    }

    public void enableRedaction(String id, List<String> forbiddenWords) {
        IDocument doc = getOrCreateDocument(id);
        decoratedDocuments.put(id, new RedactionDecorator(doc, forbiddenWords));
    }

    public void display(String id) {
        IDocument doc = getOrCreateDocument(id);
        List<String> lines = doc.getLines();

        System.out.printf("=== Document %s ===%n", id);
        for (String line: lines) {
            System.out.println(line);
        }
    }

    private IDocument getOrCreateDocument(String id) {
        if (decoratedDocuments.containsKey(id)) {
            return decoratedDocuments.get(id);
        }

        List<String> lines = rawDocuments.getOrDefault(id, new ArrayList<>());
        return new Document(lines);
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
