package com.java_desktop_application.filemanager.model;

public class FileItem {
    private final String name;
    private final String size;
    private final String files;
    private final String folder;
    private final String dateModified;
    private final java.nio.file.Path path;

    public FileItem(String name, String size, String files, String folder,
                    String dateModified, java.nio.file.Path path) {
        this.name = name;
        this.size = size;
        this.files = files;
        this.folder = folder;
        this.dateModified = dateModified;
        this.path = path;
    }

    public String getName() { return name; }
    public String getSize() { return size; }
    public String getFiles() { return files; }
    public String getFolder() { return folder; }
    public String getDateModified() { return dateModified; }
    public java.nio.file.Path getPath() { return path; }
}