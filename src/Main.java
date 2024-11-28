import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        FileSystem fileSystem = new FileSystem();

        // Load myfiles.txt
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

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number between 1 and 9.");
                scanner.next(); // Skip invalid input
                continue;
            }

            command = scanner.nextInt();
            scanner.nextLine(); // Clear buffer

            switch (command) {
                case 1: // Display File System
                    fileSystem.displayFileSystem();
                    break;

                case 2: // Add Directory
                    System.out.print("Enter the path where you want to add the directory like /root/usr/MyDocuments: ");
                    String dirPath = scanner.nextLine();
                    System.out.print("Enter the name of the new directory: ");
                    String dirName = scanner.nextLine();
                    fileSystem.addDirectory(dirPath, dirName);
                    break;

                case 3: // Add File
                    System.out.print("Enter the path where you want to add the file like /root/usr/MyDocuments: ");
                    String filePath = scanner.nextLine();
                    System.out.print("Enter the file name without extension: ");
                    String fileName = scanner.nextLine();
                    System.out.print("Enter the file extension like txt pdf... without dot: ");
                    String extension = scanner.nextLine();
                    System.out.print("Enter the last modified date (dd.MM.yyyy): ");
                    String date = scanner.nextLine();
                    System.out.print("Enter the size of the file in bytes: ");
                    if (!scanner.hasNextInt()) {
                        System.out.println("Invalid size. Please enter a numeric value.");
                        scanner.next(); // Skip invalid input
                        break;
                    }
                    int size = scanner.nextInt();
                    scanner.nextLine(); // Clear buffer
                    System.out.print("Enter the access level (USER/SYSTEM): ");
                    String access = scanner.nextLine().toUpperCase();
                    if (!access.equals("USER") && !access.equals("SYSTEM")) {
                        System.out.println("Invalid access level. Please enter either USER or SYSTEM.");
                        break;
                    }
                    fileSystem.addFile(filePath, fileName, extension, date, size, access);
                    break;

                case 4: // Delete Directory
                    System.out.print("Enter the full path of the directory to delete like /root/usr/MyDocuments/important: ");
                    String delDirPath = scanner.nextLine();
                    fileSystem.deleteDirectory(delDirPath);
                    break;

                case 5: // Delete File
                    System.out.print("Enter the full path of the file to delete like /root/usr/MyDocuments/important/Application.txt): ");
                    String delFilePath = scanner.nextLine();
                    fileSystem.deleteFile(delFilePath);
                    break;

                case 6: // Search by Name
                    System.out.print("Enter the name to search: ");
                    String name = scanner.nextLine();
                    fileSystem.searchByName(name);
                    break;

                case 7: // Search by Extension
                    System.out.print("Enter the extension to search: ");
                    String ext = scanner.nextLine();
                    fileSystem.searchByExtension(ext);
                    break;

                case 8: // Display Path
                    System.out.print("Enter the path to display like /root/usr/MyDocuments): ");
                    String displayPath = scanner.nextLine();
                    fileSystem.displayPath(displayPath);
                    break;

                case 9: // Exit
                    System.out.println("Exiting...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please select a number between 1 and 9.");
            }
        }
    }
}
