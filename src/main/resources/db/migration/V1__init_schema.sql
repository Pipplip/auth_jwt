CREATE TABLE `refresh_tokens` (
  `invalidated` bit(1) NOT NULL,
  `expires_at` datetime(6) DEFAULT NULL,
  `invalidated_at` datetime(6) DEFAULT NULL,
  `user_id` binary(16) DEFAULT NULL,
  `token` varchar(255) NOT NULL,
  PRIMARY KEY (`token`)
) ENGINE=InnoDB;

CREATE TABLE `users` (
  `registered_at` datetime(6) NOT NULL,
  `id` binary(16) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `user_role` enum('ADMIN','USER') DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB;
