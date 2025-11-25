package org.ayu.doyouknowback.fcm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ayu.doyouknowback.domain.fcm.domain.Fcm;
import org.ayu.doyouknowback.domain.fcm.form.FcmTokenRequestDTO;
import org.ayu.doyouknowback.domain.fcm.repository.FcmRepository;
import org.ayu.doyouknowback.domain.fcm.service.FcmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FcmService 단위 테스트")
class FcmServiceTest {

    @Mock
    private FcmRepository fcmRepository;

    @Spy
    private RestTemplate restTemplate;

    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    private FcmService fcmService;

    private Fcm testFcm;
    private FcmTokenRequestDTO testTokenRequestDTO;

    @BeforeEach
    void setUp() {
        testFcm = Fcm.builder()
                .id(1L)
                .token("ExponentPushToken[test-token-123]")
                .platform("android")
                .build();

        testTokenRequestDTO = FcmTokenRequestDTO.builder()
                .token("ExponentPushToken[test-token-123]")
                .platform("android")
                .build();

        // RestTemplate과 ObjectMapper를 리플렉션으로 주입
        ReflectionTestUtils.setField(fcmService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(fcmService, "objectMapper", objectMapper);
    }

    @Test
    @DisplayName("FCM 토큰 저장 - 새로운 토큰")
    void saveToken_ShouldSaveNewToken() {
        // Given
        when(fcmRepository.findByToken(testTokenRequestDTO.getToken()))
                .thenReturn(Optional.empty());

        // When
        fcmService.saveToken(testTokenRequestDTO);

        // Then
        verify(fcmRepository, times(1)).findByToken(testTokenRequestDTO.getToken());
        verify(fcmRepository, times(1)).save(any(Fcm.class));
    }

    @Test
    @DisplayName("FCM 토큰 저장 - 이미 존재하는 토큰은 저장하지 않음")
    void saveToken_ShouldNotSave_WhenTokenAlreadyExists() {
        // Given
        when(fcmRepository.findByToken(testTokenRequestDTO.getToken()))
                .thenReturn(Optional.of(testFcm));

        // When
        fcmService.saveToken(testTokenRequestDTO);

        // Then
        verify(fcmRepository, times(1)).findByToken(testTokenRequestDTO.getToken());
        verify(fcmRepository, never()).save(any(Fcm.class));
    }

    @Test
    @DisplayName("푸시 알림 전송 - 토큰 목록이 비어있을 때")
    void sendNotificationToAllExpo_ShouldNotSend_WhenNoTokens() {
        // Given
        when(fcmRepository.findAll()).thenReturn(Arrays.asList());

        // When
        fcmService.sendNotificationToAllExpo("제목", "내용");

        // Then
        verify(fcmRepository, times(1)).findAll();
        verify(restTemplate, never()).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    @DisplayName("푸시 알림 전송 - 성공 케이스")
    void sendNotificationToAllExpo_ShouldSendSuccessfully() throws Exception {
        // Given
        List<Fcm> fcmList = Arrays.asList(
                Fcm.builder().id(1L).token("ExponentPushToken[token1]").platform("android").build(),
                Fcm.builder().id(2L).token("ExponentPushToken[token2]").platform("ios").build());

        when(fcmRepository.findAll()).thenReturn(fcmList);

        // Expo API 응답 mock
        String mockResponse = """
                {
                    "data": [
                        {"status": "ok"},
                        {"status": "ok"}
                    ]
                }
                """;

        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponseEntity);

        // When
        fcmService.sendNotificationToAllExpo("테스트 제목", "테스트 내용");

        // Then
        verify(fcmRepository, times(1)).findAll();
        verify(restTemplate, times(1))
                .postForEntity(eq("https://exp.host/--/api/v2/push/send"), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("푸시 알림 전송 - 실패한 토큰 삭제")
    void sendNotificationToAllExpo_ShouldDeleteFailedToken() throws Exception {
        // Given
        List<Fcm> fcmList = Arrays.asList(
                Fcm.builder().id(1L).token("ExponentPushToken[invalid-token]").platform("android").build());

        when(fcmRepository.findAll()).thenReturn(fcmList);

        // Expo API 응답 mock (실패 케이스)
        String mockResponse = """
                {
                    "data": [
                        {
                            "status": "error",
                            "details": {
                                "error": "DeviceNotRegistered",
                                "expoPushToken": "ExponentPushToken[invalid-token]"
                            }
                        }
                    ]
                }
                """;

        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponseEntity);

        // When
        fcmService.sendNotificationToAllExpo("테스트 제목", "테스트 내용");

        // Then
        verify(fcmRepository, times(1)).findAll();
        verify(fcmRepository, times(1)).deleteByToken("ExponentPushToken[invalid-token]");
    }

    @Test
    @DisplayName("푸시 알림 전송 - 100개씩 배치 처리")
    void sendNotificationToAllExpo_ShouldSendInBatches() throws Exception {
        // Given - 150개의 토큰 생성 (2개의 배치로 나뉨)
        List<Fcm> fcmList = new java.util.ArrayList<>();
        for (int i = 1; i <= 150; i++) {
            fcmList.add(Fcm.builder()
                    .id((long) i)
                    .token("ExponentPushToken[token" + i + "]")
                    .platform("android")
                    .build());
        }

        when(fcmRepository.findAll()).thenReturn(fcmList);

        // Expo API 응답 mock
        String mockResponse = createMockResponseWithOkStatus(100);
        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponseEntity);

        // When
        fcmService.sendNotificationToAllExpo("테스트 제목", "테스트 내용");

        // Then
        verify(fcmRepository, times(1)).findAll();
        // 150개 토큰 = 100개 배치 + 50개 배치 = 총 2번 호출
        verify(restTemplate, times(2))
                .postForEntity(eq("https://exp.host/--/api/v2/push/send"), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("FCM 토큰 조회 - 존재하는 토큰")
    void findByToken_ShouldReturnToken_WhenExists() {
        // Given
        String token = "ExponentPushToken[test-token-123]";
        when(fcmRepository.findByToken(token)).thenReturn(Optional.of(testFcm));

        // When
        Optional<Fcm> result = fcmRepository.findByToken(token);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getToken()).isEqualTo(token);
        verify(fcmRepository, times(1)).findByToken(token);
    }

    @Test
    @DisplayName("FCM 토큰 조회 - 존재하지 않는 토큰")
    void findByToken_ShouldReturnEmpty_WhenNotExists() {
        // Given
        String token = "ExponentPushToken[non-existent-token]";
        when(fcmRepository.findByToken(token)).thenReturn(Optional.empty());

        // When
        Optional<Fcm> result = fcmRepository.findByToken(token);

        // Then
        assertThat(result).isEmpty();
        verify(fcmRepository, times(1)).findByToken(token);
    }

    // Helper method to create mock response
    private String createMockResponseWithOkStatus(int count) {
        StringBuilder sb = new StringBuilder("{\"data\": [");
        for (int i = 0; i < count; i++) {
            if (i > 0)
                sb.append(",");
            sb.append("{\"status\": \"ok\"}");
        }
        sb.append("]}");
        return sb.toString();
    }
}
