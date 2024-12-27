package project_2;

import javax.swing.*;
import java.awt.*;

public class ExamDetailFrame extends JFrame {
    public ExamDetailFrame(Exam exam) {
        super("Exam Details");

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Label for exam title
        JLabel examTitleLabel = new JLabel("Exam: " + exam.getTitle());
        examTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        examTitleLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(examTitleLabel);

        add(Box.createVerticalStrut(10));  // Adding space after title

        // Text area for displaying exam details
        JScrollPane questionScrollPane = new JScrollPane();
        JTextArea questionArea = new JTextArea(exam.displayExamDetails());
        questionArea.setEditable(false);
        questionArea.setWrapStyleWord(true);
        questionArea.setLineWrap(true);
        questionScrollPane.setViewportView(questionArea);

        add(questionScrollPane);

        add(Box.createVerticalStrut(10));  // Adding space before button

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.setAlignmentX(CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> dispose());

        add(closeButton);

        setVisible(true);
    }
}
