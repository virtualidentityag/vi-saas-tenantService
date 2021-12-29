package com.vi.tenantservice.api.model;

import javax.persistence.*;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tenant")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TenantEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tenant_generator")
  @SequenceGenerator(name="tenant_generator", sequenceName = "SEQUENCE_TENANT", allocationSize=1)
 // @SequenceGenerator(name = "id_seq", allocationSize = 1, sequenceName = "SEQUENCE_TENANT")
 // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_seq")

  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "subdomain", nullable = false)
  private String subdomain;

  @Column(name = "licensing_allowed_users")
  private Integer licensingAllowedUsers;

  @Column(name = "theming_logo")
  private String themingLogo;

  @Column(name = "theming_favicon")
  private String themingFavicon;

  @Column(name = "theming_primary_color")
  private String themingPrimaryColor;

  @Column(name = "theming_secondary_color")
  private String themingSecondaryColor;

  @Column(name = "content_impressum")
  private String contentImpressum;

  @Column(name = "content_claim")
  private String contentClaim;

  @Column(name = "create_date", nullable = false)
  private LocalDateTime createDate;

  @Column(name = "update_date")
  private LocalDateTime updateDate;

}
