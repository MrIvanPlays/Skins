CREATE TABLE `skins_storage` (
    `id`         INTEGER PRIMARY KEY NOT NULL,
    `ownerName`  VARCHAR(128)        NOT NULL,
    `ownerUUID`  VARCHAR(36)         NOT NULL,
    `texture`    VARCHAR(128)        NOT NULL,
    `signature`  VARCHAR(256)        NOT NULL,
    `acquirers`  VARCHAR(8000)       NOT NULL
);
CREATE INDEX `skins_storage_ownerName` ON `skins_storage` (`ownerName`);
CREATE INDEX `skins_storage_ownerUUID` ON `skins_storage` (`ownerUUID`);
