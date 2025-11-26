package exercises_courses.task25.solution2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Discounts
 */

class Pair {

    private int discountPrice;
    private int orgPrice;

    public Pair(int discountPrice, int orgPrice) {
        this.discountPrice = discountPrice;
        this.orgPrice = orgPrice;
    }

    public int getDiscountPrice() {
        return discountPrice;
    }

    public int getOrgPrice() {
        return orgPrice;
    }

    public int getDiscountPercentage() {
        return (int) Math.floor(((float)(orgPrice - discountPrice) / orgPrice) * 100);
    }

    public int absolute() {
        return orgPrice - discountPrice;
    }
}

class Store {

    private String name;
    private List<Pair> products;

    public Store(String line) {
        products = new ArrayList<>();
        String[] parts = line.split("\\s+");
        this.name = parts[0];
        for (int i = 1; i < parts.length; i++) {
            String[] prices = parts[i].split(":");
            products.add(new Pair(Integer.parseInt(prices[0]), Integer.parseInt(prices[1])));
        }
    }

    public double avg() {
        return products.stream()
                .mapToDouble(Pair::getDiscountPercentage)
                .average()
                .orElse(0.0);
    }

    public int total() {
        return products.stream()
                .mapToInt(Pair::absolute)
                .sum();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append('\n');
        sb.append(String.format("Average discount: %.1f%%\n", avg()));
        sb.append(String.format("Total discount: %s\n", total()));
        products.stream()
                .sorted(Comparator.comparingDouble(Pair::getDiscountPercentage).reversed()
                        .thenComparing(p -> -p.absolute()))
                .map(p -> String.format( "%2d%% %s/%s\n", p.getDiscountPercentage(), p.getDiscountPrice(), p.getOrgPrice()))
                .forEach(sb::append);

        return sb.substring(0, sb.length() - 1); // za da se izbrise posledniot '/n'
    }

    public String getName() {
        return name;
    }
}

class Discounts {

    List<Store> stores;

    public Discounts() {
        this.stores = new ArrayList<>();
    }

    public int readStores(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
            stores.add(new Store(line));
        }
        return stores.size();
    }

    public List<Store> byAverageDiscount() {
        return stores.stream()
                .sorted(Comparator.comparingDouble(Store::avg).reversed()
                        .thenComparing(Store::getName))
                .limit(3)
                .collect(Collectors.toList());
    }

    public List<Store> byTotalDiscount() {
        return stores.stream()
                .sorted(Comparator.comparingDouble(Store::total)
                        .thenComparing(Store::getName))
                .limit(3)
                .collect(Collectors.toList());
    }
}

public class DiscountsTest {
    public static void main(String[] args) throws IOException {
        Discounts discounts = new Discounts();
        int stores = discounts.readStores(System.in);
        System.out.println("Stores read: " + stores);
        System.out.println("=== By average discount ===");
        discounts.byAverageDiscount().forEach(System.out::println);
        System.out.println("=== By total discount ===");
        discounts.byTotalDiscount().forEach(System.out::println);
    }
}
