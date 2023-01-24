package com.vi.tenantservice.api.converter;

import static org.assertj.core.api.Assertions.assertThat;

import com.vi.tenantservice.api.model.ExtendedTenantSettings;
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
class TenantExtendedSettingsConverterTest {

  @InjectMocks TenantExtendedSettingsConverter tenantExtendedSettingsConverter;

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
    ExtendedTenantSettings extendedTenantSettings =
        tenantExtendedSettingsConverter.convertExtendedTenantSettings(
            fullConsultingTypeResponseDTO);

    // then
    assertThat(extendedTenantSettings.getWelcomeMessage().getWelcomeMessageText())
        .isEqualTo("welcome");
    assertThat(extendedTenantSettings.getWelcomeMessage().getSendWelcomeMessage()).isTrue();
    assertThat(extendedTenantSettings.getIsVideoCallAllowed()).isTrue();
    assertThat(extendedTenantSettings.getLanguageFormal()).isTrue();
    assertThat(extendedTenantSettings.getSendFurtherStepsMessage()).isTrue();
    assertThat(extendedTenantSettings.getSendSaveSessionDataMessage()).isTrue();
    assertThat(
            extendedTenantSettings
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
    ExtendedTenantSettings extendedTenantSettings =
        tenantExtendedSettingsConverter.convertExtendedTenantSettings(
            fullConsultingTypeResponseDTO);

    // then
    assertThat(extendedTenantSettings.getWelcomeMessage().getWelcomeMessageText())
        .isEqualTo("welcome");
    assertThat(extendedTenantSettings.getWelcomeMessage().getSendWelcomeMessage()).isTrue();
    assertThat(extendedTenantSettings.getIsVideoCallAllowed()).isTrue();
    assertThat(extendedTenantSettings.getLanguageFormal()).isTrue();
    assertThat(extendedTenantSettings.getSendFurtherStepsMessage()).isTrue();
    assertThat(extendedTenantSettings.getSendSaveSessionDataMessage()).isTrue();
    assertThat(
            extendedTenantSettings
                .getNotifications()
                .getTeamSessions()
                .getNewMessage()
                .getAllTeamConsultants())
        .isNull();
  }
}
