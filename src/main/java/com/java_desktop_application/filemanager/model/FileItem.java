
package com.java_desktop_application.filemanager.model;

public class FileItem {

    private final String name;
    private final String size;
    private final String files;
    private final String folder;
    private final String type;
    private final String dateModified;
    private final java.nio.file.Path path;

    // Dữ liệu gốc phục vụ sắp xếp
    private final long sizeBytes;
    private final long dateCreatedMillis;
    private final long dateModifiedMillis;

    public FileItem(String name, String size, String files, String folder,
                    String type, String dateModified,
                    java.nio.file.Path path,
                    long sizeBytes,
                    long dateCreatedMillis,
                    long dateModifiedMillis) {

        this.name = name;
        this.size = size;
        this.files = files;
        this.folder = folder;
        this.type = type;
        this.dateModified = dateModified;
        this.path = path;

        this.sizeBytes = sizeBytes;
        this.dateCreatedMillis = dateCreatedMillis;
        this.dateModifiedMillis = dateModifiedMillis;
    }

    public String getName() { return name; }
    public String getSize() { return size; }
    public String getFiles() { return files; }
    public String getFolder() { return folder; }
    public String getDateModified() { return dateModified; }
    public String getType() { return type; }
    public java.nio.file.Path getPath() { return path; }

    // Getter phục vụ sắp xếp
    public long getSizeBytes() { return sizeBytes; }
    public long getDateCreatedMillis() { return dateCreatedMillis; }
    public long getDateModifiedMillis() { return dateModifiedMillis; }
}
