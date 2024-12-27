package project_2;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.border.TitledBorder;

public class StudentDashboard extends JFrame {
    private JList<String> examList;
    private Connection conn;
    private DefaultListModel<String> examListModel;

    public StudentDashboard(Connection conn) {
        this.conn = conn;
        this.examListModel = new DefaultListModel<>();
    }

    // تحميل الامتحانات من قاعدة البيانات
    private void loadExams() {
        ArrayList<String> exams = DatabaseHelper.getExams(); // استرجاع الامتحانات من قاعدة البيانات
        examListModel.clear(); // تفريغ القائمة القديمة
        for (String exam : exams) {
            examListModel.addElement(exam); // إضافة الامتحانات إلى القائمة
        }
        examList.setModel(examListModel); // تحديث النموذج
    }

    private int getQuestionIdByText(Connection conn, String questionText) throws SQLException {
    String query = "SELECT id FROM questions WHERE question_text = ?";
    try (PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setString(1, questionText);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
    }
    throw new SQLException("Question not found: " + questionText);
}


    // تحميل الأسئلة الخاصة بالامتحان
    private void loadExamQuestions(String examTitle) {
        String query = """
                SELECT q.question_text, q.option1, q.option2, q.option3, q.option4 
                FROM questions q
                JOIN exams e ON q.exam_id = e.id
                WHERE e.title = ?""";

        int examId = DatabaseHelper.getExamIdByTitle(examTitle); // استرجاع معرّف الامتحان

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, examTitle);
            ResultSet rs = ps.executeQuery();

            // نافذة جديدة لعرض الأسئلة
            JFrame examFrame = new JFrame("Exam: " + examTitle);
            examFrame.setSize(600, 400);
            examFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            examFrame.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            while (rs.next()) {
                String questionText = rs.getString("question_text");
                String option1 = rs.getString("option1");
                String option2 = rs.getString("option2");
                String option3 = rs.getString("option3");
                String option4 = rs.getString("option4");

                // إنشاء حقل السؤال
                JPanel questionPanel = new JPanel();
                //questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
                questionPanel.setPreferredSize(new Dimension(400,100));
                questionPanel.setBorder(BorderFactory.createTitledBorder(questionText));

                // إضافة الخيارات
                ButtonGroup group = new ButtonGroup();
                JRadioButton option1Button = new JRadioButton(option1);
                JRadioButton option2Button = new JRadioButton(option2);
                JRadioButton option3Button = new JRadioButton(option3);
                JRadioButton option4Button = new JRadioButton(option4);

                group.add(option1Button);
                group.add(option2Button);
                group.add(option3Button);
                group.add(option4Button);

                questionPanel.add(option1Button);
                questionPanel.add(option2Button);
                questionPanel.add(option3Button);
                questionPanel.add(option4Button);

                panel.add(questionPanel);
            }

            // زر الإرسال (Submit)
            JButton submitButton = new JButton("Submit");
            submitButton.addActionListener(e -> {
            String studentName = JOptionPane.showInputDialog("Enter your name:");
            if (studentName == null || studentName.isEmpty()) {
                JOptionPane.showMessageDialog(examFrame, "Name is required.");
                return;
            }

            try (Connection conn = DatabaseHelper.getConnection()) {
                for (Component comp : panel.getComponents()) {
                    if (comp instanceof JPanel questionPanel) {
                        // العثور على السؤال والجواب
                        String questionText = ((TitledBorder) questionPanel.getBorder()).getTitle();
                        int questionId = getQuestionIdByText(conn, questionText); // استرجاع ID السؤال من النص
                        String selectedAnswer = null;

                        for (Component option : questionPanel.getComponents()) {
                            if (option instanceof JRadioButton radioButton && radioButton.isSelected()) {
                                selectedAnswer = radioButton.getText();
                                break;
                            }
                        }

                        if (selectedAnswer != null) {
                            DatabaseHelper.saveStudentAnswer(studentName, examId, questionId, selectedAnswer);
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(examFrame, "Answers submitted successfully!");
            examFrame.dispose();
        });


        Map<String, Integer> questionMap = new HashMap<>(); // تخزين السؤال مع المعرّف

            while (rs.next()) {
                String questionText = rs.getString("question_text");
                int questionId = rs.getInt("id"); // الحصول على المعرّف من قاعدة البيانات
                questionMap.put(questionText, questionId); // تخزين المعلومة
                // بقية الكود...
            }
   
            panel.add(Box.createVerticalStrut(20));
            panel.add(submitButton);

            examFrame.add(new JScrollPane(panel));
            examFrame.setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void display() {
        JFrame frame = new JFrame("Student Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null); // Center the window

        // Title label
        JLabel title = new JLabel("Student Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        frame.add(title, BorderLayout.NORTH);

        // Left panel with buttons
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton showExamButton = new JButton("Show Exams");

        Dimension buttonSize = new Dimension(120, 30);
        showExamButton.setMaximumSize(buttonSize);

        leftPanel.add(showExamButton);
        leftPanel.add(Box.createVerticalGlue());

        frame.add(leftPanel, BorderLayout.WEST);

        // Center panel to display the exams list
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        examList = new JList<>(examListModel);
        JScrollPane examListScrollPane = new JScrollPane(examList);
        examListScrollPane.setBorder(BorderFactory.createTitledBorder("Exams List"));
        examListScrollPane.setPreferredSize(new Dimension(250, 300));

        centerPanel.add(examListScrollPane);
        frame.add(centerPanel, BorderLayout.CENTER);

        // Action for "Show Exams" button
        showExamButton.addActionListener(e -> loadExams()); // تحميل الامتحانات عند النقر

        // حدث لاختيار اختبار من القائمة
        examList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) { // التأكد من عدم التفاعل المزدوج
                String selectedExam = examList.getSelectedValue();
                if (selectedExam != null) {
                    loadExamQuestions(selectedExam); // تحميل أسئلة الاختبار
                }
            }
        });

        // Show the frame
        frame.setVisible(true);
    }

    public static void main(String[] args) throws SQLException {
        // افتراضياً، يمكنك تمرير اتصال فعلي أو وهمي هنا لاختبار الواجهة
        Connection conn = DatabaseHelper.getConnection(); // الحصول على الاتصال بقاعدة البيانات
        new StudentDashboard(conn).display();
                DatabaseHelper.createTables();

    }
}

