ALTER TABLE `tenantservice`.`tenant`
ADD COLUMN `content_placeholders` longtext NULL AFTER `termsandconditions_activation_date`;