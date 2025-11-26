package labs.lab4.task2;

import java.util.*;
import java.util.stream.Collectors;

class StudentSameIDException extends Exception {

    public StudentSameIDException(String id) {
        super(String.format("Student with ID %s already exists", id));
    }
}

class Student {

    private String id;
    private List<Integer> grades;

    public Student(String id, List<Integer> grades) {
        this.id = id;
        this.grades = grades;
    }

    public String getId() {
        return id;
    }

    public List<Integer> getGrades() {
        return grades;
    }

    public double averageGrades() {
        double sum =  grades.stream()
                .mapToDouble(g -> g).sum();

        return sum / grades.size();
    }

    public int coursesPassed() {
        return grades.size();
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", grades=" + grades +
                '}';
    }
}

class Faculty {

    Map<String, Student> students; // Use Map for O(1) lookup

    public Faculty() {
        this.students = new HashMap<>();
    }

    public void addStudent(String id, List<Integer> grades) throws StudentSameIDException {

        if (students.containsKey(id)) {
            throw new StudentSameIDException(id);
        }

        students.put(id, new Student(id, grades));
    }

    public void addGrade(String id, int grade) {
        // O(1) lookup using Map
        Student student = students.get(id);
        if (student != null) {
            student.getGrades().add(grade);
        }
    }

    public Set<Student> getStudentsSortedByAverageGrade() {
        // Use LinkedHashSet to maintain insertion order
        return students.values().stream()
                .sorted(Comparator.comparing(Student::averageGrades).reversed()
                        .thenComparing(Student::coursesPassed, Comparator.reverseOrder())
                        .thenComparing(Student::getId, Comparator.reverseOrder()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<Student> getStudentsSortedByCoursesPassed() {
        // Use LinkedHashSet to maintain insertion order
        return students.values().stream()
                .sorted(Comparator.comparing(Student::coursesPassed, Comparator.reverseOrder())
                        .thenComparing(Student::averageGrades, Comparator.reverseOrder())
                        .thenComparing(Student::getId, Comparator.reverseOrder()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * When you write:
     * .sorted(Comparator.comparing(Student::averageGrades).reversed()
     *         .thenComparing(Student::coursesPassed).reversed()
     *
     * The .reversed() at the end applies to the entire chain, not just the last thenComparing. This causes unexpected behavior.
     *
     * Correct approach:
     * .sorted(Comparator.comparing(Student::averageGrades).reversed()
     *         .thenComparing(Student::coursesPassed, Comparator.reverseOrder())
     *         .thenComparing(Student::getId, Comparator.reverseOrder()))
     */

}

public class SetsTest {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Faculty faculty = new Faculty();

        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            String[] tokens = input.split("\\s+");
            String command = tokens[0];

            switch (command) {
                case "addStudent":
                    String id = tokens[1];
                    List<Integer> grades = new ArrayList<>();
                    for (int i = 2; i < tokens.length; i++) {
                        grades.add(Integer.parseInt(tokens[i]));
                    }
                    try {
                        faculty.addStudent(id, grades);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case "addGrade":
                    String studentId = tokens[1];
                    int grade = Integer.parseInt(tokens[2]);
                    faculty.addGrade(studentId, grade);
                    break;

                case "getStudentsSortedByAverageGrade":
                    System.out.println("Sorting students by average grade");
                    Set<Student> sortedByAverage = faculty.getStudentsSortedByAverageGrade();
                    for (Student student : sortedByAverage) {
                        System.out.println(student);
                    }
                    break;

                case "getStudentsSortedByCoursesPassed":
                    System.out.println("Sorting students by courses passed");
                    Set<Student> sortedByCourses = faculty.getStudentsSortedByCoursesPassed();
                    for (Student student : sortedByCourses) {
                        System.out.println(student);
                    }
                    break;

                default:
                    break;
            }
        }

        scanner.close();
    }
}
