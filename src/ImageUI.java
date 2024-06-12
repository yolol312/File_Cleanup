import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ImageUI extends JFrame {
    private JButton startButton;
    private JButton exitButton;
    private JLabel imageLabel;

    public ImageUI() {
        setTitle("금쪽같은 내 파일");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        String imagePath = "C:\\UI.png"; // 실제 이미지 파일 경로 입력

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        ImageIcon imageIcon = new ImageIcon(imagePath);
        Image image = imageIcon.getImage().getScaledInstance(500, 300, Image.SCALE_DEFAULT);
        imageLabel.setIcon(new ImageIcon(image));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        startButton = new JButton("시작");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 시작 버튼 동작 수행
                FileCleanup fileOrganizerUI = new FileCleanup();
                fileOrganizerUI.setVisible(true);
            }
        });

        exitButton = new JButton("종료");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 종료 버튼 동작 수행
                dispose(); // 현재 프레임 닫기
            }
        });

        buttonPanel.add(startButton);
        buttonPanel.add(exitButton);

        add(imageLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ImageUI imageUI = new ImageUI();
                imageUI.setVisible(true);
            }
        });
    }
}