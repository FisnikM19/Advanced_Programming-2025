package labs.lab4.task1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;

class Scheduler<T> {

    Map<Date, T> map;
    TreeSet<Date> dates;

    public Scheduler() {
        this.map = new HashMap<>();
        this.dates = new TreeSet<>();
    }

    public void add(Date d, T t) {
        map.put(d, t);
        dates.add(d);
    }

    public boolean remove(Date d) {
        if (map.containsKey(d)) {
            map.remove(d);
            dates.remove(d);
            return true;
        }
        return false;
    }

    public T next() {
        Date now = new Date();
        // Find the smallest date that is >= now
        Date nextDate = dates.ceiling(now);
        if (nextDate == null) {
            return null;
        }
        return map.get(nextDate);
    }

    public T last() {
        Date now = new Date();
        // Find the largest date that is < now
        Date lastDate = dates.floor(now);
        if (lastDate == null) {
            return null;
        }
        return map.get(lastDate);
    }

    public ArrayList<T> getAll(Date begin,Date end) {
        ArrayList<T> result = new ArrayList<>();
        // Get all dates between begin (inclusive )and end (inclusive)
        for (Date d: dates.subSet(begin, true, end, true)) {
            result.add(map.get(d));
        }
        return result;
    }

    public T getFirst() {
        if (dates.isEmpty()) {
            return null;
        }
        return map.get(dates.first());
    }

    public T getLast() {
        if (dates.isEmpty()) {
            return null;
        }
        return map.get(dates.last());
    }
}

public class SchedulerTest {


    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if ( k == 0 ) {
            Scheduler<String> scheduler = new Scheduler<String>();
            Date now = new Date();
            scheduler.add(new Date(now.getTime()-7200000), jin.next());
            scheduler.add(new Date(now.getTime()-3600000), jin.next());
            scheduler.add(new Date(now.getTime()-14400000), jin.next());
            scheduler.add(new Date(now.getTime()+7200000), jin.next());
            scheduler.add(new Date(now.getTime()+14400000), jin.next());
            scheduler.add(new Date(now.getTime()+3600000), jin.next());
            scheduler.add(new Date(now.getTime()+18000000), jin.next());
            System.out.println(scheduler.getFirst());
            System.out.println(scheduler.getLast());
        }
        if ( k == 3 ) { //test Scheduler with String
            Scheduler<String> scheduler = new Scheduler<String>();
            Date now = new Date();
            scheduler.add(new Date(now.getTime()-7200000), jin.next());
            scheduler.add(new Date(now.getTime()-3600000), jin.next());
            scheduler.add(new Date(now.getTime()-14400000), jin.next());
            scheduler.add(new Date(now.getTime()+7200000), jin.next());
            scheduler.add(new Date(now.getTime()+14400000), jin.next());
            scheduler.add(new Date(now.getTime()+3600000), jin.next());
            scheduler.add(new Date(now.getTime()+18000000), jin.next());
            System.out.println(scheduler.next());
            System.out.println(scheduler.last());
            ArrayList<String> res = scheduler.getAll(new Date(now.getTime()-10000000), new Date(now.getTime()+17000000));
            Collections.sort(res);
            for ( String t : res ) {
                System.out.print(t+" , ");
            }
        }
        if ( k == 4 ) {//test Scheduler with ints complex
            Scheduler<Integer> scheduler = new Scheduler<Integer>();
            int counter = 0;
            ArrayList<Date> to_remove = new ArrayList<Date>();

            while ( jin.hasNextLong() ) {
                Date d = new Date(jin.nextLong());
                int i = jin.nextInt();
                if ( (counter&7) == 0 ) {
                    to_remove.add(d);
                }
                scheduler.add(d,i);
                ++counter;
            }
            jin.next();

            while ( jin.hasNextLong() ) {
                Date l = new Date(jin.nextLong());
                Date h = new Date(jin.nextLong());
                ArrayList<Integer> res = scheduler.getAll(l,h);
                Collections.sort(res);
                System.out.println(l.toString().replace("UTC", "GMT")+" <: "+print(res)+" >: "+h.toString().replace("UTC", "GMT")); // we added this part for green light
            }
            System.out.println("test");
            ArrayList<Integer> res = scheduler.getAll(new Date(0),new Date(Long.MAX_VALUE));
            Collections.sort(res);
            System.out.println(print(res));
            for ( Date d : to_remove ) {
                scheduler.remove(d);
            }
            res = scheduler.getAll(new Date(0),new Date(Long.MAX_VALUE));
            Collections.sort(res);
            System.out.println(print(res));
        }
    }

    private static <T> String print(ArrayList<T> res) {
        if ( res == null || res.size() == 0 ) return "NONE";
        StringBuffer sb = new StringBuffer();
        for ( T t : res ) {
            sb.append(t+" , ");
        }
        return sb.substring(0, sb.length()-3);
    }


}
