package classify;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import static java.lang.System.err;
import static java.lang.System.out;

public class ClassifyShell {

    interface StudentToString {
        String exec(Student st);
    }

    private static void readStudents(String filePath) {
        var path = Paths.get(filePath);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = Files.newBufferedReader(path);
        }
        catch (IOException ex) {
            err.println("Can't open file for reading " + filePath);
            System.exit(1);
        }

        final var reader = bufferedReader;
        try (reader) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] attrs = line.strip().split(",");
                var student = new Student(
                        attrs[0].strip(),
                        attrs[1].strip(), // .strip() -> "   xpto@mail.com " ==> "xpto@mail.com"
                        attrs[2].strip(),
                        attrs[3].strip(),
                        attrs[4].strip()
                );
                students.add(student);
            }
        }
        catch (IOException ex) {
            err.println("Can't read file and/or data is corrupted");
            System.exit(1);
        }
    }

    private static void writeStudents(
            String filePath,
            String header,
            StudentToString studentToString
    ) {
        var path = Paths.get(filePath);
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = Files.newBufferedWriter(path);
        }
        catch (IOException ex) {
            err.println("Can't open file for writing " + filePath);
            System.exit(1);
        }

        final var writer = bufferedWriter;
        try (writer) {
            writer.write(header);
            for (var st : students) {
                String txtLine = studentToString.exec(st);
                writer.write(txtLine);
            }
        }
        catch (IOException ex) {
            err.println("Can't write to file");
            System.exit(1);
        }
    }

    private static void execMainMenu() {
        while (true) {
            // 1. Exibir opções o menu
            Utils.clearScreen();
            out.println("GESTÃO DE CLASSIFICAÇÕES");
            out.println("L - Listar classificações");
            out.println("P - Pesquisar classificações");
            out.println("A - Actualizar informação sobre estudante ");
            out.println("E - Exportar classificações");
            out.println("R – Reler classificações");
            out.println("0 - Terminar");

            // 2. Ler opção introduzida
            out.print(now() + " > ");     // COLOCAR A HORA CERTA
            String option = in.nextLine().strip();

            // 3. Analisar e executar a opção introduzida
            Utils.clearScreen();
            switch (option.toUpperCase()) {
                case "L":
                    execListing();
                    break;
                case "P":
                    execSearch();
                    break;
                case "A":
                    execUpdateStudent();
                    break;
                case "E":
                    execExportStudents();
                    break;
                case "0":
                    writeStudents(
                            filePath,
                            "",
                            (Student st) -> String.format(
                                    "%s,%s,%s,%.0f,%s\n",
                                    st.getName(),
                                    st.getEmail(),
                                    st.getBirthDate(),
                                    st.getGrade(),
                                    st.getStatus()
                            )
                    );
                    out.println("O programa vai terminar...");
                     System.exit(0);
                    // break ciclo;
                default:
                    out.printf("Opção <%s> inválida\n", option);
            }
        }
    }

    private static void execListing() {
        showStudentCollection(students);
        Utils.pause();
    }

    private static void execSearch() {
        while (true) {
            // 1. Exibir opções o menu
            out.println("E - Pesquisar por email");
            out.println("N - Nome");
            out.println("C - Classificação ");
            out.println("ENTER para voltar ao menu anterior");

            // 2. Ler opção introduzida
            out.print("> ");
            String option = in.nextLine().strip();
            if (option.strip().isEmpty()) {
                return;
            }

            // 3. Analisar e executar a opção introduzida
            switch (option.toUpperCase()) {
                case "N":
                    execSearchByName();
                    break;
                default:
                    out.printf("Opção <%s> inválida\n", option);
            }
        }
    }

    private static void execSearchByName() {
        out.print("Introduza parte do nome a pesquisar: ");
        String namePart = in.nextLine();
        var studentCollection = students.searchByName(namePart);
        showStudentCollection(studentCollection);
        Utils.pause();
    }

    private static void execUpdateStudent() {
        while (true) {
            // 1. Exibir opções o menu
            Utils.clearScreen();
            out.println("E - Actualizar email");
            out.println("C - Classificação ");
            out.println("S - Estatuto");
            out.println("ENTER para voltar ao menu anterior");

            // 2. Ler opção introduzida
            out.print("> ");
            String option = in.nextLine().strip();
            if (option.strip().isEmpty()) {
                return;
            }

            // 3. Solicitar email e obter estudante
            out.print("Email do estudante: ");
            String email = in.nextLine().strip();
            if (email.strip().isEmpty()) {
                continue;
            }
            var st = students.searchByEmail(email);
            if (st == null) {
                out.printf("Estudante com email <%s> não encontrado\n", email);
                return;
            }

            // 4. Analisar e executar a opção introduzida
            switch (option.toUpperCase()) {
                case "E":
                    execUpdateStudentEmail(st);
                    break;
                default:
                    out.printf("Opção <%s> inválida\n", option);
            }
        }
    }

    private static void execUpdateStudentEmail(Student st) {
        while (true) {
            String email = "";
            try {
                out.print("Novo email do estudante: ");
                email = in.nextLine().strip();
                st.setEmail(email);
                break;
            }
            catch (IllegalArgumentException ex) {
                out.println("Email inválido: " + email);
            }
        }
    }

    private static void execExportStudents() {
        out.print("Caminho do ficheiro: ");
        String filePath = in.nextLine().strip();
        if (filePath.strip().isEmpty()) {
            return;
        }

        var grade = students.average();
        var textualGrade = Student.classifyGrade(grade).toUpperCase();
        writeStudents(
                filePath,
                String.format("MÉDIA %.1f %s\n\n", grade, textualGrade),
                (Student st) -> String.format(
                        "%s,%s,%.0f,%s\n",
                        st.getName(),
                        st.getEmail(),
                        st.getGrade(),
                        st.getTextualGrade()
                )
        );
    }

    private static void showStudentCollection(StudentCollection studentCollection) {
        if (studentCollection.isEmpty()) {
            out.println("Não foram encontrados estudantes");
            return;
        }

        out.printf(
                "%-30s | %15s | %15s | %-30s\n",
                "NOME",
                "Classificação",
                "Classificação",
                Utils.center("Email", 30)
        );
        out.printf("%-30s | %15s | %15s |\n", " ", "Quantitativa", "Qualitativa");
        // melhor em: https://stackoverflow.com/a/1900485
        // e https://kodejava.org/how-do-i-align-string-print-out-in-left-right-center-alignment/
        out.println("-------------------------------+-----------------+-----------------+---------------------------");
        for (Student st : studentCollection) {
            showStudent(st);
        }

        out.println();
        var average = studentCollection.average();
        out.printf("MÉDIA: %5.2f %-15s\n", average, Student.classifyGrade(average));
        out.println();
    }

    private static void showStudent(Student student) {
        out.printf(
                "%-30s | %15.0f | %15s | %-30s\n",
                student.getName(),
                student.getGrade(),
                Utils.center(student.getTextualGrade(), 15),
                Utils.center(student.getEmail(), 30)
        );
    }

    private static String now() {
        var time = java.time.LocalTime.now();
        return String.format(
                "%02d:%02d:%02d", time.getHour(), time.getMinute(), time.getSecond()
        );
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            err.println("Erro: ficheiro com dados da turma não especificado.");
            err.println("Utilização: ClassifyShell FICHEIRO_CSV");
            System.exit(2);
        }
        filePath = args[0];

        // Ler ficheiro de entrada
        readStudents(filePath);

        // Exibir menu principal
        execMainMenu();
    }

    private static Scanner in = new Scanner(System.in);
    private static StudentCollection students = new StudentCollection();
    private static String filePath;
}
