package view;

import dao.BarangDAO;
import dao.ActivityLogDAO;
import dao.AdminDAO;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Admin;
import model.Barang;
import model.ActivityLog;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Kelas untuk tampilan utama aplikasi gudang.
 */
public class MainApp extends Application {

    // Deklarasi komponen DAO dan UI.
    private final BarangDAO barangDAO = new BarangDAO();
    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO();
    private final AdminDAO adminDAO = new AdminDAO();
    private final TableView<Barang> table = new TableView<>();
    private ObservableList<Barang> data;
    private FilteredList<Barang> filteredData;

    private ComboBox<String> gudangSelector;
    private final Map<String, String> gudangMap = new HashMap<>();

    // Field untuk form input data barang.
    private final TextField namaField = new TextField();
    private final TextField hargaField = new TextField();
    private final TextField stokField = new TextField();
    private final DatePicker tglKadaluwarsaPicker = new DatePicker();

    private final Label feedbackLabel = new Label();
    private Admin currentAdmin; // Menyimpan semua data admin yang login.
    private int currentAdminId;

    // Komponen Dashboard
    private final Label totalJenisBarangLabel = new Label();
    private final Label totalStokLabel = new Label();
    private final Label nilaiInventarisLabel = new Label();
    private final PieChart stokPaiChart = new PieChart();

    private static final int LOW_STOCK_THRESHOLD = 5;

    /**
     * Metode baru untuk menerima objek Admin lengkap dari LoginApp.
     */
    public void initSession(Admin admin) {
        this.currentAdmin = admin;
        this.currentAdminId = admin.getId();
        barangDAO.setCurrentAdminId(admin.getId());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Warehouse Management System");

        gudangMap.put("Gudang A", "barang_gudang_a");
        gudangMap.put("Gudang B", "barang_gudang_b");
        gudangMap.put("Gudang C", "barang_gudang_c");

        gudangSelector = new ComboBox<>();
        gudangSelector.getItems().addAll(gudangMap.keySet());
        gudangSelector.setValue("Gudang A");
        gudangSelector.valueProperty().addListener((obs, oldVal, newVal) -> loadData());

        Label pilihGudangLabel = new Label("Pilih Gudang:");
        HBox gudangBox = new HBox(10, pilihGudangLabel, gudangSelector);
        gudangBox.setAlignment(Pos.CENTER_LEFT);

        Label headerLabel = new Label("📦 Warehouse Management");
        headerLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Cari Nama Barang...");
        searchField.setMinWidth(250);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(barang -> {
                if (newValue == null || newValue.isEmpty()) return true;
                return barang.getNama().toLowerCase().contains(newValue.toLowerCase());
            });
        });

        VBox topLayout = new VBox(15, headerLabel, gudangBox, searchField);
        topLayout.setPadding(new Insets(20, 20, 20, 20));
        topLayout.setAlignment(Pos.CENTER);

        setupTable();
        VBox formBox = createFormBox(primaryStage);
        VBox dashboard = createDashboard();

        // --- KONDISI SUPER ADMIN ---
        if (currentAdmin != null && currentAdmin.isSuperAdmin()) {
            Button manajemenAdminBtn = new Button("👤 Manajemen Admin");
            manajemenAdminBtn.setMaxWidth(Double.MAX_VALUE);
            manajemenAdminBtn.setOnAction(e -> showAdminManagementWindow(primaryStage));
            // Tambahkan tombol sebelum label logout
            int logoutBtnIndex = formBox.getChildren().size() - 2;
            formBox.getChildren().add(logoutBtnIndex, manajemenAdminBtn);
        }

        BorderPane root = new BorderPane();
        root.setTop(topLayout);
        root.setLeft(dashboard);
        root.setCenter(table);
        root.setRight(formBox);

        loadData(); // Pindahkan loadData setelah semua UI di-setup

        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Barang, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Barang, String> namaCol = new TableColumn<>("Nama");
        namaCol.setCellValueFactory(new PropertyValueFactory<>("nama"));
        TableColumn<Barang, Double> hargaCol = new TableColumn<>("Harga");
        hargaCol.setCellValueFactory(new PropertyValueFactory<>("harga"));
        TableColumn<Barang, Integer> stokCol = new TableColumn<>("Stok");
        stokCol.setCellValueFactory(new PropertyValueFactory<>("stok"));
        TableColumn<Barang, LocalDate> tglCol = new TableColumn<>("Tgl Kadaluwarsa");
        tglCol.setCellValueFactory(new PropertyValueFactory<>("tglKadaluwarsa"));
        table.getColumns().addAll(idCol, namaCol, hargaCol, stokCol, tglCol);

        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Barang item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("low-stock-row", "expiring-soon-row", "expired-row");
                if (item != null && !empty) {
                    if (item.getStok() < LOW_STOCK_THRESHOLD) getStyleClass().add("low-stock-row");
                    if (item.getTglKadaluwarsa() != null) {
                        if (item.getTglKadaluwarsa().isBefore(LocalDate.now())) {
                            getStyleClass().add("expired-row");
                        } else if (item.getTglKadaluwarsa().isBefore(LocalDate.now().plusMonths(1))) {
                            getStyleClass().add("expiring-soon-row");
                        }
                    }
                }
            }
        });
    }

    private VBox createFormBox(Stage primaryStage) {
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(10));
        formBox.setAlignment(Pos.TOP_LEFT);
        formBox.setPrefWidth(250);
        Label formTitle = new Label("Form Barang");
        formTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        namaField.setPromptText("Nama Barang");
        hargaField.setPromptText("Harga");
        stokField.setPromptText("Stok");
        tglKadaluwarsaPicker.setPromptText("Tgl Kadaluwarsa");

        Button tambahBtn = new Button("➕ Tambah");
        Button updateBtn = new Button("✏️ Update");
        Button hapusBtn = new Button("🗑️ Hapus");
        Button historyBtn = new Button("📜 History");
        Button exportPdfBtn = new Button("📄 Ekspor ke PDF");
        Button logoutBtn = new Button("🚪 Logout");
        
        tambahBtn.setMaxWidth(Double.MAX_VALUE);
        updateBtn.setMaxWidth(Double.MAX_VALUE);
        hapusBtn.setMaxWidth(Double.MAX_VALUE);
        historyBtn.setMaxWidth(Double.MAX_VALUE);
        exportPdfBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setMaxWidth(Double.MAX_VALUE);

        formBox.getChildren().addAll(
                formTitle, namaField, hargaField, stokField, tglKadaluwarsaPicker,
                tambahBtn, updateBtn, hapusBtn, historyBtn, exportPdfBtn, logoutBtn,
                feedbackLabel
        );

        tambahBtn.setOnAction(e -> handleTambah());
        updateBtn.setOnAction(e -> handleUpdate());
        hapusBtn.setOnAction(e -> handleHapus());
        historyBtn.setOnAction(e -> showAllHistory());
        exportPdfBtn.setOnAction(e -> exportToPdf());

        logoutBtn.setOnAction(e -> {
            primaryStage.close();
            new LoginApp().start(new Stage());
            activityLogDAO.catatAktivitas(currentAdminId, "Logout", "Admin ID: " + currentAdminId);
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                namaField.setText(newSelection.getNama());
                hargaField.setText(String.valueOf(newSelection.getHarga()));
                stokField.setText(String.valueOf(newSelection.getStok()));
                tglKadaluwarsaPicker.setValue(newSelection.getTglKadaluwarsa());
            } else {
                clearForm();
            }
        });
        return formBox;
    }

    private VBox createDashboard() {
        VBox dashboardBox = new VBox(15);
        dashboardBox.setPadding(new Insets(10));
        dashboardBox.setPrefWidth(280);
        dashboardBox.setStyle("-fx-background-color: #F5F5F5; -fx-border-color: #E0E0E0;");

        Label dashboardTitle = new Label("📊 Dashboard");
        dashboardTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        totalJenisBarangLabel.setStyle("-fx-font-size: 14px;");
        totalStokLabel.setStyle("-fx-font-size: 14px;");
        nilaiInventarisLabel.setStyle("-fx-font-size: 14px;");

        stokPaiChart.setTitle("5 Barang Stok Terbanyak");
        stokPaiChart.setLegendSide(Side.BOTTOM);
        stokPaiChart.setLabelsVisible(false);

        dashboardBox.getChildren().addAll(dashboardTitle, totalJenisBarangLabel, totalStokLabel, nilaiInventarisLabel, stokPaiChart);
        return dashboardBox;
    }
    
    // ... Metode lain seperti loadData, updateDashboard, handleTambah, dll. tidak berubah ...
    // ... Cukup salin metode baru di bawah ini ke dalam kelas MainApp Anda. ...

    /**
     * METODE BARU: Menampilkan jendela untuk mengelola pengguna admin.
     * @param owner Jendela utama aplikasi, untuk mengatur modality.
     */
    private void showAdminManagementWindow(Window owner) {
        Stage adminStage = new Stage();
        adminStage.initModality(Modality.WINDOW_MODAL);
        adminStage.initOwner(owner);
        adminStage.setTitle("Manajemen Pengguna Admin");

        // Tabel untuk menampilkan admin
        TableView<Admin> adminTable = new TableView<>();
        TableColumn<Admin, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Admin, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<Admin, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        adminTable.getColumns().addAll(idCol, usernameCol, roleCol);
        adminTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Form fields
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (biarkan kosong jika tidak diubah)");
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("admin", "super_admin");
        roleComboBox.setPromptText("Pilih Role");

        // Action Buttons
        Button tambahBtn = new Button("Tambah");
        Button updateBtn = new Button("Update");
        Button hapusBtn = new Button("Hapus");
        Button clearBtn = new Button("Clear Form");

        HBox buttonBox = new HBox(10, tambahBtn, updateBtn, hapusBtn, clearBtn);
        buttonBox.setAlignment(Pos.CENTER);

        // Mengisi form saat item tabel dipilih
        adminTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                usernameField.setText(newSelection.getUsername());
                passwordField.setPromptText("Password (biarkan kosong jika tidak diubah)"); // Jangan tampilkan password
                roleComboBox.setValue(newSelection.getRole());
            }
        });
        
        Runnable clearForm = () -> {
            adminTable.getSelectionModel().clearSelection();
            usernameField.clear();
            passwordField.clear();
            passwordField.setPromptText("Password");
            roleComboBox.getSelectionModel().clearSelection();
        };

        // Memuat/memperbarui data ke tabel
        Runnable refreshTable = () -> {
            adminTable.setItems(FXCollections.observableArrayList(adminDAO.getAllAdmins()));
            clearForm.run();
        };
        
        // --- Aksi Tombol ---
        clearBtn.setOnAction(e -> clearForm.run());

        tambahBtn.setOnAction(e -> {
            if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty() || roleComboBox.getValue() == null) {
                new Alert(Alert.AlertType.ERROR, "Semua field harus diisi untuk menambah admin baru.").showAndWait();
                return;
            }
            Admin newAdmin = new Admin(usernameField.getText(), passwordField.getText(), roleComboBox.getValue());
            if (adminDAO.tambahAdmin(newAdmin)) {
                activityLogDAO.catatAktivitas(currentAdminId, "Tambah Admin", "Admin baru: " + newAdmin.getUsername());
                refreshTable.run();
            } else {
                new Alert(Alert.AlertType.ERROR, "Gagal menambahkan admin. Username mungkin sudah ada.").showAndWait();
            }
        });
        
        updateBtn.setOnAction(e -> {
            Admin selected = adminTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.setUsername(usernameField.getText());
                // Hanya update password jika field diisi
                if (!passwordField.getText().isEmpty()) {
                    selected.setPassword(passwordField.getText());
                }
                selected.setRole(roleComboBox.getValue());
                if (adminDAO.updateAdmin(selected)) {
                    activityLogDAO.catatAktivitas(currentAdminId, "Update Admin", "Data admin diperbarui untuk: " + selected.getUsername());
                    refreshTable.run();
                }
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Pilih admin dari tabel untuk di-update.").showAndWait();
            }
        });

        hapusBtn.setOnAction(e -> {
            Admin selected = adminTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                 if (selected.getId() == currentAdmin.getId()) {
                    new Alert(Alert.AlertType.ERROR, "Anda tidak dapat menghapus akun Anda sendiri.").showAndWait();
                    return;
                }
                
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin menghapus admin " + selected.getUsername() + "?", ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        if (adminDAO.hapusAdmin(selected.getId())) {
                            activityLogDAO.catatAktivitas(currentAdminId, "Hapus Admin", "Admin dihapus: " + selected.getUsername());
                            refreshTable.run();
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Gagal menghapus admin.").showAndWait();
                        }
                    }
                });
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Pilih admin dari tabel untuk dihapus.").showAndWait();
            }
        });
        
        refreshTable.run(); // Muat data awal

        VBox layout = new VBox(15, new Label("Form Admin"), usernameField, passwordField, roleComboBox, buttonBox, new Separator(), adminTable);
        layout.setPadding(new Insets(15));
        
        Scene scene = new Scene(layout, 500, 550);
        adminStage.setScene(scene);
        adminStage.show();
    }
    
    // --- Sisa metode dari MainApp.java (tidak perlu diubah) ---
    // (loadData, handleTambah, handleUpdate, handleHapus, dll.)
    
    private void loadData() {
        String selectedGudang = gudangSelector.getValue();
        if (selectedGudang == null) return;

        String tableName = gudangMap.get(selectedGudang);
        data = FXCollections.observableArrayList(barangDAO.getAllBarang(tableName));
        
        filteredData = new FilteredList<>(data, p -> true);
        table.setItems(filteredData);
        
        updateDashboard();
        clearForm();
    }
    
    private void updateDashboard() {
        if (data == null) return;
        int totalJenis = data.size();
        int totalStok = data.stream().mapToInt(Barang::getStok).sum();
        double totalNilai = data.stream().mapToDouble(b -> b.getHarga() * b.getStok()).sum();
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new java.util.Locale("id", "ID"));

        totalJenisBarangLabel.setText("Total Jenis Barang: " + totalJenis);
        totalStokLabel.setText("Total Stok Barang: " + totalStok);
        nilaiInventarisLabel.setText("Nilai Inventaris: " + currencyFormat.format(totalNilai));
        
        List<PieChart.Data> pieChartData = data.stream()
            .sorted(Comparator.comparingInt(Barang::getStok).reversed())
            .limit(5)
            .map(b -> new PieChart.Data(b.getNama(), b.getStok()))
            .collect(Collectors.toList());
        
        stokPaiChart.setData(FXCollections.observableArrayList(pieChartData));
    }

    private void handleTambah() {
        String selectedGudang = gudangSelector.getValue();
        String tableName = gudangMap.get(selectedGudang);
        if (tableName == null) {
            showFeedback("Pilih gudang terlebih dahulu.", false);
            return;
        }
        if (validasiInput()) {
            Barang b = new Barang(
                null,
                namaField.getText(), 
                Double.parseDouble(hargaField.getText()), 
                Integer.parseInt(stokField.getText()),
                tglKadaluwarsaPicker.getValue()
            );
            if (barangDAO.tambahBarang(b, tableName)) {
                loadData();
                showFeedback("Barang berhasil ditambahkan!", true);
            } else {
                showFeedback("Gagal menambahkan barang.", false);
            }
        }
    }
    
    private void handleUpdate() {
        String tableName = gudangMap.get(gudangSelector.getValue());
        Barang selected = table.getSelectionModel().getSelectedItem();
        if (selected != null && validasiInput()) {
            selected.setNama(namaField.getText());
            selected.setHarga(Double.parseDouble(hargaField.getText()));
            selected.setStok(Integer.parseInt(stokField.getText()));
            selected.setTglKadaluwarsa(tglKadaluwarsaPicker.getValue());
            
            if (barangDAO.updateBarang(selected, tableName)) {
                loadData();
                showFeedback("Barang berhasil diupdate!", true);
            } else {
                showFeedback("Gagal mengupdate barang.", false);
            }
        } else if (selected == null) {
            showFeedback("Pilih barang untuk diupdate.", false);
        }
    }

    private void handleHapus() {
        String tableName = gudangMap.get(gudangSelector.getValue());
        Barang selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Konfirmasi Hapus");
            confirmation.setHeaderText("Hapus Barang");
            confirmation.setContentText("Apakah Anda yakin ingin menghapus barang ini?");
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (barangDAO.hapusBarang(selected.getId(), tableName)) {
                    loadData();
                    showFeedback("Barang berhasil dihapus!", true);
                } else {
                    showFeedback("Gagal menghapus barang.", false);
                }
            }
        } else {
            showFeedback("Pilih barang untuk dihapus.", false);
        }
    }
    
    private void clearForm() {
        namaField.clear();
        hargaField.clear();
        stokField.clear();
        tglKadaluwarsaPicker.setValue(null);
        feedbackLabel.setText("");
        table.getSelectionModel().clearSelection();
    }
    
    private boolean validasiInput() {
        String nama = namaField.getText();
        String harga = hargaField.getText();
        String stok = stokField.getText();

        if (nama.isEmpty() || harga.isEmpty() || stok.isEmpty()) {
            showFeedback("Field Nama, Harga, dan Stok harus diisi!", false);
            return false;
        }
        if (nama.length() > 100) {
            showFeedback("Nama barang maksimal 100 karakter.", false);
            return false;
        }
        try {
            double h = Double.parseDouble(harga);
            if (h < 0) {
                showFeedback("Harga tidak boleh negatif.", false);
                return false;
            }
        } catch (NumberFormatException e) {
            showFeedback("Harga harus angka desimal.", false);
            return false;
        }
        try {
            int s = Integer.parseInt(stok);
            if (s < 0) {
                showFeedback("Stok tidak boleh negatif.", false);
                return false;
            }
        } catch (NumberFormatException e) {
            showFeedback("Stok harus angka bulat.", false);
            return false;
        }
        return true;
    }
    
    private void showFeedback(String message, boolean success) {
        feedbackLabel.setText(message);
        feedbackLabel.getStyleClass().removeAll("feedback-success", "feedback-error");
        if (success) {
            feedbackLabel.getStyleClass().add("feedback-success");
        } else {
            feedbackLabel.getStyleClass().add("feedback-error");
        }
    }

    private void showAllHistory() {
        Stage logStage = new Stage();
        logStage.setTitle("Log Aktivitas Semua Pengguna");

        TableView<ActivityLog> logTable = new TableView<>();
        logTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<ActivityLog, Integer> adminIdCol = new TableColumn<>("Admin ID");
        adminIdCol.setCellValueFactory(new PropertyValueFactory<>("adminId"));
        TableColumn<ActivityLog, LocalDateTime> waktuCol = new TableColumn<>("Waktu");
        waktuCol.setCellValueFactory(new PropertyValueFactory<>("waktu"));
        TableColumn<ActivityLog, String> aktivitasCol = new TableColumn<>("Aktivitas");
        aktivitasCol.setCellValueFactory(new PropertyValueFactory<>("aktivitas"));
        TableColumn<ActivityLog, String> detailCol = new TableColumn<>("Detail");
        detailCol.setCellValueFactory(new PropertyValueFactory<>("detail"));
        logTable.getColumns().addAll(adminIdCol, waktuCol, aktivitasCol, detailCol);

        List<ActivityLog> activityLogs = activityLogDAO.getAllActivityLogs();
        ObservableList<ActivityLog> logData = FXCollections.observableArrayList(activityLogs);
        logTable.setItems(logData);

        VBox logLayout = new VBox(logTable);
        logLayout.setPadding(new Insets(10));
        Scene logScene = new Scene(logLayout, 600, 400);
        logStage.setScene(logScene);
        logStage.showAndWait();
    }
    
    private void exportToPdf() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("laporan_barang_" + gudangSelector.getValue().replace(" ", "_") + ".pdf");

        File file = fileChooser.showSaveDialog(table.getScene().getWindow());

        if (file != null) {
            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);
                
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, 750);
                    contentStream.showText("Laporan Stok Barang - " + gudangSelector.getValue());
                    contentStream.endText();

                    // Header Tabel
                    float yPosition = 700;
                    String[] headers = {"ID", "Nama Barang", "Harga", "Stok", "Kadaluwarsa"};
                    int[] widths = {60, 160, 100, 50, 100};
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                    float xPosition = 50;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(xPosition, yPosition);
                    for(int i=0; i<headers.length; i++){
                        contentStream.showText(headers[i]);
                        contentStream.newLineAtOffset(widths[i], 0);
                    }
                    contentStream.endText();
                    
                    contentStream.moveTo(50, yPosition - 5);
                    contentStream.lineTo(550, yPosition - 5);
                    contentStream.stroke();

                    // Data Tabel
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                    yPosition -= 20;

                    for (Barang item : table.getItems()) {
                        if (yPosition < 50) {
                            contentStream.close();
                            page = new PDPage();
                            document.addPage(page);
                            contentStream.beginText();
                            yPosition = 750;
                        }

                        xPosition = 50;
                        contentStream.beginText();
                        contentStream.newLineAtOffset(xPosition, yPosition);
                        
                        String tglStr = (item.getTglKadaluwarsa() != null) ? item.getTglKadaluwarsa().toString() : "-";
                        String[] rowData = {
                            item.getId(),
                            item.getNama(),
                            String.format("Rp %.2f", item.getHarga()),
                            String.valueOf(item.getStok()),
                            tglStr
                        };

                        for(int i=0; i<rowData.length; i++){
                            contentStream.showText(rowData[i]);
                            contentStream.newLineAtOffset(widths[i], 0);
                        }

                        contentStream.endText();
                        yPosition -= 15;
                    }
                }

                document.save(file);
                showFeedback("PDF berhasil diekspor!", true);
                activityLogDAO.catatAktivitas(currentAdminId, "Ekspor PDF", "Laporan diekspor ke: " + file.getName());

            } catch (IOException ex) {
                ex.printStackTrace();
                showFeedback("Gagal mengekspor PDF.", false);
                Alert alert = new Alert(Alert.AlertType.ERROR, "Terjadi kesalahan saat mengekspor file PDF.\n" + ex.getMessage(), ButtonType.OK);
                alert.setTitle("Error Ekspor");
                alert.showAndWait();
            }
        }
    }
}