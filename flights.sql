-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 18, 2025 at 07:56 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `flights`
--

-- --------------------------------------------------------

--
-- Table structure for table `aircraft`
--

CREATE TABLE `aircraft` (
  `id` int(11) NOT NULL,
  `airline_id` int(11) NOT NULL,
  `model` text NOT NULL,
  `manufacturing_year` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `aircraft`
--

INSERT INTO `aircraft` (`id`, `airline_id`, `model`, `manufacturing_year`) VALUES
(1, 1, 'air c 1', 2025),
(2, 2, 'air c 2', 2001),
(3, 1, 'air c 3', 2003),
(4, 2, 'air c 4', 2013);

-- --------------------------------------------------------

--
-- Table structure for table `airline`
--

CREATE TABLE `airline` (
  `id` int(11) NOT NULL,
  `name` text NOT NULL,
  `code` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `airline`
--

INSERT INTO `airline` (`id`, `name`, `code`) VALUES
(1, 'emirates', '234'),
(2, 'cairo', '123');

-- --------------------------------------------------------

--
-- Table structure for table `airport`
--

CREATE TABLE `airport` (
  `id` int(11) NOT NULL,
  `country_id` int(11) NOT NULL,
  `code` text NOT NULL,
  `name` text NOT NULL,
  `address` text DEFAULT NULL,
  `status` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `airport`
--

INSERT INTO `airport` (`id`, `country_id`, `code`, `name`, `address`, `status`) VALUES
(1, 1, '234', 'ismailia', 'cairo', 'Active'),
(3, 1, '345', 'cairo airport', '1234 st cairo', 'Active');

-- --------------------------------------------------------

--
-- Table structure for table `country`
--

CREATE TABLE `country` (
  `id` int(11) NOT NULL,
  `name` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `country`
--

INSERT INTO `country` (`id`, `name`) VALUES
(1, 'Germany');

-- --------------------------------------------------------

--
-- Table structure for table `customschedule`
--

CREATE TABLE `customschedule` (
  `id` int(11) NOT NULL,
  `flight_id` int(11) NOT NULL,
  `departure_time` time NOT NULL,
  `customDate` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `flight`
--

CREATE TABLE `flight` (
  `id` int(11) NOT NULL,
  `arrival_airport_id` int(11) NOT NULL,
  `departure_airport_id` int(11) NOT NULL,
  `gate` text DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `flight_schedule_id` int(11) NOT NULL,
  `aircraft_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `flight`
--

INSERT INTO `flight` (`id`, `arrival_airport_id`, `departure_airport_id`, `gate`, `duration`, `flight_schedule_id`, `aircraft_id`) VALUES
(3, 3, 1, 'A1', 360, 8, 2),
(4, 1, 3, 'A2', 360, 9, 1);

-- --------------------------------------------------------

--
-- Table structure for table `flightreservation`
--

CREATE TABLE `flightreservation` (
  `id` int(11) NOT NULL,
  `flight_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `qr_code` text DEFAULT NULL,
  `booking_date` date DEFAULT NULL,
  `status` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `passenger`
--

CREATE TABLE `passenger` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `name` text NOT NULL,
  `passport` text DEFAULT NULL,
  `flightReservation_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `passenger_seat`
--

CREATE TABLE `passenger_seat` (
  `passenger_id` int(11) NOT NULL,
  `seat_id` int(11) NOT NULL,
  `flightReservation_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `payment`
--

CREATE TABLE `payment` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `payment_amount` decimal(10,0) NOT NULL,
  `payment_state` text NOT NULL,
  `payment_method` text NOT NULL,
  `payment_date` date NOT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `reservation_payment`
--

CREATE TABLE `reservation_payment` (
  `flightReservation_id` int(11) DEFAULT NULL,
  `payment_id` int(11) NOT NULL,
  `flight_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `role`
--

CREATE TABLE `role` (
  `id` int(11) NOT NULL,
  `role_name` text NOT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `role`
--

INSERT INTO `role` (`id`, `role_name`, `description`) VALUES
(1, 'Admin', NULL),
(2, 'User', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `seat`
--

CREATE TABLE `seat` (
  `id` int(11) NOT NULL,
  `aircraft_id` int(11) NOT NULL,
  `class` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `username` text NOT NULL,
  `email` text NOT NULL,
  `phone` text DEFAULT NULL,
  `password` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_at` timestamp NULL DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `role_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id`, `username`, `email`, `phone`, `password`, `created_at`, `updated_at`, `age`, `role_id`) VALUES
(1, 'samer', 'samer@gmail.com', '01099671155', 'a4/JBmiMD5itFEK86Q9sIXzGW4T1qLobX9aG4YSarkE=:0xehDSqQQlu/R2P6Xi7EjQ==', '2025-05-18 12:29:12', NULL, 30, 1),
(2, 'kamal', 'kamal@gmail.com', '01234567789', 'G7U66Ek0Itr7f5AsJ8dXS+Pady6hQ33hlEIJgMlo8hw=:pH79NobhZ1Y4yEnDTkHd9g==', '2025-05-18 12:59:10', NULL, 13, 2);

-- --------------------------------------------------------

--
-- Table structure for table `weeklyschedule`
--

CREATE TABLE `weeklyschedule` (
  `id` int(11) NOT NULL,
  `dayOfWeek` text NOT NULL,
  `departure_time` time NOT NULL,
  `customDate` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `weeklyschedule`
--

INSERT INTO `weeklyschedule` (`id`, `dayOfWeek`, `departure_time`, `customDate`) VALUES
(1, 'WEDNESDAY', '08:00:00', NULL),
(2, 'MONDAY', '08:00:00', NULL),
(3, 'THURSDAY', '09:00:00', NULL),
(4, 'FRIDAY', '13:00:00', NULL),
(5, 'SUNDAY', '16:00:00', NULL),
(6, 'SATURDAY', '08:00:00', NULL),
(7, 'MONDAY', '08:00:00', NULL),
(8, 'TUESDAY', '08:00:00', NULL),
(9, 'FRIDAY', '12:00:00', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `aircraft`
--
ALTER TABLE `aircraft`
  ADD PRIMARY KEY (`id`),
  ADD KEY `airline_id` (`airline_id`);

--
-- Indexes for table `airline`
--
ALTER TABLE `airline`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `airport`
--
ALTER TABLE `airport`
  ADD PRIMARY KEY (`id`),
  ADD KEY `country_id` (`country_id`);

--
-- Indexes for table `country`
--
ALTER TABLE `country`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `customschedule`
--
ALTER TABLE `customschedule`
  ADD PRIMARY KEY (`id`),
  ADD KEY `flight_id` (`flight_id`);

--
-- Indexes for table `flight`
--
ALTER TABLE `flight`
  ADD PRIMARY KEY (`id`),
  ADD KEY `arrival_airport_id` (`arrival_airport_id`),
  ADD KEY `departure_airport_id` (`departure_airport_id`),
  ADD KEY `flight_schedule_id` (`flight_schedule_id`),
  ADD KEY `aircraft_id` (`aircraft_id`);

--
-- Indexes for table `flightreservation`
--
ALTER TABLE `flightreservation`
  ADD PRIMARY KEY (`id`),
  ADD KEY `flight_id` (`flight_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `passenger`
--
ALTER TABLE `passenger`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `flightReservation_id` (`flightReservation_id`);

--
-- Indexes for table `passenger_seat`
--
ALTER TABLE `passenger_seat`
  ADD PRIMARY KEY (`passenger_id`,`seat_id`),
  ADD KEY `seat_id` (`seat_id`),
  ADD KEY `flightReservation_id` (`flightReservation_id`);

--
-- Indexes for table `payment`
--
ALTER TABLE `payment`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `reservation_payment`
--
ALTER TABLE `reservation_payment`
  ADD PRIMARY KEY (`flight_id`,`payment_id`),
  ADD KEY `payment_id` (`payment_id`);

--
-- Indexes for table `role`
--
ALTER TABLE `role`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `seat`
--
ALTER TABLE `seat`
  ADD PRIMARY KEY (`id`),
  ADD KEY `aircraft_id` (`aircraft_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD KEY `role_id` (`role_id`);

--
-- Indexes for table `weeklyschedule`
--
ALTER TABLE `weeklyschedule`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `aircraft`
--
ALTER TABLE `aircraft`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `airline`
--
ALTER TABLE `airline`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `airport`
--
ALTER TABLE `airport`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `country`
--
ALTER TABLE `country`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `customschedule`
--
ALTER TABLE `customschedule`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `flight`
--
ALTER TABLE `flight`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `flightreservation`
--
ALTER TABLE `flightreservation`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `passenger`
--
ALTER TABLE `passenger`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `payment`
--
ALTER TABLE `payment`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `role`
--
ALTER TABLE `role`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `seat`
--
ALTER TABLE `seat`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `weeklyschedule`
--
ALTER TABLE `weeklyschedule`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `aircraft`
--
ALTER TABLE `aircraft`
  ADD CONSTRAINT `aircraft_ibfk_1` FOREIGN KEY (`airline_id`) REFERENCES `airline` (`id`);

--
-- Constraints for table `airport`
--
ALTER TABLE `airport`
  ADD CONSTRAINT `airport_ibfk_1` FOREIGN KEY (`country_id`) REFERENCES `country` (`id`);

--
-- Constraints for table `customschedule`
--
ALTER TABLE `customschedule`
  ADD CONSTRAINT `customschedule_ibfk_1` FOREIGN KEY (`flight_id`) REFERENCES `flight` (`id`);

--
-- Constraints for table `flight`
--
ALTER TABLE `flight`
  ADD CONSTRAINT `flight_ibfk_1` FOREIGN KEY (`arrival_airport_id`) REFERENCES `airport` (`id`),
  ADD CONSTRAINT `flight_ibfk_2` FOREIGN KEY (`departure_airport_id`) REFERENCES `airport` (`id`),
  ADD CONSTRAINT `flight_ibfk_3` FOREIGN KEY (`flight_schedule_id`) REFERENCES `weeklyschedule` (`id`),
  ADD CONSTRAINT `flight_ibfk_4` FOREIGN KEY (`aircraft_id`) REFERENCES `aircraft` (`id`);

--
-- Constraints for table `flightreservation`
--
ALTER TABLE `flightreservation`
  ADD CONSTRAINT `flightreservation_ibfk_1` FOREIGN KEY (`flight_id`) REFERENCES `flight` (`id`),
  ADD CONSTRAINT `flightreservation_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Constraints for table `passenger`
--
ALTER TABLE `passenger`
  ADD CONSTRAINT `passenger_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `passenger_ibfk_2` FOREIGN KEY (`flightReservation_id`) REFERENCES `flightreservation` (`id`);

--
-- Constraints for table `passenger_seat`
--
ALTER TABLE `passenger_seat`
  ADD CONSTRAINT `passenger_seat_ibfk_1` FOREIGN KEY (`passenger_id`) REFERENCES `passenger` (`id`),
  ADD CONSTRAINT `passenger_seat_ibfk_2` FOREIGN KEY (`seat_id`) REFERENCES `seat` (`id`),
  ADD CONSTRAINT `passenger_seat_ibfk_3` FOREIGN KEY (`flightReservation_id`) REFERENCES `flightreservation` (`id`);

--
-- Constraints for table `payment`
--
ALTER TABLE `payment`
  ADD CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Constraints for table `reservation_payment`
--
ALTER TABLE `reservation_payment`
  ADD CONSTRAINT `reservation_payment_ibfk_1` FOREIGN KEY (`flight_id`) REFERENCES `flightreservation` (`id`),
  ADD CONSTRAINT `reservation_payment_ibfk_2` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`);

--
-- Constraints for table `seat`
--
ALTER TABLE `seat`
  ADD CONSTRAINT `seat_ibfk_1` FOREIGN KEY (`aircraft_id`) REFERENCES `aircraft` (`id`);

--
-- Constraints for table `user`
--
ALTER TABLE `user`
  ADD CONSTRAINT `user_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
