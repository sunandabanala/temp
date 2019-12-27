ALTER TABLE user_account drop index user_id,
ADD is_active boolean default 1;