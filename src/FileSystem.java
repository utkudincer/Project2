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
                if (line.trim().isEmpty()) {
                    continue; // Boş satırları atla
                }
                int indentLevel = getIndentLevel(line);
                String trimmedLine = line.trim();

                // Yeni Dizin
                if (trimmedLine.startsWith("\\")) {
                    String directoryName = trimmedLine.substring(1);
                    Directory newDirectory = new Directory(directoryName);

                    while (directoryStack.size() > indentLevel + 1) {
                        directoryStack.pop();
                    }
                    Directory parentDirectory = directoryStack.peek();
                    parentDirectory.addSubDirectory(newDirectory);
                    directoryStack.push(newDirectory);

                } else if (trimmedLine.contains("##")) { // Yeni Dosya
                    String[] fileData = trimmedLine.split("##");
                    if (fileData.length == 4) {
                        String fileName = fileData[0];
                        String lastModifiedDate = fileData[1];
                        int size = Integer.parseInt(fileData[2]);
                        String accessLevel = fileData[3];

                        String[] nameParts = fileName.split("\\.");
                        if (nameParts.length < 2) {
                            System.out.println("Geçersiz dosya girişi (uzantı eksik): " + trimmedLine);
                            continue;
                        }
                        String name = nameParts[0];
                        String extension = nameParts[1];

                        File newFile = new File(name, extension, lastModifiedDate, size, accessLevel);
                        Directory parentDirectory = directoryStack.peek();
                        parentDirectory.addFile(newFile);
                    } else {
                        System.out.println("Geçersiz dosya girişi: " + trimmedLine);
                    }
                } else {
                    System.out.println("Tanımlanamayan satır formatı: " + trimmedLine);
                }
            }
        } catch (IOException e) {
            System.err.println("Dosya okuma hatası: " + e.getMessage());
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
                System.out.println("Geçersiz yol segmenti: " + part);
                return null;
            }
        }
        return current;
    }

    // Dizin ekleme
    public void addDirectory(String path, String name) {
        Directory parent = navigateTo(path);
        if (parent != null && parent.getAccessLevel().equals("USER")) {
            Directory newDir = new Directory(name);
            parent.addSubDirectory(newDir);
            System.out.println("Dizin başarıyla eklendi: " + path + "/" + name);
        } else {
            System.out.println("Dizin eklenemiyor. Erişim reddedildi veya geçersiz yol.");
        }
    }

    // Dosya ekleme
    public void addFile(String path, String name, String extension, String lastModifiedDate, int size, String accessLevel) {
        Directory parent = navigateTo(path);
        if (parent != null && parent.getAccessLevel().equals("USER")) {
            File newFile = new File(name, extension, lastModifiedDate, size, accessLevel);
            parent.addFile(newFile);
            System.out.println("Dosya başarıyla eklendi: " + path + "/" + name + "." + extension);
        } else {
            System.out.println("Dosya eklenemiyor. Erişim reddedildi veya geçersiz yol.");
        }
    }

    // Dizin silme
    public void deleteDirectory(String path) {
        if (path.equals("/")) {
            System.out.println("Kök dizin silinemiyor.");
            return;
        }

        String[] parts = path.split("/");
        if (parts.length < 2) {
            System.out.println("Geçersiz dizin yolu.");
            return;
        }
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
                        System.out.println("Dizin başarıyla silindi: " + path);
                        parent.recalculateLastModifiedDate(); // Üst dizinleri güncelle
                    } else {
                        System.out.println("Dizin silinemiyor. Sistem dosyaları içeriyor veya erişim reddedildi.");
                    }
                    return;
                }
                prev = current;
                current = current.getNextSiblingDirectory();
            }
            System.out.println("Dizin bulunamadı: " + path);
        }
    }

    // Dosya silme metodu (Yukarıda tanımlandı)
    public void deleteFile(String path) {
        // Yol parçalarına bölme
        String[] parts = path.split("/");
        if (parts.length < 2) {
            System.out.println("Geçersiz dosya yolu.");
            return;
        }

        // Dosya adını ve uzantısını ayrıştırma
        String filePart = parts[parts.length - 1];
        String parentPath = path.substring(0, path.lastIndexOf('/'));
        if (parentPath.isEmpty()) parentPath = "/";

        String[] nameParts = filePart.split("\\.");
        if (nameParts.length < 2) {
            System.out.println("Geçersiz dosya adı formatı. Uzantı olmalı.");
            return;
        }
        String name = nameParts[0];
        String extension = nameParts[1];

        // Parent dizini bulma
        Directory parent = navigateTo(parentPath);
        if (parent != null && parent.getAccessLevel().equals("USER")) {
            File prev = null;
            File current = parent.getFirstFile();
            while (current != null) {
                if (current.getName().equals(name) && current.getExtension().equals(extension)) {
                    if (current.getAccessLevel().equals("USER")) {
                        if (prev == null) {
                            parent.setFirstFile(current.getNextSiblingFile());
                        } else {
                            prev.setNextSiblingFile(current.getNextSiblingFile());
                        }
                        System.out.println("Dosya başarıyla silindi: " + path);
                        parent.recalculateLastModifiedDate(); // Üst dizinleri güncelle
                    } else {
                        System.out.println("Dosya silinemiyor. Erişim reddedildi.");
                    }
                    return;
                }
                prev = current;
                current = current.getNextSiblingFile();
            }
            System.out.println("Dosya bulunamadı: " + path);
        } else {
            System.out.println("Dosya silinemiyor. Erişim reddedildi veya geçersiz yol.");
        }
    }

    // İsme göre arama
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
            System.out.println("Geçersiz yol.");
        }
    }
}
