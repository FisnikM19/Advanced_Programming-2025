package exercises_courses.task25;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class Store {
    String name;
    Map<Integer, Integer> products; // discountPrice -> originalPrice (using integers)

    public Store(String name, Map<Integer, Integer> products) {
        this.name = name;
        this.products = products;
    }

    public String getName() {
        return name;
    }

    // Calculate discount percentage for a single product
    public int calculateDiscountPercentage(int discountPrice, int originalPrice) {
        return (int) (((originalPrice - discountPrice) / (double) originalPrice) * 100);
    }

    // Calculate average discount across all products
    public double getAverageDiscount() {
        return products.entrySet().stream()
                .mapToDouble(entry -> calculateDiscountPercentage(entry.getKey(), entry.getValue()))
                .average()
                .orElse(0.0);
    }

    // Calculate total absolute discount
    public int getTotalDiscount() {
        return products.entrySet().stream()
                .mapToInt(entry -> entry.getValue() - entry.getKey())
                .sum();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n");
        sb.append(String.format("Average discount: %.1f%%\n", getAverageDiscount()));
        sb.append(String.format("Total discount: %d\n", getTotalDiscount()));

        // Sort products by discount percentage (desc), then by absolute discount (desc)
        products.entrySet().stream()
                .sorted((e1, e2) -> {
                    int discount1 = calculateDiscountPercentage(e1.getKey(), e1.getValue());
                    int discount2 = calculateDiscountPercentage(e2.getKey(), e2.getValue());
                    int cmp = Integer.compare(discount2, discount1); // descending
                    if (cmp == 0) {
                        int abs1 = e1.getValue() - e1.getKey();
                        int abs2 = e2.getValue() - e2.getKey();
                        return Integer.compare(abs2, abs1); // descending
                    }
                    return cmp;
                })
                .forEach(entry -> {
                    int discountPrice = entry.getKey();
                    int originalPrice = entry.getValue();
                    int percentage = calculateDiscountPercentage(discountPrice, originalPrice);
                    sb.append(String.format("%2d%% %d/%d\n", percentage, discountPrice, originalPrice));
                });

        return sb.substring(0, sb.length() - 1); // to remove the last '\n'
    }
}

class Discounts {
    List<Store> stores;

    public Discounts() {
        stores = new ArrayList<>();
    }

    public int readStores(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
            String[] parts = line.split("\\s+");
            String name = parts[0];

            // Create a NEW map for each store
            Map<Integer, Integer> prices = new HashMap<>();

            for (int i = 1; i < parts.length; i++) {
                String[] priceParts = parts[i].split(":");
                int discountPrice = Integer.parseInt(priceParts[0]);
                int originalPrice = Integer.parseInt(priceParts[1]);
                prices.put(discountPrice, originalPrice);
            }

            stores.add(new Store(name, prices));
        }
        return stores.size();
    }

    public List<Store> byAverageDiscount() {
        return stores.stream()
                .sorted((s1, s2) -> {
                    int cmp = Double.compare(s2.getAverageDiscount(), s1.getAverageDiscount()); // DESCENDING
                    if (cmp == 0) {
                        return s1.getName().compareTo(s2.getName()); // ascending by name
                    }
                    return cmp;
                })
                .limit(3)
                .collect(Collectors.toList());
    }

    public List<Store> byTotalDiscount() {
        return stores.stream()
                .sorted((s1, s2) -> {
                    int cmp = Integer.compare(s1.getTotalDiscount(), s2.getTotalDiscount()); // ascending
                    if (cmp == 0) {
                        return s1.getName().compareTo(s2.getName()); // ascending by name
                    }
                    return cmp;
                })
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