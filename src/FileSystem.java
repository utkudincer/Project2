import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

public class FileSystem {
    private Directory root;

    public FileSystem() {
        this.root = new Directory("root");
    }

    public Directory getRoot() {
        return root;
    }

    // Dosya sistemini myfiles.txt'den yükleme
    public void loadFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            Stack<Directory> directoryStack = new Stack<>();
            directoryStack.push(root);

            String line;
            while ((line = reader.readLine()) != null) {
                int indentLevel = getIndentLevel(line);
                String trimmedLine = line.trim();

                // Yeni bir klasör
                if (trimmedLine.startsWith("\\")) {
                    String directoryName = trimmedLine.substring(1);
                    Directory newDirectory = new Directory(directoryName);

                    while (directoryStack.size() > indentLevel + 1) {
                        directoryStack.pop();
                    }
                    Directory parentDirectory = directoryStack.peek();
                    parentDirectory.addSubDirectory(newDirectory);
                    directoryStack.push(newDirectory);

                } else if (trimmedLine.contains("##")) { // Yeni bir dosya
                    String[] fileData = trimmedLine.split("##");
                    if (fileData.length == 4) {
                        String fileName = fileData[0];
                        String lastModifiedDate = fileData[1];
                        int size = Integer.parseInt(fileData[2]);
                        String accessLevel = fileData[3];

                        String[] nameParts = fileName.split("\\.");
                        String name = nameParts[0];
                        String extension = nameParts.length > 1 ? nameParts[1] : "";

                        File newFile = new File(name, extension, lastModifiedDate, size, accessLevel);
                        Directory parentDirectory = directoryStack.peek();
                        parentDirectory.addFile(newFile);
                    } else {
                        System.out.println("Invalid file entry: " + trimmedLine);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    // İndent seviyesini belirleme
    private int getIndentLevel(String line) {
        int count = 0;
        while (count < line.length() && line.charAt(count) == '\t') {
            count++;
        }
        return count;
    }

    // Dosya sistemi görüntüleme
    public void displayFileSystem() {
        displayDirectory(root, 0);
    }

    private void displayDirectory(Directory directory, int level) {
        printIndent(level);
        System.out.println("\\" + directory.getName());

        // Dosyaları yazdır
        File tempFile = directory.getFirstFile();
        while (tempFile != null) {
            printIndent(level + 1);
            System.out.println(tempFile);
            tempFile = tempFile.getNextSiblingFile();
        }

        // Alt dizinleri yazdır
        Directory tempDir = directory.getFirstSubDirectory();
        while (tempDir != null) {
            displayDirectory(tempDir, level + 1);
            tempDir = tempDir.getNextSiblingDirectory();
        }
    }

    private void printIndent(int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("\t");
        }
    }

    // Belirtilen yola navigasyon
    private Directory navigateTo(String path) {
        if (path.equals("/")) return root;
        String[] parts = path.split("/");
        Directory current = root;
        for (String part : parts) {
            if (part.isEmpty()) continue;
            Directory tempDir = current.getFirstSubDirectory();
            boolean found = false;
            while (tempDir != null) {
                if (tempDir.getName().equals(part)) {
                    current = tempDir;
                    found = true;
                    break;
                }
                tempDir = tempDir.getNextSiblingDirectory();
            }
            if (!found) {
                System.out.println("Invalid path: " + path);
                return null;
            }
        }
        return current;
    }
    // Dizin ekleme
    public void addDirectory(String path, String name) {
        Directory parent = navigateTo(path);
        if (parent != null && parent.getAccessLevel().equals("USER")) {
            parent.addSubDirectory(new Directory(name));
        } else {
            System.out.println("Cannot add directory. Access denied or invalid path.");
        }
    }

    // Dosya ekleme
    public void addFile(String path, String name, String extension, String lastModifiedDate, int size, String accessLevel) {
        Directory parent = navigateTo(path);
        if (parent != null && parent.getAccessLevel().equals("USER")) {
            parent.addFile(new File(name, extension, lastModifiedDate, size, accessLevel));
        } else {
            System.out.println("Cannot add file. Access denied or invalid path.");
        }
    }

    // Dizin silme
    public void deleteDirectory(String path) {
        if (path.equals("/")) {
            System.out.println("Cannot delete root directory.");
            return;
        }

        String[] parts = path.split("/");
        String name = parts[parts.length - 1];
        String parentPath = path.substring(0, path.lastIndexOf('/'));
        if (parentPath.isEmpty()) parentPath = "/";

        Directory parent = navigateTo(parentPath);
        if (parent != null) {
            Directory prev = null;
            Directory current = parent.getFirstSubDirectory();
            while (current != null) {
                if (current.getName().equals(name)) {
                    if (current.canDelete() && parent.getAccessLevel().equals("USER")) {
                        if (prev == null) {
                            parent.setFirstSubDirectory(current.getNextSiblingDirectory());
                        } else {
                            prev.setNextSiblingDirectory(current.getNextSiblingDirectory());
                        }
                        System.out.println("Directory deleted successfully.");
                    } else {
                        System.out.println("Cannot delete directory. It contains SYSTEM files or access denied.");
                    }
                    return;
                }
                prev = current;
                current = current.getNextSiblingDirectory();
            }
            System.out.println("Directory not found.");
        }
    }

    // Dosya silme
    public void deleteFile(String path) {
        String[] parts = path.split("/");
        String fileName = parts[parts.length - 1];
        String parentPath = path.substring(0, path.lastIndexOf('/'));
        if (parentPath.isEmpty()) parentPath = "/";

        Directory parent = navigateTo(parentPath);
        if (parent != null && parent.getAccessLevel().equals("USER")) {
            File prev = null;
            File current = parent.getFirstFile();
            while (current != null) {
                if (current.getName().equals(fileName)) {
                    if (current.getAccessLevel().equals("USER")) {
                        if (prev == null) {
                            parent.setFirstFile(current.getNextSiblingFile());
                        } else {
                            prev.setNextSiblingFile(current.getNextSiblingFile());
                        }
                        System.out.println("File deleted successfully.");
                    } else {
                        System.out.println("Cannot delete file. Access denied.");
                    }
                    return;
                }
                prev = current;
                current = current.getNextSiblingFile();
            }
            System.out.println("File not found.");
        } else {
            System.out.println("Cannot delete file. Access denied or invalid path.");
        }
    }

    // İdme göre arama
    public void searchByName(String name) {
        searchByName(root, name, "");
    }

    private void searchByName(Directory dir, String name, String path) {
        String currentPath = path + "/" + dir.getName();

        // Dosyaları kontrol et
        File tempFile = dir.getFirstFile();
        while (tempFile != null) {
            if (tempFile.getName().equals(name)) {
                System.out.println(currentPath + "/" + tempFile.getName() + "." + tempFile.getExtension());
            }
            tempFile = tempFile.getNextSiblingFile();
        }

        // Alt dizinlerde arama yap
        Directory tempDir = dir.getFirstSubDirectory();
        while (tempDir != null) {
            if (tempDir.getName().equals(name)) {
                System.out.println(currentPath + "/" + tempDir.getName());
            }
            searchByName(tempDir, name, currentPath);
            tempDir = tempDir.getNextSiblingDirectory();
        }
    }

    // Uzantıya göre arama
    public void searchByExtension(String extension) {
        searchByExtension(root, extension, "");
    }

    private void searchByExtension(Directory dir, String extension, String path) {
        String currentPath = path + "/" + dir.getName();

        // Dosyaları kontrol et
        File tempFile = dir.getFirstFile();
        while (tempFile != null) {
            if (tempFile.getExtension().equals(extension)) {
                System.out.println(currentPath + "/" + tempFile.getName() + "." + tempFile.getExtension());
            }
            tempFile = tempFile.getNextSiblingFile();
        }

        // Alt dizinlerde arama yap
        Directory tempDir = dir.getFirstSubDirectory();
        while (tempDir != null) {
            searchByExtension(tempDir, extension, currentPath);
            tempDir = tempDir.getNextSiblingDirectory();
        }
    }

    // Yolu görüntüleme
    public void displayPath(String path) {
        Directory dir = navigateTo(path);
        if (dir != null) {
            System.out.println("Path: " + path);
        } else {
            System.out.println("Invalid path.");
        }
    }
}