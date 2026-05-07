CREATE TYPE mpaa_rating AS ENUM ('G', 'PG', 'PG_13', 'R', 'NC_17');
CREATE TYPE color AS ENUM ('GREEN', 'RED', 'BLUE', 'YELLOW', 'WHITE', 'BLACK', 'BROWN');
CREATE TYPE country AS ENUM ('RUSSIA', 'CHINA', 'THAILAND', 'SOUTH_KOREA', 'JAPAN');

CREATE TABLE IF NOT EXISTS users(
    username TEXT PRIMARY KEY,
    hashed_password TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS movies(
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    x FLOAT NOT NULL,
    y INT NOT NULL,
    creation_date TIMESTAMP WITH TIME ZONE NOT NULL,
    oscars_count BIGINT NOT NULL,
    golden_palm_count BIGINT NOT NULL,
    tagline TEXT,
    mpaa_rating mpaa_rating NOT NULL,
    director_name TEXT,
    director_passport_id TEXT,
    director_eye_color color,
    director_hair_color color,
    director_nationality country,
    owner TEXT REFERENCES users(username)
);