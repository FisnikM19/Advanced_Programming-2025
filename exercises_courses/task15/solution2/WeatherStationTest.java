package exercises_courses.task15.solution2;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class DataEntry implements Comparable<DataEntry> {
    float temperature;
    float humidity;
    float windVelocity;
    float visibility;
    Date date;

    public DataEntry
            (float temperature, float humidity, float windVelocity,
             float visibility, Date date) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.windVelocity = windVelocity;
        this.visibility = visibility;
        this.date = date;
    }

    @Override
    public int compareTo(DataEntry other) {
        if (date.equals(other.date)) return 0;
        return date.before(other.date) ? -1 : 1;
    }

    @Override
    public String toString() {
        return String.format(
                "%s %.1f km/h %.1f%% %.1f km %s",
                temperature, windVelocity, humidity, visibility, date.toString().replace("UTC", "GMT")
        );
    }
}

class WeatherStation {
    List<DataEntry> entries;
    int days;

    public WeatherStation(int days) {
        this.entries = new ArrayList<>();
        this.days = days;
    }

    public void addMeasurment(float temperature, float wind, float humidity, float visibility, Date date) {
        DataEntry entry = new DataEntry(temperature, humidity, wind, visibility, date);

        Instant oldestDateInstant = date.toInstant().minus(days, ChronoUnit.DAYS);

        while (!entries.isEmpty() && entries.get(0).date.before(Date.from(oldestDateInstant))) {
            entries.remove(0);
        }

        if (!entries.isEmpty() &&
                entries.get(entries.size() - 1).date
                .toInstant()
                .plus(150, ChronoUnit.SECONDS)
                .isAfter(date.toInstant())) {
                    return;
        }
        entries.add(entry);
    }

    public int total(){
        return entries.size();
    }

    public void status(Date from, Date to) {
        Predicate<DataEntry> dataEntryPredicate = e -> !e.date.before(from) && !e.date.after(to);

        List<DataEntry> filteredEntries = entries.stream()
                .filter(dataEntryPredicate)
                .sorted()
                .collect(Collectors.toList());

        if (filteredEntries.isEmpty()) throw new RuntimeException();

        float avg = 0.0f;
        for (DataEntry entry: filteredEntries) {
            avg  += entry.temperature;
            System.out.println(entry);
        }

        System.out.printf("Average temperature: %.2f%n", avg / filteredEntries.size());
    }
}

public class WeatherStationTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        int n = scanner.nextInt();
        scanner.nextLine();
        WeatherStation ws = new WeatherStation(n);
        while (true) {
            String line = scanner.nextLine();
            if (line.equals("=====")) {
                break;
            }
            String[] parts = line.split(" ");
            float temp = Float.parseFloat(parts[0]);
            float wind = Float.parseFloat(parts[1]);
            float hum = Float.parseFloat(parts[2]);
            float vis = Float.parseFloat(parts[3]);
            line = scanner.nextLine();
            Date date = df.parse(line);
            ws.addMeasurment(temp, wind, hum, vis, date);
        }
        String line = scanner.nextLine();
        Date from = df.parse(line);
        line = scanner.nextLine();
        Date to = df.parse(line);
        scanner.close();
        System.out.println(ws.total());
        try {
            ws.status(from, to);
        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }
}