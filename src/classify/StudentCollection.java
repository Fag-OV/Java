package classify;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.System.out;


public class StudentCollection implements Iterable<Student> {
    public void add(Student st) {
        if (this.searchByEmail(st.getEmail()) != null) {
            throw new DuplicateValueException("Duplicate email: " + st.getEmail());
        }
        this.students.add(st);
    }

    public Student searchByEmail(String email) {
        for (Student st : this.students) {
            if (st.getEmail().toLowerCase().equals(email.toLowerCase())) {
                return st;
            }
        }
        return null;
    }

    public StudentCollection searchByName(String name) {
        StudentCollection found = new StudentCollection();
        for (Student st : this.students) {
            if (st.getName().toLowerCase().contains(name.toLowerCase())) {
                found.add(st);
            }
        }
        return found;
    }

    public StudentCollection searchByGrades(double minGrade, double maxGrade) {
        StudentCollection found = new StudentCollection();
        for (Student st : this.students) {
            double grade = st.getGrade();
            if (grade >= minGrade && grade <= maxGrade) {
                found.add(st);
            }
        }
        return found;
    }

    public boolean isEmpty() {
        return this.students.isEmpty();
    }

    public double average() {
        double sum = 0;
        for (Student st : this.students) {
            sum += st.getGrade();
        }
        return sum / students.size();
    }

    // Iterador

    @Override
    public Iterator<Student> iterator() {
        return new StudentCollectionIterator();
    }

    public class StudentCollectionIterator implements Iterator<Student> {

        private int pos = 0;

        @Override
        public boolean hasNext() {
            return this.pos < students.size();
        }

        @Override
        public Student next() {
            Student st = students.get(pos);
            pos += 1;
            return st;
            // OU  apenas return students.get(pos++);
        }
    }

    public void show() {
        for (Student st : students) {
            out.println("---------------- " + st.getName());
            out.println(st);
            out.println();
        }
    }

    private List<Student> students = new ArrayList();    // equivalente ao vector de C++

}
