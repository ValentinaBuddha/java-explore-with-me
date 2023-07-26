DELETE FROM comments;
DELETE FROM users;
DELETE FROM categories;
DELETE FROM locations;
DELETE FROM events;

INSERT INTO users (name, email) VALUES ('user1', 'user1@mail.ru');
INSERT INTO users (name, email) VALUES ('user2', 'user2@mail.ru');

INSERT INTO categories (name) VALUES ('leto');

INSERT INTO locations (lat, lon) VALUES ('55.75', '37.61');

INSERT INTO events (annotation, category_id, description, event_date, initiator_id, location_id, title)
VALUES ('шоу красочных фонтанов', 1, 'в сопровождении классической музыки', '2024-03-03 10:00:00', 2, 1, 'event1');
INSERT INTO events (annotation, category_id, description, event_date, initiator_id, location_id, title)
VALUES ('прогулка по Москва-реке', 1, 'на современном речном электротрамвае', '2024-03-03 10:00:00', 2, 1, 'event2');

UPDATE events SET state = 'PUBLISHED' WHERE id = 1;
