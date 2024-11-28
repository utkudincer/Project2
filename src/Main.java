import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        FileSystem fileSystem = new FileSystem();

        // myfiles.txt dosyasını yükle
        System.out.println("Loading file system from myfiles.txt...");
        fileSystem.loadFromFile("src/myfiles.txt");
        System.out.println("File system loaded successfully!");

        Scanner scanner = new Scanner(System.in);
        int command;

        System.out.println("Welcome to the File System Manager!");
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Display File System");
            System.out.println("2. Add Directory");
            System.out.println("3. Add File");
            System.out.println("4. Delete Directory");
            System.out.println("5. Delete File");
            System.out.println("6. Search by Name");
            System.out.println("7. Search by Extension");
            System.out.println("8. Display Path");
            System.out.println("9. Exit");
            System.out.print("Enter your choice (1-9): ");

            command = scanner.nextInt();
            scanner.nextLine(); // Buffer temizliği

            switch (command) {
                case 1: // Display File System
                    fileSystem.displayFileSystem();
                    break;

                case 2: // Add Directory
                    System.out.print("Path: ");
                    String dirPath = scanner.nextLine();
                    System.out.print("Directory name: ");
                    String dirName = scanner.nextLine();
                    fileSystem.addDirectory(dirPath, dirName);
                    break;

                case 3: // Add File
                    System.out.print("Path: ");
                    String filePath = scanner.nextLine();
                    System.out.print("File name: ");
                    String fileName = scanner.nextLine();
                    System.out.print("Extension: ");
                    String extension = scanner.nextLine();
                    System.out.print("Last modified date: ");
                    String date = scanner.nextLine();
                    System.out.print("Size (bytes): ");
                    int size = scanner.nextInt();
                    scanner.nextLine(); // Buffer temizliği
                    System.out.print("Access level (USER/SYSTEM): ");
                    String access = scanner.nextLine();
                    fileSystem.addFile(filePath, fileName, extension, date, size, access);
                    break;

                case 4: // Delete Directory
                    System.out.print("Path: ");
                    String delDirPath = scanner.nextLine();
                    fileSystem.deleteDirectory(delDirPath);
                    break;

                case 5: // Delete File
                    System.out.print("Path: ");
                    String delFilePath = scanner.nextLine();
                    fileSystem.deleteFile(delFilePath);
                    break;

                case 6: // Search by Name
                    System.out.print("Name to search: ");
                    String name = scanner.nextLine();
                    fileSystem.searchByName(name);
                    break;

                case 7: // Search by Extension
                    System.out.print("Extension to search: ");
                    String ext = scanner.nextLine();
                    fileSystem.searchByExtension(ext);
                    break;

                case 8: // Display Path
                    System.out.print("Path to display: ");
                    String displayPath = scanner.nextLine();
                    fileSystem.displayPath(displayPath);
                    break;

                case 9: // Exit
                    System.out.println("Exiting...");
                    return;

                default:
                    System.out.println("Invalid choice. Please select a number between 1 and 9.");
            }
        }
    }
}