public class Directory {
    private String name;
    private String lastModifiedDate;
    private String accessLevel; // USER veya SYSTEM
    private Directory parent; // Üst dizine referans
    private Directory firstSubDirectory;
    private Directory nextSiblingDirectory;
    private File firstFile;
    private File nextSiblingFile;

    public Directory(String name) {
        this.name = name;
        this.lastModifiedDate = "N/A";
        this.accessLevel = "USER";
        this.parent = null;
        this.firstSubDirectory = null;
        this.nextSiblingDirectory = null;
        this.firstFile = null;
        this.nextSiblingFile = null;
    }

    // Getter ve Setter metotları
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(String lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }

    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }

    public Directory getParent() { return parent; }
    public void setParent(Directory parent) { this.parent = parent; }

    public Directory getFirstSubDirectory() { return firstSubDirectory; }
    public void setFirstSubDirectory(Directory firstSubDirectory) { this.firstSubDirectory = firstSubDirectory; }

    public Directory getNextSiblingDirectory() { return nextSiblingDirectory; }
    public void setNextSiblingDirectory(Directory nextSiblingDirectory) { this.nextSiblingDirectory = nextSiblingDirectory; }

    public File getFirstFile() { return firstFile; }
    public void setFirstFile(File firstFile) { this.firstFile = firstFile; }

    public File getNextSiblingFile() { return nextSiblingFile; }
    public void setNextSiblingFile(File nextSiblingFile) { this.nextSiblingFile = nextSiblingFile; }

    // Alt dizin ekleme
    public void addSubDirectory(Directory newDirectory) {
        if (!this.accessLevel.equals("USER")) {
            System.out.println("Erişim reddedildi. Alt dizin eklenemiyor.");
            return;
        }
        newDirectory.setParent(this); // Parent referansını ayarla
        if (this.firstSubDirectory == null) {
            this.firstSubDirectory = newDirectory;
        } else {
            Directory temp = this.firstSubDirectory;
            while (temp.getNextSiblingDirectory() != null) {
                temp = temp.getNextSiblingDirectory();
            }
            temp.setNextSiblingDirectory(newDirectory);
        }
        updateLastModifiedDate(newDirectory.getLastModifiedDate());
    }

    // Dosya ekleme
    public void addFile(File newFile) {
        if (!this.accessLevel.equals("USER")) {
            System.out.println("Erişim reddedildi. Dosya eklenemiyor.");
            return;
        }
        if (this.firstFile == null) {
            this.firstFile = newFile;
        } else {
            File temp = this.firstFile;
            while (temp.getNextSiblingFile() != null) {
                temp = temp.getNextSiblingFile();
            }
            temp.setNextSiblingFile(newFile);
        }
        updateLastModifiedDate(newFile.getLastModifiedDate());
    }

    // Son değiştirilme tarihini güncelleme
    public void updateLastModifiedDate(String date) {
        if (this.lastModifiedDate.equals("N/A") || date.compareTo(this.lastModifiedDate) > 0) {
            this.lastModifiedDate = date;
            if (this.parent != null) {
                this.parent.updateLastModifiedDate(date);
            }
        }
    }

    // Boyutu hesaplama
    public int calculateSize() {
        int totalSize = 0;
        File tempFile = this.firstFile;
        while (tempFile != null) {
            totalSize += tempFile.getSize();
            tempFile = tempFile.getNextSiblingFile();
        }

        Directory tempDir = this.firstSubDirectory;
        while (tempDir != null) {
            totalSize += tempDir.calculateSize();
            tempDir = tempDir.getNextSiblingDirectory();
        }
        return totalSize;
    }

    // Silme işlemi için kontrol
    public boolean canDelete() {
        File tempFile = this.firstFile;
        while (tempFile != null) {
            if (tempFile.getAccessLevel().equals("SYSTEM")) return false;
            tempFile = tempFile.getNextSiblingFile();
        }

        Directory tempDir = this.firstSubDirectory;
        while (tempDir != null) {
            if (!tempDir.canDelete()) return false;
            tempDir = tempDir.getNextSiblingDirectory();
        }
        return true;
    }

    @Override
    public String toString() {
        return "Directory: " + name + " (Last Modified: " + lastModifiedDate + ", Access Level: " + accessLevel + ")";
    }

    // Son değiştirilme tarihini yeniden hesaplama
    public void recalculateLastModifiedDate() {
        String latestDate = "N/A";

        // Dosyaların son değiştirilme tarihlerini kontrol et
        File tempFile = this.firstFile;
        while (tempFile != null) {
            if (tempFile.getLastModifiedDate().compareTo(latestDate) > 0) {
                latestDate = tempFile.getLastModifiedDate();
            }
            tempFile = tempFile.getNextSiblingFile();
        }

        // Alt dizinlerin son değiştirilme tarihlerini kontrol et
        Directory tempDir = this.firstSubDirectory;
        while (tempDir != null) {
            if (tempDir.getLastModifiedDate().compareTo(latestDate) > 0) {
                latestDate = tempDir.getLastModifiedDate();
            }
            tempDir = tempDir.getNextSiblingDirectory();
        }

        if (!this.lastModifiedDate.equals(latestDate)) {
            this.lastModifiedDate = latestDate;
            if (this.parent != null) {
                this.parent.recalculateLastModifiedDate();
            }
        }
    }
}
