-- MySQL Database Seeder Script
-- Populates the flights database with sample data for testing
-- Compatible with schema (artifact_id: 39219e2b-223a-41a4-96d6-c7f3f63da784)

-- Start transaction for atomicity
START TRANSACTION;

-- Disable foreign key checks for truncation
SET FOREIGN_KEY_CHECKS = 0;

-- Truncate tables in reverse dependency order
TRUNCATE TABLE passenger_seat;
TRUNCATE TABLE flight_payment;
TRUNCATE TABLE flightReservation;
TRUNCATE TABLE passenger;
TRUNCATE TABLE customSchedule;
TRUNCATE TABLE flight;
TRUNCATE TABLE seat;
TRUNCATE TABLE aircraft;
TRUNCATE TABLE weeklySchedule;
TRUNCATE TABLE payment;
TRUNCATE TABLE user;
TRUNCATE TABLE role;
TRUNCATE TABLE airport;
TRUNCATE TABLE airline;
TRUNCATE TABLE country;

-- Enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Seed country table
-- Major countries for airport locations
INSERT INTO country (name) VALUES
('United States'),
('United Kingdom'),
('Japan'),
('Germany'),
('France'),
('Canada'),
('Australia'),
('China'),
('Brazil'),
('India');

-- Seed airport table
-- Major international airports with realistic codes and addresses
INSERT INTO airport (country_id, code, name, address, status) VALUES
(1, 'JFK', 'John F Kennedy International Airport', 'Queens, NY 11430, USA', 'Active'),
(1, 'LAX', 'Los Angeles International Airport', '1 World Way, Los Angeles, CA 90045, USA', 'Active'),
(1, 'ORD', 'O''Hare International Airport', '10000 W O''Hare Ave, Chicago, IL 60666, USA', 'Active'),
(2, 'LHR', 'London Heathrow Airport', 'Longford TW6, UK', 'Active'),
(2, 'LGW', 'London Gatwick Airport', 'Horley, Gatwick RH6 0NP, UK', 'Active'),
(3, 'HND', 'Tokyo Haneda Airport', 'Hanedakuko, Ota City, Tokyo 144-0041, Japan', 'Active'),
(3, 'NRT', 'Narita International Airport', '1-1 Furugome, Narita, Chiba 282-0004, Japan', 'Active'),
(4, 'FRA', 'Frankfurt Airport', '60547 Frankfurt, Germany', 'Active'),
(5, 'CDG', 'Charles de Gaulle Airport', '95700 Roissy-en-France, France', 'Active'),
(6, 'YYZ', 'Toronto Pearson International Airport', '6301 Silver Dart Dr, Mississauga, ON L5P 1B2, Canada', 'Active'),
(7, 'SYD', 'Sydney Airport', 'Sydney NSW 2020, Australia', 'Active'),
(8, 'PEK', 'Beijing Capital International Airport', 'Shunyi, Beijing, China', 'Active'),
(9, 'GRU', 'São Paulo–Guarulhos International Airport', 'Rod. Hélio Smidt, s/nº - Aeroporto, Guarulhos - SP, Brazil', 'Active'),
(10, 'DEL', 'Indira Gandhi International Airport', 'New Delhi, Delhi 110037, India', 'Active'),
(1, 'DFW', 'Dallas/Fort Worth International Airport', '2400 Aviation Dr, DFW Airport, TX 75261, USA', 'Active');

-- Seed role table
-- User roles for access control
INSERT INTO role (role_name, description) VALUES
('Admin', 'System administrator with full access'),
('Customer', 'Regular customer user'),
('Airline Staff', 'Airline employee with access to flight management'),
('Airport Staff', 'Airport employee with access to airport operations'),
('Guest', 'Limited access user');

-- Seed user table
-- Users with BCrypt-hashed passwords and various roles
INSERT INTO user (username, email, phone, password, created_at, updated_at, age, role_id) VALUES
('admin', 'admin@airport.com', '+1-555-123-4567', '12345678', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 35, 1),
('john_doe', 'john.doe@example.com', '+1-555-234-5678', '12345678', '2023-01-02 10:30:00', '2023-04-12 14:45:00', 28, 2),
('jane_smith', 'jane.smith@example.com', '+1-555-345-6789', '12345678', '2023-01-03 14:15:00', '2023-05-20 09:30:00', 34, 2),
('airline_staff1', 'staff1@airline.com', '+1-555-456-7890', '12345678', '2023-01-04 09:00:00', '2023-01-04 09:00:00', 42, 3),
('airport_staff1', 'staff1@airport.com', '+1-555-567-8901', '12345678', '2023-01-05 11:20:00', '2023-03-15 16:30:00', 39, 4),
('guest_user', 'guest@example.com', NULL, '12345678', '2023-01-06 16:45:00', '2023-01-06 16:45:00', 25, 5),
('emma_wilson', 'emma.wilson@example.com', '+1-555-678-9012', '12345678', '2023-01-07 13:10:00', '2023-06-10 10:15:00', 31, 2),
('michael_brown', 'michael.brown@example.com', '+1-555-789-0123', '12345678', '2023-01-08 08:30:00', '2023-02-18 12:40:00', 45, 2),
('sarah_johnson', 'sarah.johnson@example.com', '+1-555-890-1234', '12345678', '2023-01-09 15:20:00', '2023-07-22 14:30:00', 29, 2),
('david_lee', 'david.lee@example.com', '+1-555-901-2345', '12345678', '2023-01-10 12:00:00', '2023-05-05 11:10:00', 37, 2);

-- Seed passenger table
-- Passengers, some linked to users, initially with NULL flightReservation_id
INSERT INTO passenger (user_id, name, passport, flightReservation_id) VALUES
(2, 'John Doe', 'US123456', NULL),
(3, 'Jane Smith', 'US234567', NULL),
(7, 'Emma Wilson', 'US345678', NULL),
(8, 'Michael Brown', 'US456789', NULL),
(9, 'Sarah Johnson', 'US567890', NULL),
(10, 'David Lee', 'US678901', NULL),
(NULL, 'Robert Davis', 'UK123456', NULL),
(NULL, 'Emily Chen', 'CN234567', NULL),
(NULL, 'James Wilson', 'AU345678', NULL),
(NULL, 'Olivia Martinez', 'BR456789', NULL);

-- Seed payment table
-- Payments linked to users with various statuses and methods
INSERT INTO payment (user_id, payment_amount, payment_state, payment_method, payment_date, updated_at) VALUES
(2, 450.00, 'Completed', 'CREDIT_CARD', '2023-02-15', '2023-02-15 14:30:00'),
(3, 550.00, 'Completed', 'PAYPAL', '2023-02-16', '2023-02-16 10:15:00'),
(7, 325.00, 'Completed', 'CREDIT_CARD', '2023-02-17', '2023-02-17 16:45:00'),
(8, 750.00, 'Completed', 'DEBIT_CARD', '2023-02-18', '2023-02-18 09:30:00'),
(9, 420.00, 'Completed', 'CREDIT_CARD', '2023-02-19', '2023-02-19 11:20:00'),
(10, 580.00, 'Completed', 'PAYPAL', '2023-02-20', '2023-02-20 13:10:00'),
(2, 610.00, 'Refunded', 'CREDIT_CARD', '2023-02-21', '2023-03-05 14:25:00'),
(3, 490.00, 'Pending', 'CREDIT_CARD', '2023-02-22', '2023-02-22 15:40:00'),
(7, 800.00, 'Completed', 'BANK_TRANSFER', '2023-02-23', '2023-02-23 17:30:00'),
(8, 375.00, 'Failed', 'CREDIT_CARD', '2023-02-24', '2023-02-24 08:50:00');

-- Seed weeklySchedule table
-- Weekly flight schedules with various days and times
INSERT INTO weeklySchedule (dayOfWeek, departure_time, customDate) VALUES
('Monday', '08:00:00', NULL),
('Monday', '14:30:00', NULL),
('Tuesday', '09:15:00', NULL),
('Wednesday', '11:45:00', NULL),
('Thursday', '16:30:00', NULL),
('Friday', '07:00:00', NULL),
('Friday', '19:20:00', NULL),
('Saturday', '10:00:00', NULL),
('Sunday', '13:45:00', NULL),
('Sunday', '22:30:00', NULL);

-- Seed airline table
-- Major airlines with IATA codes
INSERT INTO airline (name, code) VALUES
('American Airlines', 'AA'),
('Delta Air Lines', 'DL'),
('United Airlines', 'UA'),
('British Airways', 'BA'),
('Lufthansa', 'LH'),
('Air France', 'AF'),
('Japan Airlines', 'JL'),
('Qantas', 'QF'),
('Emirates', 'EK'),
('Singapore Airlines', 'SQ');

-- Seed aircraft table
-- Aircraft models linked to airlines
INSERT INTO aircraft (airline_id, model, manufacturing_year) VALUES
(1, 'Boeing 737-800', 2015),
(1, 'Boeing 777-300ER', 2018),
(2, 'Airbus A320', 2016),
(2, 'Boeing 767-300', 2012),
(3, 'Boeing 787-9', 2019),
(4, 'Airbus A380', 2014),
(5, 'Airbus A350-900', 2020),
(6, 'Boeing 777-200', 2013),
(7, 'Boeing 787-8', 2017),
(8, 'Airbus A330-300', 2016),
(9, 'Airbus A380-800', 2018),
(10, 'Boeing 777-300', 2015);

-- Seed seat table
-- Seats for aircraft with different classes
INSERT INTO seat (aircraft_id, class) VALUES
(1, 'Economy'),
(1, 'Economy'),
(1, 'Economy'),
(1, 'Business'),
(1, 'Business'),
(2, 'Economy'),
(2, 'Economy'),
(2, 'Business'),
(2, 'FirstClass'),
(3, 'Economy'),
(3, 'Economy'),
(3, 'Business'),
(4, 'Economy'),
(4, 'Business'),
(5, 'Economy'),
(5, 'Business'),
(5, 'FirstClass'),
(6, 'Economy'),
(6, 'Business'),
(6, 'FirstClass');

-- Seed flight table
-- Flights with realistic routes and schedules
INSERT INTO flight (arrival_airport_id, departure_airport_id, gate, duration, flight_schedule_id, aircraft_id) VALUES
(2, 1, 'A1', 360, 1, 1),
(4, 1, 'B3', 420, 2, 2),
(1, 4, 'C5', 440, 3, 6),
(6, 4, 'D2', 720, 4, 6),
(9, 8, 'E4', 90, 5, 7),
(11, 7, 'F6', 600, 6, 9),
(12, 3, 'G1', 840, 7, 1),
(5, 10, 'H3', 480, 8, 10),
(13, 14, 'J2', 300, 9, 11),
(8, 15, 'K4', 660, 10, 5);

-- Seed customSchedule table
-- Custom schedules for specific flights
INSERT INTO customSchedule (flight_id, departure_time, customDate) VALUES
(1, '09:30:00', '2023-06-15'),
(2, '15:45:00', '2023-06-16'),
(3, '11:00:00', '2023-06-17'),
(4, '14:15:00', '2023-06-18'),
(5, '18:30:00', '2023-06-19');

-- Seed flightReservation table
-- Reservations with various statuses
INSERT INTO flightReservation (flight_id, user_id, qr_code, booking_date, status) VALUES
(1, 2, 'QR12345678', '2023-02-15', 'Confirmed'),
(2, 3, 'QR23456789', '2023-02-16', 'Confirmed'),
(3, 7, 'QR34567890', '2023-02-17', 'Confirmed'),
(4, 8, 'QR45678901', '2023-02-18', 'Confirmed'),
(5, 9, 'QR56789012', '2023-02-19', 'Confirmed'),
(6, 10, 'QR67890123', '2023-02-20', 'Confirmed'),
(7, 2, 'QR78901234', '2023-02-21', 'Canceled'),
(8, 3, 'QR89012345', '2023-02-22', 'Pending'),
(9, 7, 'QR90123456', '2023-02-23', 'Confirmed'),
(10, 8, 'QR01234567', '2023-02-24', 'Canceled');

-- Update passenger with flightReservation_id
-- Link passengers to their reservations
UPDATE passenger SET flightReservation_id = 1 WHERE user_id = 2 AND name = 'John Doe';
UPDATE passenger SET flightReservation_id = 2 WHERE user_id = 3 AND name = 'Jane Smith';
UPDATE passenger SET flightReservation_id = 3 WHERE user_id = 7 AND name = 'Emma Wilson';
UPDATE passenger SET flightReservation_id = 4 WHERE user_id = 8 AND name = 'Michael Brown';
UPDATE passenger SET flightReservation_id = 5 WHERE user_id = 9 AND name = 'Sarah Johnson';
UPDATE passenger SET flightReservation_id = 6 WHERE user_id = 10 AND name = 'David Lee';
UPDATE passenger SET flightReservation_id = 7 WHERE user_id IS NULL AND name = 'Robert Davis';
UPDATE passenger SET flightReservation_id = 8 WHERE user_id IS NULL AND name = 'Emily Chen';
UPDATE passenger SET flightReservation_id = 9 WHERE user_id IS NULL AND name = 'James Wilson';
UPDATE passenger SET flightReservation_id = 10 WHERE user_id IS NULL AND name = 'Olivia Martinez';

-- Seed flight_payment table
-- Link flights to payments
INSERT INTO flight_payment (flight_id, payment_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(6, 6),
(7, 7),
(8, 8),
(9, 9),
(10, 10);

-- Seed passenger_seat table
-- Assign seats to passengers for reservations
INSERT INTO passenger_seat (passenger_id, seat_id, flightReservation_id) VALUES
(1, 4, 1),
(2, 9, 2),
(3, 12, 3),
(4, 14, 4),
(5, 16, 5),
(6, 20, 6),
(7, 3, 7),
(8, 10, 8),
(9, 18, 9),
(10, 15, 10);

-- Commit transaction
COMMIT;