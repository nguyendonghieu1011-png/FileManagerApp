package com.java_desktop_application.filemanager.controller;

import com.java_desktop_application.filemanager.model.FileItem;

// Type
import javax.swing.filechooser.FileSystemView;
import java.util.HashMap;
import java.util.Map;
import java.util.Deque;
import java.util.ArrayDeque;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.control.ComboBox;
import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

public class MainController {
    // @FXML: annotation - liên kết các component tới các control tương ứng trong ui
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

    // choose path
    @FXML
    private ComboBox<String> pathComboBox;

    // ===================== BUTTONS =====================

    @FXML
    private Button cutButton;
    @FXML
    private Button copyButton;
    @FXML
    private Button renameButton;
    @FXML
    private Button pasteButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button backButton;
    @FXML
    private Button forwardButton;
    @FXML
    private Button upButton;
    @FXML
    private Button refreshButton;

    // ===================== CUT / COPY STATE =====================

    // Trạng thái CSS cho dòng đang Cut
    private static final PseudoClass CUT_PSEUDO_CLASS = PseudoClass.getPseudoClass("cut");

    // Trạng thái CSS cho dòng đang Copy
    private static final PseudoClass COPY_PSEUDO_CLASS = PseudoClass.getPseudoClass("copy");

    // Lưu đường dẫn file/folder đang Cut hoặc Copy
    private Path cutPath;
    private Path copyPath;

    // Context Menu
    private final ContextMenu contextMenu = new ContextMenu();

    // dynamic list : chứa các instance type FileItem(tương ứng các folder,.) , tự
    // update list khi ds changes
    private final ObservableList<FileItem> items = FXCollections.observableArrayList();

    // ===================== NAVIGATION HISTORY =====================

    // Đường dẫn hiện tại
    private Path currentPath = Paths.get(System.getProperty("user.home"));

    // Lưu lịch sử các đường dẫn đã truy cập
    private final Deque<Path> backStack = new ArrayDeque<>();

    // Lưu lịch sử các đường dẫn có thể Forward
    private final Deque<Path> forwardStack = new ArrayDeque<>();

    //////////////////////////////////////////////////////////
    @FXML
    public void initialize() {

        // setCellValueFactory(): ORM từng column trên ui với các field trong FileItem
        // ===================== NAVIGATION BUTTON ICONS =====================

        // Back
        FontIcon backIcon = new FontIcon(FontAwesomeSolid.ARROW_LEFT);
        backIcon.setIconSize(16);
        backButton.setGraphic(backIcon);

        // Forward
        FontIcon forwardIcon = new FontIcon(FontAwesomeSolid.ARROW_RIGHT);
        forwardIcon.setIconSize(16);
        forwardButton.setGraphic(forwardIcon);

        // Up
        FontIcon upIcon = new FontIcon(FontAwesomeSolid.ARROW_UP);
        upIcon.setIconSize(16);
        upButton.setGraphic(upIcon);

        // Refresh
        FontIcon refreshIcon = new FontIcon(FontAwesomeSolid.SYNC_ALT);
        refreshIcon.setIconSize(16);
        refreshButton.setGraphic(refreshIcon);
        // ===================== FUNCTION BUTTON ICONS =====================

        // Cut
        FontIcon cutIcon = new FontIcon(FontAwesomeSolid.CUT);
        cutIcon.setIconSize(15);
        cutButton.setGraphic(cutIcon);

        // Copy
        FontIcon copyIcon = new FontIcon(FontAwesomeSolid.COPY);
        copyIcon.setIconSize(15);
        copyButton.setGraphic(copyIcon);

        // Rename
        FontIcon renameIcon = new FontIcon(FontAwesomeSolid.EDIT);
        renameIcon.setIconSize(15);
        renameButton.setGraphic(renameIcon);

        // Paste
        FontIcon pasteIcon = new FontIcon(FontAwesomeSolid.PASTE);
        pasteIcon.setIconSize(15);
        pasteButton.setGraphic(pasteIcon);

        // Delete
        FontIcon deleteIcon = new FontIcon(FontAwesomeSolid.TRASH_ALT);
        deleteIcon.setIconSize(15);
        deleteButton.setGraphic(deleteIcon);

        // Khoảng cách giữa icon và chữ
        cutButton.setGraphicTextGap(6);
        copyButton.setGraphicTextGap(6);
        renameButton.setGraphicTextGap(6);
        pasteButton.setGraphicTextGap(6);
        deleteButton.setGraphicTextGap(6);

        // setItems(): gắn data (.) items vào table trên ui
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        filesColumn.setCellValueFactory(new PropertyValueFactory<>("files"));
        folderColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateModifiedColumn.setCellValueFactory(new PropertyValueFactory<>("dateModified"));
        fileTableView.setItems(items);
        // Cấu hình hiệu ứng Cut / Copy
        configureCutRowStyle();

        // Gán sự kiện cho các nút
        cutButton.setOnAction(event -> handleCut());
        copyButton.setOnAction(event -> handleCopy());
        renameButton.setOnAction(event -> handleRename());
        pasteButton.setOnAction(event -> handlePaste());
        deleteButton.setOnAction(event -> handleDelete());

        // Cấu hình menu chuột phải
        createContextMenu();
        configureContextMenuEvents();

        // Cập nhật trạng thái nút Paste
        updatePasteButtonState();

        // setEditable(): gõ tay path vào ô ComboBox
        //
        pathComboBox.setEditable(true);
        for (File root : File.listRoots()) {
            pathComboBox.getItems().add(root.getAbsolutePath());
        }

        pathComboBox.getItems().add(System.getProperty("user.home"));

        pathComboBox.setOnAction(e -> {
            String value = pathComboBox.getValue();
            if (value == null || value.isBlank())
                return;
            Path p = Paths.get(value.trim());
            if (Files.isDirectory(p) && !p.equals(currentPath)) {
                navigateTo(p);
            } else if (!Files.isDirectory(p)) {
                showError("Đường dẫn không tồn tại: " + value);
            }
        });

        // Double-click vào folder để mở
        fileTableView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                FileItem selected = fileTableView.getSelectionModel().getSelectedItem();
                if (selected != null && Files.isDirectory(selected.getPath())) {
                    navigateTo(selected.getPath());
                }
            }
        });

        navigateTo(currentPath);
    }

    // Điều hướng thông thường: có lưu lịch sử
    private void navigateTo(Path path) {
        navigateTo(path, true);
    }

    // Điều hướng có tùy chọn lưu lịch sử
    private void navigateTo(Path path, boolean addHistory) {

        Path targetPath = path.toAbsolutePath().normalize();

        // Không làm gì nếu vẫn ở thư mục hiện tại
        if (targetPath.equals(currentPath)) {
            return;
        }

        // Lưu đường dẫn hiện tại vào lịch sử Back
        if (addHistory) {
            backStack.push(currentPath);

            // Khi chuyển sang một đường dẫn mới,
            // lịch sử Forward cũ không còn hợp lệ
            forwardStack.clear();
        }

        currentPath = targetPath;

        loadFiles(currentPath);

        pathComboBox.setValue(currentPath.toString());

        updateNavigationButtons();
    }

    @FXML
    private void handleBack() {

        if (backStack.isEmpty()) {
            return;
        }

        // Lưu vị trí hiện tại để có thể Forward
        forwardStack.push(currentPath);

        // Lấy đường dẫn trước đó
        Path previousPath = backStack.pop();

        // Điều hướng nhưng không ghi thêm lịch sử
        navigateTo(previousPath, false);
    }

    @FXML
    private void handleForward() {

        if (forwardStack.isEmpty()) {
            return;
        }

        // Lưu vị trí hiện tại để có thể Back
        backStack.push(currentPath);

        // Lấy đường dẫn tiếp theo
        Path nextPath = forwardStack.pop();

        // Điều hướng nhưng không ghi thêm lịch sử
        navigateTo(nextPath, false);
    }

    @FXML
    private void handleRefresh() {

        // Tải lại danh sách file và folder
        loadFiles(currentPath);

        // Cập nhật giao diện
        fileTableView.refresh();
    }

    private void updateNavigationButtons() {

        backButton.setDisable(backStack.isEmpty());

        forwardButton.setDisable(forwardStack.isEmpty());
    }

    @FXML
    private void handleUp(ActionEvent event) {
        Path parent = currentPath.getParent(); // null khi đang ở gốc ổ đĩa
        if (parent != null) {
            navigateTo(parent);
        }
    }

    // ===================== NEW =====================

    @FXML
    private void handleNewFolder(ActionEvent event) {
        createNew("New Folder", "", true);
    }

    @FXML
    private void handleNewTextDocument(ActionEvent event) {
        createNew("New Text Document", ".txt", false);
    }

    private void createNew(String defaultName, String extension, boolean isFolder) {
        TextInputDialog dialog = new TextInputDialog(defaultName);
        dialog.setTitle(isFolder ? "New Folder" : "New Text Document");
        dialog.setHeaderText(null);
        dialog.setContentText("Name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty() || result.get().isBlank()) {
            return;
        }

        String name = result.get().trim();

        if (name.matches(".*[\\\\/:*?\"<>|].*")) {
            showError("Tên không được chứa các ký tự: \\ / : * ? \" < > |");
            return;
        }

        Path target = uniquePath(currentPath.resolve(name + extension));

        try {
            if (isFolder) {
                Files.createDirectory(target);
            } else {
                Files.createFile(target);
            }
            loadFiles(currentPath);
        } catch (IOException e) {
            showError("Không thể tạo: " + e.getMessage());
        }
    }

    private Path uniquePath(Path path) {
        if (!Files.exists(path))
            return path;

        String fileName = path.getFileName().toString();
        int dot = fileName.lastIndexOf('.');
        String base = (dot > 0) ? fileName.substring(0, dot) : fileName;
        String ext = (dot > 0) ? fileName.substring(dot) : "";

        int i = 2;
        Path candidate;
        do {
            candidate = path.resolveSibling(base + " (" + i + ")" + ext);
            i++;
        } while (Files.exists(candidate));
        return candidate;
    }

    private void handleCut() {
        FileItem selectedItem = fileTableView.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            return;
        }

        // Lưu đường dẫn file/folder được chọn
        cutPath = selectedItem.getPath();

        // Cut và Copy không hoạt động đồng thời
        copyPath = null;

        fileTableView.refresh();
        updatePasteButtonState();
    }

    private void handleCopy() {
        FileItem selectedItem = fileTableView.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            return;
        }

        // Lưu đường dẫn file/folder được chọn
        copyPath = selectedItem.getPath();

        // Cut và Copy không hoạt động đồng thời
        cutPath = null;

        fileTableView.refresh();
        updatePasteButtonState();
    }

    private void configureCutRowStyle() {
        fileTableView.setRowFactory(tableView -> new TableRow<FileItem>() {

            @Override
            protected void updateItem(FileItem item, boolean empty) {
                super.updateItem(item, empty);

                boolean isCut = !empty
                        && item != null
                        && cutPath != null
                        && item.getPath().equals(cutPath);

                boolean isCopy = !empty
                        && item != null
                        && copyPath != null
                        && item.getPath().equals(copyPath);

                pseudoClassStateChanged(
                        CUT_PSEUDO_CLASS, isCut);

                pseudoClassStateChanged(
                        COPY_PSEUDO_CLASS, isCopy);
            }
        });
    }

    private void handleRename() {
        FileItem selectedItem = fileTableView.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            return;
        }

        Path oldPath = selectedItem.getPath();
        String oldName = oldPath.getFileName().toString();

        TextInputDialog dialog = new TextInputDialog(oldName);
        dialog.setTitle("Rename");
        dialog.setHeaderText("Đổi tên file/folder");
        dialog.setContentText("Nhập tên mới:");

        dialog.showAndWait().ifPresent(inputName -> {
            String newName = inputName.trim();

            if (newName.isEmpty()) {
                showError("Tên không được để trống!");
                return;
            }

            if (newName.matches(".*[\\\\/:*?\"<>|].*")) {
                showError("Tên không được chứa các ký tự: \\ / : * ? \" < > |");
                return;
            }

            if (newName.equals(oldName)) {
                return;
            }

            Path newPath = oldPath.resolveSibling(newName);

            if (Files.exists(newPath)) {
                showError("Tên file/folder đã tồn tại!");
                return;
            }

            try {
                Files.move(oldPath, newPath);

                // Cập nhật lại danh sách sau khi đổi tên
                loadFiles(currentPath);

            } catch (IOException e) {
                showError("Không thể đổi tên: " + e.getMessage());
            }
        });
    }

    private void copyDirectory(Path source, Path target)
            throws IOException {

        try (Stream<Path> stream = Files.walk(source)) {
            stream.forEach(path -> {
                try {
                    Path destination = target.resolve(
                            source.relativize(path));

                    if (Files.isDirectory(path)) {
                        Files.createDirectories(destination);
                    } else {
                        Files.copy(path, destination);
                    }

                } catch (IOException e) {
                    throw new java.io.UncheckedIOException(e);
                }
            });
        } catch (java.io.UncheckedIOException e) {
            throw e.getCause();
        }
    }

    private void handlePaste() {
        Path sourcePath = cutPath != null ? cutPath : copyPath;

        if (sourcePath == null) {
            return;
        }

        if (!Files.exists(sourcePath)) {
            showError("File/folder nguồn không còn tồn tại!");
            cutPath = null;
            copyPath = null;
            updatePasteButtonState();
            fileTableView.refresh();
            return;
        }

        // Không cho phép dán một folder vào chính nó hoặc folder con của nó
        if (Files.isDirectory(sourcePath)
                && currentPath.startsWith(sourcePath)) {
            showError("Không thể dán folder vào chính nó hoặc folder con!");
            return;
        }

        Path targetPath = uniquePath(
                currentPath.resolve(sourcePath.getFileName()));

        try {
            if (cutPath != null) {
                // Di chuyển file/folder
                Files.move(sourcePath, targetPath);
                cutPath = null;

            } else {
                // Sao chép file hoặc folder
                if (Files.isDirectory(sourcePath)) {
                    copyDirectory(sourcePath, targetPath);
                } else {
                    Files.copy(sourcePath, targetPath);
                }
            }

            loadFiles(currentPath);
            fileTableView.getSelectionModel().clearSelection();

        } catch (IOException e) {
            showError("Không thể Paste: " + e.getMessage());
        }

        updatePasteButtonState();
        fileTableView.refresh();
    }

    private void updatePasteButtonState() {
        boolean hasClipboardItem = cutPath != null || copyPath != null;

        pasteButton.setDisable(!hasClipboardItem);
    }

    private void createContextMenu() {
        MenuItem openItem = new MenuItem("Open");
        MenuItem cutMenuItem = new MenuItem("Cut");
        MenuItem copyMenuItem = new MenuItem("Copy");
        MenuItem renameItem = new MenuItem("Rename");
        MenuItem deleteItem = new MenuItem("Delete");
        MenuItem propertiesItem = new MenuItem("Properties");

        openItem.setOnAction(event -> {
            FileItem selected = fileTableView.getSelectionModel().getSelectedItem();

            if (selected != null && Files.isDirectory(selected.getPath())) {
                navigateTo(selected.getPath());
            }
        });

        cutMenuItem.setOnAction(event -> handleCut());
        copyMenuItem.setOnAction(event -> handleCopy());
        renameItem.setOnAction(event -> handleRename());
        deleteItem.setOnAction(event -> handleDelete());

        propertiesItem.setOnAction(event -> showMessage("Properties"));

        contextMenu.getItems().setAll(
                openItem,
                cutMenuItem,
                copyMenuItem,
                new javafx.scene.control.SeparatorMenuItem(),
                renameItem,
                deleteItem,
                new javafx.scene.control.SeparatorMenuItem(),
                propertiesItem);
    }

    private void configureContextMenuEvents() {

        fileTableView.getSelectionModel()
                .setSelectionMode(SelectionMode.SINGLE);

        // Chọn dòng được nhấn chuột phải
        fileTableView.addEventFilter(
                MouseEvent.MOUSE_PRESSED,
                event -> {
                    if (event.getButton() != MouseButton.SECONDARY) {
                        return;
                    }

                    Node node = event.getPickResult().getIntersectedNode();

                    while (node != null && !(node instanceof TableRow)) {
                        node = node.getParent();
                    }

                    if (node instanceof TableRow<?> row && !row.isEmpty()) {
                        fileTableView.getSelectionModel()
                                .select(row.getIndex());
                    } else {
                        fileTableView.getSelectionModel()
                                .clearSelection();
                    }
                });

        // Hiển thị Context Menu
        fileTableView.setOnContextMenuRequested(event -> {
            if (fileTableView.getSelectionModel().getSelectedItem() != null) {
                contextMenu.show(
                        fileTableView,
                        event.getScreenX(),
                        event.getScreenY());
            }

            event.consume();
        });
    }

    private void showMessage(String action) {
        FileItem selectedItem = fileTableView.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("File Manager");
        alert.setHeaderText(action);
        alert.setContentText(
                action + ": " + selectedItem.getName());
        alert.showAndWait();
    }

    private void handleDelete() {
        // Lấy file/folder đang được chọn
        FileItem selectedItem = fileTableView.getSelectionModel().getSelectedItem();

        // Kiểm tra nếu chưa chọn đối tượng
        if (selectedItem == null) {
            showError("Vui lòng chọn file hoặc folder cần xóa!");
            return;
        }

        // Tạo hộp thoại xác nhận
        Alert confirmAlert = new Alert(
                Alert.AlertType.CONFIRMATION);

        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc muốn xóa?");
        confirmAlert.setContentText(
                "Đối tượng: " + selectedItem.getName());

        // Chờ người dùng xác nhận
        Optional<ButtonType> result = confirmAlert.showAndWait();

        // Chỉ xóa khỏi danh sách hiển thị nếu chọn OK
        if (result.isPresent()
                && result.get() == ButtonType.OK) {

            // Xóa đối tượng khỏi TableView
            items.remove(selectedItem);

            // Hủy trạng thái Cut nếu trùng đối tượng
            if (cutPath != null
                    && cutPath.equals(selectedItem.getPath())) {
                cutPath = null;
            }

            // Hủy trạng thái Copy nếu trùng đối tượng
            if (copyPath != null
                    && copyPath.equals(selectedItem.getPath())) {
                copyPath = null;
            }

            // Cập nhật trạng thái nút Paste
            updatePasteButtonState();

            // Cập nhật giao diện
            fileTableView.refresh();

            // Thông báo
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Delete");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Đã xóa \"" + selectedItem.getName()
                            + "\" khỏi danh sách hiển thị.");
            alert.showAndWait();
        }
    }

    // ===================== TYPE FILES =====================
    private final Map<String, String> typeCache = new HashMap<>();

    private String getTypeName(Path p, boolean isDir) {
        if (isDir)
            return "File folder";

        String name = p.getFileName().toString();
        int dot = name.lastIndexOf('.');
        String ext = (dot > 0) ? name.substring(dot).toLowerCase() : "";

        // Cache theo đuôi file để không hỏi hệ điều hành lặp lại
        return typeCache.computeIfAbsent(ext, k -> {
            try {
                // Lấy đúng tên loại file mà Windows hiển thị (Text Document, PDF File...)
                String desc = FileSystemView.getFileSystemView()
                        .getSystemTypeDescription(p.toFile());
                if (desc != null && !desc.isBlank())
                    return desc;
            } catch (Exception ignored) {
            }
            return ext.isEmpty() ? "File" : ext.substring(1).toUpperCase() + " File";
        });
    }

    // ===================== LOAD FILES =====================
    private void loadFiles(Path path) {
        items.clear();
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        try (Stream<Path> stream = Files.list(path)) {
            stream.forEach(p -> {
                try {
                    BasicFileAttributes attr = Files.readAttributes(p, BasicFileAttributes.class);
                    boolean isDir = attr.isDirectory();

                    String size = isDir ? "" : formatSize(attr.size());
                    String filesFolders = "";

                    if (isDir) {
                        long fileCount = countChildren(p, false);
                        long folderCount = countChildren(p, true);

                        if (fileCount == 0 && folderCount == 0) {
                            filesFolders = "Empty";
                        } else if (fileCount == 0) {
                            filesFolders = folderCount + " Folders";
                        } else if (folderCount == 0) {
                            filesFolders = fileCount + " Files";
                        } else {
                            filesFolders = fileCount + " Files, " + folderCount + " Folders";
                        }
                    }
                    String type = getTypeName(p, isDir);
                    String date = fmt.format(new Date(attr.lastModifiedTime().toMillis()));

                    items.add(new FileItem(p.getFileName().toString(),
                            size, filesFolders, "", type, date, p));
                } catch (IOException ignored) {
                    // file bị khóa / bỏ qua
                }
            });
        } catch (IOException e) {
            showError("Không thể đọc thư mục: " + e.getMessage());
        }
    }

    private long countChildren(Path dir, boolean directories) {
        try (Stream<Path> s = Files.list(dir)) {
            return s.filter(c -> Files.isDirectory(c) == directories).count();
        } catch (IOException | SecurityException e) {
            return 0;
        }
    }

    private String formatSize(long bytes) {
        if (bytes < 1024)
            return bytes + " B";
        if (bytes < 1024 * 1024)
            return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024L * 1024 * 1024)
            return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }

    // Error Ráise

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}