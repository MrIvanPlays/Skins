CREATE TABLE `skins_storage` (
    `id`         INT AUTO_INCREMENT NOT NULL,
    `ownerName`  VARCHAR(128)       NOT NULL,
    `ownerUUID`  VARCHAR(36)        NOT NULL,
    `texture`    VARCHAR(2048)      NOT NULL,
    `signature`  VARCHAR(2048)      NOT NULL,
    PRIMARY KEY (`id`)
);
CREATE INDEX ON `skins_storage` (`ownerUUID`);
CREATE INDEX ON `skins_storage` (`ownerName`);

CREATE TABLE `skins_acquired` (
    `id`         INT AUTO_INCREMENT NOT NULL,
    `acquirer_uuid`  VARCHAR(36)    NOT NULL,
    `acquired_uuid`  VARCHAR(36)    NOT NULL,
    PRIMARY KEY (`id`)
);
CREATE INDEX ON `skins_acquired` (`acquirer_uuid`);
CREATE INDEX ON `skins_acquired` (`acquired_uuid`);
