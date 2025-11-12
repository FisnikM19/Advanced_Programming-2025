package exercises_courses.task14;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

class InvalidPositionException extends Exception {
    public InvalidPositionException(int pos) {
        super(String.format("Invalid position %d, alredy taken!", pos));
    }
}

class Component {
    private String color;
    private int weight;
    List<Component> list;

    public Component(String color, int weight) {
        this.color = color;
        this.weight = weight;
        this.list = new ArrayList<>();
    }

    public void addComponent(Component component) {
        list.add(component);
        list.sort(Comparator.comparing(Component::getWeight)
                .thenComparing(Component::getColor));
    }

    public String getColor() {
        return color;
    }

    public int getWeight() {
        return weight;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

class Window {
    private String name;
    List<Component> components;

    public Window(String name) {
        this.name = name;
        components = new ArrayList<>();
    }

    public void addComponent(int position, Component component) throws InvalidPositionException{

        if (position < components.size() && components.get(position) != null) {
            throw new InvalidPositionException(position);
        }
        // fill holes
        while (components.size() <= position) {
            components.add(null);
        }

        components.set(position, component);
    }

    public void changeColor(int weight, String color) {
        for (Component c : components) {
            if (c != null) {
                changeColorRec(c, weight, color);
            }
        }
    }

    private void changeColorRec(Component c, int weight, String color) {
        if (c.getWeight() < weight)
            c.setColor(color);

        for (Component child : c.list)
            changeColorRec(child, weight, color);
    }

    public void swichComponents(int pos1, int pos2) {
        Component tmp = components.get(pos1);
        components.set(pos1, components.get(pos2));
        components.set(pos2, tmp);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WINDOW ").append(name).append("\n");

        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if (c != null) {
                sb.append(i).append(":")
                        .append(c.getWeight()).append(":").append(c.getColor()).append("\n");
                for (Component child : c.list)
                    printComponent(child, sb, 1);
            }
        }
        return sb.toString();
    }


    private void printComponent(Component c, StringBuilder sb, int level) {
        sb.append("---".repeat(level))
                .append(c.getWeight()).append(":").append(c.getColor()).append("\n");

        for (Component child : c.list)
            printComponent(child, sb, level + 1);
    }

}

public class ComponentTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        Window window = new Window(name);
        Component prev = null;
        while (true) {
            try {
                int what = scanner.nextInt();
                scanner.nextLine();
                if (what == 0) {
                    int position = scanner.nextInt();
                    window.addComponent(position, prev);
                } else if (what == 1) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev = component;
                } else if (what == 2) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                    prev = component;
                } else if (what == 3) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                } else if(what == 4) {
                    break;
                }

            } catch (InvalidPositionException e) {
                System.out.println(e.getMessage());
            }
            scanner.nextLine();
        }

        System.out.println("=== ORIGINAL WINDOW ===");
        System.out.println(window);
        int weight = scanner.nextInt();
        scanner.nextLine();
        String color = scanner.nextLine();
        window.changeColor(weight, color);
        System.out.println(String.format("=== CHANGED COLOR (%d, %s) ===", weight, color));
        System.out.println(window);
        int pos1 = scanner.nextInt();
        int pos2 = scanner.nextInt();
        System.out.println(String.format("=== SWITCHED COMPONENTS %d <-> %d ===", pos1, pos2));
        window.swichComponents(pos1, pos2);
        System.out.println(window);
    }
}

