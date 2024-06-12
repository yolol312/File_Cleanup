import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileGrouper {

    public static void main(String[] args) {
        String directoryPath = "C:\\Users\\안성모\\Desktop\\새 폴더";
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null && files.length > 0) {
                Map<String, List<File>> fileGroups = groupFiles(files);

                for (Map.Entry<String, List<File>> entry : fileGroups.entrySet()) {
                    String groupName = entry.getKey();
                    List<File> groupFiles = entry.getValue();

                    System.out.println("Group: " + groupName);
                    for (File file : groupFiles) {
                        System.out.println(file.getName());
                    }
                    System.out.println();

                    createGroupFolderAndMoveFiles(directoryPath, groupName, groupFiles);
                }
            } else {
                System.out.println("해당 폴더에 파일이 존재하지 않습니다.");
            }
        } else {
            System.out.println("잘못된 폴더 경로입니다.");
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

    private static boolean isDuplicateFile(File file1, File file2) {
        // 중복 파일 여부를 확인하는 로직 구현
        // 예시로 파일 크기를 비교하는 방식을 사용
        return file1.length() == file2.length();
    }

    private static void moveFileToGroupFolder(File file, String groupName) {
        // 중복된 파일을 새로운 폴더로 이동하는 로직 구현
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

    private static String getFileKey(File file, File[] files) {
        String fileName = file.getName();
        String[] fileNameParts = fileName.split("\\."); // 파일명을 확장자와 나눕니다.
        String filePrefix = fileNameParts[0]; // 파일명의 접두어를 가져옵니다.

        // 파일명을 비교하여 동일한 문자열을 찾습니다.
        for (File otherFile : files) {
            if (otherFile.equals(file)) {
                continue; // 같은 파일은 비교하지 않습니다.
            }

            String otherFileName = otherFile.getName();
            String[] otherFileNameParts = otherFileName.split("\\.");

            // 파일명에서 동일한 문자열을 찾습니다.
            String commonString = findCommonString(filePrefix, otherFileNameParts[0]);
            if (!commonString.isEmpty()) {
                return commonString;
            }
        }

        // 동일한 문자열을 찾지 못한 경우 파일명을 그룹화 키로 사용합니다.
        return filePrefix;
    }

    private static String findCommonString(String str1, String str2) {
        StringBuilder commonString = new StringBuilder();
        int length = Math.min(str1.length(), str2.length());

        // 두 문자열을 비교하며 동일한 문자를 찾습니다.
        for (int i = 0; i < length; i++) {
            if (str1.charAt(i) == str2.charAt(i)) {
                commonString.append(str1.charAt(i));
            } else {
                break; // 동일하지 않은 문자를 만나면 반복문을 종료합니다.
            }
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
}