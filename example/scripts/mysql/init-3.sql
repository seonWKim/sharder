CREATE DATABASE IF NOT EXISTS `test`;
USE `test`;

CREATE TABLE members (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

DELIMITER ;;
CREATE PROCEDURE InsertDummyData()
BEGIN
    DECLARE i INT DEFAULT 3;
    WHILE i <= 100 DO
            INSERT INTO members (id, name) VALUES (i, CONCAT('Member ', i));
            SET i = i + 3;
        END WHILE;
END;;
DELIMITER ;

CALL InsertDummyData();
