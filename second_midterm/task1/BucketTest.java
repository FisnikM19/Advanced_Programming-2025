package second_midterm.task1;

import java.util.*;
import java.util.stream.Collectors;

abstract class Component {

    public void addObject(String key) {
        throw new UnsupportedOperationException();
    }

    public void removeObject(String key) {
        throw new UnsupportedOperationException();
    }

    public String print(int indent) {
        throw new UnsupportedOperationException();
    }
}

class BucketLeaf extends Component {

    String key;

    public BucketLeaf(String key) {
        this.key = key;
    }

    @Override
    public String print(int indent) {
        String indentation = "";
        for (int i = 0; i < indent; i++) {
            indentation += "    ";
        }
        return indentation + key;
    }
}

class Bucket extends Component {

    String key;
    Map<String, Component> components;

    public Bucket(String key) {
        this.key = key;
        components = new LinkedHashMap<>();
    }

    @Override
    public void addObject(String key) {

        if (!key.contains("/")) {
            // Base case: it's just a file, add it as a leaf
            components.put(key, new BucketLeaf(key));
            return;
        }

        String firstPart = key.substring(0, key.indexOf("/"));
        String rest = key.substring(key.indexOf("/") + 1);

        if (!components.containsKey(firstPart)) {
            components.put(firstPart, new Bucket(firstPart));
        }

        components.get(firstPart).addObject(rest);
    }

    @Override
    public void removeObject(String key) {

        if (!key.contains("/")) {
            components.remove(key);
            return;
        }

        String firstPart = key.substring(0, key.indexOf("/"));
        String rest = key.substring(key.indexOf("/") + 1);

        if (components.containsKey(firstPart)) {
            components.get(firstPart).removeObject(rest);

            Bucket bucket = (Bucket) components.get(firstPart);
            if (bucket.components.isEmpty()) {
                components.remove(firstPart);
            }
        }

    }

    @Override
    public String print(int indent) {
        StringBuilder sb = new StringBuilder();
        String indentation = "";
        for (int i = 0; i < indent; i++) {
            indentation += "    ";
        }

        sb.append(indentation).append(key).append("/");

        if (!components.isEmpty()) {
            sb.append("\n");

            List<Component> componentsList = new ArrayList<>(components.values());
            for (int i = 0; i < componentsList.size(); i++) {
                sb.append(componentsList.get(i).print(indent + 1));
                if (i < componentsList.size() - 1) {
                    sb.append("\n");
                }
            }
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return print(0) + "\n";
    }
}

public class BucketTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // bucket name is fixed
        Bucket bucket = new Bucket("bucket");

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 2);
            String command = parts[0];

            if (command.equalsIgnoreCase("ADD")) {
                bucket.addObject(parts[1]);
            } else if (command.equalsIgnoreCase("REMOVE")) {
                bucket.removeObject(parts[1]);
            } else if (command.equalsIgnoreCase("PRINT")) {
                System.out.print(bucket);
            }
        }
    }
}


