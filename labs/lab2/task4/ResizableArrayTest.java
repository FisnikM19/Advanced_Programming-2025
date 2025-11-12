package labs.lab2.task4;

import java.util.Arrays;
import java.util.Scanner;
import java.util.LinkedList;

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
    }

}
