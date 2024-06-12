import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileCleanup extends JFrame {
    private JButton organizeByTypeButton;
    private JButton organizeBySimilarNameButton;
    private JButton organizeByDateButton;
    private JButton deleteButton;
    private JButton selectFolderButton;
    private JList<File> fileList;
    private DefaultListModel<File> fileListModel;
    private JTextField selectedFolderTextField;
    private File selectedFolder; // 선택한 폴더를 저장하기 위한 변수

    public FileCleanup() {
        setTitle("금쪽같은 내 파일");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        organizeByTypeButton = new JButton("동일 파일종류 정리");
        organizeByTypeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                organizeFilesByType();
            }
        });

        organizeBySimilarNameButton = new JButton("유사 파일명 정리");
        organizeBySimilarNameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                organizeFilesBySimilarName();
            }
        });

        organizeByDateButton = new JButton("날짜별 정리");
        organizeByDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 날짜별 정리 기능 수행
                // TODO: 날짜별 정리 기능 구현
                JOptionPane.showMessageDialog(FileCleanup.this, "날짜별 정리 기능 실행");
            }
        });

        deleteButton = new JButton("삭제");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 삭제 기능 수행
                // TODO: 삭제 기능 구현
                JOptionPane.showMessageDialog(FileCleanup.this, "삭제 기능 실행");
            }
        });

        selectFolderButton = new JButton("폴더 선택");
        selectFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(FileCleanup.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFolder = fileChooser.getSelectedFile(); // 선택한 폴더를 저장
                    selectedFolderTextField.setText(selectedFolder.getAbsolutePath());
                    displayFiles(selectedFolder);
                }
            }
        });

        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);
        fileList.setCellRenderer(new FileListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 0, 30)); // 패딩 추가

        JPanel selectFolderPanel = new JPanel(new BorderLayout());
        JLabel selectFolderLabel = new JLabel("선택한 폴더: ");
        selectFolderLabel.setBorder(BorderFactory.createEmptyBorder(10, 30, 0, 30)); // 패딩 추가
        selectedFolderTextField = new JTextField();
        selectedFolderTextField.setBorder(BorderFactory.createEmptyBorder(10, 30, 0, 30)); // 패딩 추가
        selectedFolderTextField.setEditable(false);

        selectFolderPanel.add(selectFolderLabel, BorderLayout.WEST);
        selectFolderPanel.add(selectedFolderTextField, BorderLayout.CENTER);

        buttonPanel.add(organizeByTypeButton);
        buttonPanel.add(organizeBySimilarNameButton);
        buttonPanel.add(organizeByDateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(selectFolderButton);

        add(selectFolderPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void displayFiles(File folder) {
        fileListModel.clear();

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                fileListModel.addElement(file);
            }
        }
    }

    private void organizeFilesByType() {
        if (selectedFolder == null) {
            JOptionPane.showMessageDialog(FileCleanup.this, "폴더를 선택해주세요.");
            return;
        }

        // 선택한 폴더 내의 파일들을 가져옴
        File[] files = selectedFolder.listFiles();
        if (files == null || files.length == 0) {
            JOptionPane.showMessageDialog(FileCleanup.this, "폴더에 파일이 존재하지 않습니다.");
            return;
        }

        // 파일을 종류별로 그룹화
        Map<String, List<File>> fileGroups = groupFilesByType(files);

        // 그룹화된 파일을 새로운 폴더로 이동
        moveFilesToTypeFolders(fileGroups);

        // fileList 새로고침
        displayFiles(selectedFolder);
    }
    
 // 파일을 종류별로 그룹화하는 메서드
    private Map<String, List<File>> groupFilesByType(File[] files) {
    	// 파일 종류별로 그룹화된 결과를 담을 Map
        Map<String, List<File>> fileGroups = new HashMap<>();
        
        // 모든 파일을 순회하면서 그룹화
        for (File file : files) {
            if (file.isFile()) {
            	// 파일의 확장자를 가져옴
                String fileExtension = getFileExtension(file);
                // 해당 확장자를 가진 파일들을 담을 리스트를 가져옴
                List<File> groupedFiles = fileGroups.getOrDefault(fileExtension, new ArrayList<>());
                // 파일을 리스트에 추가
                groupedFiles.add(file);
                // 확장자를 키로하여 파일 리스트를 맵에 저장
                fileGroups.put(fileExtension, groupedFiles);
            }
        }

        return fileGroups;
    }
    // 파일의 확장자를 가져오는 메서드
    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");
        // 파일 이름에 점이 있고, 점 다음에 문자가 있는 경우 확장자를 반환
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        // 확장자가 없는 경우 빈 문자열 반환
        return "";
    }

    private void moveFilesToTypeFolders(Map<String, List<File>> fileGroups) {
        for (Map.Entry<String, List<File>> entry : fileGroups.entrySet()) {
            String fileType = entry.getKey();
            List<File> files = entry.getValue();

            File typeFolder = new File(selectedFolder.getAbsolutePath() + File.separator + fileType);
            if (!typeFolder.exists()) {
                typeFolder.mkdir();
            }

            for (File file : files) {
                Path sourcePath = file.toPath();
                Path destinationPath = new File(typeFolder.getAbsolutePath() + File.separator + file.getName()).toPath();

                try {
                    Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void organizeFilesBySimilarName() {
        if (selectedFolder == null) {
            JOptionPane.showMessageDialog(FileCleanup.this, "폴더를 선택해주세요.");
            return;
        }

        // 선택한 폴더 내의 파일들을 가져옴
        File[] files = selectedFolder.listFiles();
        if (files == null || files.length == 0) {
            JOptionPane.showMessageDialog(FileCleanup.this, "폴더에 파일이 존재하지 않습니다.");
            return;
        }

        // 파일을 그룹화
        Map<String, List<File>> fileGroups = groupFiles(files);

        // 중복 파일을 새로운 폴더로 이동
        moveDuplicateFilesToGroupFolder(fileGroups);

        // fileList 새로고침
        displayFiles(selectedFolder);
    }

    private static void moveDuplicateFilesToGroupFolder(Map<String, List<File>> fileGroups) {
        for (Map.Entry<String, List<File>> entry : fileGroups.entrySet()) {
            String groupName = entry.getKey();
            List<File> files = entry.getValue();

            if (files.size() > 1) {
                for (File file : files) {
                    moveFileToGroupFolder(file, groupName);
                }
            } else {
                moveFileToGroupFolder(files.get(0), groupName); // Move single file to the group folder
            }
        }
    }

    private static void moveFileToGroupFolder(File file, String groupName) {
        String directoryPath = file.getParent(); // 파일이 위치한 디렉토리 경로
        String newFolderPath = directoryPath + File.separator + groupName; // 새로운 폴더 경로

        File newFolder = new File(newFolderPath);
        if (!newFolder.exists()) {
            if (newFolder.mkdirs()) {
                System.out.println("새로운 폴더 생성: " + newFolderPath);
            } else {
                System.out.println("새로운 폴더 생성 실패: " + newFolderPath);
                return;
            }
        }

        String newFilePath = newFolderPath + File.separator + file.getName(); // 이동할 파일 경로
        Path source = file.toPath();
        Path destination = new File(newFilePath).toPath();

        try {
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("파일 이동: " + file.getName() + " -> " + newFilePath);
        } catch (IOException e) {
            System.out.println("파일 이동 실패: " + file.getName());
            e.printStackTrace();
        }
    }
    private static Map<String, List<File>> groupFiles(File[] files) {
        Map<String, List<File>> fileGroups = new HashMap<>();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String key = getFileKey(file, files); // 파일을 그룹화할 키 생성

            List<File> groupedFiles = fileGroups.getOrDefault(key, new ArrayList<>());
            groupedFiles.add(file); // 그룹에 파일 추가

            fileGroups.put(key, groupedFiles);
        }

        return fileGroups;
    }

    private static String getFileKey(File file, File[] files) {
        String fileName = file.getName().replaceAll("[<>:\"/\\|?* ]", ""); // 파일명으로 사용 불가능한 문자들 제거
        String[] fileNameParts = fileName.split("\\.");
        String filePrefix = fileNameParts[0];
        String longestCommonString = "";

        // 파일명을 비교하여 가장 긴 공통된 문자열을 찾습니다.
        for (File otherFile : files) {
            if (otherFile.equals(file)) {
                continue; // 같은 파일은 비교하지 않습니다.
            }

            String otherFileName = otherFile.getName().replace("[<>:\"/\\|?* ]", "");
            String[] otherFileNameParts = otherFileName.split("\\.");

            // 파일명에서 공통된 문자열을 찾습니다.
            String commonString = findCommonString(filePrefix, otherFileNameParts[0]);
            if (commonString.length() > longestCommonString.length()) {
                longestCommonString = commonString;
            }
        }
        
        // 가장 긴 공통된 문자열을 그룹화 키로 사용합니다.
        if (!longestCommonString.isEmpty()) {
            return longestCommonString;
        }
        
        // 공통된 문자열을 찾지 못한 경우 파일 접두어를 그룹화 키로 사용합니다.
        return filePrefix;
    }

    private static String findCommonString(String str1, String str2) {
        StringBuilder commonString = new StringBuilder();
        int length = Math.min(str1.length(), str2.length());

        // 두 문자열을 비교하여 공통된 문자열을 찾습니다.
        for (int i = 0; i < length; i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                break;
            }
            commonString.append(str1.charAt(i));
        }

        return commonString.toString();
    }

    private static void createGroupFolderAndMoveFiles(String directoryPath, String groupName, List<File> files) {
        String newFolderPath = directoryPath + File.separator + groupName; // 새로운 폴더 경로

        File newFolder = new File(newFolderPath);
        if (!newFolder.exists()) {
            if (newFolder.mkdirs()) {
                System.out.println("새로운 폴더 생성: " + newFolderPath);
            } else {
                System.out.println("새로운 폴더 생성 실패: " + newFolderPath);
                return;
            }
        }

        for (File file : files) {
            String newFilePath = newFolderPath + File.separator + file.getName(); // 이동할 파일 경로
            Path source = file.toPath();
            Path destination = new File(newFilePath).toPath();

            try {
                Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("파일 이동: " + file.getName() + " -> " + newFilePath);
            } catch (IOException e) {
                System.out.println("파일 이동 실패: " + file.getName());
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FileCleanup();
            }
        });
    }

    private static class FileListCellRenderer extends DefaultListCellRenderer {
        private static final FileSystemView FILE_SYSTEM_VIEW = FileSystemView.getFileSystemView();

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            File file = (File) value;
            setText(FILE_SYSTEM_VIEW.getSystemDisplayName(file));
            setIcon(FILE_SYSTEM_VIEW.getSystemIcon(file));
            setBorder(new EmptyBorder(5, 10, 5, 10));

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            return this;
        }
    }
}
