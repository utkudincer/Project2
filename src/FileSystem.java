import java.util.*;
import java.io.*;


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
        while (line.startsWith("\t", count)) {
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

        for (File file : directory.getFiles()) {
            printIndent(level + 1);
            System.out.println(file);
        }

        for (Directory subDirectory : directory.getSubDirectories()) {
            displayDirectory(subDirectory, level + 1);
        }
    }

    private void printIndent(int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("\t");
        }
    }

    private Directory navigateTo(String path) {
        String[] parts = path.split("/");
        Directory current = root;
        for (String part : parts) {
            if (part.isEmpty()) continue;
            Optional<Directory> dir = current.getSubDirectories().stream().filter(d -> d.getName().equals(part)).findFirst();
            if (dir.isPresent()) {
                current = dir.get();
            } else {
                System.out.println("Invalid path: " + path);
                return null;
            }
        }
        return current;
    }

    public void addDirectory(String path, String name) {
        Directory parent = navigateTo(path);
        if (parent != null && parent.getAccessLevel().equals("USER")) {
            parent.addSubDirectory(new Directory(name));
        } else {
            System.out.println("Cannot add directory. Access denied or invalid path.");
        }
    }

    public void addFile(String path, String name, String extension, String lastModifiedDate, int size, String accessLevel) {
        Directory parent = navigateTo(path);
        if (parent != null && parent.getAccessLevel().equals("USER")) {
            parent.addFile(new File(name, extension, lastModifiedDate, size, accessLevel));
        } else {
            System.out.println("Cannot add file. Access denied or invalid path.");
        }
    }

    public void deleteDirectory(String path) {
        String[] parts = path.split("/");
        String name = parts[parts.length - 1];
        Directory parent = navigateTo(String.join("/", Arrays.copyOf(parts, parts.length - 1)));
        if (parent != null) {
            Optional<Directory> dirToDelete = parent.getSubDirectories().stream().filter(d -> d.getName().equals(name)).findFirst();
            if (dirToDelete.isPresent() && dirToDelete.get().canDelete()) {
                parent.getSubDirectories().remove(dirToDelete.get());
            } else {
                System.out.println("Cannot delete directory. It contains SYSTEM files or invalid path.");
            }
        }
    }

    public void deleteFile(String path) {
        String[] parts = path.split("/");
        String name = parts[parts.length - 1];
        Directory parent = navigateTo(String.join("/", Arrays.copyOf(parts, parts.length - 1)));
        if (parent != null) {
            Optional<File> fileToDelete = parent.getFiles().stream().filter(f -> f.getName().equals(name)).findFirst();
            if (fileToDelete.isPresent()) {
                parent.getFiles().remove(fileToDelete.get());
            } else {
                System.out.println("File not found.");
            }
        }
    }

    public void searchByName(String name) {
        searchByName(root, name, "");
    }

    private void searchByName(Directory dir, String name, String path) {
        for (File file : dir.getFiles()) {
            if (file.getName().equals(name)) {
                System.out.println(path + "/" + dir.getName() + "/" + file.getName());
            }
        }
        for (Directory subDir : dir.getSubDirectories()) {
            searchByName(subDir, name, path + "/" + dir.getName());
        }
    }

    public void searchByExtension(String extension) {
        searchByExtension(root, extension, "");
    }

    private void searchByExtension(Directory dir, String extension, String path) {
        for (File file : dir.getFiles()) {
            if (file.getExtension().equals(extension)) {
                System.out.println(path + "/" + dir.getName() + "/" + file.getName() + "." + extension);
            }
        }
        for (Directory subDir : dir.getSubDirectories()) {
            searchByExtension(subDir, extension, path + "/" + dir.getName());
        }
    }

    public void displayPath(String path) {
        Directory dir = navigateTo(path);
        if (dir != null) {
            System.out.println("Path: /" + path);
        }
    }
}
