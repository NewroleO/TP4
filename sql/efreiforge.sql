CREATE DATABASE IF NOT EXISTS efrei_tp4 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE efrei_tp4;

DROP TABLE IF EXISTS ticket;
DROP TABLE IF EXISTS collaborateur;
DROP TABLE IF EXISTS adresse;

CREATE TABLE adresse (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    rue         VARCHAR(100) NOT NULL,
    code_postal VARCHAR(10)  NOT NULL,
    ville       VARCHAR(60)  NOT NULL,
    pays        VARCHAR(60)  NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE collaborateur (
    identifiant     VARCHAR(10)  NOT NULL,
    metier          VARCHAR(40)  NOT NULL,
    prenom          VARCHAR(50)  NOT NULL,
    nom             VARCHAR(50)  NOT NULL,
    salaire         DOUBLE       NOT NULL,
    langage_prefere VARCHAR(40)  NULL,
    adresse_id      BIGINT       NOT NULL,
    PRIMARY KEY (identifiant),
    CONSTRAINT fk_collab_adresse FOREIGN KEY (adresse_id) REFERENCES adresse (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ticket (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    titre       VARCHAR(255),
    priorite    VARCHAR(20),
    statut      VARCHAR(20),
    assigne_id  VARCHAR(10)  NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_ticket_collab FOREIGN KEY (assigne_id) REFERENCES collaborateur (identifiant)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO adresse (id, rue, code_postal, ville, pays) VALUES
(1, '12 rue de la Paix',            '75002', 'Paris', 'France'),
(2, '5 avenue des Champs-Élysées',  '75008', 'Paris', 'France'),
(3, '8 boulevard Haussmann',        '75009', 'Paris', 'France'),
(4, '3 rue de Rivoli',              '75001', 'Paris', 'France'),
(5, '15 rue du Commerce',           '75015', 'Paris', 'France'),
(6, '22 rue de Vaugirard',          '75006', 'Paris', 'France');

INSERT INTO collaborateur (identifiant, metier, prenom, nom, salaire, langage_prefere, adresse_id) VALUES
('C001', 'Programmeur', 'Alice',  'Martin',  42000, 'Java',   1),
('C002', 'Programmeur', 'Alex',   'Dupont',  44500, 'Python', 2),
('C003', 'Testeur',     'Chloé',  'Bernard', 39000, NULL,     3),
('C004', 'Programmeur', 'Nora',   'Diallo',  47000, 'Java',   4),
('C005', 'Testeur',     'Hugo',   'Petit',   41000, NULL,     5),
('C006', 'Programmeur', 'Lina',   'Moreau',  45500, 'Kotlin', 6);

INSERT INTO ticket (id, titre, priorite, statut, assigne_id) VALUES
(1, 'Corriger le bug de connexion',   'HAUTE',   'EN_COURS',  'C001'),
(2, 'Ajouter la page de profil',      'NORMALE', 'A_FAIRE',   'C002'),
(3, 'Mettre à jour la documentation', 'BASSE',   'A_FAIRE',    NULL),
(4, 'Optimiser les requêtes SQL',     'HAUTE',   'A_VALIDER', 'C004'),
(5, 'Corriger l''export PDF',         'NORMALE', 'TERMINE',   'C001'),
(6, 'Ajouter la recherche par nom',   'HAUTE',   'A_FAIRE',    NULL);

