package com.vi.tenantservice.api.exception.httpresponse;

public enum HttpStatusExceptionReason {
  SUBDOMAIN_NOT_UNIQUE,
  NOT_ALLOWED_TO_CHANGE_SUBDOMAIN,
  NOT_ALLOWED_TO_CHANGE_LICENSING,
  NOT_ALLOWED_TO_CHANGE_SETTING,

  LANGUAGE_KEY_NOT_VALID;
}
