package com.vi.tenantservice.api.model;


import com.google.common.collect.Lists;
import com.vi.tenantservice.api.authorisation.UserRole;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public enum TenantContent {
  IMPRESSUM(List.of(UserRole.TENANT_ADMIN)),
  PRIVACY(List.of(UserRole.TENANT_ADMIN)),
  TERMS_AND_CONDITIONS(List.of(UserRole.TENANT_ADMIN));

  private List<UserRole> rolesAuthorisedToChange;


  public List<UserRole> getRolesAuthorizedToChange(boolean singleTenantAdminIsPermittedToChangeLegalTexts) {
    if (singleTenantAdminIsPermittedToChangeLegalTexts) {
      return getUserRolesWithSingleTenantAdmin();
    } else {
      return rolesAuthorisedToChange;
    }
  }

  private List<UserRole> getUserRolesWithSingleTenantAdmin() {
    List<UserRole>  result = Lists.newArrayList();
    result.addAll(rolesAuthorisedToChange);
    result.add(UserRole.SINGLE_TENANT_ADMIN);
    return result;
  }
}
