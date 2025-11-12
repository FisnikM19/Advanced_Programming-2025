package exercises_courses.task2.solution2;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// Exception class
class InvalidCanvasException extends Exception {
    public InvalidCanvasException(String id, double maxArea) {
        super(String.format("Canvas %s has a shape with area larger than %.2f", id, maxArea));
    }
}

// Abstract base class for geometric shapes
abstract class GeometricShape {
    protected int size;

    public GeometricShape(int size) {
        this.size = size;
    }

    public abstract double getArea();
    public abstract String getType();

    public int getSize() {
        return size;
    }
}

// Square class
class Square extends GeometricShape {
    public Square(int side) {
        super(side);
    }

    @Override
    public double getArea() {
        return size * size;
    }

    @Override
    public String getType() {
        return "S";
    }
}

// Circle class
class Circle extends GeometricShape {
    public Circle(int radius) {
        super(radius);
    }

    @Override
    public double getArea() {
        return size * size * Math.PI;
    }

    @Override
    public String getType() {
        return "C";
    }
}

// Canvas class - represents a window with multiple shapes
class Canvas {
    private String id;
    private List<GeometricShape> shapes;

    public Canvas(String id) {
        this.id = id;
        this.shapes = new ArrayList<>();
    }

    public void addShape(GeometricShape shape) {
        shapes.add(shape);
    }

    public String getId() {
        return id;
    }

    public List<GeometricShape> getShapes() {
        return shapes;
    }

    public double getTotalArea() {
        return shapes.stream()
                .mapToDouble(GeometricShape::getArea)
                .sum();
    }

    public int getTotalShapes() {
        return shapes.size();
    }

    public long getTotalCircles() {
        return shapes.stream()
                .filter(s -> s instanceof Circle)
                .count();
    }

    public long getTotalSquares() {
        return shapes.stream()
                .filter(s -> s instanceof Square)
                .count();
    }

    public double getMinArea() {
        return shapes.stream()
                .mapToDouble(GeometricShape::getArea)
                .min()
                .orElse(0.0);
    }

    public double getMaxArea() {
        return shapes.stream()
                .mapToDouble(GeometricShape::getArea)
                .max()
                .orElse(0.0);
    }

    public double getAverageArea() {
        return shapes.stream()
                .mapToDouble(GeometricShape::getArea)
                .average()
                .orElse(0.0);
    }

    @Override
    public String toString() {
        return String.format("%s %d %d %d %.2f %.2f %.2f",
                id,
                getTotalShapes(),
                getTotalCircles(),
                getTotalSquares(),
                getMinArea(),
                getMaxArea(),
                getAverageArea());
    }
}

// Main application class
class ShapesApplication {
    private double maxArea;
    private List<Canvas> canvases;

    public ShapesApplication(double maxArea) {
        this.maxArea = maxArea;
        this.canvases = new ArrayList<>();
    }

    public void readCanvases(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
            try {
                Canvas canvas = parseCanvas(line);
                canvases.add(canvas);
            } catch (InvalidCanvasException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private Canvas parseCanvas(String line) throws InvalidCanvasException {
        String[] parts = line.split("\\s+");
        String id = parts[0];
        Canvas canvas = new Canvas(id);

        // Parse type-size pairs
        for (int i = 1; i < parts.length; i += 2) {
            String type = parts[i];
            int size = Integer.parseInt(parts[i + 1]);

            GeometricShape shape = createShape(type, size);

            // Check if shape exceeds max area
            if (shape.getArea() > maxArea) {
                throw new InvalidCanvasException(id, maxArea);
            }

            canvas.addShape(shape);
        }

        return canvas;
    }

    private GeometricShape createShape(String type, int size) {
        if (type.equals("S")) {
            return new Square(size);
        } else if (type.equals("C")) {
            return new Circle(size);
        }
        throw new IllegalArgumentException("Unknown shape type: " + type);
    }

    public void printCanvases(OutputStream os) {
        PrintWriter writer = new PrintWriter(os);

        // Sort canvases by total area in descending order
        List<Canvas> sortedCanvases = canvases.stream()
                .sorted(Comparator.comparingDouble(Canvas::getTotalArea).reversed())
                .collect(Collectors.toList());

        for (Canvas canvas : sortedCanvases) {
            writer.println(canvas);
        }

        writer.flush();
    }
}

// Test class
public class Shapes2Test {
    public static void main(String[] args) throws IOException {
        ShapesApplication shapesApplication = new ShapesApplication(10000);

        System.out.println("===READING CANVASES AND SHAPES FROM INPUT STREAM===");
        shapesApplication.readCanvases(System.in);

        System.out.println("===PRINTING SORTED CANVASES TO OUTPUT STREAM===");
        shapesApplication.printCanvases(System.out);
    }
}
