import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        FileSystem fileSystem = new FileSystem();

        // myfiles.txt dosyasını yükle
        System.out.println("Dosya sistemini myfiles.txt'den yüklüyor...");
        fileSystem.loadFromFile("src/myfiles.txt");
        System.out.println("Dosya sistemi başarıyla yüklendi!");

        Scanner scanner = new Scanner(System.in);
        int command;

        System.out.println("Dosya Sistemi Yöneticisine Hoş Geldiniz!");
        while (true) {
            System.out.println("\nMenü:");
            System.out.println("1. Dosya Sistemini Göster");
            System.out.println("2. Dizin Ekle");
            System.out.println("3. Dosya Ekle");
            System.out.println("4. Dizin Sil");
            System.out.println("5. Dosya Sil");
            System.out.println("6. İsimle Ara");
            System.out.println("7. Uzantıyla Ara");
            System.out.println("8. Yolu Göster");
            System.out.println("9. Çıkış");
            System.out.print("Seçiminizi girin (1-9): ");

            if (!scanner.hasNextInt()) {
                System.out.println("Geçersiz giriş. Lütfen 1 ile 9 arasında bir sayı girin.");
                scanner.next(); // Geçersiz girişi atla
                continue;
            }

            command = scanner.nextInt();
            scanner.nextLine(); // Buffer temizliği

            switch (command) {
                case 1: // Dosya Sistemini Göster
                    fileSystem.displayFileSystem();
                    break;

                case 2: // Dizin Ekle
                    System.out.print("Dizini eklemek istediğiniz yolu girin (örnek: /root/MyDocuments): ");
                    String dirPath = scanner.nextLine();
                    System.out.print("Yeni dizinin adını girin: ");
                    String dirName = scanner.nextLine();
                    fileSystem.addDirectory(dirPath, dirName);
                    break;

                case 3: // Dosya Ekle
                    System.out.print("Dosyayı eklemek istediğiniz yolu girin (örnek: /root/MyDocuments): ");
                    String filePath = scanner.nextLine();
                    System.out.print("Dosya adını girin (uzantısız): ");
                    String fileName = scanner.nextLine();
                    System.out.print("Dosya uzantısını girin (örnek: txt, pdf): ");
                    String extension = scanner.nextLine();
                    System.out.print("Son değiştirilme tarihini girin (gg.MM.yyyy): ");
                    String date = scanner.nextLine();
                    System.out.print("Dosya boyutunu girin (bayt cinsinden): ");
                    if (!scanner.hasNextInt()) {
                        System.out.println("Geçersiz boyut. Lütfen sayısal bir değer girin.");
                        scanner.next(); // Geçersiz girişi atla
                        break;
                    }
                    int size = scanner.nextInt();
                    scanner.nextLine(); // Buffer temizliği
                    System.out.print("Erişim seviyesini girin (USER/SYSTEM): ");
                    String access = scanner.nextLine().toUpperCase();
                    if (!access.equals("USER") && !access.equals("SYSTEM")) {
                        System.out.println("Geçersiz erişim seviyesi. Lütfen USER veya SYSTEM girin.");
                        break;
                    }
                    fileSystem.addFile(filePath, fileName, extension, date, size, access);
                    break;

                case 4: // Dizin Sil
                    System.out.print("Silmek istediğiniz dizinin tam yolunu girin (örnek: /root/MyDocuments/important): ");
                    String delDirPath = scanner.nextLine();
                    fileSystem.deleteDirectory(delDirPath);
                    break;

                case 5: // Dosya Sil
                    System.out.print("Silmek istediğiniz dosyanın tam yolunu girin (örnek: /root/MyDocuments/important/Application.txt): ");
                    String delFilePath = scanner.nextLine();
                    fileSystem.deleteFile(delFilePath);
                    break;

                case 6: // İsimle Ara
                    System.out.print("Aramak istediğiniz ismi girin: ");
                    String name = scanner.nextLine();
                    fileSystem.searchByName(name);
                    break;

                case 7: // Uzantıyla Ara
                    System.out.print("Aramak istediğiniz uzantıyı girin: ");
                    String ext = scanner.nextLine();
                    fileSystem.searchByExtension(ext);
                    break;

                case 8: // Yolu Göster
                    System.out.print("Göstermek istediğiniz yolun tam adını girin (örnek: /root/MyDocuments): ");
                    String displayPath = scanner.nextLine();
                    fileSystem.displayPath(displayPath);
                    break;

                case 9: // Çıkış
                    System.out.println("Çıkış yapılıyor...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Geçersiz seçim. Lütfen 1 ile 9 arasında bir sayı seçin.");
            }
        }
    }
}
