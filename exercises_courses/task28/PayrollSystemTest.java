package exercises_courses.task28;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

abstract class Employee {
    protected String type;
    protected String id;
    protected String level;

    public Employee(String type, String id, String level) {
        this.type = type;
        this.id = id;
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getLevel() {
        return level;
    }

    public abstract double getSalary();
}

class HourlyEmployee extends Employee {

    private double hours;
    private double rate;

    public HourlyEmployee(String type, String id, String level, double hours, double rate) {
        super(type, id, level);
        this.hours = hours;
        this.rate = rate;
    }

    @Override
    public double getSalary() {
        double regularHours = Math.min(hours, 40);
        double overtimeHours = Math.max(0, hours - 40);
        return (regularHours * rate) + (overtimeHours * rate * 1.5);
    }

    @Override
    public String toString() {
        double regularHours = Math.min(hours, 40);
        double overtimeHours = Math.max(0, hours - 40);
        return String.format(
                "Employee ID: %s Level: %s Salary: %.2f Regular hours: %.2f Overtime hours: %.2f",
                id, level, getSalary(), regularHours, overtimeHours
        );
    }
}

class FreelanceEmployee extends Employee {
    private List<Integer> ticketPoints;
    private double rate;

    public FreelanceEmployee(String type, String id, String level, List<Integer> ticketPoints, double rate) {
        super(type, id, level);
        this.ticketPoints = ticketPoints;
        this.rate = rate;
    }

    public int getTicketsCount() {
        return ticketPoints.size();
    }

    public int getTicketsSum() {
        return ticketPoints.stream()
                .mapToInt(ticket -> ticket)
                .sum();
    }

    @Override
    public double getSalary() {
        return ticketPoints.stream()
                .mapToDouble(ticket -> ticket * rate)
                .sum();
    }

    @Override
    public String toString() {
        return String.format(
                "Employee ID: %s Level: %s Salary: %.2f Tickets count: %d Tickets points: %d",
                    id, level, getSalary(), getTicketsCount(), getTicketsSum()
                );
    }
}

class PayrollSystem {

    List<HourlyEmployee> hourlyEmployees;
    List<FreelanceEmployee> freelanceEmployees;

    Map<String, Double> hourlyRateByLevel;
    Map<String,Double> ticketRateByLevel;

    public PayrollSystem(Map<String, Double> hourlyRateByLevel, Map<String, Double> ticketRateByLevel) {
        this.hourlyRateByLevel = hourlyRateByLevel;
        this.ticketRateByLevel = ticketRateByLevel;
        this.hourlyEmployees = new ArrayList<>();
        this.freelanceEmployees = new ArrayList<>();
    }

    public void readEmployees(InputStream is) {
        Scanner sc = new Scanner(is);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(";");
            String type = parts[0];
            String id = parts[1];
            String level = parts[2];
            if (type.equals("H")) {
                double hours = Double.parseDouble(parts[3]);
                double rate = hourlyRateByLevel.get(level);
                hourlyEmployees.add(new HourlyEmployee(type, id, level, hours, rate));
            } else if (type.equals("F")){
                List<Integer> ticketHours = Arrays.stream(parts)
                        .skip(3)
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());

                double rate = ticketRateByLevel.get(level);

                freelanceEmployees.add(new FreelanceEmployee(type, id, level, ticketHours, rate));
            }

        }
    }

    public Map<String, Set<Employee>> printEmployeesByLevels (OutputStream os, Set<String> levels) {

        PrintWriter pw = new PrintWriter(os);

        // Merge both lists
        List<Employee> listOfAllEmployees = new ArrayList<>();
        listOfAllEmployees.addAll(hourlyEmployees);
        listOfAllEmployees.addAll(freelanceEmployees);

        // Filter by levels
        List<Employee> filteredList = listOfAllEmployees.stream()
                .filter(employee -> levels.contains(employee.getLevel()))
                .collect(Collectors.toList());

        Map<String, Set<Employee>> employees = filteredList.stream()
                .collect(Collectors.groupingBy(
                        Employee::getLevel,
                        TreeMap::new,
                        Collectors.toCollection(() -> new TreeSet<>(
                                Comparator.comparing(Employee::getSalary).reversed()
                                        .thenComparing(Employee::getId)
                        ))
                ));

        // Print formatted output
        employees.forEach((level, emps) -> {
            pw.println("LEVEL: " + level);
            pw.println("Employees: ");
            emps.forEach(pw::println);
            pw.println("------------");
        });

        pw.flush();

        return employees;
    }
}

public class PayrollSystemTest {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 10 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5 + i * 2.5);
        }

        PayrollSystem payrollSystem = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);

        System.out.println("READING OF THE EMPLOYEES DATA");
        payrollSystem.readEmployees(System.in);

        System.out.println("PRINTING EMPLOYEES BY LEVEL");
        Set<String> levels = new LinkedHashSet<>();
        for (int i=5;i<=10;i++) {
            levels.add("level"+i);
        }
        Map<String, Set<Employee>> result = payrollSystem.printEmployeesByLevels(System.out, levels);
//        result.forEach((level, employees) -> {
//            System.out.println("LEVEL: "+ level);
//            System.out.println("Employees: ");
//            employees.forEach(System.out::println);
//        });


    }
}