DROP TABLE IF EXISTS biz_notification;
DROP TABLE IF EXISTS biz_review;
DROP TABLE IF EXISTS biz_order;
DROP TABLE IF EXISTS biz_schedule_slot;
DROP TABLE IF EXISTS biz_service;
DROP TABLE IF EXISTS sys_provider;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
    user_id     INT         AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL,
    password    VARCHAR(255) NOT NULL,
    phone       VARCHAR(20)  UNIQUE,
    email       VARCHAR(100) UNIQUE,
    role        VARCHAR(20)  DEFAULT 'USER',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE IF EXISTS sys_user ADD COLUMN IF NOT EXISTS email VARCHAR(100) UNIQUE;

INSERT INTO sys_user (username, password, phone, email, role) VALUES 
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', NULL, 'admin@example.com', 'ADMIN');

CREATE TABLE sys_provider (
    provider_id  INT         AUTO_INCREMENT PRIMARY KEY,
    user_id      INT         UNIQUE,
    company_name VARCHAR(100) NOT NULL,
    category     VARCHAR(50),
    rating       DECIMAL(3,2) DEFAULT 5.00,
    status       INT          DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES sys_user(user_id)
);

CREATE TABLE biz_service (
    service_id   INT          AUTO_INCREMENT PRIMARY KEY,
    provider_id  INT          NOT NULL,
    title        VARCHAR(100) NOT NULL,
    description  TEXT,
    price        DECIMAL(10,2) NOT NULL,
    duration_min INT          DEFAULT 60,
    category     VARCHAR(50),
    FOREIGN KEY (provider_id) REFERENCES sys_provider(provider_id)
);

CREATE TABLE biz_schedule_slot (
    slot_id      INT  AUTO_INCREMENT PRIMARY KEY,
    service_id   INT  NOT NULL,
    slot_date    DATE NOT NULL,
    start_time   TIME NOT NULL,
    end_time     TIME NOT NULL,
    is_available INT  DEFAULT 1,
    FOREIGN KEY (service_id) REFERENCES biz_service(service_id)
);

CREATE TABLE biz_order (
    order_id     INT          AUTO_INCREMENT PRIMARY KEY,
    user_id      INT          NOT NULL,
    slot_id      INT          UNIQUE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status       VARCHAR(20)  DEFAULT 'PENDING',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES sys_user(user_id),
    FOREIGN KEY (slot_id) REFERENCES biz_schedule_slot(slot_id)
);

CREATE TABLE biz_review (
    review_id  INT  AUTO_INCREMENT PRIMARY KEY,
    user_id    INT  NOT NULL,
    order_id   INT  UNIQUE NOT NULL,
    rating     INT  NOT NULL,
    comment    TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES sys_user(user_id),
    FOREIGN KEY (order_id) REFERENCES biz_order(order_id)
);

CREATE TABLE biz_notification (
    notification_id INT  AUTO_INCREMENT PRIMARY KEY,
    user_id         INT  NOT NULL,
    title           VARCHAR(100) NOT NULL,
    body            TEXT,
    is_read         INT  DEFAULT 0,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES sys_user(user_id)
);

CREATE TABLE sys_favorite (
    favorite_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT NOT NULL,
    service_id    INT NOT NULL,
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES sys_user(user_id),
    FOREIGN KEY (service_id) REFERENCES biz_service(service_id),
    UNIQUE KEY uk_user_service (user_id, service_id)
);
