package exercises_courses.task1;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Shape {
    private String id;
    private List<Integer> sizes;

    public Shape(String id, List<Integer> sizes) {
        this.id = id;
        this.sizes = sizes;
    }

    public String getId() {
        return id;
    }

    public List<Integer> getSizes() {
        return sizes;
    }
}

class ShapesApplication {

    List<Shape> shapes;
    public ShapesApplication() {
        shapes = new ArrayList<>();
    }

    public int readCanvases(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        int totalSquares = 0;

        while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
            String[] parts = line.split("\\s+");
            String canvas_id = parts[0];

            // Create a NEW list for each canvas
            List<Integer> sizes = new ArrayList<>();
            for (int i = 1; i < parts.length; i++) {
                sizes.add(Integer.parseInt(parts[i]));
            }
            shapes.add(new Shape(canvas_id, sizes));
            totalSquares += sizes.size();
        }

        return totalSquares;
    }

    public void printLargestCanvasTo(PrintStream out) {
        PrintWriter writer = new PrintWriter(out);

        int maxPerimeter = Integer.MIN_VALUE;
        String maxCanva = "";
        int squaresCount = 0;

        for (Shape shape: shapes) {
            // Calculate perimeter: each square contributes 4 * side_length
            int perimeter = shape.getSizes().stream()
                    .mapToInt(size -> size * 4)
                    .sum();

            if (perimeter > maxPerimeter) {
                maxPerimeter = perimeter;
                maxCanva = shape.getId();
                squaresCount = shape.getSizes().size();
            }
        }

        writer.print(maxCanva + " " + squaresCount + " " + maxPerimeter);
        writer.flush();

    }
}

public class Shapes1Test {

    public static void main(String[] args) throws IOException {
        ShapesApplication shapesApplication = new ShapesApplication();

        System.out.println("===READING SQUARES FROM INPUT STREAM===");
        System.out.println(shapesApplication.readCanvases(System.in));
        System.out.println("===PRINTING LARGEST CANVAS TO OUTPUT STREAM===");
        shapesApplication.printLargestCanvasTo(System.out);

    }
}