CREATE TABLE TENANT
(
    ID         bigint(21) NOT NULL,
    name          varchar(36) NOT NULL,
    subdomain        varchar(255) NOT NULL,
    licensing_allowed_users int,
    theming_logo longtext,
    theming_favicon longtext,
    theming_primary_color varchar(15),
    theming_secondary_color varchar(15),
    content_impressum longtext,
    content_claim varchar(1024),
    content_privacy longtext,
    content_termsandconditions longtext,
    create_date datetime NOT NULL,
    update_date datetime,
    PRIMARY KEY (ID)
);

ALTER TABLE TENANT
ADD CONSTRAINT unique_subdomain UNIQUE (subdomain);

CREATE SEQUENCE SEQUENCE_TENANT
    START WITH 100000
    INCREMENT BY 1;

INSERT INTO TENANT (`id`, `name`, `subdomain`, `licensing_allowed_users`, `content_impressum`,`content_privacy`, `create_date`, `update_date`)
                  VALUES (1, 'Happylife Gmbh', 'happylife', 5, 'Impressum', 'Privacy', '2021-12-28', '2021-12-29');
INSERT INTO TENANT (`id`, `name`, `subdomain`, `licensing_allowed_users`, `content_impressum`,`content_privacy`,  `create_date`, `update_date`)
                  VALUES (2, 'Another tenant', 'examplesubdomain', 10, 'Impressum', 'Privacy','2021-12-28', '2021-12-29');