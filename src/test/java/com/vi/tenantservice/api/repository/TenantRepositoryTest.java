package com.vi.tenantservice.api.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

import com.vi.tenantservice.api.model.TenantEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@TestPropertySource(properties = "spring.profiles.active=testing")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RunWith(SpringRunner.class)
@DataJpaTest
public class TenantRepositoryTest {

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    public void shouldFindTenantById() {
        // given, when
        Optional<TenantEntity> tenantEntity = tenantRepository.findById(1L);
        // then
        assertThat(tenantEntity).isPresent();
    }

    @Test
    public void shouldRemoveTenantById() {
        // given, when
        tenantRepository.deleteById(1L);
        Optional<TenantEntity> tenantEntity = tenantRepository.findById(1L);
        // then
        assertThat(tenantEntity).isNotPresent();
    }

    @Test
    public void shouldSaveTenant() {
        // given
        TenantEntity entity = new TenantEntity();
        entity.setName("new tenant");
        entity.setSubdomain("a subdomain");
        entity.setCreateDate(LocalDateTime.now());

        // when
        TenantEntity saved = tenantRepository.save(entity);
        tenantRepository.flush();

        Optional<TenantEntity> tenantEntity = tenantRepository.findById(saved.getId());
        // then
        assertThat(tenantEntity).isPresent();
        assertThat(tenantEntity.get()).isEqualTo(entity);
    }
}