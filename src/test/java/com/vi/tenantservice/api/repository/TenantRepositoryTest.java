package com.vi.tenantservice.api.repository;

import com.vi.tenantservice.api.model.TenantEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@TestPropertySource(properties = "spring.profiles.active=testing")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
@DataJpaTest
public class TenantRepositoryTest {


    @Autowired
    private TenantRepository tenantRepository;

    @Test
    public void findTenantById() {
        // given, when
        Optional<TenantEntity> tenantEntity = tenantRepository.findById(1L);
        assertThat(tenantEntity).isPresent();
    }
}