package dao;

import model.Barang;
import util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Mengelola data barang (operasi CRUD) di database.
public class BarangDAO {

    private int currentAdminId;
    private ActivityLogDAO activityLogDAO = new ActivityLogDAO();

    // Mengatur ID admin yang sedang aktif.
    public void setCurrentAdminId(int adminId) {
        this.currentAdminId = adminId;
    }
    
    // Membuat ID barang baru secara otomatis (misal: "GA-0001").
    private String generateNewId(String tableName) throws SQLException {
        String prefix = "";
        if (tableName.equalsIgnoreCase("barang_gudang_a")) {
            prefix = "GA";
        } else if (tableName.equalsIgnoreCase("barang_gudang_b")) {
            prefix = "GB";
        } else if (tableName.equalsIgnoreCase("barang_gudang_c")) {
            prefix = "GC";
        } else {
            throw new SQLException("Nama tabel tidak valid untuk generate ID.");
        }

        String sql = String.format("SELECT id FROM %s WHERE id LIKE ? ORDER BY id DESC LIMIT 1", tableName);
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, prefix + "-%");
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String lastId = rs.getString("id");
                int lastNumber = Integer.parseInt(lastId.substring(prefix.length() + 1));
                int newNumber = lastNumber + 1;
                return String.format("%s-%04d", prefix, newNumber);
            } else {
                return String.format("%s-0001", prefix);
            }
        }
    }

    // Mengambil semua barang dari gudang.
    public List<Barang> getAllBarang(String tableName) {
        List<Barang> list = new ArrayList<>();
        String sql = String.format("SELECT * FROM %s ORDER BY nama", tableName);

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Date tglSql = rs.getDate("tgl_kadaluwarsa");
                LocalDate tglKadaluwarsa = (tglSql != null) ? tglSql.toLocalDate() : null;

                Barang b = new Barang(
                        rs.getString("id"), // DIUBAH ke getString
                        rs.getString("nama"),
                        rs.getDouble("harga"),
                        rs.getInt("stok"),
                        tglKadaluwarsa
                );
                list.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Menambah barang baru ke database.
    public boolean tambahBarang(Barang barang, String tableName) {
        String newId;
        try {
            newId = generateNewId(tableName);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = String.format("INSERT INTO %s (id, nama, harga, stok, tgl_kadaluwarsa) VALUES (?, ?, ?, ?, ?)", tableName);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, newId); // Set ID yang baru dibuat
            ps.setString(2, barang.getNama());
            ps.setDouble(3, barang.getHarga());
            ps.setInt(4, barang.getStok());

            if (barang.getTglKadaluwarsa() != null) {
                ps.setDate(5, Date.valueOf(barang.getTglKadaluwarsa()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            
            ps.executeUpdate();
            activityLogDAO.catatAktivitas(currentAdminId, "Tambah Barang", "Barang: " + barang.getNama() + " dengan ID " + newId + " di tabel " + tableName);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Memperbarui data barang di database.
    public boolean updateBarang(Barang barang, String tableName) {
        String sql = String.format("UPDATE %s SET nama = ?, harga = ?, stok = ?, tgl_kadaluwarsa = ? WHERE id = ?", tableName);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, barang.getNama());
            ps.setDouble(2, barang.getHarga());
            ps.setInt(3, barang.getStok());

            if (barang.getTglKadaluwarsa() != null) {
                ps.setDate(4, Date.valueOf(barang.getTglKadaluwarsa()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setString(5, barang.getId()); // DIUBAH ke setString
            ps.executeUpdate();

            String detail = "Update di " + tableName + " ID: " + barang.getId();
            activityLogDAO.catatAktivitas(currentAdminId, "Update Barang", detail);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Menghapus barang dari database.
    public boolean hapusBarang(String id, String tableName) { // Parameter diubah ke String
        String sql = String.format("DELETE FROM %s WHERE id = ?", tableName);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id); // DIUBAH ke setString
            ps.executeUpdate();
            activityLogDAO.catatAktivitas(currentAdminId, "Hapus Barang", "Barang ID: " + id + " dari tabel " + tableName);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}