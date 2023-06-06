ALTER TABLE `tenantservice`.`tenant`
    MODIFY COLUMN `id` bigint(21) NOT NULL DEFAULT nextval(`tenantservice`.`sequence_tenant`);