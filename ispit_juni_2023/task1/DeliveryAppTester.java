package ispit_juni_2023.task1;

import java.util.*;

/*
YOUR CODE HERE
DO NOT MODIFY THE interfaces and classes below!!!
*/

interface Location {
    int getX();

    int getY();

    default int distance(Location other) {
        int xDiff = Math.abs(getX() - other.getX());
        int yDiff = Math.abs(getY() - other.getY());
        return xDiff + yDiff;
    }
}

class LocationCreator {
    public static Location create(int x, int y) {
        return new Location() {
            @Override
            public int getX() {
                return x;
            }

            @Override
            public int getY() {
                return y;
            }
        };
    }
}

class DeliveryPerson {
    String id;
    String name;
    Location currentLocation;
    int deliveryCount;
    float totalEarnings;

    public DeliveryPerson(String id, String name, Location currentLocation) {
        this.id = id;
        this.name = name;
        this.currentLocation = currentLocation;
        this.deliveryCount = 0;
        this.totalEarnings = 0;
    }

    public String getId() {
        return id;
    }

    public void addEarnings(float earnings) {
        this.totalEarnings += earnings;
    }

    public void incrementDeliveryCount() {
        this.deliveryCount++;
    }

    public void updateLocation(Location newLocation) {
        this.currentLocation = newLocation;
    }

    public float getTotalEarnings() {
        return totalEarnings;
    }

    public float calculateEarnings(int distance) {
        return 90 + (distance / 10) * 10;
    }

    @Override
    public String toString() {
        float avgFee = deliveryCount > 0 ? totalEarnings / deliveryCount : 0.0f;
        return String.format("ID: %s Name: %s Total deliveries: %d Total delivery fee: %.2f Average delivery fee: %.2f",
                id, name, deliveryCount, totalEarnings, avgFee);
    }
}

class Restaurant {
    String id;
    String name;
    Location location;
    List<Float> orders;

    public Restaurant(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.orders = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void addOrder(float cost) {
        orders.add(cost);
    }

    public double averagePrice() {
        if (orders.isEmpty()) return 0.0;

        return orders.stream()
                .mapToDouble(Float::doubleValue)
                .average()
                .getAsDouble();
    }

    public double totalAmount() {
        return orders.stream()
                .mapToDouble(Float::doubleValue)
                .sum();
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total orders: %d Total amount earned: %.2f Average amount earned: %.2f",
                id, name, orders.size(), totalAmount(), averagePrice());
    }
}

class User {
    String id;
    String name;
    float totalSpent;
    int orderCount;
    Map<String, Location> addresses;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
        this.addresses = new HashMap<>();
        this.totalSpent = 0;
        this.orderCount = 0;
    }

    public String getId() {
        return id;
    }

    public void addAddress(String addressName, Location location) {
        addresses.put(addressName, location);
    }

    public void addSpent(float amount) {
        this.totalSpent += amount;
        this.orderCount++;
    }

    public float getTotalSpent() {
        return totalSpent;
    }

    @Override
    public String toString() {
        float avgSpent = orderCount > 0 ? totalSpent / orderCount : 0.0f;
        return String.format("ID: %s Name: %s Total orders: %d Total amount spent: %.2f Average amount spent: %.2f",
                id, name, orderCount, totalSpent, avgSpent);
    }
}

class DeliveryApp {
    String name;
    Map<String, DeliveryPerson> deliveryPeople;
    Map<String, Restaurant> restaurants;
    Map<String, User> users;

    public DeliveryApp(String name) {
        this.name = name;
        this.deliveryPeople = new HashMap<>();
        this.restaurants = new HashMap<>();
        this.users = new HashMap<>();
    }

    public void registerDeliveryPerson(String id, String name, Location currentLocation) {
        deliveryPeople.put(id, new DeliveryPerson(id, name, currentLocation));
    }

    public void addRestaurant(String id, String name, Location location) {
        restaurants.put(id, new Restaurant(id, name, location));
    }

    public void addUser(String id, String name) {
        users.put(id, new User(id, name));
    }

    public void addAddress(String id, String addressName, Location location) {
        User user = users.get(id);
        if (user != null) {
            user.addAddress(addressName, location);
        }
    }

    public void orderFood(String userId, String userAddressName, String restaurantId, float cost) {
        User user = users.get(userId);
        Restaurant restaurant = restaurants.get(restaurantId);

        if (user == null || restaurant == null) return;

        Location userAddress = user.addresses.get(userAddressName);
        if (userAddress == null) return;

        // Find closest delivery person using streams
        DeliveryPerson closestPerson = deliveryPeople.values().stream()
                .min(Comparator.comparingInt((DeliveryPerson dp) -> dp.currentLocation.distance(restaurant.location))
                        .thenComparingInt(dp -> dp.deliveryCount))
                .orElse(null);

        if (closestPerson == null) return;

        // Calculate and update
        int deliveryDistance = closestPerson.currentLocation.distance(restaurant.location);
        float earnings = closestPerson.calculateEarnings(deliveryDistance);

        closestPerson.addEarnings(earnings);
        closestPerson.incrementDeliveryCount();
        closestPerson.updateLocation(userAddress);

        user.addSpent(cost);
        restaurant.addOrder(cost);
    }

    public void printUsers() {
        users.values().stream()
                .sorted(Comparator.comparing(User::getTotalSpent, Comparator.reverseOrder())
                        .thenComparing(User::getId, Comparator.reverseOrder()))
                .forEach(System.out::println);
    }

    public void printRestaurants() {
        restaurants.values().stream()
                .sorted(Comparator.comparing(Restaurant::averagePrice, Comparator.reverseOrder())
                        .thenComparing(Restaurant::getId, Comparator.reverseOrder()))
                .forEach(System.out::println);
    }

    public void printDeliveryPeople() {
        deliveryPeople.values().stream()
                .sorted(Comparator.comparing(DeliveryPerson::getTotalEarnings, Comparator.reverseOrder())
                        .thenComparing(DeliveryPerson::getId, Comparator.reverseOrder()))
                .forEach(System.out::println);
    }
}

public class DeliveryAppTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String appName = sc.nextLine();
        DeliveryApp app = new DeliveryApp(appName);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(" ");

            switch (parts[0]) {
                case "addUser":
                    app.addUser(parts[1], parts[2]);
                    break;
                case "registerDeliveryPerson":
                    app.registerDeliveryPerson(parts[1], parts[2],
                            LocationCreator.create(Integer.parseInt(parts[3]), Integer.parseInt(parts[4])));
                    break;
                case "addRestaurant":
                    app.addRestaurant(parts[1], parts[2],
                            LocationCreator.create(Integer.parseInt(parts[3]), Integer.parseInt(parts[4])));
                    break;
                case "addAddress":
                    app.addAddress(parts[1], parts[2],
                            LocationCreator.create(Integer.parseInt(parts[3]), Integer.parseInt(parts[4])));
                    break;
                case "orderFood":
                    app.orderFood(parts[1], parts[2], parts[3], Float.parseFloat(parts[4]));
                    break;
                case "printUsers":
                    app.printUsers();
                    break;
                case "printRestaurants":
                    app.printRestaurants();
                    break;
                default:
                    app.printDeliveryPeople();
                    break;
            }
        }
    }
}