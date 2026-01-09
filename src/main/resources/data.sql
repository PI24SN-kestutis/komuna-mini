INSERT INTO roles (id, name) VALUES
                                 (1, 'ADMIN'),
                                 (2, 'MANAGER'),
                                 (3, 'RESIDENT');

INSERT INTO communities (id, name, address, code)
VALUES
    (1, 'Žirmūnų bendrija', 'Žirmūnų g. 15, Vilnius', 'BND001'),
    (2, 'Antakalnio bendrija', 'Antakalnio g. 10, Vilnius', 'BND002');

INSERT INTO users (id, name, email, password, role_id, community_id)
VALUES
    (1, 'Administratorius', 'admin@komuna.lt', '$2a$12$89jgDgkNDbpXSE2tqYumvO7WRTkWwcms05IKuMMXhtrJCnlY7Nd9a', 1, NULL),
    (2, 'Vadybininkas Jonas', 'jonas.manager@komuna.lt', '$2a$12$hE0LaZF3WZ4BltiAO5DhYe9VW3OqBPV2GzJSPTISZ.XN9N2vH/Jp.', 2, 1),
    (3, 'Petras Gyventojas', 'petras@komuna.lt', '$2a$12$h/6JujhQEgR4c2/lEUK8.eP2uc8/TWmu3GflEiQe9VwaXtng9UDnW', 3, 1),
    (4, 'Ona Gyventoja', 'ona@komuna.lt', '$2a$12$rq58rmh/jmoeFh8x7d0gvOUAh36w0eEX8bqTtC45YPGMiIaReSJ9W', 3, 2);

INSERT INTO fees (id, name, unit, description, community_id, paid)
VALUES
    (1, 'Šildymas', 'kWh', 'Šildymo paslauga pagal suvartojimą', 1, false),
    (2, 'Vanduo', 'm³', 'Šalto ir karšto vandens tiekimas', 1, false),
    (3, 'Šiukšlių išvežimas', 'vnt', 'Atliekų tvarkymas', 2, false);


INSERT INTO prices (id, fee_id, community_id, amount, valid_from, valid_to)
VALUES
    (1, 1, 1, 0.12, '2025-01-01', NULL),
    (2, 2, 1, 1.85, '2025-01-01', NULL),
    (3, 3, 2, 3.50, '2025-01-01', NULL);


SELECT MAX(id) FROM roles;
ALTER TABLE roles ALTER COLUMN id RESTART WITH 4;
SELECT MAX(id) FROM communities;
ALTER TABLE communities ALTER COLUMN id RESTART WITH 3;
SELECT MAX(id) FROM users;
ALTER TABLE users ALTER COLUMN id RESTART WITH 5;
SELECT MAX(id) FROM fees;
ALTER TABLE fees ALTER COLUMN id RESTART WITH 4;
SELECT MAX(id) FROM prices;
ALTER TABLE prices ALTER COLUMN id RESTART WITH 4;
