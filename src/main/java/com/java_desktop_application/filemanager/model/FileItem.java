package com.java_desktop_application.filemanager.model;

import java.util.Objects;

public class FileItem {

    private final String name;
    private final String size;
    private final String files;
    private final String folder;
    private final String dateModified;

    public FileItem(
            String name,
            String size,
            String files,
            String folder,
            String dateModified) {

        this.name = Objects.requireNonNull(name, "name không được null");
        this.size = Objects.requireNonNull(size, "size không được null");
        this.files = Objects.requireNonNull(files, "files không được null");
        this.folder = Objects.requireNonNull(folder, "folder không được null");
        this.dateModified = Objects.requireNonNull(
                dateModified, "dateModified không được null");
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getFiles() {
        return files;
    }

    public String getFolder() {
        return folder;
    }

    public String getDateModified() {
        return dateModified;
    }
}
