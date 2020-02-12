ALTER TABLE user_account
ADD CONSTRAINT `user_id_username_unique` UNIQUE(`user_id`,`username`);