package exercises_courses.task15;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

class Measurement {
    private float temperature;
    private float wind;
    private float humidity;
    private float visibility;
    private Date date;

    public Measurement(float temperature, float wind, float humidity, float visibility, Date date) {
        this.temperature = temperature;
        this.wind = wind;
        this.humidity = humidity;
        this.visibility = visibility;
        this.date = date;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getWind() {
        return wind;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getVisibility() {
        return visibility;
    }

    public Date getDate() {
        return date;
    }
}

class WeatherStation {
    private int days;
    List<Measurement> measurements;

    public WeatherStation(int days) {
        this.days = days;
        measurements = new ArrayList<>();
    }

    public void addMeasurment(float temperature, float wind, float humidity, float visibility, Date date) {
        Measurement measurement = new Measurement(temperature, wind, humidity, visibility, date);

        // 1. Ignore measurements less than 2.5 minutes apart
        for (Measurement m: measurements) {
            long diff = Math.abs(date.getTime() - m.getDate().getTime());
            if (diff < 150000) { // 2.5 minutes in ms
                return; // ignore new measurement
            }
        }

        // 2. Add new measurement
        measurements.add(measurement);

        // 3. Remove older than X days relative to this new date
        long threshold = date.getTime() - (long) days * 24 * 60 * 60 * 1000;
        measurements.removeIf(m -> m.getDate().getTime() < threshold);
    }

    public int total() {
        return measurements.size();
    }

    public void status(Date from, Date to) {
        List<Measurement> inPeriod = measurements.stream()
                .filter(m -> !m.getDate().before(from) && !m.getDate().after(to))
                .sorted(Comparator.comparing(Measurement::getDate))
                .collect(Collectors.toList());

        if (inPeriod.isEmpty()) {
            throw new RuntimeException();
        }

        double avgTemperature = inPeriod.stream()
                .mapToDouble(Measurement::getTemperature)
                .average()
                .orElse(0);

        inPeriod.forEach(m -> System.out.printf("%.1f %.1f km/h %.1f%% %.1f km %s%n",
                m.getTemperature(),
                m.getWind(),
                m.getHumidity(),
                m.getVisibility(),
                m.getDate().toString().replace("UTC", "GMT")));

        System.out.printf("Average temperature: %.2f%n", avgTemperature);
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

// vashiot kod ovde