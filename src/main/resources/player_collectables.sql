CREATE TABLE IF NOT EXISTS player_collectables
(
    uuid VARCHAR(36) NOT NULL,
    collectable_name VARCHAR(36) NOT NULL,
    CONSTRAINT player_collectables_pk UNIQUE (uuid, collectable_name)
);
