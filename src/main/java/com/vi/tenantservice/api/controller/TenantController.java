package com.vi.tenantservice.api.controller;

import com.vi.tenantservice.api.facade.TenantServiceFacade;
import com.vi.tenantservice.generated.api.controller.TenantApi;
import io.swagger.annotations.Api;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for triggering live events.
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "tenant-controller")
public class TenantController implements TenantApi {

  private final @NonNull TenantServiceFacade tenantServiceFacade;

  /**
   * Trigger entry point for live event sending.
   *
   * @param liveEventMessage the {@link LiveEventMessage} of the live event
   */
  /*@Override
  public ResponseEntity<Void> sendLiveEvent(@Valid @RequestBody LiveEventMessage liveEventMessage) {
    if (isEmpty(liveEventMessage.getUserIds())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ids must not be empty");
    }
    this.liveEventFacade.triggerLiveEvent(liveEventMessage);
    return new ResponseEntity<>(HttpStatus.OK);
  }*/

}
