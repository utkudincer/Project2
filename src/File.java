public class File {
    private String name;
    private String extension;
    private String lastModifiedDate;
    private int size;
    private String accessLevel; // USER or SYSTEM

    public File(String name, String extension, String lastModifiedDate, int size, String accessLevel) {
        this.name = name;
        this.extension = extension;
        this.lastModifiedDate = lastModifiedDate;
        this.size = size;
        this.accessLevel = accessLevel;
    }

    // Getter ve Setter metotları
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    public String getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(String lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }

    @Override
    public String toString() {
        return name + "." + extension + "##" + lastModifiedDate + "##" + size + "##" + accessLevel;
    }
}
