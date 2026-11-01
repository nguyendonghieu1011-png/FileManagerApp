//package com.java_desktop_application.filemanager.controller;
//
//import com.java_desktop_application.filemanager.model.FileItem;
//
//import javafx.css.PseudoClass;
//import javafx.scene.control.Button;
//import javafx.scene.control.ButtonType;
//import javafx.scene.control.TextInputDialog;
//import javafx.scene.Node;
//import javafx.scene.control.Alert;
//import javafx.scene.control.ContextMenu;
//import javafx.scene.control.MenuItem;
//import javafx.scene.control.TableRow;
//import javafx.scene.input.MouseButton;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.input.ContextMenuEvent;
//import javafx.scene.control.SelectionMode;
//
//import javafx.collections.FXCollections;
//import javafx.fxml.FXML;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//
//public class UIControllerTest {
//
//    @FXML
//    private TableView<FileItem> fileTableView;
//
//    @FXML
//    private TableColumn<FileItem, String> nameColumn;
//
//    @FXML
//    private TableColumn<FileItem, String> sizeColumn;
//
//    @FXML
//    private TableColumn<FileItem, String> filesColumn;
//
//    @FXML
//    private TableColumn<FileItem, String> folderColumn;
//
//    @FXML
//    private TableColumn<FileItem, String> dateModifiedColumn;
//
//    @FXML
//    private Button cutButton;
//
//// Trạng thái CSS tùy chỉnh dành cho dòng đang Cut
//    private static final PseudoClass CUT_PSEUDO_CLASS
//            = PseudoClass.getPseudoClass("cut");
//
//// Lưu file/folder đang được Cut
//    private FileItem cutItem;
//
//    @FXML
//    private Button copyButton;
//
//    private static final PseudoClass COPY_PSEUDO_CLASS
//            = PseudoClass.getPseudoClass("copy");
//
//    private FileItem copyItem;
//
//    @FXML
//    private Button renameButton;
//
//    @FXML
//    private Button pasteButton;
//
//    @FXML
//    private Button deleteButton;
//
//    @FXML
//    private void initialize() {
//        configureTableColumns();
//        loadMockData();
//
//        // Cấu hình hiệu ứng Cut cho từng dòng
//        configureCutRowStyle();
//
//        // Gán sự kiện cho nút Cut
//        cutButton.setOnAction(event -> handleCut());
//        copyButton.setOnAction(event -> handleCopy());
//        renameButton.setOnAction(event -> handleRename());
//        pasteButton.setOnAction(event -> handlePaste());
//        deleteButton.setOnAction(event -> handleDelete());
//
//        createContextMenu();
//        configureContextMenuEvents();
//
//        updatePasteButtonState();
//    }
//
//    private void configureTableColumns() {
//        nameColumn.setCellValueFactory(
//                cellData -> new javafx.beans.property.SimpleStringProperty(
//                        cellData.getValue().getName()));
//
//        sizeColumn.setCellValueFactory(
//                cellData -> new javafx.beans.property.SimpleStringProperty(
//                        cellData.getValue().getSize()));
//
//        filesColumn.setCellValueFactory(
//                cellData -> new javafx.beans.property.SimpleStringProperty(
//                        cellData.getValue().getFiles()));
//
//        folderColumn.setCellValueFactory(
//                cellData -> new javafx.beans.property.SimpleStringProperty(
//                        cellData.getValue().getFolder()));
//
//        dateModifiedColumn.setCellValueFactory(
//                cellData -> new javafx.beans.property.SimpleStringProperty(
//                        cellData.getValue().getDateModified()));
//    }
//
//    private void loadMockData() {
//        fileTableView.setItems(
//                FXCollections.observableArrayList(
//                        new FileItem(
//                                "Documents", "--", "12",
//                                "Folder", "14/10/2026 09:30"),
//                        new FileItem(
//                                "Downloads", "--", "8",
//                                "Folder", "13/10/2026 15:20"),
//                        new FileItem(
//                                "report.docx", "256 KB", "--",
//                                "File", "12/10/2026 10:15"),
//                        new FileItem(
//                                "image.png", "1.5 MB", "--",
//                                "File", "11/10/2026 18:45"),
//                        new FileItem(
//                                "test.txt", "4 KB", "--",
//                                "File", "10/10/2026 08:00"),
//                        new FileItem(
//                                "Project Java", "--", "5",
//                                "Folder", "09/10/2026 14:00")
//                )
//        );
//    }
//
//    private final ContextMenu contextMenu = new ContextMenu();
//
//    private void createContextMenu() {
//        MenuItem openItem = new MenuItem("Open");
//        MenuItem cutMenuItem = new MenuItem("Cut");
//        MenuItem copyMenuItem = new MenuItem("Copy");
//        MenuItem renameItem = new MenuItem("Rename");
//        MenuItem deleteItem = new MenuItem("Delete");
//        MenuItem propertiesItem = new MenuItem("Properties");
//
//        openItem.setOnAction(event -> showMessage("Open"));
//        cutMenuItem.setOnAction(event -> handleCut());
//        copyMenuItem.setOnAction(event -> handleCopy());
//        renameItem.setOnAction(event -> handleRename());
//        deleteItem.setOnAction(event -> showMessage("Delete"));
//        propertiesItem.setOnAction(event -> showMessage("Properties"));
//
//        contextMenu.getItems().setAll(
//                openItem,
//                cutMenuItem,
//                copyMenuItem,
//                new javafx.scene.control.SeparatorMenuItem(),
//                renameItem,
//                deleteItem,
//                new javafx.scene.control.SeparatorMenuItem(),
//                propertiesItem
//        );
//    }
//
//    private void showMessage(String action) {
//        FileItem selectedItem = fileTableView.getSelectionModel()
//                .getSelectedItem();
//
//        if (selectedItem == null) {
//            return;
//        }
//
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("File Manager");
//        alert.setHeaderText(action);
//        alert.setContentText(
//                action + " được chọn cho: " + selectedItem.getName()
//        );
//        alert.showAndWait();
//    }
//
//    private void configureContextMenuEvents() {
//        fileTableView.getSelectionModel()
//                .setSelectionMode(SelectionMode.SINGLE);
//
//        // Chọn đúng dòng được nhấn chuột phải
//        fileTableView.addEventFilter(
//                MouseEvent.MOUSE_PRESSED,
//                event -> {
//                    if (event.getButton() != MouseButton.SECONDARY) {
//                        return;
//                    }
//
//                    Node node = event.getPickResult().getIntersectedNode();
//
//                    // Tìm TableRow chứa vị trí con trỏ
//                    while (node != null && !(node instanceof TableRow)) {
//                        node = node.getParent();
//                    }
//
//                    if (node instanceof TableRow<?> row && !row.isEmpty()) {
//                        fileTableView.getSelectionModel()
//                                .select(row.getIndex());
//                    } else {
//                        fileTableView.getSelectionModel().clearSelection();
//                    }
//                }
//        );
//
//        // Hiển thị menu tại vị trí con trỏ
//        fileTableView.setOnContextMenuRequested(
//                (ContextMenuEvent event) -> {
//                    if (fileTableView.getSelectionModel().getSelectedItem() != null) {
//                        contextMenu.show(
//                                fileTableView,
//                                event.getScreenX(),
//                                event.getScreenY()
//                        );
//                    }
//
//                    event.consume();
//                }
//        );
//    }
//
//    private void configureCutRowStyle() {
//        fileTableView.setRowFactory(tableView -> new TableRow<FileItem>() {
//
//            @Override
//            protected void updateItem(FileItem item, boolean empty) {
//                super.updateItem(item, empty);
//
//                // Kiểm tra dòng có phải dòng đang được Cut hay không
//                boolean isCut = !empty
//                        && item != null
//                        && item == cutItem;
//                boolean isCopy = !empty
//                        && item != null
//                        && item == copyItem;
//
//                // Bật hoặc tắt trạng thái CSS :cut
//                pseudoClassStateChanged(CUT_PSEUDO_CLASS, isCut);
//                pseudoClassStateChanged(COPY_PSEUDO_CLASS, isCopy);
//            }
//        });
//    }
//
//    private void handleCut() {
//        FileItem selectedItem = fileTableView.getSelectionModel()
//                .getSelectedItem();
//
//        // Không làm gì nếu chưa chọn file/folder
//        if (selectedItem == null) {
//            return;
//        }
//
//        // Lưu file/folder đang được Cut
//        cutItem = selectedItem;
//
//        copyItem = null;
//
//        // Cập nhật giao diện TableView
//        fileTableView.refresh();
//
//        updatePasteButtonState();
//    }
//
//    private void handleCopy() {
//        FileItem selectedItem = fileTableView.getSelectionModel()
//                .getSelectedItem();
//
//        // Không làm gì nếu chưa chọn file/folder
//        if (selectedItem == null) {
//            return;
//        }
//
//        // Lưu file/folder đang được Copy
//        copyItem = selectedItem;
//
//        cutItem = null;
//
//        // Cập nhật giao diện TableView
//        fileTableView.refresh();
//        updatePasteButtonState();
//        // Hien thi thong bao
//        showMessage("Copy");
//
//    }
//
//    private void handleRename() {
//        // Lấy file/folder đang được chọn
//        FileItem selectedItem = fileTableView.getSelectionModel()
//                .getSelectedItem();
//
//        // Nếu chưa chọn file/folder thì không làm gì
//        if (selectedItem == null) {
//            return;
//        }
//
//        // Lấy tên hiện tại
//        String oldName = selectedItem.getName();
//
//        // Tạo hộp thoại nhập tên mới
//        TextInputDialog dialog = new TextInputDialog(oldName);
//
//        dialog.setTitle("Rename");
//        dialog.setHeaderText("Đổi tên file/folder");
//        dialog.setContentText("Nhập tên mới:");
//
//        // Xử lý khi người dùng nhấn OK
//        dialog.showAndWait().ifPresent(newName -> {
//
//            // Loại bỏ khoảng trắng ở đầu và cuối
//            newName = newName.trim();
//
//            // Kiểm tra tên rỗng
//            if (newName.isEmpty()) {
//                showRenameError("Tên không được để trống!");
//                return;
//            }
//
//            // Kiểm tra tên có thay đổi hay không
//            if (newName.equals(oldName)) {
//                return;
//            }
//
//            // Kiểm tra ký tự không hợp lệ trên Windows
//            if (newName.matches(".*[\\\\/:*?\"<>|].*")) {
//                showRenameError(
//                        "Tên không được chứa các ký tự: \\\\ / : * ? \" < > |"
//                );
//                return;
//            }
//
//            // Lấy vị trí dòng đang được chọn
//            int selectedIndex = fileTableView.getSelectionModel()
//                    .getSelectedIndex();
//
//            // Tạo FileItem mới với tên mới
//            FileItem renamedItem = new FileItem(
//                    newName,
//                    selectedItem.getSize(),
//                    selectedItem.getFiles(),
//                    selectedItem.getFolder(),
//                    selectedItem.getDateModified()
//            );
//
//            // Thay thế dữ liệu cũ bằng dữ liệu mới
//            fileTableView.getItems().set(selectedIndex, renamedItem);
//
//            // Chọn lại dòng vừa đổi tên
//            fileTableView.getSelectionModel().select(selectedIndex);
//        });
//    }
//
//    private void handlePaste() {
//        // Ưu tiên đối tượng đang Cut hoặc Copy
//        FileItem sourceItem = cutItem != null ? cutItem : copyItem;
//
//        if (sourceItem == null) {
//            return;
//        }
//
//        String action = cutItem != null ? "Cut" : "Copy";
//
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Paste");
//        alert.setHeaderText("Thao tác Paste");
//        alert.setContentText(
//                "Đối tượng \"" + sourceItem.getName()
//                + "\" đã sẵn sàng để dán.\n"
//                + "Thao tác nguồn: " + action
//                + "\nChức năng dán file thật chưa được triển khai."
//        );
//
//        alert.showAndWait();
//        fileTableView.getSelectionModel().clearSelection();
//    }
//
//    private void showDeleteMessage(
//            Alert.AlertType type,
//            String title,
//            String message) {
//
//        Alert alert = new Alert(type);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//
//    private void handleDelete() {
//        // Lấy file hoặc folder đang được chọn
//        FileItem selectedItem
//                = fileTableView.getSelectionModel().getSelectedItem();
//
//        // Nếu chưa chọn đối tượng nào thì không làm gì
//        if (selectedItem == null) {
//            showDeleteMessage(
//                    Alert.AlertType.WARNING,
//                    "Chưa chọn đối tượng",
//                    "Vui lòng chọn file hoặc folder cần xóa."
//            );
//            return;
//        }
//
//        // Hỏi xác nhận trước khi xóa
//        Alert confirmAlert = new Alert(
//                Alert.AlertType.CONFIRMATION
//        );
//
//        confirmAlert.setTitle("Xác nhận xóa");
//        confirmAlert.setHeaderText("Bạn có chắc muốn xóa?");
//        confirmAlert.setContentText(
//                "Đối tượng: " + selectedItem.getName()
//        );
//
//        // Chờ người dùng chọn Delete hoặc Cancel
//        java.util.Optional<ButtonType> result
//                = confirmAlert.showAndWait();
//
//        // Chỉ xóa khi người dùng xác nhận
//        if (result.isPresent()
//                && result.get() == ButtonType.OK) {
//
//            fileTableView.getItems().remove(selectedItem);
//
//            // Xóa trạng thái Cut/Copy nếu trùng đối tượng vừa xóa
//            if (cutItem == selectedItem) {
//                cutItem = null;
//            }
//
//            if (copyItem == selectedItem) {
//                copyItem = null;
//            }
//
//            updatePasteButtonState();
//
//            showDeleteMessage(
//                    Alert.AlertType.INFORMATION,
//                    "Xóa thành công",
//                    "Đã xóa \"" + selectedItem.getName()
//                    + "\" khỏi danh sách."
//            );
//        }
//    }
//
//    private void showRenameError(String message) {
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//
//        alert.setTitle("Rename Error");
//        alert.setHeaderText("Không thể đổi tên");
//        alert.setContentText(message);
//
//        alert.showAndWait();
//    }
//
//    private void updatePasteButtonState() {
//        boolean hasClipboardItem = cutItem != null || copyItem != null;
//
//        pasteButton.setDisable(!hasClipboardItem);
//    }
//
//}
