package labs.lab9.task2;


import java.util.*;

interface XMLComponent { // Component
    void addAttribute(String attribute, String value);
    String print(int indent);
}

abstract class XMLBase implements XMLComponent { // Base class with common functionality
    protected String tag;
    protected Map<String, String> attributes;

    public XMLBase(String tag) {
        this.tag = tag;
        this.attributes = new LinkedHashMap<>();
    }

    @Override
    public void addAttribute(String attribute, String value) {
        attributes.put(attribute, value);
    }

    protected String getAttributesString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            sb.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return print(0);
    }
}

class XMLLeaf extends XMLBase { // Leaf
    private String value;

    public XMLLeaf(String tag, String value) {
        super(tag);
        this.value = value;
    }

    @Override
    public String print(int indent) {
        String indentStr = "    ".repeat(indent);
        return indentStr + "<" + tag + getAttributesString() + ">" + value + "</" + tag + ">";
    }
}

class XMLComposite extends XMLBase { // Composite
    private List<XMLComponent> children;

    public XMLComposite(String name) {
        super(name);
        children = new ArrayList<>();
    }

    public void addComponent(XMLComponent component) {
        children.add(component);
    }

    @Override
    public String print(int indent) {
        StringBuilder sb = new StringBuilder();
        String indentStr = "    ".repeat(indent);

        // Opening tag
        sb.append(indentStr).append("<").append(tag).append(getAttributesString()).append(">\n");

        // Add children
        for (XMLComponent child : children) {
            sb.append(child.print(indent + 1)).append("\n");
        }

        // Closing tag
        sb.append(indentStr).append("</").append(tag).append(">");

        return sb.toString();
    }
}

public class XMLTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = sc.nextInt();
        XMLComponent component = new XMLLeaf("student", "Trajce Trajkovski");
        component.addAttribute("type", "redoven");
        component.addAttribute("program", "KNI");

        XMLComposite composite = new XMLComposite("name");
        composite.addComponent(new XMLLeaf("first-name", "trajce"));
        composite.addComponent(new XMLLeaf("last-name", "trajkovski"));
        composite.addAttribute("type", "redoven");
        component.addAttribute("program", "KNI");

        if (testCase==1) {
            //TODO Print the component object
            System.out.println(component);
        } else if(testCase==2) {
            //TODO print the composite object
            System.out.println(composite);
        } else if (testCase==3) {
            XMLComposite main = new XMLComposite("level1");
            main.addAttribute("level","1");
            XMLComposite lvl2 = new XMLComposite("level2");
            lvl2.addAttribute("level","2");
            XMLComposite lvl3 = new XMLComposite("level3");
            lvl3.addAttribute("level","3");
            lvl3.addComponent(component);
            lvl2.addComponent(lvl3);
            lvl2.addComponent(composite);
            lvl2.addComponent(new XMLLeaf("something", "blabla"));
            main.addComponent(lvl2);
            main.addComponent(new XMLLeaf("course", "napredno programiranje"));

            //TODO print the main object
            System.out.println(main);
        }
    }
}
