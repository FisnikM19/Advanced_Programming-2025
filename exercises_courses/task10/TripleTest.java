package exercises_courses.task10;

import java.util.Scanner;

class Triple<T extends Number>{
    private T num1;
    private T num2;
    private T num3;

    public Triple(T num1, T num2, T num3) {
        this.num1 = num1;
        this.num2 = num2;
        this.num3 = num3;
    }

    public double max() {
        return Math.max(num1.doubleValue(), Math.max(num2.doubleValue(), num3.doubleValue()));
    }

    public double average() {
        double sum = num1.doubleValue() + num2.doubleValue() + num3.doubleValue();
        return sum / 3;
    }

    public void sort() {
        double d1 = num1.doubleValue();
        double d2 = num2.doubleValue();
        double d3 = num3.doubleValue();

        // Sort the three values
        if (d1 > d2) {
            T temp = num1;
            num1 = num2;
            num2 = temp;
            // Update doubles as well
            double tempD = d1;
            d1 = d2;
            d2 = tempD;
        }
        if (d2 > d3) {
            T temp = num2;
            num2 = num3;
            num3 = temp;
            // Update doubles as well
            double tempD = d2;
            d2 = d3;
            d3 = tempD;
        }
        if (d1 > d2) {
            T temp = num1;
            num1 = num2;
            num2 = temp;
        }
    }

    @Override
    public String toString() {
        return String.format("%.2f %.2f %.2f", num1.doubleValue(), num2.doubleValue(), num3.doubleValue());
    }
}

public class TripleTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int a = scanner.nextInt();
        int b = scanner.nextInt();
        int c = scanner.nextInt();
        Triple<Integer> tInt = new Triple<Integer>(a, b, c);
        System.out.printf("%.2f\n", tInt.max());
        System.out.printf("%.2f\n", tInt.average());
        tInt.sort();
        System.out.println(tInt);
        float fa = scanner.nextFloat();
        float fb = scanner.nextFloat();
        float fc = scanner.nextFloat();
        Triple<Float> tFloat = new Triple<Float>(fa, fb, fc);
        System.out.printf("%.2f\n", tFloat.max());
        System.out.printf("%.2f\n", tFloat.average());
        tFloat.sort();
        System.out.println(tFloat);
        double da = scanner.nextDouble();
        double db = scanner.nextDouble();
        double dc = scanner.nextDouble();
        Triple<Double> tDouble = new Triple<Double>(da, db, dc);
        System.out.printf("%.2f\n", tDouble.max());
        System.out.printf("%.2f\n", tDouble.average());
        tDouble.sort();
        System.out.println(tDouble);
    }
}
