-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jul 21, 2025 at 02:21 AM
-- Server version: 8.0.30
-- PHP Version: 8.3.23

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `warehouse`
--

-- --------------------------------------------------------

--
-- Table structure for table `activity_log`
--

CREATE TABLE `activity_log` (
  `id` int NOT NULL,
  `waktu` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `admin_id` int DEFAULT NULL,
  `aktivitas` varchar(255) NOT NULL,
  `detail` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `activity_log`
--

INSERT INTO `activity_log` (`id`, `waktu`, `admin_id`, `aktivitas`, `detail`) VALUES
(133, '2025-07-07 01:58:16', 1, 'Login', 'Username: admin'),
(134, '2025-07-07 01:58:50', 1, 'Update Barang', 'Update di barang_gudang_a ID: GA-0005'),
(135, '2025-07-07 01:59:03', 1, 'Logout', 'Admin ID: 1'),
(136, '2025-07-07 02:37:30', 1, 'Login', 'Username: admin'),
(137, '2025-07-07 02:41:24', 1, 'Logout', 'Admin ID: 1'),
(138, '2025-07-20 13:35:53', 2, 'Login', 'Username: admin2'),
(139, '2025-07-20 13:36:08', 2, 'Logout', 'Admin ID: 2'),
(140, '2025-07-20 13:36:15', 1, 'Login', 'Username: admin'),
(141, '2025-07-20 13:36:47', 1, 'Tambah Admin', 'Admin baru: admin3'),
(142, '2025-07-20 13:37:00', 1, 'Logout', 'Admin ID: 1'),
(143, '2025-07-20 13:37:08', 3, 'Login', 'Username: admin3'),
(144, '2025-07-20 13:37:15', 3, 'Logout', 'Admin ID: 3'),
(145, '2025-07-21 01:42:57', 1, 'Login', 'Username: admin'),
(146, '2025-07-21 01:43:03', 1, 'Logout', 'Admin ID: 1'),
(147, '2025-07-21 02:16:29', 1, 'Login', 'Username: admin'),
(148, '2025-07-21 02:16:38', 1, 'Logout', 'Admin ID: 1'),
(149, '2025-07-21 02:19:32', 1, 'Login', 'Username: admin'),
(150, '2025-07-21 02:19:41', 1, 'Logout', 'Admin ID: 1');

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `id` int NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(20) NOT NULL DEFAULT 'admin'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`id`, `username`, `password`, `role`) VALUES
(1, 'admin', 'admin123', 'super_admin'),
(2, 'admin2', 'admin123', 'admin'),
(3, 'admin3', 'admin123', 'admin');

-- --------------------------------------------------------

--
-- Table structure for table `barang_gudang_a`
--

CREATE TABLE `barang_gudang_a` (
  `id` varchar(20) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `harga` double NOT NULL,
  `stok` int NOT NULL,
  `tgl_kadaluwarsa` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `barang_gudang_a`
--

INSERT INTO `barang_gudang_a` (`id`, `nama`, `harga`, `stok`, `tgl_kadaluwarsa`) VALUES
('GA-0001', 'Buku Tulis Sinar Dunia 58 Lbr', 5500, 150, NULL),
('GA-0002', 'Pensil 2B Faber-Castell', 4000, 200, NULL),
('GA-0003', 'Pulpen Pilot G2 0.5mm Hitam', 25000, 75, NULL),
('GA-0004', 'Kertas HVS A4 80gr Rim', 65000, 50, NULL),
('GA-0005', 'Tinta Printer Epson 003 Hitam', 85000, 25, NULL),
('GA-0006', 'Stabilo Boss Original Kuning', 12000, 4, NULL),
('GA-0007', 'Map Plastik Kancing Folio', 3000, 300, NULL),
('GA-0008', 'Cutter Joyko L-500', 15000, 30, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `barang_gudang_b`
--

CREATE TABLE `barang_gudang_b` (
  `id` varchar(20) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `harga` double NOT NULL,
  `stok` int NOT NULL,
  `tgl_kadaluwarsa` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `barang_gudang_b`
--

INSERT INTO `barang_gudang_b` (`id`, `nama`, `harga`, `stok`, `tgl_kadaluwarsa`) VALUES
('GB-0001', 'Indomie Goreng Original Dus', 125000, 80, '2026-08-15'),
('GB-0002', 'Susu Ultra Milk Coklat 1L', 18000, 45, '2025-07-20'),
('GB-0003', 'Minyak Goreng Sania 2L', 38000, 60, '2027-01-01'),
('GB-0004', 'Beras Sania Premium 5kg', 72000, 40, NULL),
('GB-0005', 'Telur Ayam Negeri per Kg', 28000, 15, '2025-07-10'),
('GB-0006', 'Kopi Kapal Api Special Mix Sachet', 1500, 500, '2026-11-20'),
('GB-0007', 'Sarden ABC Saus Tomat 425g', 22000, 3, '2025-09-05'),
('GB-0008', 'Roti Tawar Sari Roti', 16000, 10, '2025-06-25');

-- --------------------------------------------------------

--
-- Table structure for table `barang_gudang_c`
--

CREATE TABLE `barang_gudang_c` (
  `id` varchar(20) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `harga` double NOT NULL,
  `stok` int NOT NULL,
  `tgl_kadaluwarsa` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `barang_gudang_c`
--

INSERT INTO `barang_gudang_c` (`id`, `nama`, `harga`, `stok`, `tgl_kadaluwarsa`) VALUES
('GC-0001', 'Sapu Ijuk Nagata', 25000, 30, NULL),
('GC-0002', 'Deterjen Rinso Anti Noda 1.8kg', 55000, 20, NULL),
('GC-0003', 'Pembersih Lantai Super Pell 770ml', 14000, 40, NULL),
('GC-0004', 'Sunlight Cairan Cuci Piring 750ml', 15000, 50, '2028-01-01'),
('GC-0005', 'Ember Plastik 20L', 35000, 15, NULL),
('GC-0006', 'Gayung Air Calista', 8000, 2, NULL),
('GC-0007', 'Lampu LED Philips 12W', 45000, 25, NULL),
('GC-0008', 'Kain Lap Microfiber', 12000, 100, NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `activity_log`
--
ALTER TABLE `activity_log`
  ADD PRIMARY KEY (`id`),
  ADD KEY `admin_id` (`admin_id`);

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `barang_gudang_a`
--
ALTER TABLE `barang_gudang_a`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `barang_gudang_b`
--
ALTER TABLE `barang_gudang_b`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `barang_gudang_c`
--
ALTER TABLE `barang_gudang_c`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `activity_log`
--
ALTER TABLE `activity_log`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=151;

--
-- AUTO_INCREMENT for table `admin`
--
ALTER TABLE `admin`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `activity_log`
--
ALTER TABLE `activity_log`
  ADD CONSTRAINT `fk_admin_id` FOREIGN KEY (`admin_id`) REFERENCES `admin` (`id`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
