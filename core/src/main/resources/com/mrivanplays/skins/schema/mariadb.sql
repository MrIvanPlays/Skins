CREATE TABLE `skins_storage` (
    `id`         INT AUTO_INCREMENT NOT NULL,
    `ownerName`  VARCHAR(128)       NOT NULL,
    `ownerUUID`  VARCHAR(36)        NOT NULL,
    `texture`    VARCHAR(128)       NOT NULL,
    `signature`  VARCHAR(256)       NOT NULL,
    `acquirers`  VARCHAR(8000)      NOT NULL,
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;
CREATE INDEX `skins_storage_ownerName` ON `skins_storage` (`ownerName`);
CREATE INDEX `skins_storage_ownerUUID` ON `skins_storage` (`ownerUUID`);
