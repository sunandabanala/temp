--liquibase formatted sql
--changeset jvddrift:1
CREATE TABLE IF NOT EXISTS `user_account` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL UNIQUE,
  `provider_name` varchar(255) DEFAULT NULL,
  `refresh_token` varchar(255) DEFAULT NULL,
  `nylas_token` varchar(255) DEFAULT NULL,
  `nylas_account_id` varchar(255) DEFAULT NULL,
  `cursor_id` varchar(255) DEFAULT NULL,
  `is_deleted` boolean not null default 0,
  `created_at` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP,
  `uuid` char(32) NOT NULL UNIQUE,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;