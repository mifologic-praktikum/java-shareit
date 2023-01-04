CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name         VARCHAR(255)                            NOT NULL,
    description  TEXT(1000),
    is_available BOOLEAN                                 NOT NULL,
    user_id      BIGINT REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date   TIMESTAMP NOT NULL,
    item_id    BIGINT NOT NULL,
    booker_id  BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_bookings_item_id FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_bookings_booker_id FOREIGN KEY (booker_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text VARCHAR(255) NOT NULL,
    item_id    BIGINT,
    author_id  BIGINT,
    created TIMESTAMP NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comments_item_id FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_comments_booker_id FOREIGN KEY (author_id) REFERENCES users (id)
);