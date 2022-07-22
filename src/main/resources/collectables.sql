CREATE TABLE IF NOT EXISTS collectables
(
    collectable_name VARCHAR(36) NOT NULL,
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
    PRIMARY KEY (collectable_name)
);