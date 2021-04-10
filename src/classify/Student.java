package classify;

import java.time.LocalDate;
import java.util.Arrays;

public class Student {
    public Student(
            String name,
            String email,
            String birthDate,
            String grade,
            String status
    ) {
        if (!name.matches("^[a-zA-Z]{2,} [a-zA-Z]{2,}$")) {
            // regex para validar dois ou mais nomes:
            // name.matches("^[a-zA-Z]{2,}( [a-zA-Z]{2,})+$")
            throw new IllegalArgumentException("Invalid name " + name);
        }

        Utils.validateEmail(email);

        // Validar data (apenas fulanos nascidos após 1920)

        this.grade = Double.parseDouble(grade);
        if (this.grade < 0 || this.grade > 200) {
            throw new IllegalArgumentException("Invalid grade " + grade);
        }

        if (!Arrays.asList(VALID_STUDENT_STATUS).contains(status)) {
            throw new IllegalArgumentException("Invalid status " + status);
        }

        this.name = name;
        this.email = email;
        this.birthDate = LocalDate.parse(birthDate);
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("NOME: %s  EMAIL: %s  CLASS.QUAL: %s",
                             this.name, this.email, this.getTextualGrade());
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        Utils.validateEmail(email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public double getGrade() {
        return grade;
    }

    public String getTextualGrade() {
        return Student.classifyGrade(this.grade);
    }

    public String getStatus() {
        return status;
    }

    public static String classifyGrade(double grade) {
        return   grade >= 180 ? "Excelente"
               : grade >= 150 ? "Bom"
               : grade >= 120 ? "Suficiente"
               : "Insuficiente"
               ;
    }

    private String name;
    private String email;
    private LocalDate birthDate;
    private double grade;
    private String status;

    public static final String[] VALID_STUDENT_STATUS = {
            "REG",
            "TRAB",
            "BOLS"
    };

}

