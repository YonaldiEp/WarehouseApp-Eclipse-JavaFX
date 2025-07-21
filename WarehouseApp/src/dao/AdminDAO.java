package dao;

import model.Admin;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Mengelola data admin di database, termasuk CRUD untuk super admin.
public class AdminDAO {

    /**
     * Memeriksa login admin.
     * @return Objek Admin jika berhasil, null jika gagal atau error.
     */
    public Admin cekLogin(String username, String password) {
        String sql = "SELECT id, username, password, role FROM admin WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Admin(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
            return null; // Gagal: username atau password salah.
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Error koneksi atau query.
        }
    }

    // --- Metode CRUD untuk Manajemen Admin (Fitur Super Admin) ---

    /**
     * Mengambil semua data admin dari database.
     */
    public List<Admin> getAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT id, username, password, role FROM admin";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                admins.add(new Admin(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }

    /**
     * Menambah admin baru ke database.
     */
    public boolean tambahAdmin(Admin admin) {
        String sql = "INSERT INTO admin (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, admin.getUsername());
            ps.setString(2, admin.getPassword());
            ps.setString(3, admin.getRole());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Memperbarui data admin yang ada di database.
     */
    public boolean updateAdmin(Admin admin) {
        String sql = "UPDATE admin SET username = ?, password = ?, role = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, admin.getUsername());
            ps.setString(2, admin.getPassword());
            ps.setString(3, admin.getRole());
            ps.setInt(4, admin.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Menghapus admin dari database berdasarkan ID.
     */
    public boolean hapusAdmin(int adminId) {
        // Pencegahan agar super admin tidak bisa menghapus dirinya sendiri
        if (adminId == 1) {
            return false;
        }
        String sql = "DELETE FROM admin WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}