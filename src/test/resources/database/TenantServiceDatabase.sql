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
    settings_topics_in_registration_enabled BOOLEAN default FALSE,
    PRIMARY KEY (ID)
);

ALTER TABLE TENANT
ADD CONSTRAINT unique_subdomain UNIQUE (subdomain);

CREATE SEQUENCE SEQUENCE_TENANT
    START WITH 100000
    INCREMENT BY 1;