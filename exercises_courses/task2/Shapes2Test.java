package exercises_courses.task2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

//class InvalidCanvasException extends Exception {
//    public InvalidCanvasException(String id, double area) {
//        super(String.format("Canvas %s has a shape with area larger than %f", id, area));
//    }
//}

class Pair {
    private String type;
    private int size;

    public Pair(String type, int size) {
        this.type = type;
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public int getSize() {
        return size;
    }
}

class Shape {
    private String id;
    private List<Pair> pairs;

    public Shape(String id, List<Pair> pairs) {
        this.id = id;
        this.pairs = pairs;
    }

    public String getId() {
        return id;
    }

    public List<Pair> getPairs() {
        return pairs;
    }
}

class ShapesApplication {

    private double maxArea;
    List<Shape> shapes;

    public ShapesApplication(double maxArea) {
        this.maxArea = maxArea;
        shapes = new ArrayList<>();
    }

    public void readCanvases (InputStream inputStream) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
            String[] parts = line.split("\\s+");
            String id = parts[0];

            boolean invalidCanvas = false;
            List<Pair> pairs = new ArrayList<>();

            for (int i = 1; i < parts.length; i+=2) {
                String type = parts[i];
                int size = Integer.parseInt(parts[i + 1]);

                double area = 0.0;

                if (type.contains("S")) {
                    area += size * size;
                } else if (type.contains("C")) {
                    area += size * size * Math.PI;
                }

                // Check if this shape exceeds max area
                if (area > maxArea) {
                    System.out.println(String.format("Canvas %s has a shape with area larger than %.2f", id, maxArea));
                    invalidCanvas = true;
                    break;
                }

                Pair pair = new Pair(type, size);
                pairs.add(pair);
            }

            // Only add the canvas if all shapes are valid
            if (!invalidCanvas) {
                Shape shape = new Shape(id, pairs);
                shapes.add(shape);
            }
        }
    }

    public void printCanvases(OutputStream os) {

        PrintWriter writer = new PrintWriter(os);

        // Sort shapes by total area in descending order
        shapes.sort((s1, s2) -> Double.compare(getTotalArea(s2), getTotalArea(s1)));

        for (Shape shape: shapes) {
            String id = shape.getId();
            int totalShapes = shape.getPairs().size();
            int totalCircles = 0;
            int totalSquares = 0;
            double minArea = Double.MAX_VALUE;
            double maxArea = Double.MIN_VALUE;
            double sumArea = 0.0;

            // Calculate statistics for each canvas
            for (Pair pair: shape.getPairs()) {
                double area = 0.0;

                if (pair.getType().equals("S")) {
                    area = getAreaSquare(pair.getSize());
                    totalSquares++;
                } else if (pair.getType().equals("C")) {
                    area = getAreaCircle(pair.getSize());
                    totalCircles++;
                }

                sumArea += area;
                minArea = Math.min(minArea, area);
                maxArea = Math.max(maxArea, area);
            }

            double averageArea = sumArea / totalShapes;

            // Print in the required format
            writer.println(String.format("%s %d %d %d %.2f %.2f %.2f",
                    id, totalShapes, totalCircles, totalSquares,
                    minArea, maxArea, averageArea));
        }
        writer.flush();
    }

    public double getAreaSquare(int size) {
        return size * size;
    }

    public double getAreaCircle(int radius) {
        return radius*radius*Math.PI;
    }

    // Helper method to calculate total area of a canvas
    private double getTotalArea(Shape shape) {
        double total = 0.0;
        for (Pair pair : shape.getPairs()) {
            if (pair.getType().equals("S")) {
                total += getAreaSquare(pair.getSize());
            } else if (pair.getType().equals("C")) {
                total += getAreaCircle(pair.getSize());
            }
        }
        return total;
    }

}

public class Shapes2Test {

    public static void main(String[] args) throws IOException {

        ShapesApplication shapesApplication = new ShapesApplication(10000);

        System.out.println("===READING CANVASES AND SHAPES FROM INPUT STREAM===");
        shapesApplication.readCanvases(System.in);

        System.out.println("===PRINTING SORTED CANVASES TO OUTPUT STREAM===");
        shapesApplication.printCanvases(System.out);


    }
}