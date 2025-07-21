package model;

import java.time.LocalDateTime;

// Kelas untuk menyimpan data log aktivitas.
public class ActivityLog {
    private int id;
    private LocalDateTime waktu;
    private Integer adminId;
    private String aktivitas;
    private String detail;

    // Konstruktor untuk membuat objek ActivityLog.
    public ActivityLog(int id, LocalDateTime waktu, Integer adminId, String aktivitas, String detail) {
        this.id = id;
        this.waktu = waktu;
        this.adminId = adminId;
        this.aktivitas = aktivitas;
        this.detail = detail;
    }

    // Kumpulan method Getter untuk mengambil data.
    public int getId() {
        return id;
    }

    public LocalDateTime getWaktu() {
        return waktu;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public String getAktivitas() {
        return aktivitas;
    }

    public String getDetail() {
        return detail;
    }
}