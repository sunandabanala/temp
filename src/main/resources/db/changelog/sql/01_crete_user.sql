--liquibase formatted sql
--changeset jvddruyt:1
CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` char(32) NOT NULL UNIQUE,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `email_id` varchar(50) NOT NULL,
  `password` varchar(1000) NOT NULL,
  `mobile` varchar(20) DEFAULT NULL,
  `country` varchar(50) DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `is_deleted` boolean not null default 0,
  `created_at` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP,
  `organization_id` char(32) REFERENCES `organizations` (`uuid`),
  `is_enabled` boolean not null default 0,
  PRIMARY KEY (`id`),
  CONSTRAINT `org_email_unique` UNIQUE(`organization_id`,`email_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `users` (`id`, `uuid`, `first_name`, `last_name`, `email_id`, `password`, `mobile`, `country`, `type`, `organization_id`, `is_enabled`)
VALUES
	(2,'b2f4f00baf5645b8a0b8662746151137','John','Doe','john.doe@example.com','$2a$10$eUB5vV/xWXrcR61TqcWRXOCfl37r3fk.UXNXEzx7xTol5inz5xZ.2','+918939052159','USA','admin','aa62bb4897f047ec9f346df70885bc46',1);