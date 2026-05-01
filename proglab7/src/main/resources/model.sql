CREATE TYPE MpaaRating AS ENUM ('G', 'PG', 'PG_13', 'R', 'NC_17');
CREATE TYPE Color AS ENUM ('GREEN', 'RED', 'BLUE', 'YELLOW', 'WHITE', 'BLACK', 'BROWN');
CREATE TYPE Country AS ENUM ('RUSSIA', 'CHINA', 'THAILAND', 'SOUTH_KOREA', 'JAPAN');

CREATE TABLE IF NOT EXISTS users(
    username TEXT PRIMARY KEY,
    hashed_password TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS movies(
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    x FLOAT NOT NULL,
    y INT NOT NULL,
    creationDate TIMESTAMP WITH TIME ZONE NOT NULL,
    oscarsCount BIGINT NOT NULL,
    goldenPalmCount BIGINT NOT NULL,
    tagline TEXT,
    mpaarating MpaaRating NOT NULL,
    director_name TEXT,
    director_passportid TEXT,
    director_eyecolor Color,
    director_haircolor Color,
    director_nationality Country,
    owner TEXT REFERENCES users(username)
);