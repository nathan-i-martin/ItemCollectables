CREATE TABLE IF NOT EXISTS player_collectables
(
    uuid VARCHAR(36) NOT NULL,
    collectable_name VARCHAR(36) NOT NULL,
    CONSTRAINT player_collectables_collectable_name_fk FOREIGN KEY (collectable_name) REFERENCES collectables (name),
    PRIMARY KEY (uuid)
);
