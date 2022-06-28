TRUNCATE TABLE TENANT;
INSERT INTO TENANT (`id`, `name`, `subdomain`, `licensing_allowed_users`, `content_impressum`,`content_privacy`, `create_date`, `update_date`)
                  VALUES (1, 'Happylife Gmbh', 'happylife', 5, 'Impressum', 'Privacy', '2021-12-28', '2021-12-29');
INSERT INTO TENANT (`id`, `name`, `subdomain`, `licensing_allowed_users`, `content_impressum`,`content_privacy`,  `create_date`, `update_date`,`settings_topics_in_registration_enabled`)
                  VALUES (2, 'Another tenant', 'examplesubdomain', 10, 'Impressum', 'Privacy','2021-12-28', '2021-12-29', TRUE);