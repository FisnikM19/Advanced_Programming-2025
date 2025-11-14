package exercises_courses.task17.solution2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class AmountNotAllowedException extends Exception {
    AmountNotAllowedException(int sum) {
        super("Receipt with amount " + sum + " is not allowed to be scanned");
    }
}

class ScannedReceipt implements Comparable<ScannedReceipt> {
    private final String id;
    private final int sum;
    private final double taxReturn;

    public ScannedReceipt(String id, int sum, double taxReturn) throws AmountNotAllowedException {
        if (sum > 30000) throw new AmountNotAllowedException(sum);
        this.id = id;
        this.sum = sum;
        this.taxReturn = taxReturn;
    }

    public double getTaxReturn() {
        return taxReturn;
    }

    @Override
    public String toString() {
        return String.format("%10s\t%10d\t%10.5f", id, sum, taxReturn);
    }

    @Override
    public int compareTo(ScannedReceipt other) {
        return Double.compare(taxReturn, other.taxReturn);
    }
}

class MojDDV {

    private List<ScannedReceipt> receipts;
    static final Map<String, Double> TAXES = Map.of(
            "A", 0.18d,
            "B", 0.05d,
            "V", 0d);

    public MojDDV() {
        this.receipts = new ArrayList<>();
    }

    public void readRecords(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
            String[] parts = line.split("\\s+");
            String id = parts[0];
            int totalSum = 0;
            double totalTax = 0;
            for (int i = 1; i < parts.length; i+=2) {
                int price = Integer.parseInt(parts[i]);
                String type = parts[i + 1];
                totalSum += price;
                totalTax += TAXES.get(type) * price * 0.15d;
            }

            try {
                receipts.add(new ScannedReceipt(id, totalSum, totalTax));
            } catch (AmountNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void printTaxReturns(OutputStream outputStream) {
        PrintWriter writer = new PrintWriter(outputStream);
        receipts.forEach(r -> {
            String outputString = r.toString();
            writer.println(outputString);
        });
        writer.flush();
    }

    double min() {
        return receipts.stream()
                .mapToDouble(ScannedReceipt::getTaxReturn)
                .min()
                .orElse(0.0);
    }

    double max() {
        return receipts.stream()
                .mapToDouble(ScannedReceipt::getTaxReturn)
                .max()
                .orElse(0.0);
    }

    double sum(){
        return receipts.stream()
                .mapToDouble(ScannedReceipt::getTaxReturn)
                .sum();
    }

    long count(){
        return receipts.size();
    }

    double average(){
        return receipts.stream()
                .mapToDouble(ScannedReceipt::getTaxReturn)
                .average()
                .orElse(0.0);
    }

    public void printStatistics (OutputStream outputStream) {
        PrintWriter writer = new PrintWriter(outputStream);

        writer.printf("min:\t%5.3f%n", min());
        writer.printf("max:\t%5.3f%n", max());
        writer.printf("sum:\t%5.3f%n", sum());
        writer.printf("count:\t%-5d%n", count());
        writer.printf("avg:\t%5.3f%n", average());

        writer.flush();
    }
}

public class MojDDVTest {

    public static void main(String[] args) throws IOException {

        MojDDV mojDDV = new MojDDV();

        System.out.println("===READING RECORDS FROM INPUT STREAM===");
        mojDDV.readRecords(System.in);

        System.out.println("===PRINTING TAX RETURNS RECORDS TO OUTPUT STREAM ===");
        mojDDV.printTaxReturns(System.out);

        System.out.println("===PRINTING SUMMARY STATISTICS FOR TAX RETURNS TO OUTPUT STREAM===");
        mojDDV.printStatistics(System.out);

    }
}
