package com.vi.tenantservice.api.converter;

import static org.assertj.core.api.Assertions.assertThat;

import com.vi.tenantservice.api.model.ConsultingTypePatchDTO;
import com.vi.tenantservice.consultingtypeservice.generated.web.model.FullConsultingTypeResponseDTO;
import com.vi.tenantservice.consultingtypeservice.generated.web.model.NotificationsDTO;
import com.vi.tenantservice.consultingtypeservice.generated.web.model.NotificationsDTOTeamSessions;
import com.vi.tenantservice.consultingtypeservice.generated.web.model.TeamSessionsDTONewMessage;
import com.vi.tenantservice.consultingtypeservice.generated.web.model.WelcomeMessageDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsultingTypePatchDTOConverterTest {

  @InjectMocks ConsultingTypePatchDTOConverter consultingTypePatchDTOConverter;

  @Test
  void convertExtendedTenantSettings_Should_ConvertConsultingTypeSettings() {

    // given
    FullConsultingTypeResponseDTO fullConsultingTypeResponseDTO =
        new FullConsultingTypeResponseDTO();
    fullConsultingTypeResponseDTO.languageFormal(true);
    fullConsultingTypeResponseDTO.setIsVideoCallAllowed(true);
    fullConsultingTypeResponseDTO.setWelcomeMessage(
        new WelcomeMessageDTO().sendWelcomeMessage(true).welcomeMessageText("welcome"));
    fullConsultingTypeResponseDTO.setSendFurtherStepsMessage(true);
    fullConsultingTypeResponseDTO.sendSaveSessionDataMessage(true);
    fullConsultingTypeResponseDTO.setNotifications(
        new NotificationsDTO()
            .teamSessions(
                new NotificationsDTOTeamSessions()
                    .newMessage(new TeamSessionsDTONewMessage().allTeamConsultants(true))));

    // when
    ConsultingTypePatchDTO consultingTypePatchDTO =
        consultingTypePatchDTOConverter.convertConsultingTypePatchDTO(
            fullConsultingTypeResponseDTO);

    // then
    assertThat(consultingTypePatchDTO.getWelcomeMessage().getWelcomeMessageText())
        .isEqualTo("welcome");
    assertThat(consultingTypePatchDTO.getWelcomeMessage().getSendWelcomeMessage()).isTrue();
    assertThat(consultingTypePatchDTO.getIsVideoCallAllowed()).isTrue();
    assertThat(consultingTypePatchDTO.getLanguageFormal()).isTrue();
    assertThat(consultingTypePatchDTO.getSendFurtherStepsMessage()).isTrue();
    assertThat(consultingTypePatchDTO.getSendSaveSessionDataMessage()).isTrue();
    assertThat(
            consultingTypePatchDTO
                .getNotifications()
                .getTeamSessions()
                .getNewMessage()
                .getAllTeamConsultants())
        .isTrue();
  }

  @Test
  void convertExtendedTenantSettings_Should_NotConvertNotificationsIfItsNull() {

    // given
    FullConsultingTypeResponseDTO fullConsultingTypeResponseDTO =
        new FullConsultingTypeResponseDTO();
    fullConsultingTypeResponseDTO.languageFormal(true);
    fullConsultingTypeResponseDTO.setIsVideoCallAllowed(true);
    fullConsultingTypeResponseDTO.setWelcomeMessage(
        new WelcomeMessageDTO().sendWelcomeMessage(true).welcomeMessageText("welcome"));
    fullConsultingTypeResponseDTO.setSendFurtherStepsMessage(true);
    fullConsultingTypeResponseDTO.sendSaveSessionDataMessage(true);

    // when
    ConsultingTypePatchDTO consultingTypePatchDTO =
        consultingTypePatchDTOConverter.convertConsultingTypePatchDTO(
            fullConsultingTypeResponseDTO);

    // then
    assertThat(consultingTypePatchDTO.getWelcomeMessage().getWelcomeMessageText())
        .isEqualTo("welcome");
    assertThat(consultingTypePatchDTO.getWelcomeMessage().getSendWelcomeMessage()).isTrue();
    assertThat(consultingTypePatchDTO.getIsVideoCallAllowed()).isTrue();
    assertThat(consultingTypePatchDTO.getLanguageFormal()).isTrue();
    assertThat(consultingTypePatchDTO.getSendFurtherStepsMessage()).isTrue();
    assertThat(consultingTypePatchDTO.getSendSaveSessionDataMessage()).isTrue();
    assertThat(
            consultingTypePatchDTO
                .getNotifications()
                .getTeamSessions()
                .getNewMessage()
                .getAllTeamConsultants())
        .isNull();
  }
}
