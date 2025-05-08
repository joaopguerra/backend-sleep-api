package com.noom.interview.fullstack.sleep.controlllers;

import com.noom.interview.fullstack.sleep.controllers.SleepController;
import com.noom.interview.fullstack.sleep.domain.Sleep;
import com.noom.interview.fullstack.sleep.domain.User;
import com.noom.interview.fullstack.sleep.dto.SleepAverageResponse;
import com.noom.interview.fullstack.sleep.dto.SleepRequestDTO;
import com.noom.interview.fullstack.sleep.dto.SleepResponseDTO;
import com.noom.interview.fullstack.sleep.enums.Feeling;
import com.noom.interview.fullstack.sleep.services.SleepService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SleepControllerTest {
    @Mock
    private SleepService sleepService;

    @InjectMocks
    private SleepController sleepController;

    @Test
    @DisplayName("Should test controller get all sleeps")
    void getSleepsControllerTest() {
        List<SleepResponseDTO> sleeps = new ArrayList<>();
        when(sleepService.getAllSleeps()).thenReturn(sleeps);

        ResponseEntity<List<SleepResponseDTO>> response = sleepController.getSleeps();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sleeps, response.getBody());
        verify(sleepService, times(1)).getAllSleeps();
    }

    @Test
    @DisplayName("Should test controller get all sleeps by id")
    void getSleepByIdControllerTest() {
        Sleep sleep = createSingleSleep();
        UUID sleepId = sleep.getId();
        SleepResponseDTO sleepResponseDTO = new SleepResponseDTO();
        when(sleepService.getSleepById(sleepId)).thenReturn(Optional.of(sleepResponseDTO));

        ResponseEntity<SleepResponseDTO> response = sleepController.getSleepById(sleepId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sleepResponseDTO, response.getBody());
        verify(sleepService, times(1)).getSleepById(sleepId);
    }

    @Test
    @DisplayName("Should test controller get all sleeps by userId")
    void getSleepByUserIdControllerTest() {
        Sleep sleep = createSingleSleep();
        UUID userId = sleep.getUser().getId();
        SleepResponseDTO sleepResponseDTO = SleepResponseDTO.toSleepDto(sleep);
        when(sleepService.getSleepsByUserId(userId)).thenReturn(List.of(sleepResponseDTO));

        ResponseEntity<List<SleepResponseDTO>> response = sleepController.getSleepsByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(sleepResponseDTO), response.getBody());
        verify(sleepService, times(1)).getSleepsByUserId(userId);
    }

    @Test
    @DisplayName("Should test controller to create sleep")
    void createSleepControllerTest() {
        Sleep sleep = createSingleSleep();
        SleepRequestDTO sleepRequestDTO = createSleepRequestDto();
        when(sleepService.createSleep(sleepRequestDTO)).thenReturn(sleep);

        ResponseEntity<Sleep> response = sleepController.createSleep(sleepRequestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(sleep, response.getBody());
        verify(sleepService, times(1)).createSleep(sleepRequestDTO);
    }

    @Test
    @DisplayName("Should test controller to delete sleep")
    void deleteSleepControllerTest() {
        Sleep sleep = createSingleSleep();
        UUID sleepId = sleep.getId();
        doNothing().when(sleepService).deleteSleep(sleepId);

        ResponseEntity<Sleep> response = sleepController.deleteSleep(sleepId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(sleepService, times(1)).deleteSleep(sleepId);
    }

    @Test
    @DisplayName("Should test controller to get all sleeps times average")
    void getAverageSleepControllerTest() {
        UUID userId = UUID.randomUUID();
        SleepAverageResponse sleepAverageResponse = new SleepAverageResponse();

        when(sleepService.getAverageSleep("2025-05-01", "2025-05-31", userId)).thenReturn(sleepAverageResponse);

        ResponseEntity<SleepAverageResponse> response = sleepController.getAverageSleep("2025-05-01", "2025-05-31", userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sleepAverageResponse, response.getBody());
        verify(sleepService, times(1)).getAverageSleep("2025-05-01", "2025-05-31", userId);
    }

    private Sleep createSingleSleep() {
        User user = new User(UUID.randomUUID(), "Alice", "alice@email.com");
        return new Sleep(UUID.randomUUID(),
                LocalDateTime.of(2025, 5, 5, 23, 0, 0),
                LocalDateTime.of(2025, 5, 5, 22, 20, 0),
                LocalDateTime.of(2025, 5, 6, 8, 30, 0),
                Feeling.GOOD,
                user,
                false);
    }

    private SleepRequestDTO createSleepRequestDto() {
        User user = new User(UUID.randomUUID(), "Bob", "bob@email.com");
        user.setId(UUID.randomUUID());

        SleepRequestDTO sleepRequestDTO = new SleepRequestDTO();
        sleepRequestDTO.setUserId(user.getId().toString());
        sleepRequestDTO.setSleepDate(LocalDateTime.now());
        sleepRequestDTO.setBedTime(LocalDateTime.now());
        sleepRequestDTO.setWakeTime(LocalDateTime.now().plusHours(8));
        sleepRequestDTO.setFeeling("GOOD");

        return sleepRequestDTO;
    }
}
