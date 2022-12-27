MERGE INTO GENRE (genre_id, name) VALUES (1, 'Комедия');
MERGE INTO GENRE (genre_id, name) VALUES (2, 'Драма');
MERGE INTO GENRE (genre_id, name) VALUES (3, 'Мультфильм');
MERGE INTO GENRE (genre_id, name) VALUES (4, 'Триллер');
MERGE INTO GENRE (genre_id, name) VALUES (5, 'Документальный');
MERGE INTO GENRE (genre_id, name) VALUES (6, 'Боевик');

MERGE INTO rate (rate_id, name) VALUES (1, 'G');
MERGE INTO rate (rate_id, name) VALUES (2, 'PG');
MERGE INTO rate (rate_id, name) VALUES (3, 'PG-13');
MERGE INTO rate (rate_id, name) VALUES (4, 'R');
MERGE INTO rate (rate_id, name) VALUES (5, 'NC-17');