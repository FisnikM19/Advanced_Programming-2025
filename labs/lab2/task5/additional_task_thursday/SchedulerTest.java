package labs.lab2.task5.additional_task_thursday;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class Timestamp<T> implements Comparable<Timestamp<?>> {
    private final LocalDateTime time;
    private final T element;

    public Timestamp(LocalDateTime time, T element) {
        this.time = time;
        this.element = element;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public T getElement() {
        return element;
    }

    @Override
    public int compareTo(Timestamp<?> t) {
        return this.time.compareTo(t.time);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Timestamp<?> timestamp = (Timestamp<?>) o;
        return Objects.equals(time, timestamp.time);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(time);
    }

    @Override
    public String toString() {
        return time.toString() + " " + element.toString();
    }
}

class Scheduler<T> {
    private List<Timestamp<T>> timestamps;

    public Scheduler() {
        this.timestamps = new ArrayList<>();
    }

    public void add(Timestamp<T> t) {
        timestamps.add(t);
    }

    public boolean remove(Timestamp<T> t) {
        return timestamps.remove(t);
    }

    public Timestamp<T> next() {
        LocalDateTime now = LocalDateTime.now();
        return timestamps.stream()
                .filter(t -> t.getTime().isAfter(now))
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    public Timestamp<T> last() {
        LocalDateTime now = LocalDateTime.now();
        return timestamps.stream()
                .filter(t -> t.getTime().isBefore(now))
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    public List<Timestamp<T>> getAll(LocalDateTime begin, LocalDateTime end) {
        return timestamps.stream()
                .filter(t -> t.getTime().isAfter(begin) && t.getTime().isBefore(end))
                .collect(Collectors.toList());
    }
    /**
     *next():
     * Gets the current time with LocalDateTime.now()
     * Filters timestamps that are after the current time (not yet passed)
     * Returns the minimum (earliest) one using Comparator.naturalOrder() which uses your compareTo method
     *
     * last():
     * Gets the current time
     * Filters timestamps that are before the current time (already passed)
     * Returns the maximum (most recent) one
     *
     * getAll(LocalDateTime begin, LocalDateTime end):
     * Filters timestamps where the time is strictly between begin and end
     * Uses isAfter(begin) and isBefore(end) to exclude the boundaries
     * Returns the filtered list
     */

    //TODO:START
    public static <T, R> Scheduler<R> map(Scheduler<T> source, Function<Timestamp<T>, Timestamp<R>> mapper, Predicate<Timestamp<T>> filter) {
        Scheduler<R> scheduler = new Scheduler<>();

        for (Timestamp<T> timestamp: source.timestamps) {
            if (filter.test(timestamp)) {
                scheduler.add(mapper.apply(timestamp));
            }
        }

        return scheduler;
    }

    public static <T> long countIf(Scheduler<T> source, Predicate<Timestamp<T>> predicate) {
        long count = 0;
        for (Timestamp<T> timestamp: source.timestamps) {
            if (predicate.test(timestamp)) {
                count++;
            }
        }

        return count;
    }

    public static <T> Scheduler<T> merge(Scheduler<? extends T> first, Scheduler<? extends T> second) {
        Scheduler<T> scheduler = new Scheduler<>();

        for (Timestamp<? extends  T> timestamp: first.timestamps) {
            scheduler.add(new Timestamp<>(timestamp.getTime(), timestamp.getElement()));
        }

        for (Timestamp<? extends T> timestamp: second.timestamps) {
            scheduler.add(new Timestamp<>(timestamp.getTime(), timestamp.getElement()));
        }

        return scheduler;
    }
//TODO: DONE

}

public class SchedulerTest {

    static final LocalDateTime TIME = LocalDateTime.of(2016, 10, 25, 10, 15);

    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if (k == 0) { //test Timestamp with String
            Timestamp<String> t = new Timestamp<>(TIME, jin.next());
            System.out.println(t);
            System.out.println(t.getTime());
            System.out.println(t.getElement());
        }
        if (k == 1) { //test Timestamp with ints
            Timestamp<Integer> t1 = new Timestamp<>(TIME, jin.nextInt());
            System.out.println(t1);
            System.out.println(t1.getTime());
            System.out.println(t1.getElement());
            Timestamp<Integer> t2 = new Timestamp<>(TIME.plusDays(10), jin.nextInt());
            System.out.println(t2);
            System.out.println(t2.getTime());
            System.out.println(t2.getElement());
            System.out.println(t1.compareTo(t2));
            System.out.println(t2.compareTo(t1));
            System.out.println(t1.equals(t2));
            System.out.println(t2.equals(t1));
        }
        if (k == 2) {//test Timestamp with String, complex
            Timestamp<String> t1 = new Timestamp<>(ofEpochMS(jin.nextLong()), jin.next());
            System.out.println(t1);
            System.out.println(t1.getTime());
            System.out.println(t1.getElement());
            Timestamp<String> t2 = new Timestamp<>(ofEpochMS(jin.nextLong()), jin.next());
            System.out.println(t2);
            System.out.println(t2.getTime());
            System.out.println(t2.getElement());
            System.out.println(t1.compareTo(t2));
            System.out.println(t2.compareTo(t1));
            System.out.println(t1.equals(t2));
            System.out.println(t2.equals(t1));
        }
        if (k == 3) { //test Scheduler with String
            Scheduler<String> scheduler = new Scheduler<>();
            LocalDateTime now = LocalDateTime.now();
            scheduler.add(new Timestamp<>(now.minusHours(2), jin.next()));
            scheduler.add(new Timestamp<>(now.minusHours(1), jin.next()));
            scheduler.add(new Timestamp<>(now.minusHours(4), jin.next()));
            scheduler.add(new Timestamp<>(now.plusHours(2), jin.next()));
            scheduler.add(new Timestamp<>(now.plusHours(4), jin.next()));
            scheduler.add(new Timestamp<>(now.plusHours(1), jin.next()));
            scheduler.add(new Timestamp<>(now.plusHours(5), jin.next()));
            System.out.println(scheduler.next().getElement());
            System.out.println(scheduler.last().getElement());
            List<Timestamp<String>> result = scheduler.getAll(now.minusHours(3), now.plusHours(4).plusMinutes(15));
            String out = result.stream()
                    .sorted()
                    .map(Timestamp::getElement)
                    .collect(Collectors.joining(", "));
            System.out.println(out);
        }
        if (k == 4) {//test Scheduler with ints complex
            Scheduler<Integer> scheduler = new Scheduler<>();
            int counter = 0;
            ArrayList<Timestamp<Integer>> forRemoval = new ArrayList<>();
            while (jin.hasNextLong()) {
                Timestamp<Integer> ti = new Timestamp<>(ofEpochMS(jin.nextLong()), jin.nextInt());
                if ((counter & 7) == 0) {
                    forRemoval.add(ti);
                }
                scheduler.add(ti);
                ++counter;
            }
            jin.next();

            while (jin.hasNextLong()) {
                LocalDateTime left = ofEpochMS(jin.nextLong());
                LocalDateTime right = ofEpochMS(jin.nextLong());
                List<Timestamp<Integer>> res = scheduler.getAll(left, right);
                Collections.sort(res);
                System.out.println(left + " <: " + print(res) + " >: " + right);
            }
            System.out.println("test");
            List<Timestamp<Integer>> res = scheduler.getAll(ofEpochMS(0), ofEpochMS(Long.MAX_VALUE));
            Collections.sort(res);
            System.out.println(print(res));
            forRemoval.forEach(scheduler::remove);
            res = scheduler.getAll(ofEpochMS(0), ofEpochMS(Long.MAX_VALUE));
            Collections.sort(res);
            System.out.println(print(res));
        }
    }

    private static LocalDateTime ofEpochMS(long ms) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.systemDefault());
    }

    private static <T> String print(List<Timestamp<T>> res) {
        if (res == null || res.size() == 0) return "NONE";
        return res.stream()
                .map(each -> each.getElement().toString())
                .collect(Collectors.joining(", "));
    }

}

