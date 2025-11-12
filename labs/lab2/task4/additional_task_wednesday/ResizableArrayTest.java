package labs.lab2.task4.additional_task_wednesday;

import java.util.Arrays;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Why @SuppressWarnings("unchecked") is necessary:
 * - The cast (T[]) new Object[10] is technically unsafe from Java's perspective
 * - However, it's safe in practice because you control what goes into the array through addElement(T element)
 * - This is exactly what Java's own ArrayList does internally
 * - The annotation just tells the compiler "I know what I'm doing, suppress this warning"
 *
 * This is a well-known limitation of Java generics with arrays,
 * and using @SuppressWarnings("unchecked") in this specific case is the standard, accepted practice.
 */

class ResizableArray<T> {

    private T[] elements;
    private int size;

    @SuppressWarnings("unchecked")
    public ResizableArray() {
        this.elements = (T[]) new Object[10];
        this.size = 0;
    }

    public void addElement(T element) {
        if (size == elements.length) {
            elements = Arrays.copyOf(elements, elements.length * 2);
        }
        elements[size++] = element;
    }

    public boolean removeElement(T element) {
        int idx = -1;
        for (int i = 0; i < size; i++) {
            if (elements[i].equals(element)) {
                idx = i;
                break;
            }
        }
        if (idx == -1) return false;

        // Shifting elements left in place
        for (int i = idx; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }
        elements[--size] = null; // Clear the last element and decrement size

        return true;
    }

    public boolean contains(T element) {
        for (int i = 0; i < size; i++) {
            if (elements[i].equals(element)) return true;
        }
        return false;
    }

    public Object[] toArray() {
        return Arrays.copyOf(elements, size);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int count() {
        return size;
    }

    public T elementAt(int idx) throws ArrayIndexOutOfBoundsException{
        if (idx < 0 || idx >= size) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return elements[idx];
    }

    public static <T> void copyAll(ResizableArray<? super T> dest, ResizableArray<? extends T> src) {
        int originalSize = src.size;
        for (int i = 0; i < originalSize; i++) {
            dest.addElement(src.elements[i]);
        }
        /**
         * If we had: for (int i = 0; i < src.size; i++){}
         * then: As you add elements, src.size grows (because dest and src are the same)
         * The loop never ends, causing infinite growth until memory runs out!
         */
    }

    //TODO: START
    // Example usage:
    // Integer sum = ResizableArray.reduce(arr, 0, (acc, elem) -> acc + elem);
    // String concat = ResizableArray.reduce(strArr, "", (acc, elem) -> acc + elem);

    public static <T> T reduce(ResizableArray<T> source, T identity, BinaryOperator<T> accumulator) {
        T result = identity;

        for (int i = 0; i < source.size; i++) {
            result = accumulator.apply(result, source.elements[i]);
        }

        return result;
    }

    public static <T>ResizableArray<T> copyIf(ResizableArray<T> source, Predicate<T> predicate) {
        ResizableArray<T> newArray = new ResizableArray<>();

        for (int i = 0; i < source.size; i++) {
            if (predicate.test(source.elements[i])) {
                newArray.addElement(source.elements[i]);
            }
        }

        return newArray;
    }

    public static <T, R> ResizableArray<R> map(ResizableArray<T> source, Function<T, R> mapper) {
        ResizableArray<R> newArray = new ResizableArray<>();

        for (int i = 0; i < source.size; i++) {
            newArray.addElement(mapper.apply(source.elements[i]));
        }

        return newArray;
    }

    //TODO: DONE


    @Override
    public String toString() {
        return "ResizableArray{" +
                "elements=" + Arrays.toString(elements) +
                ", size=" + size +
                '}';
    }
}

class IntegerArray extends ResizableArray<Integer> {



    public IntegerArray() {
        super();
    }

    public double sum() {
        double s = 0;
        for (int i = 0; i < count(); i++) {
            s += elementAt(i);
        }
        return s;
    }

    public double mean() { // average
        return sum() / count();
    }

    public int countNonZero() {
        int cnt = 0;
        for (int i = 0; i < count(); i++) {
            if (!elementAt(i).equals(0)) {
                cnt++;
            }
        }
        return cnt;
    }

    public IntegerArray distinct() {
        IntegerArray result = new IntegerArray();

        for (int i = 0; i < count(); i++) {
            Integer current = elementAt(i);
            if (!result.contains(current)) {
                result.addElement(current);
            }
        }

        return result;
    }

    public IntegerArray increment(int offset) {
        IntegerArray result = new IntegerArray();

        for (int i = 0; i < count(); i++) {
            result.addElement(elementAt(i) + offset);
        }

        return result;
    }
}

public class ResizableArrayTest {

    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int test = jin.nextInt();
        if ( test == 0 ) { //test ResizableArray on ints
            ResizableArray<Integer> a = new ResizableArray<Integer>();
            System.out.println(a.count());
            int first = jin.nextInt();
            a.addElement(first);
            System.out.println(a.count());
            int last = first;
            while ( jin.hasNextInt() ) {
                last = jin.nextInt();
                a.addElement(last);
            }
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(a.removeElement(first));
            System.out.println(a.contains(first));
            System.out.println(a.count());
        }
        if ( test == 1 ) { //test ResizableArray on strings
            ResizableArray<String> a = new ResizableArray<String>();
            System.out.println(a.count());
            String first = jin.next();
            a.addElement(first);
            System.out.println(a.count());
            String last = first;
            for ( int i = 0 ; i < 4 ; ++i ) {
                last = jin.next();
                a.addElement(last);
            }
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(a.removeElement(first));
            System.out.println(a.contains(first));
            System.out.println(a.count());
            ResizableArray<String> b = new ResizableArray<String>();
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
            System.out.println(b.removeElement(first));
            System.out.println(b.contains(first));
            System.out.println(b.removeElement(first));
            System.out.println(b.contains(first));

            System.out.println(a.removeElement(first));
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
        }
        if ( test == 2 ) { //test IntegerArray
            IntegerArray a = new IntegerArray();
            System.out.println(a.isEmpty());
            while ( jin.hasNextInt() ) {
                a.addElement(jin.nextInt());
            }
            jin.next();
            System.out.println(a.sum());
            System.out.println(a.mean());
            System.out.println(a.countNonZero());
            System.out.println(a.count());
            IntegerArray b = a.distinct();
            System.out.println(b.sum());
            IntegerArray c = a.increment(5);
            System.out.println(c.sum());
            if ( a.sum() > 100 )
                ResizableArray.copyAll(a, a);
            else
                ResizableArray.copyAll(a, b);
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.contains(jin.nextInt()));
            System.out.println(a.contains(jin.nextInt()));
        }
        if ( test == 3 ) { //test insanely large arrays
            LinkedList<ResizableArray<Integer>> resizable_arrays = new LinkedList<ResizableArray<Integer>>();
            for ( int w = 0 ; w < 500 ; ++w ) {
                ResizableArray<Integer> a = new ResizableArray<Integer>();
                int k =  2000;
                int t =  1000;
                for ( int i = 0 ; i < k ; ++i ) {
                    a.addElement(i);
                }

                a.removeElement(0);
                for ( int i = 0 ; i < t ; ++i ) {
                    a.removeElement(k-i-1);
                }
                resizable_arrays.add(a);
            }
            System.out.println("You implementation finished in less then 3 seconds, well done!");
        }
        //TODO:START
        if (test == 4) {
            ResizableArray<Integer> numbers = new ResizableArray<>();
            for (int i = 1; i <= 10; i++) {
                numbers.addElement(i);
            }

        // Reduce to sum
            Integer sum = ResizableArray.reduce(numbers, 0, (a, b) -> a + b);  // 6

        // Copy only even numbers
            ResizableArray<Integer> evens = ResizableArray.copyIf(numbers, n -> n % 2 == 0);

        // Map to strings
            ResizableArray<String> strings = ResizableArray.map(numbers, n -> "Num: " + n);

            System.out.println(sum);
            System.out.println(evens);
            System.out.println(strings);
        }
        //TODO:DONE
    }

}
