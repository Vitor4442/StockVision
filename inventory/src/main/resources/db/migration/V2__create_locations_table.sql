CREATE TABLE locations (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           description VARCHAR(255),
                           active BOOLEAN NOT NULL
);