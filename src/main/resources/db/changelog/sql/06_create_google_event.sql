--liquibase formatted sql
--changeset jvddrift:1
CREATE TABLE IF NOT EXISTS `google_event` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `account_id` char(32) REFERENCES `user_account` (`uuid`),
  `meet_link` varchar(255) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP,
  `uuid` char(32) NOT NULL UNIQUE,
  `google_id` VARCHAR(100) NOT NULL UNIQUE,
  `event_details` TEXT(65535) DEFAULT NULL,
  `user_id` char(32) NOT NULL,
  `timezone` VARCHAR(100),
  `platform_event_id` char(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;