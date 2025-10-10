INSERT INTO roles (id, name) VALUES (1, 'ADMIN'), (2, 'MANAGER'), (3, 'RESIDENT');

INSERT INTO communities (id, name, address, code) VALUES (1, 'Žalgirio bendrija', 'Žalgirio g. 3, Vilnius', '12345678');
INSERT INTO communities (id, name, address, code) VALUES (2, 'Naugarduko bendrija', 'Naugarduko g. 45, Vilnius', '87654321');


INSERT INTO users (id, name, email, password, community_id, role_id) VALUES (1, 'Jonas Petrauskas', 'jonas@x.com', '$2a$12$.I0fqwohPzAajqn13TdyweNt7skw.WbSGtl1YFmFRG3WY6KVGM.gC', 1, 3);
INSERT INTO users (id, name, email, password, community_id, role_id) VALUES (2, 'Agnė Kazlauskienė', 'agne@x.com', '$2a$12$r/jJQvZ80xNwGHWiD0cVxO2F8UgYzWSOF0GYQ7a.BCGuZT867RZ7C', 1, 2);
INSERT INTO users (id, name, email, password, community_id, role_id) VALUES (3, 'Administratorius', 'admin@x.com', '$2a$12$P3Z1FRBXU6Dvk6fAgU90Fed2WHB.PombEdgak1.6/A/nSEPuRe32K', 1, 1);

INSERT INTO fees (type, amount, paid, user_id) VALUES ('Vanduo', 24.50, false, 1);
INSERT INTO fees (type, amount, paid, user_id) VALUES ('Šildymas', 80.00, true, 1);
INSERT INTO fees (type, amount, paid, user_id) VALUES ('Atliekos', 10.20, false, 2);
INSERT INTO fees (type, amount, paid, user_id) VALUES ('Vanduo', 30.10, true, 3);


SELECT MAX(ID) FROM USERS;
ALTER TABLE USERS ALTER COLUMN ID RESTART WITH 4;
SELECT MAX(ID) FROM COMMUNITIES;
ALTER TABLE COMMUNITIES ALTER COLUMN ID RESTART WITH 3;
SELECT MAX(ID) FROM FEES;
ALTER TABLE FEES ALTER COLUMN ID RESTART WITH 5;