
CREATE TABLE IF NOT EXISTS families
(
    family_name VARCHAR(36) NOT NULL,
    gui_rows INT(2) NOT NULL DEFAULT 3,
    family_icon_material VARCHAR(36) NOT NULL DEFAULT "STONE",
    family_icon_CMD INT(36) NOT NULL DEFAULT 0,
    gui_missing_item_material VARCHAR(36) NOT NULL DEFAULT "STONE",
    gui_missing_item_CMD INT(36) NOT NULL DEFAULT 0,
    PRIMARY KEY (family_name)
);