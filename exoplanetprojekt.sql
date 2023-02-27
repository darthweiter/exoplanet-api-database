-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

CREATE DATABASE IF NOT EXISTS exoplanetprojekt;

--
-- Datenbank: `exoplanetprojekt`
--

DELIMITER $$
--
-- Prozeduren
--
DROP PROCEDURE IF EXISTS `exoplanetprojekt`.`calculate_Koordinaten`
$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `exoplanetprojekt`.`calculate_Koordinaten` (IN `width` INT, IN `height` INT)   BEGIN
DECLARE xCoord INT;
DECLARE yCoord INT;
DECLARE coordId INT;
IF width <= 0 THEN
   SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Width must be greater 0';
END IF;
IF height <= 0 THEN
   SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Height must be greater 0';
END IF;
SET xCoord = 0;
SET yCoord = 0;
SET coordId = -1;

loop_x:LOOP
	loop_y: LOOP
    	SELECT KID FROM koordinaten WHERE X = xCoord AND Y = yCoord INTO coordId;
    	IF coordID = -1 THEN
        	INSERT INTO koordinaten (X, Y) VALUES(xCoord, yCoord);
        END IF;
        SET coordId = -1;
    	IF yCoord = (height -1) THEN
    		SET yCoord = 0;
    		LEAVE loop_y;
    	END IF;    
    	SET yCoord = yCoord + 1;
	END LOOP;
	IF xCoord = (width - 1) THEN
    	SET xCoord = 0;
    	LEAVE loop_x;
    END IF;
    SET xCoord = xCoord + 1;
END LOOP;

END$$

DROP PROCEDURE IF EXISTS `exoplanetprojekt`.`check_valid_planet_x_y`
$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `exoplanetprojekt`.`check_valid_planet_x_y` (IN `pidIN` INT, IN `xIn` INT, IN `yIn` INT)   BEGIN

DECLARE planetWidth INT;
DECLARE planetHeight INT;

SET planetWidth = -1;
SET planetHeight = -1;

SELECT Breite FROM planeten WHERE PID = pidIN INTO planetWidth;
SELECT Hoehe FROM planeten WHERE PID = pidIN INTO planetHeight;

IF planetWidth = -1 OR planetHeight = -1 THEN
  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Planet dosnt exist';
END IF;

IF xIN >= planetWidth OR yIN >= planetHeight THEN
 SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Coordinate doesnt exist on Planet'; 
END IF;
END$$

DROP PROCEDURE IF EXISTS `exoplanetprojekt`.`insert_or_update_messdaten`
$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `exoplanetprojekt`.`insert_or_update_messdaten` (IN `pidIN` INT, IN `xIN` INT, IN `yIN` INT, IN `bodenIN` VARCHAR(255), IN `temperature` DOUBLE)  DETERMINISTIC BEGIN
DECLARE bodenID INT;
DECLARE messdatenID INT;
DECLARE koordinatenID INT;
DECLARE msg VARCHAR(255);

CALL check_valid_planet_x_y(pidIN, xIN, yIN);

SET bodenID = -1;
SELECT BID FROM boeden WHERE Typ = bodenIN INTO bodenID;
SET msg = CONCAT('ID:', bodenID);
IF bodenID = -1 THEN
  INSERT INTO boeden (Typ) VALUES(bodenIN);
  SELECT BID FROM boeden WHERE Typ = bodenIN INTO bodenID;
END IF;

SET koordinatenID = -1;
SELECT KID FROM koordinaten WHERE X = xIN AND Y = yIN INTO koordinatenID;
IF koordinatenID = -1 THEN
  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Coordinates doesnt exist on planet.';
END IF;

SET messdatenID = -1;
SELECT MID FROM messdaten WHERE PID = pidIN AND KID = koordinatenID INTO messdatenID;

IF messdatenID = -1 THEN
 INSERT INTO messdaten (KID, PID, BID, Temperatur) VALUES (koordinatenID, pidIN, bodenID, temperature);
ELSE
 UPDATE messdaten SET KID = koordinatenID, PID = pidIN, BID = bodenID, Temperatur = temperature WHERE MID = messdatenID;
END IF;
END$$

DROP PROCEDURE IF EXISTS `exoplanetprojekt`.`update_roboter`
$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `exoplanetprojekt`.`update_roboter` (IN `ridIn` INT, IN `pidIn` INT, IN `xIn` INT, IN `yIn` INT, IN `directionIn` VARCHAR(255), IN `nameIn` VARCHAR(255), IN `energyIn` DOUBLE, IN `temperatureIn` DOUBLE, IN `statusIn` VARCHAR(255), IN `heaterIn` tinyint(1), IN `coolerIn` tinyint(1))   BEGIN
DECLARE coordID INT;
DECLARE directionID INT;
DECLARE robotID INT;
DECLARE statusID INT;
DECLARE crashID INT;
DECLARE isCrash INT;

CALL check_valid_planet_x_y(pidIn, xIn, yIn);

SELECT KID FROM koordinaten WHERE X = xIn AND Y = yIN INTO coordID;

SET directionID = -1;
SELECT RTID FROM richtungen WHERE Bezeichnung = directionIn INTO directionID;
IF directionID = -1 THEN
	SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Richtung doesnt exist';
END IF;

SET statusID = -1;
SELECT SID FROM stati WHERE Status = statusIn INTO statusID;

IF statusID = -1 THEN
	SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = "Status doesnt exist";
END IF;

SET crashID = -1;
SELECT SID FROM stati WHERE Status = "CRASHED" INTO crashID;

IF crashID = -1 THEN
	SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = "Status CRASHED not found";
END IF;

SET isCrash = -1;
SELECT  SID FROM roboter WHERE RID = ridIn INTO isCRASH;
IF isCrash = crashID THEN
	SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = " Updating a crashed Roboter is not allowed";
END IF;

UPDATE roboter SET KID = coordID, PID = pidIn, RTID = directionID, Name = nameIn, Energie = energyIn, Betriebstemperatur = temperatureIn, SID = statusID, Heater = heaterIn, Cooler = coolerIn WHERE RID = ridIn AND SID NOT IN(crashID);

CALL update_roboter_crash(pidIn, coordID);

END$$

DROP PROCEDURE IF EXISTS `exoplanetprojekt`.`update_roboter_crash`
$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `exoplanetprojekt`.`update_roboter_crash` (IN `pidIn` INT, IN `kidIn` INT)   BEGIN

DECLARE crashID INT;
DECLARE roboterCount INT;
SET crashID = -1;

SELECT SID FROM stati WHERE Status = "CRASHED" INTO crashID;

IF crashID = -1 THEN
	SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = "CRASHED Status doesnt exist";
END IF;

SET roboterCount = 0;
SELECT COUNT(*) FROM roboter WHERE PID = pidIn AND KID = kidIn INTO roboterCount;

IF roboterCount > 1 THEN
UPDATE roboter set SID = crashID WHERE PID = pidIN AND KID = kidIN;
END IF;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `boeden`
--

CREATE TABLE IF NOT EXISTS `exoplanetprojekt`.`boeden` (
  `BID` int(11) NOT NULL AUTO_INCREMENT,
  `Typ` varchar(255) NOT NULL,
   PRIMARY KEY (`BID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Daten für Tabelle `boeden`
--

INSERT IGNORE INTO `exoplanetprojekt`.`boeden` (`BID`, `Typ`) VALUES
(1, 'NICHTS'),
(2, 'SAND'),
(3, 'GEROELL'),
(4, 'FELS'),
(5, 'WASSER'),
(6, 'PFLANZEN'),
(7, 'MORAST'),
(8, 'LAVA'),
(9, 'FLUSS');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `koordinaten`
--

CREATE TABLE IF NOT EXISTS `exoplanetprojekt`.`koordinaten` (
  `KID` int(11) NOT NULL AUTO_INCREMENT,
  `X` int(11) NOT NULL,
  `Y` int(11) NOT NULL,
   PRIMARY KEY (`KID`),
   CONSTRAINT `unique_koordinaten` UNIQUE (`X`,`Y`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `planeten`
--

CREATE TABLE IF NOT EXISTS `exoplanetprojekt`.`planeten` (
  `PID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(255) NOT NULL,
  `Breite` int(11) NOT NULL,
  `Hoehe` int(11) NOT NULL,
   PRIMARY KEY (`PID`),
  CONSTRAINT `Unique_Name` UNIQUE (`Name`,`Breite`,`Hoehe`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


--
-- Trigger `planeten`
--
DELIMITER $$
DROP TRIGGER IF EXISTS `exoplanetprojekt`.`insert_planet_calculateKoordinates`
$$
CREATE TRIGGER `exoplanetprojekt`.`insert_planet_calculateKoordinates` AFTER INSERT ON `exoplanetprojekt`.`planeten` FOR EACH ROW CALL calculate_Koordinaten(New.Breite, New.Hoehe);
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `messdaten`
--

CREATE TABLE IF NOT EXISTS `exoplanetprojekt`.`messdaten` (
  `MID` int(11) NOT NULL AUTO_INCREMENT,
  `KID` int(11) NOT NULL,
  `PID` int(11) NOT NULL,
  `BID` int(11) NOT NULL,
  `Temperatur` double NOT NULL,
   PRIMARY KEY (`MID`),
   CONSTRAINT `unique_messdaten` UNIQUE (`KID`,`PID`),
   CONSTRAINT `fk_messdaten_boden` FOREIGN KEY (`BID`) REFERENCES `exoplanetprojekt`.`boeden` (`BID`),
   CONSTRAINT `fk_messdaten_koordinaten` FOREIGN KEY (`KID`) REFERENCES `exoplanetprojekt`.`koordinaten` (`KID`),
   CONSTRAINT `fk_messdaten_planeten` FOREIGN KEY (`PID`) REFERENCES `exoplanetprojekt`.`planeten` (`PID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `richtungen`
--

CREATE TABLE IF NOT EXISTS `exoplanetprojekt`.`richtungen` (
  `RTID` int(11) NOT NULL AUTO_INCREMENT,
  `Bezeichnung` varchar(255) NOT NULL,
   PRIMARY KEY (`RTID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Daten für Tabelle `richtungen`
--

INSERT IGNORE INTO `exoplanetprojekt`.`richtungen` (`RTID`, `Bezeichnung`) VALUES
(1, 'NORTH'),
(2, 'EAST'),
(3, 'SOUTH'),
(4, 'WEST');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `stati`
--

CREATE TABLE IF NOT EXISTS `exoplanetprojekt`.`stati` (
  `SID` int(11) NOT NULL AUTO_INCREMENT,
  `Status` varchar(255) NOT NULL,
   PRIMARY KEY (`SID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Daten für Tabelle `stati`
--

INSERT IGNORE INTO `exoplanetprojekt`.`stati` (`SID`, `Status`) VALUES
(1, 'CRASHED'),
(2, 'WORKING');
(3, 'STUCK_IN_MUD');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `roboter`
--

CREATE TABLE IF NOT EXISTS `exoplanetprojekt`.`roboter` (
  `RID` int(11) NOT NULL AUTO_INCREMENT,
  `KID` int(11) DEFAULT NULL,
  `PID` int(11) NOT NULL,
  `RTID` int(11) DEFAULT NULL,
  `Name` varchar(255) NOT NULL,
  `Energie` double NOT NULL DEFAULT 100,
  `Betriebstemperatur` double NOT NULL DEFAULT 0,
  `SID` int(11) NOT NULL DEFAULT 2,
  `Heater` tinyint(1) NOT NULL DEFAULT 0,
  `Cooler` tinyint(1) NOT NULL DEFAULT 0,
   PRIMARY KEY (`RID`),
   CONSTRAINT `unique_pid_name` UNIQUE (`Name`,`PID`) USING BTREE,
   CONSTRAINT `fk_roboter_koordinaten` FOREIGN KEY (`KID`) REFERENCES `exoplanetprojekt`.`koordinaten` (`KID`),
   CONSTRAINT `fk_roboter_planet` FOREIGN KEY (`PID`) REFERENCES `exoplanetprojekt`.`planeten` (`PID`),
   CONSTRAINT `fk_roboter_richtung` FOREIGN KEY (`RTID`) REFERENCES `exoplanetprojekt`.`richtungen` (`RTID`),
   CONSTRAINT `fk_roboter_status` FOREIGN KEY (`SID`) REFERENCES `exoplanetprojekt`.`stati` (`SID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
