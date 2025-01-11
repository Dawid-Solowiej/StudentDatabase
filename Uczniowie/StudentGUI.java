import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/*
  Klasa StudentGUI – tworzy okno aplikacji:
  - Pola tekstowe: studentIDField, nameField, ageField, gradeField
  - Przyciski: Add, Remove, Update, Display, Average
  - Wyniki i komunikaty wyświetlamy w outputArea (wielowierszowym)
  - Walidacja danych (ID-tylko cyfry, Name-tylko litery, Age 18–100, Grade ∈ {2,3,3.5,4,4.5,5})
  - Niepożądane dane -> wyświetlenie błędu w outputArea (setText)
*/

public class StudentGUI extends JFrame {

    private JTextField studentIDField;  // ID: tylko cyfry
    private JTextField nameField;       // Imię: tylko litery
    private JTextField ageField;        // Wiek: 18–100
    private JTextField gradeField;      // Ocena: 2,3,3.5,4,4.5,5

    private JTextArea outputArea;       // Wyświetlanie wiadomości / listy

    private StudentManagerImpl manager; // Obiekt implementujący interfejs StudentManager

    public StudentGUI() {
        super("Advanced Student Management System"); // Tytuł okna

        // Tworzymy obiekt menedżera
        manager = new StudentManagerImpl();

        // Ustawienia okna
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Dwa główne panele: inputPanel (z polami), buttonPanel (z przyciskami)
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5));

        // outputArea do wyświetlania komunikatów i rezultatów
        outputArea = new JTextArea(12, 60);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        // Pola tekstowe
        studentIDField = new JTextField();
        nameField = new JTextField();
        ageField = new JTextField();
        gradeField = new JTextField();

        // Etykiety i pola
        inputPanel.add(new JLabel("Student ID (digits):"));
        inputPanel.add(studentIDField);

        inputPanel.add(new JLabel("Name (letters):"));
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Age (18–100):"));
        inputPanel.add(ageField);

        inputPanel.add(new JLabel("Grade (2,3,3.5,4,4.5,5):"));
        inputPanel.add(gradeField);

        // Tworzymy przyciski
        JButton addBtn = new JButton("Add");
        JButton removeBtn = new JButton("Remove");
        JButton updateBtn = new JButton("Update");
        JButton displayBtn = new JButton("Display");
        JButton avgBtn = new JButton("Average");

        // Dodajemy przyciski do panelu
        buttonPanel.add(addBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(displayBtn);
        buttonPanel.add(avgBtn);

        // Rozmieszczenie elementów w oknie
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        // Słuchacze akcji dla przycisków
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStudentAction();
            }
        });
        removeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeStudentAction();
            }
        });
        updateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStudentAction();
            }
        });
        displayBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayAllAction();
            }
        });
        avgBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                averageAction();
            }
        });
    }

    // Metoda do dodania nowego studenta
    private void addStudentAction() {
        String sid = studentIDField.getText();
        String nm = nameField.getText();
        String ageTxt = ageField.getText();
        String gradeTxt = gradeField.getText();

        // Walidacja ID – tylko cyfry
        if (!sid.matches("\\d+")) {
            outputArea.setText("Error: ID must be digits only!");
            return;
        }
        // Walidacja Name – tylko litery (bez polskich znaków, jeśli chcesz polskie, zmień regex)
        if (!nm.matches("[a-zA-Z]+")) {
            outputArea.setText("Error: Name must be letters only!");
            return;
        }

        // Walidacja wieku (18–100)
        int ag;
        try {
            ag = Integer.parseInt(ageTxt);
            if (ag < 18 || ag > 100) {
                outputArea.setText("Error: Age must be in [18..100]!");
                return;
            }
        } catch (NumberFormatException ex) {
            outputArea.setText("Error: Age must be integer!");
            return;
        }

        // Walidacja oceny
        double gr;
        try {
            gr = Double.parseDouble(gradeTxt);
            double[] allowed = {2.0, 3.0, 3.5, 4.0, 4.5, 5.0};
            boolean ok = false;
            for (double d : allowed) {
                if (Math.abs(d - gr) < 0.000001) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                outputArea.setText("Error: Grade must be one of 2,3,3.5,4,4.5,5!");
                return;
            }
        } catch (NumberFormatException ex) {
            outputArea.setText("Error: Grade must be a number (e.g. 3.5)!");
            return;
        }

        // Tworzymy obiekt Student
        Student st = new Student(nm, ag, gr, sid);
        // Wywołujemy metodę addStudent u menedżera
        manager.addStudent(st);

        clearFields();
        outputArea.setText("Attempted to add student: " + sid);
    }

    // Metoda do usunięcia studenta
    private void removeStudentAction() {
        String sid = studentIDField.getText();
        if (sid.isEmpty()) {
            outputArea.setText("Error: Provide an ID to remove!");
            return;
        }
        manager.removeStudent(sid);
        clearFields();
        outputArea.setText("Attempted to remove student: " + sid);
    }

    // Metoda do aktualizacji danych studenta
    private void updateStudentAction() {
        String sid = studentIDField.getText();
        if (sid.isEmpty()) {
            outputArea.setText("Error: Provide an ID to update!");
            return;
        }
        // W interfejsie mamy updateStudent(String studentID), a implementacja
        // pyta o nowe wartości w okienkach JOptionPane.
        manager.updateStudent(sid);
        clearFields();
        outputArea.setText("Attempted to update student: " + sid);
    }

    // Metoda do wyświetlania wszystkich studentów w bazie
    private void displayAllAction() {
        ArrayList<Student> list = manager.displayAllStudents();
        if (list.isEmpty()) {
            outputArea.setText("No students in the database.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Student s : list) {
            sb.append("ID: ").append(s.getStudentID())
              .append(", Name: ").append(s.getName())
              .append(", Age: ").append(s.getAge())
              .append(", Grade: ").append(s.getGrade())
              .append("\n");
        }
        outputArea.setText(sb.toString());
    }

    // Metoda do obliczania i wyświetlania średniej ocen
    private void averageAction() {
        double avg = manager.calculateAverageGrade();
        outputArea.setText("Average grade: " + avg);
    }

    // Czyścimy pola tekstowe
    private void clearFields() {
        studentIDField.setText("");
        nameField.setText("");
        ageField.setText("");
        gradeField.setText("");
    }

    // Metoda main – uruchamia okno
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentGUI gui = new StudentGUI();
            gui.setVisible(true);
        });
    }
}
