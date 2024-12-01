public class Directory {
    private String name;
    private String lastModifiedDate;
    private String accessLevel; // USER or SYSTEM
    private Directory firstSubDirectory;
    private Directory nextSiblingDirectory;
    private File firstFile;
    private File nextSiblingFile;

    public Directory(String name) {
        this.name = name;
        this.lastModifiedDate = "empty";
        this.accessLevel = "USER";
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
            System.out.println("Access denied. Cannot add subdirectory.");
            return;
        }
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
            System.out.println("Access denied. Cannot add file.");
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
}