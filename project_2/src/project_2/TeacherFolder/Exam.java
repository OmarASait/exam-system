package project_2;

import java.util.ArrayList;
import project_2.TeacherFolder.Question;

public class Exam {
    private String title;
    private ArrayList<Question> questions;
    private int id;  // إضافة id للامتحان

    // مُنشئ مع إضافة id
    public Exam(String title, ArrayList<Question> questions, int id) {
        this.title = title;
        this.questions = questions;
        this.id = id;  // تعيين id
    }

    Exam(String title, ArrayList<Question> questions, int examId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public int getId() {
        return id;  // Getter لـ id
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    // عرض تفاصيل الامتحان: العنوان + الأسئلة مع الخيارات
    public String displayExamDetails() {
        StringBuilder details = new StringBuilder("Exam: " + title + "\n\n");

        // إضافة كل سؤال مع خياراته
        for (Question question : questions) {
            details.append(question.displayQuestionWithOptions()).append("\n\n");
        }

        return details.toString();  // إرجاع النص الكامل
    }
}
