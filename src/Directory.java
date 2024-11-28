import java.util.ArrayList;
import java.util.List;

public class Directory {
    private String name;
    private String lastModifiedDate;
    private String accessLevel; // USER or SYSTEM
    private List<Directory> subDirectories;
    private List<File> files;

    public Directory(String name) {
        this.name = name;
        this.lastModifiedDate = "N/A";
        this.accessLevel = "USER";
        this.subDirectories = new ArrayList<>();
        this.files = new ArrayList<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(String lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }

    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }

    public List<Directory> getSubDirectories() { return subDirectories; }
    public List<File> getFiles() { return files; }

    public void addSubDirectory(Directory directory) {
        if (this.accessLevel.equals("USER")) {
            subDirectories.add(directory);
            updateLastModifiedDate(directory.getLastModifiedDate());
        }
    }

    public void addFile(File file) {
        if (this.accessLevel.equals("USER")) {
            files.add(file);
            updateLastModifiedDate(file.getLastModifiedDate());
        }
    }

    public void updateLastModifiedDate(String date) {
        // Son tarih güncellemesi: daha yeni tarih atanır
        if (this.lastModifiedDate.equals("N/A") || date.compareTo(this.lastModifiedDate) > 0) {
            this.lastModifiedDate = date;
        }
    }

    public int calculateSize() {
        int totalSize = 0;
        for (File file : files) {
            totalSize += file.getSize();
        }
        for (Directory subDirectory : subDirectories) {
            totalSize += subDirectory.calculateSize();
        }
        return totalSize;
    }

    public boolean canDelete() {
        for (File file : files) {
            if (file.getAccessLevel().equals("SYSTEM")) return false;
        }
        for (Directory dir : subDirectories) {
            if (!dir.canDelete()) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Directory: " + name + " (Last Modified: " + lastModifiedDate + ", Access Level: " + accessLevel + ")";
    }
}
