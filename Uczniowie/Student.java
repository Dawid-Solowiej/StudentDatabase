// Klasa Student – przechowuje dane studenta (name, age, grade, studentID).
// Zawiera gettery, settery i metodę displayInfo().

public class Student {
    // Imię (tylko litery w walidacji GUI)
    private String name;      
    // Wiek (18–100, walidowane w GUI)
    private int age;          
    // Ocena (2, 3, 3.5, 4, 4.5, 5 – walidowane w GUI)
    private double grade;     
    // ID (tylko cyfry, nie może się powtarzać – sprawdzane w StudentManagerImpl)
    private String studentID; 

    // Konstruktor
    public Student(String name, int age, double grade, String studentID) {
        this.name = name;
        this.age = age;
        this.grade = grade;
        this.studentID = studentID;
    }

    // Gettery i settery
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    public double getGrade() {
        return grade;
    }
    public void setGrade(double grade) {
        this.grade = grade;
    }

    public String getStudentID() {
        return studentID;
    }
    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    // Metoda displayInfo() – wypisuje dane o studencie w konsoli
    public void displayInfo() {
        System.out.println("StudentID: " + studentID
                + ", Name: " + name
                + ", Age: " + age
                + ", Grade: " + grade);
    }
}
