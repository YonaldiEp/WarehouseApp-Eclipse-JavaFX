package dao;

import model.ActivityLog;
import util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Mengelola log aktivitas di database.
public class ActivityLogDAO {

    // Mencatat aktivitas baru ke database.
    public void catatAktivitas(int adminId, String aktivitas, String detail) {
        String sql = "INSERT INTO activity_log (admin_id, aktivitas, detail) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            ps.setString(2, aktivitas);
            ps.setString(3, detail);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mengambil semua log dari database.
    public List<ActivityLog> getAllActivityLogs() {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM activity_log ORDER BY waktu DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ActivityLog log = new ActivityLog(
                        rs.getInt("id"),
                        rs.getTimestamp("waktu").toLocalDateTime(),
                        rs.getInt("admin_id"),
                        rs.getString("aktivitas"),
                        rs.getString("detail")
                );
                logs.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    // Mengambil log untuk admin tertentu.
    public List<ActivityLog> getActivityLogsByAdmin(int adminId) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM activity_log WHERE admin_id = ? ORDER BY waktu DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ActivityLog log = new ActivityLog(
                            rs.getInt("id"),
                            rs.getTimestamp("waktu").toLocalDateTime(),
                            Integer.valueOf(rs.getInt("admin_id")),
                            rs.getString("aktivitas"),
                            rs.getString("detail")
                    );
                    logs.add(log);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
}