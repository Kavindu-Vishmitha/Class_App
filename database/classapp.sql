-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.32 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.4.0.6659
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for classapp
CREATE DATABASE IF NOT EXISTS `classapp` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `classapp`;

-- Dumping structure for table classapp.attendance
CREATE TABLE IF NOT EXISTS `attendance` (
  `id` int NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL,
  `time` time DEFAULT NULL,
  `student_reg_id` varchar(20) NOT NULL,
  `subject_id` int NOT NULL,
  `attendance_status_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_attendance_attendance_status1_idx` (`attendance_status_id`),
  KEY `fk_attendance_student1_idx` (`student_reg_id`),
  KEY `fk_attendance_subject1_idx` (`subject_id`),
  CONSTRAINT `fk_attendance_attendance_status1` FOREIGN KEY (`attendance_status_id`) REFERENCES `attendance_status` (`id`),
  CONSTRAINT `fk_attendance_student1` FOREIGN KEY (`student_reg_id`) REFERENCES `student` (`reg_id`),
  CONSTRAINT `fk_attendance_subject1` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table classapp.attendance: ~51 rows (approximately)
INSERT INTO `attendance` (`id`, `date`, `time`, `student_reg_id`, `subject_id`, `attendance_status_id`) VALUES
	(2, '2025-07-21', '19:09:05', 'SRI1753104624011', 1, 1),
	(3, '2025-07-22', '12:29:38', 'SRI1753166930638', 2, 1),
	(4, '2025-07-22', '12:29:54', 'SRI1753166930638', 1, 1),
	(5, '2025-07-23', '01:26:55', 'SRI1753166930638', 2, 1),
	(6, '2025-07-23', '01:27:34', 'SRI1753166930638', 1, 1),
	(7, '2025-07-23', '01:28:43', 'SRI1753167271885', 2, 1),
	(8, '2025-07-23', '01:29:13', 'SRI1753167271885', 1, 1),
	(9, '2025-07-23', '12:55:25', 'SRI1753104624011', 2, 1),
	(10, '2025-07-23', '12:55:36', 'SRI1753104624011', 1, 1),
	(11, '2025-07-23', '13:17:37', 'SRI1753256329469', 2, 1),
	(12, '2025-07-23', '13:17:49', 'SRI1753256329469', 1, 1),
	(13, '2025-07-23', '13:18:24', 'SRI1753256233310', 2, 1),
	(14, '2025-07-23', '13:18:36', 'SRI1753256233310', 1, 1),
	(15, '2025-07-23', '13:19:10', 'SRI1753256466254', 2, 1),
	(16, '2025-07-23', '13:19:21', 'SRI1753256466254', 1, 1),
	(17, '2025-07-23', '13:30:17', 'SRI1753257108742', 2, 1),
	(18, '2025-07-23', '13:30:48', 'SRI1753257108742', 1, 1),
	(19, '2025-07-23', '13:31:28', 'SRI1753257012822', 2, 1),
	(20, '2025-07-23', '13:31:41', 'SRI1753257012822', 1, 1),
	(21, '2025-07-25', '04:12:14', 'SRI1753257012822', 2, 1),
	(22, '2025-07-25', '04:13:38', 'SRI1753257012822', 1, 1),
	(23, '2025-07-25', '04:16:16', 'SRI1753257012822', 3, 1),
	(24, '2025-07-25', '04:30:20', 'SRI1753257012822', 4, 1),
	(25, '2025-07-25', '04:31:05', 'SRI1753257108742', 4, 1),
	(26, '2025-07-22', NULL, 'SRI1753257012822', 2, 2),
	(27, '2025-07-25', NULL, 'SRI1753104624011', 2, 2),
	(28, '2025-07-25', NULL, 'SRI1753309727580', 2, 2),
	(29, '2025-07-25', NULL, 'SRI1753166930638', 2, 2),
	(30, '2025-07-25', NULL, 'SRI1753256233310', 2, 2),
	(31, '2025-07-25', NULL, 'SRI1753167271885', 2, 2),
	(32, '2025-07-25', NULL, 'SRI1753256466254', 2, 2),
	(33, '2025-07-25', NULL, 'SRI1753310288398', 2, 2),
	(34, '2025-07-25', NULL, 'SRI1753385178937', 2, 2),
	(35, '2025-07-25', NULL, 'SRI1753256329469', 2, 2),
	(36, '2025-07-25', NULL, 'SRI1753257108742', 2, 2),
	(37, '2025-07-25', NULL, 'SRI1753104624011', 1, 2),
	(38, '2025-07-25', NULL, 'SRI1753309727580', 1, 2),
	(39, '2025-07-25', NULL, 'SRI1753166930638', 1, 2),
	(40, '2025-07-25', NULL, 'SRI1753256233310', 1, 2),
	(41, '2025-07-25', NULL, 'SRI1753167271885', 1, 2),
	(42, '2025-07-25', NULL, 'SRI1753256466254', 1, 2),
	(43, '2025-07-25', NULL, 'SRI1753256329469', 1, 2),
	(44, '2025-07-25', NULL, 'SRI1753257108742', 1, 2),
	(45, '2025-07-25', NULL, 'SRI1753104624011', 3, 2),
	(46, '2025-07-25', NULL, 'SRI1753166930638', 3, 2),
	(47, '2025-07-25', NULL, 'SRI1753310288398', 3, 2),
	(48, '2025-07-26', '11:10:32', 'SRI1753257012822', 2, 1),
	(49, '2025-07-22', NULL, 'SRI1753507963201', 2, 2),
	(50, '2025-07-23', NULL, 'SRI1753507963201', 2, 2),
	(51, '2025-07-25', NULL, 'SRI1753507963201', 2, 2),
	(52, '2025-07-25', NULL, 'SRI1753507963201', 3, 2),
	(53, '2025-07-26', '22:04:42', 'SRI1753256466254', 4, 1),
	(54, '2025-07-26', '22:30:12', 'SRI1753256466254', 3, 1);

-- Dumping structure for table classapp.attendance_status
CREATE TABLE IF NOT EXISTS `attendance_status` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table classapp.attendance_status: ~2 rows (approximately)
INSERT INTO `attendance_status` (`id`, `name`) VALUES
	(1, 'Present'),
	(2, 'Absent');

-- Dumping structure for table classapp.city
CREATE TABLE IF NOT EXISTS `city` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table classapp.city: ~23 rows (approximately)
INSERT INTO `city` (`id`, `name`) VALUES
	(1, 'Veyangoda'),
	(2, 'Muddaragama'),
	(3, 'Mirigama'),
	(4, 'Yagoda'),
	(5, 'Jaela'),
	(6, 'Dompe'),
	(7, 'Ganemulla'),
	(8, 'Abepussa'),
	(9, 'Anuradhapura'),
	(10, 'Ihala Yagoda'),
	(11, 'Nittambuwa'),
	(12, 'Kiridiwela'),
	(13, 'Daraluwa'),
	(14, 'Banduragoda'),
	(15, 'Wataddara'),
	(16, 'Magalegoda'),
	(17, 'Pasyala'),
	(18, 'Kalleliya'),
	(19, 'Ederamulla'),
	(20, 'Madawela'),
	(21, 'Keenawala'),
	(22, 'Heendeniya'),
	(23, 'Pallewela');

-- Dumping structure for table classapp.employee
CREATE TABLE IF NOT EXISTS `employee` (
  `id` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `first_name` varchar(45) NOT NULL,
  `last_name` varchar(45) NOT NULL,
  `mobile` varchar(10) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `password` varchar(45) DEFAULT NULL,
  `register_date` date NOT NULL,
  `employee_type_id` int NOT NULL,
  `gender_id` int NOT NULL,
  `employee_address_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_employee_employee_type1_idx` (`employee_type_id`),
  KEY `fk_employee_gender1_idx` (`gender_id`),
  KEY `fk_employee_employee_address1_idx` (`employee_address_id`),
  CONSTRAINT `fk_employee_employee_address1` FOREIGN KEY (`employee_address_id`) REFERENCES `employee_address` (`id`),
  CONSTRAINT `fk_employee_employee_type1` FOREIGN KEY (`employee_type_id`) REFERENCES `employee_type` (`id`),
  CONSTRAINT `fk_employee_gender1` FOREIGN KEY (`gender_id`) REFERENCES `gender` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Dumping data for table classapp.employee: ~5 rows (approximately)
INSERT INTO `employee` (`id`, `first_name`, `last_name`, `mobile`, `email`, `password`, `register_date`, `employee_type_id`, `gender_id`, `employee_address_id`) VALUES
	('ERI1753377881937', 'Amal', 'Deshan', '0765546500', 'amal@gmail.com', 'Am@chjgt356', '2025-07-24', 6, 1, 3),
	('ERI1753378104345', 'Suraj', 'Mapa', '0705640900', 'suraj@gmail.com', '', '2025-07-24', 7, 1, 4),
	('ERI1753379215874', 'Sachintha', 'Tharuka', '0764567700', 'sachintha@gmail.com', '', '2025-07-24', 7, 1, 5),
	('ERI1753406811848', 'Kavindu', 'Vishmitha', '0751320801', 'kavindu@gmail.com', 'cv12FD@3', '2025-07-25', 6, 1, 6),
	('ERI1753507249530', 'Supun', 'Ayesh', '0743540901', 'supun@gmail.com', '23DF@cn', '2025-07-26', 7, 1, 9);

-- Dumping structure for table classapp.employee_address
CREATE TABLE IF NOT EXISTS `employee_address` (
  `id` int NOT NULL AUTO_INCREMENT,
  `line1` varchar(100) NOT NULL,
  `line2` varchar(100) NOT NULL,
  `city_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_employee_address_city1_idx` (`city_id`),
  CONSTRAINT `fk_employee_address_city1` FOREIGN KEY (`city_id`) REFERENCES `city` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table classapp.employee_address: ~6 rows (approximately)
INSERT INTO `employee_address` (`id`, `line1`, `line2`, `city_id`) VALUES
	(1, 'sdsd', 'asdf', 1),
	(3, 'NO 56/8 Banduragoda', 'Veyangoda', 14),
	(4, 'NO 144/2 Wataddara', 'Veyangoda', 17),
	(5, 'No 155/5 Maligathanna', 'Veyangoda', 1),
	(6, 'NO 32/2 Mangedara', 'Muddaragama', 2),
	(9, 'NO 132/2 Kottala', 'Keenawala', 18);

-- Dumping structure for table classapp.employee_type
CREATE TABLE IF NOT EXISTS `employee_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table classapp.employee_type: ~2 rows (approximately)
INSERT INTO `employee_type` (`id`, `name`) VALUES
	(6, 'Admin'),
	(7, 'Officer');

-- Dumping structure for table classapp.gender
CREATE TABLE IF NOT EXISTS `gender` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table classapp.gender: ~2 rows (approximately)
INSERT INTO `gender` (`id`, `name`) VALUES
	(1, 'Male'),
	(2, 'Female');

-- Dumping structure for table classapp.grade
CREATE TABLE IF NOT EXISTS `grade` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table classapp.grade: ~6 rows (approximately)
INSERT INTO `grade` (`id`, `name`) VALUES
	(1, '1'),
	(2, '2'),
	(3, '3'),
	(4, '4'),
	(5, '5'),
	(6, '6');

-- Dumping structure for table classapp.inactive_st_reg_payment
CREATE TABLE IF NOT EXISTS `inactive_st_reg_payment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `inactive_st_reg_id` varchar(20) NOT NULL,
  `inactive_payment` double NOT NULL,
  `reg_payment_date` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table classapp.inactive_st_reg_payment: ~6 rows (approximately)
INSERT INTO `inactive_st_reg_payment` (`id`, `inactive_st_reg_id`, `inactive_payment`, `reg_payment_date`) VALUES
	(1, 'SRI1753097210305', 2000, '2025-07-21'),
	(2, 'SRI1753103402402', 2000, '2025-07-21'),
	(3, 'SRI1753103552683', 2000, '2025-07-21'),
	(4, 'SRI1753098851505', 2000, '2025-07-21'),
	(5, 'SRI1753383658755', 2000, '2025-07-25'),
	(6, 'SRI1753454300577', 2000, '2025-07-25');

-- Dumping structure for table classapp.inactive_st_subject_fee
CREATE TABLE IF NOT EXISTS `inactive_st_subject_fee` (
  `id` int NOT NULL AUTO_INCREMENT,
  `inactive_st_reg_id` varchar(20) NOT NULL,
  `fee` double NOT NULL,
  `payment_date` date NOT NULL,
  `subject_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_Inactive_st_subject_fee_subject1_idx` (`subject_id`),
  CONSTRAINT `fk_Inactive_st_subject_fee_subject1` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table classapp.inactive_st_subject_fee: ~4 rows (approximately)
INSERT INTO `inactive_st_subject_fee` (`id`, `inactive_st_reg_id`, `fee`, `payment_date`, `subject_id`) VALUES
	(1, 'SRI1753097210305', 500, '2025-07-21', 1),
	(2, 'SRI1753098851505', 500, '2025-07-21', 1),
	(3, 'SRI1753098851505', 500, '2025-06-21', 1),
	(4, 'SRI1753098851505', 500, '2025-07-21', 2);

-- Dumping structure for table classapp.register_payment
CREATE TABLE IF NOT EXISTS `register_payment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `payment` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table classapp.register_payment: ~1 rows (approximately)
INSERT INTO `register_payment` (`id`, `payment`) VALUES
	(1, 2000);

-- Dumping structure for table classapp.student
CREATE TABLE IF NOT EXISTS `student` (
  `reg_id` varchar(20) NOT NULL,
  `first_name` varchar(45) NOT NULL,
  `last_name` varchar(45) NOT NULL,
  `mobile` varchar(10) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `register_date` date NOT NULL,
  `st_reg_payment_id` int NOT NULL,
  `gender_id` int NOT NULL,
  `student_address_id` int NOT NULL,
  `grade_id` int NOT NULL,
  PRIMARY KEY (`reg_id`),
  KEY `fk_student_st_reg_payment1_idx` (`st_reg_payment_id`),
  KEY `fk_student_gender1_idx` (`gender_id`),
  KEY `fk_student_student_address1_idx` (`student_address_id`),
  KEY `fk_student_grade1_idx` (`grade_id`),
  CONSTRAINT `fk_student_gender1` FOREIGN KEY (`gender_id`) REFERENCES `gender` (`id`),
  CONSTRAINT `fk_student_grade1` FOREIGN KEY (`grade_id`) REFERENCES `grade` (`id`),
  CONSTRAINT `fk_student_st_reg_payment1` FOREIGN KEY (`st_reg_payment_id`) REFERENCES `st_reg_payment` (`id`),
  CONSTRAINT `fk_student_student_address1` FOREIGN KEY (`student_address_id`) REFERENCES `student_address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Dumping data for table classapp.student: ~23 rows (approximately)
INSERT INTO `student` (`reg_id`, `first_name`, `last_name`, `mobile`, `email`, `register_date`, `st_reg_payment_id`, `gender_id`, `student_address_id`, `grade_id`) VALUES
	('SRI1753104624011', 'Kavindu', 'Vishmitha', '0751320801', 'kavindu@gmail.com', '2025-07-21', 5, 1, 5, 1),
	('SRI1753166930638', 'Chamindu', 'Induwara', '0703616764', 'chamindu@gmail.com', '2025-07-22', 6, 1, 6, 2),
	('SRI1753167271885', 'Chamika', 'Sandamali', '0765434200', 'chamika@gmail.com', '2025-07-22', 7, 2, 7, 3),
	('SRI1753256233310', 'Jude', 'Thamel', '0786750080', 'jude@gmail.com', '2025-07-23', 8, 1, 8, 4),
	('SRI1753256329469', 'Sanjana', 'Kumara', '0752334500', 'sanjana@gmail.com', '2025-07-23', 9, 1, 9, 5),
	('SRI1753256466254', 'Pasindu', 'Nethsara', '0765545300', 'pasindu@gmail.com', '2025-07-23', 10, 1, 10, 5),
	('SRI1753257012822', 'Avishka', 'Jayalath', '0775565400', 'avishka@gmail.com', '2025-07-23', 11, 1, 11, 2),
	('SRI1753257108742', 'Sheshan', 'Edirisingha', '0775045300', 'sheshan@gmail.com', '2025-07-23', 12, 1, 12, 1),
	('SRI1753309460357', 'Thimira', 'Kolitha', '0765534230', 'thimira@gmail.com', '2025-07-24', 13, 1, 13, 1),
	('SRI1753309595598', 'Dewmini', 'Dulari', '0785567550', 'dulari@gmail.com', '2025-07-24', 14, 2, 14, 2),
	('SRI1753309727580', 'Chamodi', 'Nethmini', '0705614764', 'chamodi@gmail.com', '2025-07-24', 15, 2, 15, 2),
	('SRI1753309875413', 'Ganula', 'Jayasekara', '0786755412', 'ganula@gmail.com', '2025-07-24', 16, 1, 16, 1),
	('SRI1753309957174', 'Kaveesha', 'Dewmina', '0705567500', 'kaveesha@gmail.com', '2025-07-24', 17, 1, 17, 3),
	('SRI1753310092556', 'Ravindu', 'Hasaranga', '0763533412', 'ravindu@gmail.com', '2025-07-24', 18, 1, 18, 4),
	('SRI1753310206765', 'Sathira', 'Nimsara', '0753320901', 'sathira@gmail.com', '2025-07-24', 19, 1, 19, 3),
	('SRI1753310288398', 'Amal', 'Nimantha', '0786754300', 'amal@gmail.com', '2025-07-24', 20, 1, 20, 3),
	('SRI1753310439980', 'Sandaru', 'Deshan', '0764320901', 'sandaru@gmail.com', '2025-07-24', 21, 1, 21, 4),
	('SRI1753384226855', 'Chamara', 'Laknindu', '0766570090', 'chamara@gmail.com', '2025-07-25', 23, 1, 23, 3),
	('SRI1753385178937', 'Kamal', 'Gunarathna', '0787655409', 'kamal@gmail.com', '2025-07-25', 24, 1, 24, 5),
	('SRI1753385934144', 'Dinesh', 'Muthugala', '0705678880', 'dinesh@gmail.com', '2025-07-25', 25, 1, 25, 3),
	('SRI1753395605541', 'Nimesh', 'Kumara', '0775645300', 'nimesh@gmail.com', '2025-07-25', 26, 1, 26, 4),
	('SRI1753507963201', 'Sunera', 'Basu', '0786745600', 'sunera@gmail.com', '2025-07-26', 28, 1, 28, 5),
	('SRI1753511861658', 'Kamal', 'Gunarathna', '0765623400', 'kamal@gmail.com', '2025-07-26', 29, 1, 29, 6);

-- Dumping structure for table classapp.student_address
CREATE TABLE IF NOT EXISTS `student_address` (
  `id` int NOT NULL AUTO_INCREMENT,
  `line1` varchar(100) NOT NULL,
  `line2` varchar(100) NOT NULL,
  `city_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_student_address_city1_idx` (`city_id`),
  CONSTRAINT `fk_student_address_city1` FOREIGN KEY (`city_id`) REFERENCES `city` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table classapp.student_address: ~23 rows (approximately)
INSERT INTO `student_address` (`id`, `line1`, `line2`, `city_id`) VALUES
	(5, 'NO 32/2 Mangedara', 'Muddaragama', 2),
	(6, 'NO 54/5 Mirigama', 'Banduragoda', 3),
	(7, 'NO 60/5 Gampaha', 'Yagoda', 4),
	(8, 'NO 55/3 Gampaha', 'Jaela', 5),
	(9, 'NO 45/6 Dompe', 'Hagalawaththa', 6),
	(10, 'NO 167/2 Gampaha', 'Ganemulla', 7),
	(11, 'NO 180/3 Mirigama', 'Abepussa', 8),
	(12, 'NO 159/7 Mirigama', 'Abepussa', 8),
	(13, 'NO 156/7 Anuradhapura', 'New Town', 9),
	(14, 'NO 45/2 Gampaha', 'Ihala Yagoda', 10),
	(15, 'NO 140/3 Nittambuwa', 'Veyangoda', 11),
	(16, 'NO 32/5 Mangedara', 'Muddaragama', 2),
	(17, 'NO 56/9 Kiridiwela', 'Nittambuwa', 12),
	(18, 'NO 87/1 Mangedara', 'Muddaragama', 2),
	(19, 'NO 32/6 Mangedara', 'Muddaragama', 2),
	(20, 'NO 76/3 Gampaha', 'Daraluwa', 13),
	(21, 'NO 167/3 Divlapitiya', 'Banduragoda', 14),
	(23, 'NO 76/4 Gampaha', 'Kiridiwela', 12),
	(24, 'NO 156/8 Ragama', 'Ederamulla', 19),
	(25, 'NO 144/5 Pallewela', 'Madawala', 20),
	(26, 'NO 177/4 Maligathanna', 'Veyangoda', 1),
	(28, 'NO 177/3 Midellawala', 'Pallewela', 23),
	(29, 'NO 45/9 Kalleliya', 'Pasyala', 18);

-- Dumping structure for table classapp.st_reg_payment
CREATE TABLE IF NOT EXISTS `st_reg_payment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `payment` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table classapp.st_reg_payment: ~23 rows (approximately)
INSERT INTO `st_reg_payment` (`id`, `payment`) VALUES
	(5, 2000),
	(6, 2000),
	(7, 2000),
	(8, 2000),
	(9, 2000),
	(10, 2000),
	(11, 2000),
	(12, 2000),
	(13, 2000),
	(14, 2000),
	(15, 2000),
	(16, 2000),
	(17, 2000),
	(18, 2000),
	(19, 2000),
	(20, 2000),
	(21, 2000),
	(23, 2000),
	(24, 2000),
	(25, 2000),
	(26, 2000),
	(28, 2000),
	(29, 2000);

-- Dumping structure for table classapp.st_subject_fee
CREATE TABLE IF NOT EXISTS `st_subject_fee` (
  `id` int NOT NULL AUTO_INCREMENT,
  `fee` double NOT NULL,
  `payment_date` date NOT NULL,
  `subject_id` int NOT NULL,
  `student_reg_id` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_student_has_subject_subject1_idx` (`subject_id`),
  KEY `fk_st_subject_fee_student1_idx` (`student_reg_id`),
  CONSTRAINT `fk_st_subject_fee_student1` FOREIGN KEY (`student_reg_id`) REFERENCES `student` (`reg_id`),
  CONSTRAINT `fk_student_has_subject_subject1` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table classapp.st_subject_fee: ~34 rows (approximately)
INSERT INTO `st_subject_fee` (`id`, `fee`, `payment_date`, `subject_id`, `student_reg_id`) VALUES
	(5, 500, '2025-07-23', 2, 'SRI1753104624011'),
	(6, 500, '2025-07-23', 2, 'SRI1753166930638'),
	(7, 500, '2025-07-23', 1, 'SRI1753166930638'),
	(8, 500, '2025-07-24', 1, 'SRI1753104624011'),
	(9, 500, '2025-07-24', 2, 'SRI1753257012822'),
	(10, 500, '2025-07-24', 1, 'SRI1753257012822'),
	(11, 500, '2025-07-24', 2, 'SRI1753167271885'),
	(12, 500, '2025-07-24', 1, 'SRI1753167271885'),
	(13, 500, '2025-07-24', 2, 'SRI1753256466254'),
	(14, 500, '2025-07-24', 1, 'SRI1753256466254'),
	(15, 500, '2025-07-24', 2, 'SRI1753256233310'),
	(16, 500, '2025-07-24', 1, 'SRI1753256233310'),
	(17, 500, '2025-07-24', 2, 'SRI1753256329469'),
	(18, 500, '2025-07-24', 1, 'SRI1753256329469'),
	(19, 500, '2025-07-24', 2, 'SRI1753257108742'),
	(20, 500, '2025-07-24', 1, 'SRI1753257108742'),
	(21, 500, '2025-07-24', 2, 'SRI1753310288398'),
	(22, 500, '2025-07-24', 2, 'SRI1753309727580'),
	(23, 500, '2025-07-24', 2, 'SRI1753385178937'),
	(24, 500, '2025-07-25', 3, 'SRI1753257012822'),
	(25, 500, '2025-07-25', 3, 'SRI1753104624011'),
	(26, 500, '2025-07-24', 4, 'SRI1753257012822'),
	(27, 500, '2025-07-25', 1, 'SRI1753309727580'),
	(28, 500, '2025-07-25', 3, 'SRI1753310288398'),
	(29, 500, '2025-07-25', 3, 'SRI1753166930638'),
	(30, 500, '2025-04-25', 2, 'SRI1753257012822'),
	(31, 500, '2025-07-26', 2, 'SRI1753507963201'),
	(32, 500, '2025-01-26', 2, 'SRI1753507963201'),
	(33, 500, '2025-01-26', 3, 'SRI1753507963201'),
	(34, 500, '2025-07-26', 2, 'SRI1753511861658'),
	(35, 500, '2025-07-26', 5, 'SRI1753511861658'),
	(36, 500, '2025-07-26', 3, 'SRI1753511861658'),
	(37, 500, '2025-07-26', 1, 'SRI1753511861658'),
	(38, 500, '2025-07-26', 4, 'SRI1753511861658');

-- Dumping structure for table classapp.subject
CREATE TABLE IF NOT EXISTS `subject` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `fee` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table classapp.subject: ~5 rows (approximately)
INSERT INTO `subject` (`id`, `name`, `fee`) VALUES
	(1, 'Sinhala', 500),
	(2, 'English', 500),
	(3, 'Science', 500),
	(4, 'Tamil', 500),
	(5, 'ICT', 500);

-- Dumping structure for table classapp.teacher
CREATE TABLE IF NOT EXISTS `teacher` (
  `id` varchar(20) NOT NULL,
  `first_name` varchar(45) NOT NULL,
  `last_name` varchar(45) NOT NULL,
  `mobile` varchar(10) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `register_date` date NOT NULL,
  `teacher_address_id` int NOT NULL,
  `gender_id` int NOT NULL,
  `subject_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_teacher_teacher_address1_idx` (`teacher_address_id`),
  KEY `fk_teacher_gender1_idx` (`gender_id`),
  KEY `fk_teacher_subject1_idx` (`subject_id`),
  CONSTRAINT `fk_teacher_gender1` FOREIGN KEY (`gender_id`) REFERENCES `gender` (`id`),
  CONSTRAINT `fk_teacher_subject1` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`),
  CONSTRAINT `fk_teacher_teacher_address1` FOREIGN KEY (`teacher_address_id`) REFERENCES `teacher_address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Dumping data for table classapp.teacher: ~5 rows (approximately)
INSERT INTO `teacher` (`id`, `first_name`, `last_name`, `mobile`, `email`, `register_date`, `teacher_address_id`, `gender_id`, `subject_id`) VALUES
	('TRI1753381037572', 'Isuru', 'Ranasingha', '0765430901', 'isuru@gmail.com', '2025-07-24', 1, 1, 3),
	('TRI1753381260656', 'Sampath', 'Kumara', '0764567300', 'sampath@gmail.com', '2025-07-24', 2, 1, 4),
	('TRI1753381975611', 'Namal', 'Udayanga', '0764550900', 'namal@gmail.com', '2025-07-25', 3, 1, 2),
	('TRI1753382180079', 'Chathura', 'Alvis', '0785670801', 'chathura@gmail.com', '2025-07-25', 4, 1, 1),
	('TRI1753507905175', 'Sandaru', 'Theekshana', '0765523100', 'sandaru@gmail.com', '2025-07-26', 6, 1, 5);

-- Dumping structure for table classapp.teacher_address
CREATE TABLE IF NOT EXISTS `teacher_address` (
  `id` int NOT NULL AUTO_INCREMENT,
  `line1` varchar(100) NOT NULL,
  `line2` varchar(100) NOT NULL,
  `city_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_teacher_address_city1_idx` (`city_id`),
  CONSTRAINT `fk_teacher_address_city1` FOREIGN KEY (`city_id`) REFERENCES `city` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table classapp.teacher_address: ~5 rows (approximately)
INSERT INTO `teacher_address` (`id`, `line1`, `line2`, `city_id`) VALUES
	(1, 'NO 167/2 Gampaha', 'Magalegoda', 16),
	(2, 'NO 155/9 Ganemulla', 'Gampaha', 7),
	(3, 'NO 45/9 Pasyala', 'Mirigama', 17),
	(4, 'NO 155/4 Hiriwala', 'Kalleliya', 18),
	(6, 'NO 120/4 Magalegoda', 'Heendeniya', 22);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
