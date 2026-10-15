CREATE TABLE companies (
                           id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                           name VARCHAR(150) NOT NULL,
                           cnpj VARCHAR(14) UNIQUE,
                           email VARCHAR(254),
                           phone VARCHAR(20),
                           active BOOLEAN NOT NULL DEFAULT TRUE,
                           user_id BIGINT NOT NULL,
                           created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           CONSTRAINT fk_companies_user
                               FOREIGN KEY (user_id)
                                   REFERENCES users(id)
                                   ON DELETE RESTRICT
);

CREATE INDEX idx_companies_user_id ON companies(user_id);