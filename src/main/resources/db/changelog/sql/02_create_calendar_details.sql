--liquibase formatted sql
--changeset jvddrift:1
CREATE TABLE IF NOT EXISTS `calendar_details` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `object_id` varchar(255) DEFAULT NULL,
  `object_type` ENUM('EVENT'),
  `calendar_id` varchar(255) DEFAULT NULL,
  `calendar_details` TEXT(65535) DEFAULT NULL,
  `is_deleted` boolean not null default 0,
  `is_owner` boolean not null default 1,
  `created_at` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP,
  `uuid` char(32) NOT NULL UNIQUE,
  `account_id` char(32) REFERENCES `user_account` (`uuid`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;