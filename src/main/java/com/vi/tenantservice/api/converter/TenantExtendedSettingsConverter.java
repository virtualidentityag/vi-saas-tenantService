package com.vi.tenantservice.api.converter;

import com.vi.tenantservice.api.model.ExtendedTenantSettings;
import com.vi.tenantservice.api.model.ExtendedTenantSettingsNotifications;
import com.vi.tenantservice.api.model.ExtendedTenantSettingsWelcomeMessage;
import com.vi.tenantservice.api.model.NotificationsDTOTeamSessions;
import com.vi.tenantservice.api.model.TeamSessionsDTONewMessage;
import com.vi.tenantservice.consultingtypeservice.generated.web.model.FullConsultingTypeResponseDTO;
import com.vi.tenantservice.consultingtypeservice.generated.web.model.NotificationsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TenantExtendedSettingsConverter {

  public ExtendedTenantSettings convertExtendedTenantSettings(
      FullConsultingTypeResponseDTO consultingTypeResponseDTO) {
    ExtendedTenantSettings extendedTenantSettings = initializeExtendedTenantSettings();
    extendedTenantSettings.setLanguageFormal(consultingTypeResponseDTO.getLanguageFormal());
    extendedTenantSettings.setIsVideoCallAllowed(consultingTypeResponseDTO.getIsVideoCallAllowed());

    if (consultingTypeResponseDTO.getWelcomeMessage() != null) {
      extendedTenantSettings
          .getWelcomeMessage()
          .setSendWelcomeMessage(
              consultingTypeResponseDTO.getWelcomeMessage().getSendWelcomeMessage());
      extendedTenantSettings
          .getWelcomeMessage()
          .setWelcomeMessageText(
              consultingTypeResponseDTO.getWelcomeMessage().getWelcomeMessageText());
    }

    extendedTenantSettings.setSendFurtherStepsMessage(
        consultingTypeResponseDTO.getSendFurtherStepsMessage());
    extendedTenantSettings.setSendSaveSessionDataMessage(
        consultingTypeResponseDTO.getSendSaveSessionDataMessage());

    convertNotifications(consultingTypeResponseDTO, extendedTenantSettings);
    return extendedTenantSettings;
  }

  private void convertNotifications(
      FullConsultingTypeResponseDTO consultingTypeResponseDTO,
      ExtendedTenantSettings extendedTenantSettings) {
    NotificationsDTO notifications = consultingTypeResponseDTO.getNotifications();
    if (notifications != null
        && notifications.getTeamSessions() != null
        && notifications.getTeamSessions().getNewMessage() != null) {
      extendedTenantSettings
          .getNotifications()
          .getTeamSessions()
          .getNewMessage()
          .allTeamConsultants(
              notifications.getTeamSessions().getNewMessage().getAllTeamConsultants());
    }
  }

  private ExtendedTenantSettings initializeExtendedTenantSettings() {
    ExtendedTenantSettings extendedTenantSettings = new ExtendedTenantSettings();
    extendedTenantSettings.setNotifications(
        new ExtendedTenantSettingsNotifications()
            .teamSessions(
                new NotificationsDTOTeamSessions().newMessage(new TeamSessionsDTONewMessage())));
    extendedTenantSettings.setWelcomeMessage(new ExtendedTenantSettingsWelcomeMessage());
    return extendedTenantSettings;
  }
}
