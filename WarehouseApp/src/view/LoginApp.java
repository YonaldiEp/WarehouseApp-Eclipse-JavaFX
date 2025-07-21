package view;

import dao.AdminDAO;
import dao.ActivityLogDAO;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Admin; // Import model Admin

/**
 * Mengatur tampilan dan logika untuk form login.
 */
public class LoginApp extends Application {

    private final AdminDAO adminDAO = new AdminDAO();
    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Warehouse Login");

        // Membuat semua komponen UI
        Label titleLabel = new Label("🔐 Warehouse Login");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginBtn = new Button("Login");
        Label messageLabel = new Label();

        // Menggunakan GridPane untuk tata letak
        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER);

        // Menambahkan komponen ke grid
        grid.add(titleLabel, 0, 0, 2, 1);
        grid.add(new Label("Username:"), 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(loginBtn, 1, 3);
        grid.add(messageLabel, 1, 4);

        // Mengatur aksi tombol login
        loginBtn.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // DIUBAH: cekLogin sekarang mengembalikan objek Admin lengkap
            Admin loggedInAdmin = adminDAO.cekLogin(username, password);

            if (loggedInAdmin != null) {
                // Jika login berhasil
                messageLabel.setText("✅ Login berhasil!");
                primaryStage.close();

                MainApp mainApp = new MainApp();
                // KIRIM OBJEK ADMIN: Mengirim seluruh objek admin ke MainApp
                mainApp.initSession(loggedInAdmin);
                mainApp.start(new Stage());

                // Mencatat aktivitas login
                activityLogDAO.catatAktivitas(loggedInAdmin.getId(), "Login", "Username: " + username);

            } else {
                // Jika gagal, tampilkan pesan error
                messageLabel.setText("❌ Login gagal. Coba lagi.");
            }
        });

        // Menyiapkan dan menampilkan Scene
        StackPane root = new StackPane(grid);
        root.setPadding(new Insets(20));
        Scene scene = new Scene(root, 400, 300);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}