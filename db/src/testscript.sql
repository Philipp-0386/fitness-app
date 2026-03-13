DROP TABLE userdata cascade constraints;

CREATE TABLE userdata (
    id INTEGER PRIMARY KEY,
    name VARCHAR2(32) NOT NULL,
    age INTEGER NOT NULL
);

INSERT INTO userdata VALUES (1, 'Philipp', 24);
INSERT INTO userdata VALUES (2, 'Lara', 23);

COMMIT;