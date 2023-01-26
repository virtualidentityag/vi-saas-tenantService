package com.vi.tenantservice.api.converter;

import com.vi.tenantservice.api.model.ConsultingTypePatchDTO;
import com.vi.tenantservice.api.model.ConsultingTypePatchDTONotifications;
import com.vi.tenantservice.api.model.ConsultingTypePatchDTOWelcomeMessage;
import com.vi.tenantservice.api.model.NotificationsDTOTeamSessions;
import com.vi.tenantservice.api.model.TeamSessionsDTONewMessage;
import com.vi.tenantservice.consultingtypeservice.generated.web.model.FullConsultingTypeResponseDTO;
import com.vi.tenantservice.consultingtypeservice.generated.web.model.NotificationsDTO;
import lombok.extern.slf4j.Slf4j;
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
    NotificationsDTO notifications = consultingTypeResponseDTO.getNotifications();
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
}
