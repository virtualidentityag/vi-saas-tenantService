ALTER TABLE `tenant`
MODIFY `content_claim` varchar(1024) COLLATE 'utf8_unicode_ci' NULL;

UPDATE `tenantservice`.`tenant` SET content_privacy = concat('[{"lang":"de","value":"', content_privacy, '"}]');
UPDATE `tenantservice`.`tenant` SET content_termsandconditions = concat('[{"lang":"de","value":"', content_termsandconditions, '"}]');
UPDATE `tenantservice`.`tenant` SET content_impressum = concat('[{"lang":"de","value":"', content_impressum, '"}]');
UPDATE `tenantservice`.`tenant` SET content_claim = concat('[{"lang":"de","value":"', content_claim, '"}]');



