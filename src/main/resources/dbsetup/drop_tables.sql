DROP TABLE IF EXISTS families, collectables;

CREATE TABLE IF NOT EXISTS player_collectables
(
    uuid VARCHAR(36) NOT NULL,
    collectable_name VARCHAR(36) NOT NULL,
    CONSTRAINT player_collectables_collectable_name_fk FOREIGN KEY (collectable_name) REFERENCES collectables (name),
    PRIMARY KEY (uuid)
);
CREATE TABLE IF NOT EXISTS families
(
    name VARCHAR(36) NOT NULL,
    guiRows INT(2) NOT NULL DEFAULT 3,
    PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS collectables
(
    name VARCHAR(36) NOT NULL,
    lore VARCHAR(128) NOT NULL,
    gui_slot_index INT(2) NOT NULL,
    material VARCHAR(24) NOT NULL,
    x DOUBLE NOT NULL DEFAULT 0.0,
    y DOUBLE NOT NULL DEFAULT 0.0,
    z DOUBLE NOT NULL DEFAULT 0.0,
    world VARCHAR(24) NOT NULL DEFAULT "world",
    active_radius DOUBLE NOT NULL DEFAULT 15.0,
    enchanted INT(2) NOT NULL DEFAULT 0,
    glowing INT(2) NOT NULL DEFAULT 0,
    custom_model_data INT(36) NOT NULL DEFAULT 0,
    family_name VARCHAR(36) NOT NULL,
    CONSTRAINT collectables_family_name_fk FOREIGN KEY (family_name) REFERENCES families (name),
    PRIMARY KEY (name)
);