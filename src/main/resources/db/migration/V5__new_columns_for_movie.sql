ALTER TABLE MOVIES
    ADD COLUMN RELEASE_YEAR INT DEFAULT 0;

ALTER TABLE MOVIES
    ADD COLUMN IMDB_RATING NUMERIC(3,1) DEFAULT 0.0;