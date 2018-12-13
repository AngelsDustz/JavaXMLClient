USE `unwdmi`;

CREATE TABLE IF NOT EXISTS `measurements` (
    `id`            INT AUTO_INCREMENT NOT NULL,
    `unwdmi_id`     INT(10) UNSIGNED NOT NULL,
    `temp`          FLOAT(5, 1) NOT NULL,
    `dewp`          FLOAT(5, 1) NOT NULL,
    `stp`           FLOAT(5, 1) UNSIGNED NOT NULL,
    `slp`           FLOAT(5, 1) UNSIGNED NOT NULL,
    `visibility`    FLOAT(4, 1) UNSIGNED NOT NULL,
    `wind_speed`    FLOAT(4, 1) UNSIGNED NOT NULL,
    `prcp`          FLOAT(5, 2) UNSIGNED NOT NULL,
    `sndp`          FLOAT(5, 1) NOT NULL,
    `cloud`         FLOAT(3, 1) NOT NULL,
    `wind_dir`      INT(4) NOT NULL,
    `ev_freeze`     BOOLEAN NOT NULL,
    `ev_rain`       BOOLEAN NOT NULL,
    `ev_snow`       BOOLEAN NOT NULL,
    `ev_hail`       BOOLEAN NOT NULL,
    `ev_thunder`    BOOLEAN NOT NULL,
    `ev_tornado`    BOOLEAN NOT NULL,
    `measured_at`   DATETIME NOT NULL,
    `created_at`    DATETIME NOT NULL,

    PRIMARY KEY (`id`),
    CONSTRAINT fk_unwdmi FOREIGN KEY (`unwdmi_id`) REFERENCES stations(`stn`) ON DELETE CASCADE
);