package com.vi.tenantservice.api.converter;

import static org.assertj.core.api.Assertions.assertThat;

import com.vi.tenantservice.api.model.ConsultingTypePatchDTO;
import com.vi.tenantservice.consultingtypeservice.generated.web.model.ExtendedConsultingTypeResponseDTOAllOfNotifications;
import com.vi.tenantservice.consultingtypeservice.generated.web.model.ExtendedConsultingTypeResponseDTOAllOfWelcomeMessage;
import com.vi.tenantservice.consultingtypeservice.generated.web.model.FullConsultingTypeResponseDTO;
import com.vi.tenantservice.consultingtypeservice.generated.web.model.NotificationsDTOTeamSessions;
import com.vi.tenantservice.consultingtypeservice.generated.web.model.TeamSessionsDTONewMessage;
import org.jeasy.random.EasyRandom;
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
        new ExtendedConsultingTypeResponseDTOAllOfWelcomeMessage()
            .sendWelcomeMessage(true)
            .welcomeMessageText("welcome"));
    fullConsultingTypeResponseDTO.setSendFurtherStepsMessage(true);
    fullConsultingTypeResponseDTO.sendSaveSessionDataMessage(true);
    fullConsultingTypeResponseDTO.setNotifications(
        new ExtendedConsultingTypeResponseDTOAllOfNotifications()
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
        new ExtendedConsultingTypeResponseDTOAllOfWelcomeMessage()
            .sendWelcomeMessage(true)
            .welcomeMessageText("welcome"));
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

  @Test
  void convertToConsultingTypeServiceModel_Should_ConvertAllSettings() {

    // given
    ConsultingTypePatchDTO source = new EasyRandom().nextObject(ConsultingTypePatchDTO.class);

    // when
    com.vi.tenantservice.consultingtypeservice.generated.web.model.ConsultingTypePatchDTO target =
        consultingTypePatchDTOConverter.convertToConsultingTypeServiceModel(source);

    // then
    assertThat(target.getLanguageFormal()).isEqualTo(source.getLanguageFormal());
    assertThat(target.getWelcomeMessage().getSendWelcomeMessage())
        .isEqualTo(source.getWelcomeMessage().getSendWelcomeMessage());
    assertThat(target.getIsVideoCallAllowed()).isEqualTo(source.getIsVideoCallAllowed());
    assertThat(target.getSendFurtherStepsMessage()).isEqualTo(source.getSendFurtherStepsMessage());
    assertThat(target.getSendSaveSessionDataMessage())
        .isEqualTo(source.getSendSaveSessionDataMessage());
  }
}
