TRUNCATE TABLE TENANT;
INSERT INTO TENANT (`id`, `name`, `subdomain`, `licensing_allowed_users`, `content_impressum`,`content_privacy`, `create_date`, `update_date`,`settings`)
                  VALUES (1, 'Happylife Gmbh', 'happylife', 5, '{"de" : "Impressum", "en": "en Impressum"}', '{ "de" : "Privacy"}', '2021-12-28', '2021-12-29', '{"featureStatisticsEnabled":"true","featureTopicsEnabled":"true","topicsInRegistrationEnabled":"true","featureDemographicsEnabled":"true","featureAppointmentsEnabled":"true","featureGroupChatV2Enabled":"true","featureToolsEnabled":"true"}');
INSERT INTO TENANT (`id`, `name`, `subdomain`, `licensing_allowed_users`, `content_impressum`,`content_privacy`,  `create_date`, `update_date`,`settings`)
                  VALUES (2, 'Another tenant', 'examplesubdomain', 10, '{"de" : "Impressum"}', '{ "de" : "Privacy"}','2021-12-28', '2021-12-29', '{"topicsInRegistrationEnabled":"true"}');
INSERT INTO TENANT (`id`, `name`, `subdomain`, `licensing_allowed_users`, `content_impressum`,`content_privacy`,  `create_date`, `update_date`,`settings`)
                  VALUES (3, 'localhost tenant', 'localhost', 12, '{"de" : "Impressum"}', '{ "de" : "Privacy"}','2021-12-28', '2021-12-29', '{"topicsInRegistrationEnabled":"true"}');
