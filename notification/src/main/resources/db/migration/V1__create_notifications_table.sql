CREATE TABLE notifications (
                               id UUID PRIMARY KEY,
                               user_id UUID NOT NULL,
                               type VARCHAR(100) NOT NULL,
                               title VARCHAR(200) NOT NULL,
                               message VARCHAR(2000) NOT NULL,
                               status VARCHAR(255) NOT NULL,
                               channel VARCHAR(255) NOT NULL,
                               read BOOLEAN NOT NULL DEFAULT FALSE,
                               created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_user_id
    ON notifications (user_id);

CREATE INDEX idx_notifications_user_id_created_at
    ON notifications (user_id, created_at DESC);