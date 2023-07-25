package com.vi.tenantservice.api.converter;

import com.vi.tenantservice.api.model.ConsultingTypePatchDTO;
import com.vi.tenantservice.api.model.ConsultingTypePatchDTONotifications;
import com.vi.tenantservice.api.model.ConsultingTypePatchDTOWelcomeMessage;
import com.vi.tenantservice.api.model.NotificationsDTOTeamSessions;
import com.vi.tenantservice.api.model.TeamSessionsDTONewMessage;
import com.vi.tenantservice.consultingtypeservice.generated.web.model.FullConsultingTypeResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConsultingTypePatchDTOConverter {

  public ConsultingTypePatchDTO convertConsultingTypePatchDTO(
      FullConsultingTypeResponseDTO consultingTypeResponseDTO) {
    ConsultingTypePatchDTO consultingTypePatchDTO = initializeExtendedTenantSettings();
    consultingTypePatchDTO.setLanguageFormal(consultingTypeResponseDTO.getLanguageFormal());
    consultingTypePatchDTO.setIsVideoCallAllowed(consultingTypeResponseDTO.getIsVideoCallAllowed());

    if (consultingTypeResponseDTO.getWelcomeMessage() != null) {
      consultingTypePatchDTO
          .getWelcomeMessage()
          .setSendWelcomeMessage(
              consultingTypeResponseDTO.getWelcomeMessage().getSendWelcomeMessage());
      consultingTypePatchDTO
          .getWelcomeMessage()
          .setWelcomeMessageText(
              consultingTypeResponseDTO.getWelcomeMessage().getWelcomeMessageText());
    }

    consultingTypePatchDTO.setSendFurtherStepsMessage(
        consultingTypeResponseDTO.getSendFurtherStepsMessage());
    consultingTypePatchDTO.setSendSaveSessionDataMessage(
        consultingTypeResponseDTO.getSendSaveSessionDataMessage());

    convertNotifications(consultingTypeResponseDTO, consultingTypePatchDTO);
    return consultingTypePatchDTO;
  }

  private void convertNotifications(
      FullConsultingTypeResponseDTO consultingTypeResponseDTO,
      ConsultingTypePatchDTO consultingTypePatchDTO) {
    var notifications = consultingTypeResponseDTO.getNotifications();
    if (notifications != null
        && notifications.getTeamSessions() != null
        && notifications.getTeamSessions().getNewMessage() != null) {
      consultingTypePatchDTO
          .getNotifications()
          .getTeamSessions()
          .getNewMessage()
          .allTeamConsultants(
              notifications.getTeamSessions().getNewMessage().getAllTeamConsultants());
    }
  }

  private ConsultingTypePatchDTO initializeExtendedTenantSettings() {
    ConsultingTypePatchDTO consultingTypePatchDTO = new ConsultingTypePatchDTO();
    consultingTypePatchDTO.setNotifications(
        new ConsultingTypePatchDTONotifications()
            .teamSessions(
                new NotificationsDTOTeamSessions().newMessage(new TeamSessionsDTONewMessage())));
    consultingTypePatchDTO.setWelcomeMessage(new ConsultingTypePatchDTOWelcomeMessage());
    return consultingTypePatchDTO;
  }

  public com.vi.tenantservice.consultingtypeservice.generated.web.model.ConsultingTypePatchDTO
      convertToConsultingTypeServiceModel(ConsultingTypePatchDTO extendedSettings) {
    com.vi.tenantservice.consultingtypeservice.generated.web.model.ConsultingTypePatchDTO
        targetDTO =
            new com.vi.tenantservice.consultingtypeservice.generated.web.model
                .ConsultingTypePatchDTO();

    BeanUtils.copyProperties(extendedSettings, targetDTO);
    convertWelcomeMessage(extendedSettings, targetDTO);
    convertNotifications(extendedSettings, targetDTO);
    return targetDTO;
  }

  private void convertNotifications(
      ConsultingTypePatchDTO extendedSettings,
      com.vi.tenantservice.consultingtypeservice.generated.web.model.ConsultingTypePatchDTO
          targetDTO) {
    if (extendedSettings.getNotifications() != null
        && extendedSettings.getNotifications().getTeamSessions() != null
        && extendedSettings.getNotifications().getTeamSessions().getNewMessage() != null) {
      createEmptyNotificationsObjectAndSubobjects(targetDTO);
      targetDTO
          .getNotifications()
          .getTeamSessions()
          .getNewMessage()
          .allTeamConsultants(
              extendedSettings
                  .getNotifications()
                  .getTeamSessions()
                  .getNewMessage()
                  .getAllTeamConsultants());
    }
  }

  private void createEmptyNotificationsObjectAndSubobjects(
      com.vi.tenantservice.consultingtypeservice.generated.web.model.ConsultingTypePatchDTO
          targetDTO) {
    targetDTO.notifications(
        new com.vi.tenantservice.consultingtypeservice.generated.web.model
                .ExtendedConsultingTypeResponseDTOAllOfNotifications()
            .teamSessions(
                new com.vi.tenantservice.consultingtypeservice.generated.web.model
                        .NotificationsDTOTeamSessions()
                    .newMessage(
                        new com.vi.tenantservice.consultingtypeservice.generated.web.model
                            .TeamSessionsDTONewMessage())));
  }

  private void convertWelcomeMessage(
      ConsultingTypePatchDTO extendedSettings,
      com.vi.tenantservice.consultingtypeservice.generated.web.model.ConsultingTypePatchDTO
          targetDTO) {
    if (extendedSettings.getWelcomeMessage() != null) {
      targetDTO.setWelcomeMessage(
          new com.vi.tenantservice.consultingtypeservice.generated.web.model
              .ExtendedConsultingTypeResponseDTOAllOfWelcomeMessage());
      BeanUtils.copyProperties(extendedSettings.getWelcomeMessage(), targetDTO.getWelcomeMessage());
    }
  }
}
