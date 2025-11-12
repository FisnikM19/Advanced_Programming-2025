package labs.lab1.task2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

enum TYPE {
    POINT,
    CIRCLE
}

enum DIRECTION {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

// Custom Exceptions
class ObjectCanNotBeMovedException extends Exception {
    public ObjectCanNotBeMovedException(int x, int y) {
        super(String.format("Point (%d,%d) is out of bounds", x, y));
    }
}

class MovableObjectNotFittableException extends Exception {
    public MovableObjectNotFittableException(String message) {
        super(message);
    }
}

interface Movable {
    void moveUp() throws ObjectCanNotBeMovedException;
    void moveDown() throws ObjectCanNotBeMovedException;
    void moveRight() throws ObjectCanNotBeMovedException;
    void moveLeft() throws ObjectCanNotBeMovedException;
    int getCurrentXPosition();
    int getCurrentYPosition();
    TYPE getType();
}

class MovablePoint implements Movable {
    private int x;
    private int y;
    private int xSpeed;
    private int ySpeed;

    public MovablePoint(int x, int y, int xSpeed, int ySpeed) {
        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    @Override
    public void moveUp() throws ObjectCanNotBeMovedException {
        int newY = y + ySpeed;
        if (newY > MovablesCollection.getY_MAX()) {
            throw new ObjectCanNotBeMovedException(x, newY);
        }
        y = newY;
    }

    @Override
    public void moveDown() throws ObjectCanNotBeMovedException {
        int newY = y - ySpeed;
        if (newY < 0) {
            throw new ObjectCanNotBeMovedException(x, newY);
        }
        y = newY;
    }

    @Override
    public void moveRight() throws ObjectCanNotBeMovedException {
        int newX = x + xSpeed;
        if (newX > MovablesCollection.getX_MAX()) {
            throw new ObjectCanNotBeMovedException(newX, y);
        }
        x = newX;
    }

    @Override
    public void moveLeft() throws ObjectCanNotBeMovedException {
        int newX = x - xSpeed;
        if (newX < 0) {
            throw new ObjectCanNotBeMovedException(newX, y);
        }
        x = newX;
    }

    @Override
    public int getCurrentXPosition() {
        return x;
    }

    @Override
    public int getCurrentYPosition() {
        return y;
    }

    @Override
    public TYPE getType() {
        return TYPE.POINT;
    }

    @Override
    public String toString() {

        return String.format("Movable point with coordinates (%d,%d)", x, y);
    }
}

class MovableCircle implements Movable {
    private int radius;
    MovablePoint center;

    public MovableCircle(int radius, MovablePoint center) {
        this.radius = radius;
        this.center = center;
    }

    @Override
    public void moveUp() throws ObjectCanNotBeMovedException {
        center.moveUp();
    }

    @Override
    public void moveDown() throws ObjectCanNotBeMovedException {
        center.moveDown();
    }

    @Override
    public void moveRight() throws ObjectCanNotBeMovedException {
        center.moveRight();
    }

    @Override
    public void moveLeft() throws ObjectCanNotBeMovedException {
        center.moveLeft();
    }

    @Override
    public int getCurrentXPosition() {
        return center.getCurrentXPosition();
    }

    @Override
    public int getCurrentYPosition() {
        return center.getCurrentYPosition();
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public TYPE getType() {
        return TYPE.CIRCLE;
    }

    @Override
    public String toString() {
        return String.format("Movable circle with center coordinates (%d,%d) and radius %d",
                getCurrentXPosition(), getCurrentYPosition(), radius);
    }
}

class MovablesCollection {
    private List<Movable> movables;
    private static int X_MAX;
    private static int Y_MAX;

    public MovablesCollection(int x_MAX, int y_MAX) {
        MovablesCollection.X_MAX = x_MAX;
        MovablesCollection.Y_MAX = y_MAX;
        this.movables = new ArrayList<>();
    }

    public void addMovableObject(Movable m) throws MovableObjectNotFittableException {
        if (m instanceof MovableCircle) {
            MovableCircle circle = (MovableCircle) m;
            int x = circle.getCurrentXPosition();
            int y = circle.getCurrentYPosition();
            int r = circle.getRadius();

            // Check if entire circle fits within bounds
            if (x - r < 0 || x + r > X_MAX || y - r < 0 || y + r > Y_MAX) {
                throw new MovableObjectNotFittableException(
                        String.format("Movable circle with center (%d,%d) and radius %d can not be fitted into the collection",
                                x, y, r)
                );
            }
        } else {
            // For points
            int x = m.getCurrentXPosition();
            int y = m.getCurrentYPosition();

            if (x < 0 || x > X_MAX || y < 0 || y > Y_MAX) {
                throw new MovableObjectNotFittableException(
                        String.format("Movable point with coordinates (%d,%d) can not be fitted into the collection",
                                x, y)
                );
            }
        }
        movables.add(m);
    }

    public void moveObjectsFromTypeWithDirection (TYPE type, DIRECTION direction) {
        for (Movable m: movables) {
            if (m.getType() == type) {
                try {
                    switch (direction) {
                        case UP:
                            m.moveUp();
                            break;
                        case DOWN:
                            m.moveDown();
                            break;
                        case RIGHT:
                            m.moveRight();
                            break;
                        case LEFT:
                            m.moveLeft();
                            break;
                    }
                } catch (ObjectCanNotBeMovedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public static int getX_MAX() {
        return X_MAX;
    }

    public static void setxMax(int x_MAX) {
        MovablesCollection.X_MAX = x_MAX;
    }

    public static int getY_MAX() {
        return Y_MAX;
    }

    public static void setyMax(int y_MAX) {
        MovablesCollection.Y_MAX = y_MAX;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Collection of movable objects with size %d:\n", movables.size()));
        for (Movable m: movables) {
            sb.append(m.toString()).append("\n");
        }
        return sb.toString();
    }
}

public class CirclesTest {

    public static void main(String[] args) {

        System.out.println("===COLLECTION CONSTRUCTOR AND ADD METHOD TEST===");
        MovablesCollection collection = new MovablesCollection(100, 100);
        Scanner sc = new Scanner(System.in);
        int samples = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < samples; i++) {
            String inputLine = sc.nextLine();
            String[] parts = inputLine.split(" ");

            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int xSpeed = Integer.parseInt(parts[3]);
            int ySpeed = Integer.parseInt(parts[4]);

            try {


                if (Integer.parseInt(parts[0]) == 0) { //point
                    collection.addMovableObject(new MovablePoint(x, y, xSpeed, ySpeed));
                } else { //circle
                    int radius = Integer.parseInt(parts[5]);
                    collection.addMovableObject(new MovableCircle(radius, new MovablePoint(x, y, xSpeed, ySpeed)));
                }
            } catch (MovableObjectNotFittableException e) {
                System.out.println(e.getMessage());
            }

        }
        System.out.println(collection.toString());

        System.out.println("MOVE POINTS TO THE LEFT");
        collection.moveObjectsFromTypeWithDirection(TYPE.POINT, DIRECTION.LEFT);
        System.out.println(collection.toString());

        System.out.println("MOVE CIRCLES DOWN");
        collection.moveObjectsFromTypeWithDirection(TYPE.CIRCLE, DIRECTION.DOWN);
        System.out.println(collection.toString());

        System.out.println("CHANGE X_MAX AND Y_MAX");
        MovablesCollection.setxMax(90);
        MovablesCollection.setyMax(90);

        System.out.println("MOVE POINTS TO THE RIGHT");
        collection.moveObjectsFromTypeWithDirection(TYPE.POINT, DIRECTION.RIGHT);
        System.out.println(collection.toString());

        System.out.println("MOVE CIRCLES UP");
        collection.moveObjectsFromTypeWithDirection(TYPE.CIRCLE, DIRECTION.UP);
        System.out.println(collection.toString());


    }


}
