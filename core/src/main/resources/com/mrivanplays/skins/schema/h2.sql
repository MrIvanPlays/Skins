CREATE TABLE `skins_storage` (
    `id`         INT AUTO_INCREMENT NOT NULL,
    `ownerName`  VARCHAR(128)       NOT NULL,
    `ownerUUID`  VARCHAR(36)        NOT NULL,
    `texture`    VARCHAR(2048)       NOT NULL,
    `signature`  VARCHAR(2048)       NOT NULL,
    `acquirers`  VARCHAR(8000)      NOT NULL,
    PRIMARY KEY (`id`)
);
CREATE INDEX ON `skins_storage` (`ownerUUID`);
CREATE INDEX ON `skins_storage` (`ownerName`);
