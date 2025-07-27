CREATE TABLE IF NOT EXISTS users
(
    id
    INT
    NOT
    NULL
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    name
    VARCHAR
    NOT
    NULL,
    email
    VARCHAR
    NOT
    NULL
    UNIQUE
);

CREATE TABLE IF NOT EXISTS items
(
    id
    INT
    NOT
    NULL
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    name
    VARCHAR,
    description
    VARCHAR
    NOT
    NULL,
    available
    BOOLEAN
    NOT
    NULL,
    owner
    INT
    NOT
    NULL,
    request
    INT
);