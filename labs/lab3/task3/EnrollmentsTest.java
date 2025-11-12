package labs.lab3.task3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// TODO: Add classes and implement methods


class Applicant {
    private int id;
    private String name;
    private double gpa;
    List<SubjectWithGrade> subjectsWithGrade;
    StudyProgramme studyProgramme;

    public Applicant(int id, String name, double gpa, StudyProgramme studyProgramme) {
        this.id = id;
        this.name = name;
        this.gpa = gpa;
        this.subjectsWithGrade = new ArrayList<>();
        this.studyProgramme = studyProgramme;
    }

    public void addSubjectAndGrade(String subject, int grade) {
        subjectsWithGrade.add(new SubjectWithGrade(subject, grade));
    }

    public double calculatePoints() {
        double points = gpa * 12;

        for (SubjectWithGrade subject: subjectsWithGrade) {
            if (studyProgramme != null && studyProgramme.faculty != null &&
            studyProgramme.faculty.getAppropriateSubjects().contains(subject.getSubject())) {
                points += subject.getGrade() * 2;
            } else {
                points += subject.getGrade() * 1.2;
            }
        }

        return points;
    }

    @Override
    public String toString() {
        return String.format("Id: %d, Name: %s, GPA: %.1f - %.1f", id, name, gpa, calculatePoints());
    }
}

class StudyProgramme {
    private String code;
    private String name;
    int numPublicQuota;
    int numPrivateQuota;
    int enrolledInPublicQuota;
    int enrolledInPrivateQuota;
    private List<Applicant> applicants;
    Faculty faculty;

    public StudyProgramme(String code, String name, Faculty faculty, int numPublicQuota, int numPrivateQuota) {
        this.code = code;
        this.name = name;
        this.faculty = faculty;
        this.numPublicQuota = numPublicQuota;
        this.numPrivateQuota = numPrivateQuota;
        this.enrolledInPublicQuota = 0;
        this.enrolledInPrivateQuota = 0;
        this.applicants = new ArrayList<>();
    }

    public void addApplicant(Applicant a) {
        applicants.add(a);
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public List<Applicant> getApplicants() {
        return applicants;
    }

    public void calculateEnrollmentNumbers() {
        /**
         * Sort applicants by points descending
         * Fill public quota first
         * Fill private quota second
         * Others → rejected
         */
        applicants.sort(Comparator.comparing(Applicant::calculatePoints).reversed());

        enrolledInPublicQuota = Math.min(numPublicQuota, applicants.size());
        int remaining = applicants.size() - enrolledInPublicQuota;
        enrolledInPrivateQuota = Math.min(numPrivateQuota, remaining);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(name).append("\n");
        sb.append("Public Quota:").append("\n");

        applicants.sort(Comparator.comparing(Applicant::calculatePoints).reversed());

        int total = applicants.size();
        int pub = enrolledInPublicQuota;
        int priv = enrolledInPrivateQuota;

        for (int i = 0; i < pub; i++) {
            sb.append(applicants.get(i)).append("\n");
        }

        sb.append("Private Quota:\n");
        for (int i = pub; i < pub + priv; i++) {
            sb.append(applicants.get(i)).append("\n");
        }

        sb.append("Rejected:\n");
        for (int i = pub + priv; i < total; i++) {
            sb.append(applicants.get(i)).append("\n");
        }

        return sb.toString();
    }
}

class Faculty {
    private String shortName;
    private List<String> appropriateSubjects;
    private List<StudyProgramme> studyProgrammes;

    public Faculty(String shortName) {
        this.shortName = shortName;
        this.appropriateSubjects = new ArrayList<>();
        this.studyProgrammes = new ArrayList<>();
    }

    public void addSubject(String subject) {
        appropriateSubjects.add(subject);
    }

    public void addStudyProgramme(StudyProgramme studyProgramme) {
        studyProgrammes.add(studyProgramme);
    }

    public List<String> getAppropriateSubjects() {
        return appropriateSubjects;
    }

    public List<StudyProgramme> getStudyProgrammes() {
        return studyProgrammes;
    }

    @Override
    public String toString() {
        // Sort rules applied in printRanked
        return shortName;
    }
}

class SubjectWithGrade
{
    private String subject;
    private int grade;
    public SubjectWithGrade(String subject, int grade) {
        this.subject = subject;
        this.grade = grade;
    }
    public String getSubject() {
        return subject;
    }
    public int getGrade() {
        return grade;
    }
}

class EnrollmentsIO {
    public static void printRanked(List<Faculty> faculties) {

        for (Faculty f: faculties) {

            System.out.printf("Faculty: %s%n", f);
            System.out.printf("Subjects: %s%n", f.getAppropriateSubjects());
            System.out.println("Study Programmes:");

            List<StudyProgramme> programmes = f.getStudyProgrammes();

            // Sort study programmes according to the criteria
            programmes.sort(Comparator
                    // 1. Number of relevant subjects (ascending)
                    .comparingDouble((StudyProgramme sp) -> {
                        return sp.getApplicants().stream()
                                .mapToDouble(a -> {
                                    long relevantCount = a.subjectsWithGrade.stream()
                                            .filter(swg -> f.getAppropriateSubjects().contains(swg.getSubject()))
                                            .count();
                                    return relevantCount;
                                })
                                .average()
                                .orElse(0.0);
                    })
                    // 2. Percentage of enrolled students (descending)
                    .thenComparingDouble((StudyProgramme sp) -> {
                        int totalQuota = sp.numPublicQuota + sp.numPrivateQuota;
                        int enrolled = sp.enrolledInPublicQuota + sp.enrolledInPrivateQuota;
                        return -((double) enrolled / totalQuota * 100);
                    })
                    // 3. Average points of top enrolled students (descending)
                    .thenComparingDouble((StudyProgramme sp) -> {
                        int enrolled = sp.enrolledInPublicQuota + sp.enrolledInPrivateQuota;
                        if (enrolled == 0) return 0.0;

                        List<Applicant> sortedApplicants = new ArrayList<>(sp.getApplicants());
                        sortedApplicants.sort(Comparator.comparing(Applicant::calculatePoints).reversed());

                        double avgPoints = sortedApplicants.stream()
                                .limit(enrolled)
                                .mapToDouble(Applicant::calculatePoints)
                                .average()
                                .orElse(0.0);

                        return -avgPoints;
                    })
            );

            for (StudyProgramme sp: programmes) {
                System.out.println(sp);
            }
        }
    }

    public static void readEnrollments(List<StudyProgramme> studyProgrammes, InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        while ((line = br.readLine()) != null && !line.isEmpty()) {

            String[] parts = line.split(";");
            int id = Integer.parseInt(parts[0]);
            String name = parts[1];
            double gpa = Double.parseDouble(parts[2]);

            String programCode = parts[parts.length - 1];

            StudyProgramme sp = studyProgrammes.stream()
                    .filter(p -> p.getCode().equals(programCode))
                    .findFirst()
                    .orElse(null);

            if (sp == null) continue;

            Applicant a = new Applicant(id, name, gpa, sp);

            for (int i = 3; i < parts.length - 1; i += 2) {
                String subj = parts[i];
                int gr = Integer.parseInt(parts[i + 1]);
                a.addSubjectAndGrade(subj, gr);
            }

            sp.addApplicant(a);
        }
    }
}

public class EnrollmentsTest {

    public static void main(String[] args) throws IOException {
        Faculty finki = new Faculty("FINKI");
        finki.addSubject("Mother Tongue");
        finki.addSubject("Mathematics");
        finki.addSubject("Informatics");

        Faculty feit = new Faculty("FEIT");
        feit.addSubject("Mother Tongue");
        feit.addSubject("Mathematics");
        feit.addSubject("Physics");
        feit.addSubject("Electronics");

        Faculty medFak = new Faculty("MEDFAK");
        medFak.addSubject("Mother Tongue");
        medFak.addSubject("English");
        medFak.addSubject("Mathematics");
        medFak.addSubject("Biology");
        medFak.addSubject("Chemistry");

        StudyProgramme si = new StudyProgramme("SI", "Software Engineering", finki, 4, 4);
        StudyProgramme it = new StudyProgramme("IT", "Information Technology", finki, 2, 2);
        finki.addStudyProgramme(si);
        finki.addStudyProgramme(it);

        StudyProgramme kti = new StudyProgramme("KTI", "Computer Technologies and Engineering", feit, 3, 3);
        StudyProgramme ees = new StudyProgramme("EES", "Electro-energetic Systems", feit, 2, 2);
        feit.addStudyProgramme(kti);
        feit.addStudyProgramme(ees);

        StudyProgramme om = new StudyProgramme("OM", "General Medicine", medFak, 6, 6);
        StudyProgramme nurs = new StudyProgramme("NURS", "Nursing", medFak, 2, 2);
        medFak.addStudyProgramme(om);
        medFak.addStudyProgramme(nurs);

        List<StudyProgramme> allProgrammes = new ArrayList<>();
        allProgrammes.add(si);
        allProgrammes.add(it);
        allProgrammes.add(kti);
        allProgrammes.add(ees);
        allProgrammes.add(om);
        allProgrammes.add(nurs);

        EnrollmentsIO.readEnrollments(allProgrammes, System.in);

        List<Faculty> allFaculties = new ArrayList<>();
        allFaculties.add(finki);
        allFaculties.add(feit);
        allFaculties.add(medFak);

        allProgrammes.stream().forEach(StudyProgramme::calculateEnrollmentNumbers);

        EnrollmentsIO.printRanked(allFaculties);

    }


}
