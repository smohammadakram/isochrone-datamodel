DROP DATABASE IF EXISTS spatial;
DROP ROLE IF EXISTS spatial;

CREATE ROLE spatial WITH LOGIN PASSWORD 'spatial';
CREATE DATABASE spatial
	WITH OWNER = spatial
	ENCODING = 'UTF8';

ALTER ROLE spatial SET client_min_messages TO WARNING;
GRANT ALL ON DATABASE spatial TO spatial;
