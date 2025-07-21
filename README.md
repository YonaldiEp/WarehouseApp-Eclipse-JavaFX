# WarehouseApp-Eclipse-JavaFX
Tugas Besar Algoritma II

# Warehouse Management System 📦

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://www.java.com)
[![JavaFX](https://img.shields.io/badge/JavaFX-17-blue.svg)](https://openjfx.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)](https://www.mysql.com/)

**Warehouse Management System** adalah aplikasi desktop yang dirancang untuk membantu mengelola inventaris barang di beberapa gudang. Aplikasi ini dibangun menggunakan **JavaFX** dan menyediakan antarmuka yang intuitif untuk melakukan operasi CRUD (Create, Read, Update, Delete) pada data barang, mengelola pengguna, serta memantau aktivitas dan status inventaris secara *real-time*.

## Fitur Utama ✨

* **Manajemen Multi-Gudang**: Kelola inventaris dari beberapa gudang (Gudang A, Gudang B, Gudang C) dari satu aplikasi terpusat.
* **Autentikasi Pengguna**: Sistem login aman dengan dua level peran: **Super Admin** dan **Admin**.
* **Manajemen Barang (CRUD)**:
    * Menambah, memperbarui, dan menghapus data barang.
    * ID barang unik yang digenerasi secara otomatis untuk setiap gudang (misal: `GA-0001`).
    * Pencarian barang secara dinamis berdasarkan nama.
* **Dashboard Interaktif**:
    * Visualisasi data inventaris: total jenis barang, total stok, dan total nilai inventaris.
    * Grafik Pai untuk menampilkan 5 barang dengan stok terbanyak.
* **Notifikasi Status Barang**:
    * Pewarnaan baris pada tabel untuk menandai barang yang **stoknya menipis**, akan **segera kedaluwarsa** (kurang dari 1 bulan), dan sudah **kedaluwarsa**.
* **Manajemen Admin (Khusus Super Admin)**:
    * Super Admin dapat menambah, mengedit, dan menghapus akun admin lainnya.
* **Pencatatan Aktivitas (Activity Log)**:
    * Semua aktivitas penting seperti login, logout, dan operasi CRUD pada barang atau admin akan tercatat dalam history.
* **Ekspor ke PDF**:
    * Buat laporan inventaris dari setiap gudang dan ekspor ke dalam format PDF.

## Teknologi yang Digunakan 🛠️

* **Bahasa**: Java
* **Framework UI**: JavaFX
* **Database**: MySQL
* **Konektivitas Database**: JDBC
* **Library Tambahan**:
    * Apache PDFBox (untuk ekspor PDF)

## Prasyarat 📋

Sebelum menjalankan aplikasi, pastikan Anda telah menginstal perangkat lunak berikut:
* **JDK (Java Development Kit)** versi 11 atau yang lebih baru.
* **JavaFX SDK**.
* **MySQL Server** versi 8.0 atau yang lebih baru.
* **IDE** seperti IntelliJ IDEA, Eclipse, atau NetBeans.
* **MySQL Connector/J** (library untuk menghubungkan Java ke MySQL).

## Instalasi & Konfigurasi ⚙️

1.  **Clone Repositori**
    ```bash
    git clone [URL_REPOSITORI_ANDA]
    cd [NAMA_DIREKTORI_PROYEK]
    ```

2.  **Setup Database**
    * Buka *command-line* MySQL atau *tool* GUI database Anda (misal: phpMyAdmin, DBeaver).
    * Buat database baru dengan nama `warehouse`.
        ```sql
        CREATE DATABASE warehouse;
        ```
    * Gunakan database tersebut dan jalankan skrip SQL di bawah ini untuk membuat semua tabel yang diperlukan.

    <details>
      <summary><strong>Klik untuk melihat Skema SQL</strong></summary>

      ```sql
      CREATE TABLE admin (
          id INT PRIMARY KEY AUTO_INCREMENT,
          username VARCHAR(255) UNIQUE NOT NULL,
          password VARCHAR(255) NOT NULL,
          role VARCHAR(50) NOT NULL -- 'admin' atau 'super_admin'
      );

      -- Buat satu akun super_admin untuk login awal
      INSERT INTO admin (username, password, role) VALUES ('superadmin', 'superadmin', 'super_admin');

      CREATE TABLE barang_gudang_a (
          id VARCHAR(10) PRIMARY KEY, -- contoh: GA-0001
          nama VARCHAR(100) NOT NULL,
          harga DOUBLE NOT NULL,
          stok INT NOT NULL,
          tgl_kadaluwarsa DATE NULL
      );

      CREATE TABLE barang_gudang_b (
          id VARCHAR(10) PRIMARY KEY, -- contoh: GB-0001
          nama VARCHAR(100) NOT NULL,
          harga DOUBLE NOT NULL,
          stok INT NOT NULL,
          tgl_kadaluwarsa DATE NULL
      );

      CREATE TABLE barang_gudang_c (
          id VARCHAR(10) PRIMARY KEY, -- contoh: GC-0001
          nama VARCHAR(100) NOT NULL,
          harga DOUBLE NOT NULL,
          stok INT NOT NULL,
          tgl_kadaluwarsa DATE NULL
      );

      CREATE TABLE activity_log (
          id INT PRIMARY KEY AUTO_INCREMENT,
          waktu TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          admin_id INT,
          aktivitas VARCHAR(255),
          detail TEXT,
          FOREIGN KEY (admin_id) REFERENCES admin(id) ON DELETE SET NULL
      );
      ```
    </details>

3.  **Konfigurasi Proyek di IDE**
    * Buka proyek di IDE Anda.
    * Pastikan untuk menambahkan library **JavaFX** dan **MySQL Connector/J** ke dalam *build path* atau dependensi proyek Anda.
    * Konfigurasi VM Options untuk JavaFX jika diperlukan. Contoh untuk IntelliJ IDEA:
        ```
        --module-path /path/to/javafx-sdk-17/lib --add-modules javafx.controls,javafx.fxml
        ```

4.  **Jalankan Aplikasi**
    * Temukan dan jalankan file `src/view/LoginApp.java` sebagai file utama.

## Cara Menggunakan Aplikasi 🚀

1.  **Login**:
    * Gunakan akun default yang telah dibuat:
        * **Username**: `superadmin`
        * **Password**: `superadmin`
2.  **Navigasi Utama**:
    * Setelah login, Anda akan masuk ke halaman utama.
    * Pilih gudang yang ingin Anda kelola melalui *dropdown* di bagian atas.
3.  **Mengelola Barang**:
    * **Tambah**: Isi formulir di sisi kanan dan klik tombol "Tambah".
    * **Update**: Pilih barang dari tabel, data akan muncul di formulir. Ubah data yang diinginkan, lalu klik "Update".
    * **Hapus**: Pilih barang dari tabel, lalu klik tombol "Hapus".
4.  **Manajemen Admin (Super Admin)**:
    * Klik tombol "Manajemen Admin" untuk membuka jendela baru di mana Anda dapat mengelola data pengguna admin lain.
5.  **Ekspor Laporan**:
    * Klik "Ekspor ke PDF" untuk menyimpan snapshot inventaris gudang yang sedang aktif ke dalam file PDF.