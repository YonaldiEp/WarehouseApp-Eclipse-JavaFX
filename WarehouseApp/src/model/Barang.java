package model;

import java.time.LocalDate;

// Kelas untuk menyimpan data barang.
public class Barang {
    private String id; // ID barang (contoh: "GA-0001").
    private String nama;
    private double harga;
    private int stok;
    private LocalDate tglKadaluwarsa;

    // Konstruktor untuk membuat objek Barang.
    public Barang(String id, String nama, double harga, int stok, LocalDate tglKadaluwarsa) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
        this.tglKadaluwarsa = tglKadaluwarsa;
    }

    // Kumpulan method Getter dan Setter untuk data barang.
    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public double getHarga() {
        return harga;
    }

    public int getStok() {
        return stok;
    }

    public LocalDate getTglKadaluwarsa() {
        return tglKadaluwarsa;
    }

    public void setTglKadaluwarsa(LocalDate tglKadaluwarsa) {
        this.tglKadaluwarsa = tglKadaluwarsa;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }
}