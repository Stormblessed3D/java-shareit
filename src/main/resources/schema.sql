DROP TABLE IF EXISTS users CASCADE;

DROP TABLE IF EXISTS items CASCADE;

DROP TABLE IF EXISTS bookings CASCADE;

DROP TABLE IF EXISTS requests CASCADE;

DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS users 
(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT user_id PRIMARY KEY (user_id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

create unique index if not exists USER_EMAIL_UINDEX on USERS (email);

CREATE TABLE IF NOT EXISTS requests
(
    request_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description VARCHAR(255),
    requestor_id BIGINT NOT NULL REFERENCES users (user_id),
    created TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS items
(
    item_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255),
    available BOOLEAN,
    owner_id BIGINT NOT NULL REFERENCES users (user_id),
    request_id BIGINT REFERENCES requests (request_id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    item_id BIGINT REFERENCES items (item_id),
    booker_id BIGINT REFERENCES users (user_id),
    status VARCHAR
);

CREATE TABLE IF NOT EXISTS comments
(
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text VARCHAR(255),
    item_id BIGINT REFERENCES items (item_id),
    author_id BIGINT REFERENCES users (user_id),
    created TIMESTAMP WITHOUT TIME ZONE
);    
    