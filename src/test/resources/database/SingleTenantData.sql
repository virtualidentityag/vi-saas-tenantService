TRUNCATE TABLE TENANT;
INSERT INTO TENANT (`id`, `name`, `subdomain`, `licensing_allowed_users`, `content_impressum`,`content_privacy`, `create_date`, `update_date`,`settings`,`content_placeholders`)
                  VALUES (1, 'Happylife Gmbh', 'happylife', 5, '{"de" : "Impressum"}', '{"de" : "Privacy"}', '2021-12-28', '2021-12-29', null, '{"de":[{"key":"placeholder key","value":"placeholder value"}],"en":[{"key":"placeholder key","value":"placeholder value en"}]}"}');



