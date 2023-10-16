CREATE TABLE inflation (
    id INT PRIMARY KEY,
    region TEXT,
    year TEXT,
    inflation DECIMAL,
    unit TEXT,
    subregion TEXT,
    country TEXT
);

COPY inflation
FROM '/docker-entrypoint-initdb.d/inflation.csv'
DELIMITER ','
CSV HEADER;