package com.java_desktop_application.filemanager.controller;

import com.java_desktop_application.filemanager.model.FileItem;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class UIControllerTest {

    @FXML
    private TableView<FileItem> fileTableView;

    @FXML
    private TableColumn<FileItem, String> nameColumn;

    @FXML
    private TableColumn<FileItem, String> sizeColumn;

    @FXML
    private TableColumn<FileItem, String> filesColumn;

    @FXML
    private TableColumn<FileItem, String> folderColumn;

    @FXML
    private TableColumn<FileItem, String> dateModifiedColumn;

    @FXML
    private void initialize() {
        configureTableColumns();
        loadMockData();
    }

    private void configureTableColumns() {
        nameColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getName()));

        sizeColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getSize()));

        filesColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getFiles()));

        folderColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getFolder()));

        dateModifiedColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDateModified()));
    }

    private void loadMockData() {
        fileTableView.setItems(
                FXCollections.observableArrayList(
                        new FileItem(
                                "Documents", "--", "12",
                                "Folder", "14/10/2026 09:30"),
                        new FileItem(
                                "Downloads", "--", "8",
                                "Folder", "13/10/2026 15:20"),
                        new FileItem(
                                "report.docx", "256 KB", "--",
                                "File", "12/10/2026 10:15"),
                        new FileItem(
                                "image.png", "1.5 MB", "--",
                                "File", "11/10/2026 18:45"),
                        new FileItem(
                                "test.txt", "4 KB", "--",
                                "File", "10/10/2026 08:00"),
                        new FileItem(
                                "Project Java", "--", "5",
                                "Folder", "09/10/2026 14:00")
                )
        );
    }
}
