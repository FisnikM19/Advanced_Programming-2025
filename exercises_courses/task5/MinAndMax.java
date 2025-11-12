package exercises_courses.task5;

import java.util.Scanner;

// Make T extend Comparable<T> so you can compare elements:
class MinMax<T extends Comparable<T>> {
    private T minimum;
    private T maximum;
    private int totalCount;
    private int minCount;
    private int maxCount;


    public MinMax(){
        minimum = null;
        maximum = null;
        totalCount = 0;
        minCount = 0;
        maxCount = 0;
    }

    public void update(T element) {
        totalCount++;

        if (minimum == null || maximum == null) {
            minimum = element;
            maximum = element;
            minCount = 1;
            maxCount = 1;
            return;
        }

        if (element.compareTo(minimum) < 0) {
            minimum = element;
            minCount = 1;
        } else if (element.compareTo(minimum) == 0) {
            minCount++;
        }

        if (element.compareTo(maximum) > 0) {
            maximum = element;
            maxCount = 1;
        } else if (element.compareTo(maximum) == 0) {
            maxCount++;
        }


    }

    public T max() {
        return maximum;
    }

    public T min() {
        return minimum;
    }

    @Override
    public String toString() {
        int diffCount = totalCount - minCount - maxCount;

        return minimum + " " + maximum + " " + diffCount + "\n";
    }
}

public class MinAndMax {
    public static void main(String[] args) throws ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        MinMax<String> strings = new MinMax<String>();
        for(int i = 0; i < n; ++i) {
            String s = scanner.next();
            strings.update(s);
        }
        System.out.println(strings);
        MinMax<Integer> ints = new MinMax<Integer>();
        for(int i = 0; i < n; ++i) {
            int x = scanner.nextInt();
            ints.update(x);
        }
        System.out.println(ints);
    }
}