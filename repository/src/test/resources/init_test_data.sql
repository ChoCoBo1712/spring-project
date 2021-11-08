INSERT INTO gift_certificates (name, description, price, duration, create_date, last_update_date)
VALUES ('certificate1', 'description1', 1, 1, '2021-11-05 00:00:00', '2021-11-05 00:00:00');

INSERT INTO gift_certificates (name, description, price, duration, create_date, last_update_date)
VALUES ('cert', 'desc', 1.1, 2, '2021-11-05 00:00:00', '2021-11-05 00:00:00');

INSERT INTO gift_certificates (name, description, price, duration, create_date, last_update_date)
VALUES ('some name', 'lorem ipsum', 10.3, 10, '2021-11-05 00:00:00', '2021-11-05 00:00:00');

INSERT INTO gift_certificates (name, description, price, duration, create_date, last_update_date)
VALUES ('test', 'description', 1.0, 20, '2021-11-05 00:00:00', '2021-11-05 00:00:00');

INSERT INTO tags (name)
VALUES ('tag1');

INSERT INTO tags (name)
VALUES ('tag2');

INSERT INTO tags (name)
VALUES ('tag3');

INSERT INTO certificates_tags (certificate_id, tag_id)
VALUES (1, 1);