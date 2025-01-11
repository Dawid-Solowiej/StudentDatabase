import java.util.ArrayList;

// Interfejs StudentManager – określa metody, których użyje StudentManagerImpl.
// Te metody wywołuje GUI (StudentGUI).

public interface StudentManager {
    // Dodaje nowego studenta (po walidacji danych w GUI).
    void addStudent(Student student);

    // Usuwa studenta o podanym ID (jeśli istnieje).
    void removeStudent(String studentID);

    // Aktualizuje dane istniejącego studenta (metoda sama może pytać o nowe dane).
    void updateStudent(String studentID);

    // Zwraca listę wszystkich studentów z bazy.
    ArrayList<Student> displayAllStudents();

    // Oblicza średnią ocen wszystkich studentów.
    double calculateAverageGrade();
}
