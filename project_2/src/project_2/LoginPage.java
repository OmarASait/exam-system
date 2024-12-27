package project_2;
import project_2.TeacherDashboard;
import project_2.StudentDashboard;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginPage extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private Connection connection;
    private JButton loginButton;
    private JButton adminPageButton;
    private AdminPage adminPage; 


    public LoginPage(Connection connection) {
        this.connection = connection;  // الاتصال بقاعدة البيانات يتم تمريره هنا

        setTitle("Login Page");
        setLayout(new BorderLayout());
        setSize(400, 300); // تحديد حجم النافذة
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // إغلاق التطبيق عند غلق النافذة

        // إعداد الفريم
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // إضافة عنوان "تسجيل الدخول"
        JLabel titleLabel = new JLabel("Welcome to the Login Page", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 51, 102));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(400, 20)); // يمكن تعديل 20 حسب الحاجة
        mainPanel.add(spacerPanel, BorderLayout.CENTER);

        // إعداد النموذج (نصوص وأزرار)
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        loginButton = new JButton("Login");
        adminPageButton = new JButton("Go to Admin Page");

        // تنسيق الأزرار
        loginButton.setBackground(new Color(34, 167, 240));
        loginButton.setForeground(Color.WHITE);
        adminPageButton.setBackground(new Color(192, 57, 43));
        adminPageButton.setForeground(Color.WHITE);

        // إضافة الأحداث للأزرار
        loginButton.addActionListener(e -> {
            try {
                login();
            } catch (SQLException ex) {
                Logger.getLogger(LoginPage.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        adminPageButton.addActionListener(e -> openAdminPage());

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        // إعداد لوحة الأزرار
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(adminPageButton);

        // إضافة المكونات إلى الفريم
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // إضافة المكونات إلى الإطار
        add(mainPanel);

        setLocationRelativeTo(null); // لضبط نافذة التطبيق في وسط الشاشة
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // الإبقاء على النافذة مفتوحة عند إغلاقها

    }

    // دالة لتسجيل الدخول والتحقق من البيانات
    private void login() throws SQLException {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // تحقق من بيانات الدخول واسترجاع نوع المستخدم
            String userType = checkCredentials(username, password);
        if (userType != null) {
            JOptionPane.showMessageDialog(this, "Login successful!");

            if (userType.equals("teacher")) {
               new TeacherDashboard(connection).display();
                

            } else if (userType.equals("student")) {
               new StudentDashboard(connection).display();  // عرض النافذة
                }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }
            
    }

    // دالة للتحقق من البيانات في قاعدة البيانات
   private String checkCredentials(String username, String password) {
        // استعلام للتحقق من المعلم
        String queryTeacher = "SELECT * FROM teachers WHERE username = ? AND password = ?";
        // استعلام للتحقق من الطالب
        String queryStudent = "SELECT * FROM student1 WHERE username = ? AND password = ?";  

        try (PreparedStatement psTeacher = connection.prepareStatement(queryTeacher)) {
            psTeacher.setString(1, username);
            psTeacher.setString(2, password);
            ResultSet rsTeacher = psTeacher.executeQuery();

            if (rsTeacher.next()) {
                return "teacher"; // Found in teachers table
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (PreparedStatement psStudent = connection.prepareStatement(queryStudent)) {
            psStudent.setString(1, username);
            psStudent.setString(2, password);
            ResultSet rsStudent = psStudent.executeQuery();

            if (rsStudent.next()) {
                return "student"; // Found in student1 table
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // No match found
    }

    // دالة لفتح صفحة الإدارة
    private void openAdminPage() {
        // عند الضغط على زر "Admin Login"، سيتم فتح صفحة الإدارة
        if (adminPage == null) {
            adminPage = new AdminPage(); // إنشاء كائن من صفحة الإدارة فقط إذا لم يكن قد تم إنشاؤه من قبل
            adminPage.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // التأكد من أنه عند إغلاق نافذة الإدارة، لا تغلق نافذة تسجيل الدخول
            adminPage.setSize(600, 400); // تحديد حجم نافذة الإدارة
            adminPage.setLocationRelativeTo(this); // تعيين الموقع نسبيًا
        }
        adminPage.setVisible(true); // فتح صفحة الإدارة
    }

    public static void main(String[] args) {
        // الاتصال بقاعدة البيانات باستخدام DatabaseHelper
        try {
            Connection connection = DatabaseHelper.getConnection();
            LoginPage loginPage = new LoginPage(connection); // إنشاء نافذة تسجيل الدخول
            loginPage.setVisible(true); // عرض النافذة
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error connecting to database: " + e.getMessage());
        }
    }
}

