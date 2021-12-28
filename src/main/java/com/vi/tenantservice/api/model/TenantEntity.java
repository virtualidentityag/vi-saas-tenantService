package com.vi.tenantservice.api.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
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
  @SequenceGenerator(name = "id_seq", allocationSize = 1, sequenceName = "sequence_tenant")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_seq")
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "subdomain", nullable = false)
  private String subdomain;

  @Column(name = "licensing_allowed_users", nullable = false)
  private Integer licensing_allowed_users;

  @Column(name = "theming_logo", nullable = false)
  private String theming_logo;

  @Column(name = "theming_favicon", nullable = false)
  private String theming_favicon;

  @Column(name = "theming_primary_color", nullable = false)
  private String theming_primary_color;

  @Column(name = "theming_secondary_color", nullable = false)
  private String theming_secondary_color;

  @Column(name = "content_impressum", nullable = false)
  private String content_impressum;

  @Column(name = "content_claim", nullable = false)
  private String content_claim;

  @Column(name = "create_date", nullable = false)
  private LocalDateTime createDate;

  @Column(name = "update_date", nullable = false)
  private LocalDateTime updateDate;

}
