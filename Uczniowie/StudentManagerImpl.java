import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/*
  Klasa StudentManagerImpl – implementuje interfejs StudentManager.
  Obsługuje bazę SQLite (plik students.db), tworzy tabelę "students"
  oraz realizuje operacje: add, remove, update, displayAll, calculateAverage.
*/

public class StudentManagerImpl implements StudentManager {

    // Ścieżka do bazy danych SQLite (plik).
    private static final String DB_URL = "jdbc:sqlite:students.db";

    // Konstruktor – przy tworzeniu obiektu tworzymy tabelę, jeśli nie istnieje.
    public StudentManagerImpl() {
        createTableIfNotExists();
    }

    // Metoda do nawiązania połączenia z bazą.
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Tworzymy tabelę "students" (o kolumnach: name, age, grade, studentID PRIMARY KEY).
    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS students ("
                + "name TEXT, "
                + "age INTEGER, "
                + "grade REAL, "
                + "studentID TEXT PRIMARY KEY)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql); // Wykonanie CREATE TABLE
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metoda pomocnicza – sprawdza, czy dane studentID już istnieje w bazie.
    private boolean studentExists(String studentID) {
        String sql = "SELECT studentID FROM students WHERE studentID = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentID);
            try (ResultSet rs = pstmt.executeQuery()) {
                // Jeśli rs.next() jest true, to rekord istnieje
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Dodanie nowego studenta (zobacz interfejs)
    @Override
    public void addStudent(Student student) {
        // Sprawdzamy, czy to ID już jest w bazie (ID musi być unikalne)
        if (studentExists(student.getStudentID())) {
            JOptionPane.showMessageDialog(null,
                    "Student with ID: " + student.getStudentID() + " already exists!",
                    "Add Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Jeśli ID jest wolne, to INSERT
        String insertSQL = "INSERT INTO students(name, age, grade, studentID) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            // Ustawiamy parametry
            pstmt.setString(1, student.getName());
            pstmt.setInt(2, student.getAge());
            pstmt.setDouble(3, student.getGrade());
            pstmt.setString(4, student.getStudentID());

            pstmt.executeUpdate(); // Wykonanie INSERT

            JOptionPane.showMessageDialog(null,
                    "Student added successfully!",
                    "Add Student",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Usunięcie studenta
    @Override
    public void removeStudent(String studentID) {
        String deleteSQL = "DELETE FROM students WHERE studentID = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {

            pstmt.setString(1, studentID);
            pstmt.executeUpdate(); // Wykonanie DELETE

            JOptionPane.showMessageDialog(null,
                    "Removed student with ID: " + studentID,
                    "Remove Student",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Aktualizacja danych istniejącego studenta
    // Zgodnie z interfejsem, przyjmuje tylko ID – pobieramy resztę danych w okienkach (JOptionPane).
    @Override
    public void updateStudent(String studentID) {
        // Sprawdzamy, czy student istnieje w bazie
        String checkSQL = "SELECT name, age, grade FROM students WHERE studentID = ?";
        String oldName = null;
        int oldAge = 0;
        double oldGrade = 0.0;
        boolean found = false;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkSQL)) {
            pstmt.setString(1, studentID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    oldName = rs.getString("name");
                    oldAge = rs.getInt("age");
                    oldGrade = rs.getDouble("grade");
                    found = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!found) {
            // Jeśli nie ma takiego ID w bazie, pokazujemy komunikat
            JOptionPane.showMessageDialog(null,
                    "No student found with ID: " + studentID,
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Pytamy użytkownika (w okienkach) o nowe dane
        String newName = JOptionPane.showInputDialog(null,
                "Enter new name (letters only). Current: " + oldName,
                oldName);

        // Sprawdzamy, czy user nie kliknął "Cancel" (newName == null) i czy pasuje do regex
        if (newName == null || !newName.matches("[a-zA-Z]+")) {
            JOptionPane.showMessageDialog(null,
                    "Invalid name! Must be letters only.",
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newAgeStr = JOptionPane.showInputDialog(null,
                "Enter new age (18–100). Current: " + oldAge,
                oldAge);

        int newAge;
        try {
            newAge = Integer.parseInt(newAgeStr);
            if (newAge < 18 || newAge > 100) {
                JOptionPane.showMessageDialog(null,
                        "Age must be in [18..100]!",
                        "Update Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null,
                    "Invalid age format!",
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newGradeStr = JOptionPane.showInputDialog(null,
                "Enter new grade (2, 3, 3.5, 4, 4.5, 5). Current: " + oldGrade,
                oldGrade);

        double newGrade;
        try {
            newGrade = Double.parseDouble(newGradeStr);
            // Sprawdzamy, czy jest w dozwolonej liście
            double[] allowedGrades = {2.0, 3.0, 3.5, 4.0, 4.5, 5.0};
            boolean ok = false;
            for (double d : allowedGrades) {
                if (Math.abs(d - newGrade) < 0.000001) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                JOptionPane.showMessageDialog(null,
                        "Grade must be one of (2, 3, 3.5, 4, 4.5, 5)!",
                        "Update Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null,
                    "Invalid grade format!",
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Wykonujemy UPDATE w bazie
        String updateSQL = "UPDATE students SET name = ?, age = ?, grade = ? WHERE studentID = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {

            pstmt.setString(1, newName);
            pstmt.setInt(2, newAge);
            pstmt.setDouble(3, newGrade);
            pstmt.setString(4, studentID);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(null,
                    "Student with ID " + studentID + " updated successfully!",
                    "Update Student",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Pobranie listy wszystkich studentów z bazy
    @Override
    public ArrayList<Student> displayAllStudents() {
        ArrayList<Student> list = new ArrayList<>();
        String sql = "SELECT name, age, grade, studentID FROM students";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String nm = rs.getString("name");
                int ag = rs.getInt("age");
                double gr = rs.getDouble("grade");
                String sid = rs.getString("studentID");

                // Tworzymy obiekt Student i dodajemy do listy
                Student st = new Student(nm, ag, gr, sid);
                list.add(st);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Obliczenie średniej ocen
    @Override
    public double calculateAverageGrade() {
        double total = 0.0;
        int count = 0;
        String sql = "SELECT grade FROM students";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                total += rs.getDouble("grade");
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Jeśli brak studentów, zwracamy 0.0 (żeby uniknąć dzielenia przez zero).
        if (count == 0) {
            return 0.0;
        }

        return total / count;
    }
}
