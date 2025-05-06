CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255)
);

CREATE TABLE sleep (
    id UUID PRIMARY KEY,
    sleep_date TIMESTAMP,
    time_in_bed TIMESTAMP,
    total_time_in_bed TIMESTAMP,
    feeling VARCHAR(10) NOT NULL,
    user_id UUID REFERENCES "users"(id) ON DELETE CASCADE
);
