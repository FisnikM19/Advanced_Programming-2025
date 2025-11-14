package exercises_courses.task16;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class AmountNotAllowedException extends Exception {

    public AmountNotAllowedException(double amount) {
        super(String.format("Receipt with amount %.0f is not allowed to be scanned", amount));
    }
}

abstract class Taxes {

    protected double price;

    public Taxes(double price) {
        this.price = price;
    }

    public abstract String getType();

    public abstract double getTaxAmount();

    public double getPrice() {
        return price;
    }

    public double getTaxReturn() {
        return getTaxAmount() * 0.15;
    }
}

class A extends Taxes {

    public A(double price) {
        super(price);
    }

    @Override
    public String getType() {
        return "A";
    }

    @Override
    public double getTaxAmount() {
        return price * 0.18;
    }
}

class B extends Taxes {

    public B(double price) {
        super(price);
    }

    @Override
    public String getType() {
        return "B";
    }

    @Override
    public double getTaxAmount() {
        return price * 0.05;
    }
}

class V extends Taxes {

    public V(double price) {
        super(price);
    }

    @Override
    public String getType() {
        return "V";
    }

    @Override
    public double getTaxAmount() {
        return 0;
    }
}

class Receipts {
    private String id;
    private List<Taxes> taxes;

    public Receipts(String id) {
        this.id = id;
        this.taxes = new ArrayList<>();
    }

    public void addTax(Taxes tax) {
        taxes.add(tax);
    }

    public double getTotalAmount() {
        return taxes.stream()
                .mapToDouble(Taxes::getPrice)
                .sum();
    }

    public double getTaxReturn() {
        return taxes.stream()
                .mapToDouble(Taxes::getTaxReturn)
                .sum();
    }

    @Override
    public String toString() {
        return String.format("%s %.0f %.2f", id, getTotalAmount(), getTaxReturn());
    }
}

class MojDDV {
    private List<Receipts> receipts;

    public MojDDV() {
        receipts = new ArrayList<>();
    }

    public void readRecords (InputStream inputStream) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
            String[] tokens = line.split("\\s+");
            String id = tokens[0];

            Receipts receipt = new Receipts(id);

            for (int i = 1; i < tokens.length; i+=2) {
                double price = Double.parseDouble(tokens[i]);
                String type = tokens[i+1];

                Taxes tax = null;
                if (type.equals("A")) {
                    tax = new A(price);
                } else if (type.equals("B")) {
                    tax = new B(price);
                } else if (type.equals("V")) {
                    tax = new V(price);
                }
                if (tax == null) throw new IllegalArgumentException();

                receipt.addTax(tax);
            }

            try {
                if (receipt.getTotalAmount() > 30000) {
                    throw new AmountNotAllowedException(receipt.getTotalAmount());
                }
                receipts.add(receipt);
            } catch (AmountNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void printTaxReturns (OutputStream outputStream) {
        PrintWriter printWriter = new PrintWriter(outputStream);

        for (Receipts receipt: receipts) {
            printWriter.println(receipt);
        }

        printWriter.flush();

    }
}

public class MojDDVTest {

    public static void main(String[] args) throws IOException {

        MojDDV mojDDV = new MojDDV();

        System.out.println("===READING RECORDS FROM INPUT STREAM===");
        mojDDV.readRecords(System.in);

        System.out.println("===PRINTING TAX RETURNS RECORDS TO OUTPUT STREAM ===");
        mojDDV.printTaxReturns(System.out);

    }
}
