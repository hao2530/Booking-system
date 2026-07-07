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
    role        VARCHAR(20)  DEFAULT 'USER',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP
);

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

CREATE TABLE biz_audit_log (
    log_id      INT          AUTO_INCREMENT PRIMARY KEY,
    operator_id INT          NOT NULL,
    action      VARCHAR(100) NOT NULL,
    target_type VARCHAR(50),
    target_id   INT,
    old_value   TEXT,
    new_value   TEXT,
    status      VARCHAR(20)  DEFAULT 'APPROVED',
    remark      VARCHAR(255),
    reviewer_id INT,
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    reviewed_at DATETIME,
    FOREIGN KEY (operator_id) REFERENCES sys_user(user_id)
);
