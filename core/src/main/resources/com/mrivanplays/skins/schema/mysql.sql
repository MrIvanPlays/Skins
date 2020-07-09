CREATE TABLE `skins_storage` (
    `id`         INT AUTO_INCREMENT NOT NULL,
    `ownerName`  VARCHAR(128)       NOT NULL,
    `ownerUUID`  VARCHAR(36)        NOT NULL,
    `texture`    VARCHAR(2048)      NOT NULL,
    `signature`  VARCHAR(2048)      NOT NULL,
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;
CREATE INDEX `skins_storage_ownerName` ON `skins_storage` (`ownerName`);
CREATE INDEX `skins_storage_ownerUUID` ON `skins_storage` (`ownerUUID`);

CREATE TABLE `skins_acquired` (
    `id`         INT AUTO_INCREMENT NOT NULL,
    `acquirer_uuid`  VARCHAR(36)    NOT NULL,
    `acquired_uuid`  VARCHAR(36)    NOT NULL,
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;
CREATE INDEX `skins_acquired_acquirer_uuid` ON `skins_acquired` (`acquirer_uuid`);
CREATE INDEX `skins_acquired_acquired_uuid` ON `skins_acquired` (`acquired_uuid`);
