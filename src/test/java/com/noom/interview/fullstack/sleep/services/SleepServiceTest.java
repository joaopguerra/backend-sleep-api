package com.noom.interview.fullstack.sleep.services;

import com.noom.interview.fullstack.sleep.domain.Sleep;
import com.noom.interview.fullstack.sleep.domain.User;
import com.noom.interview.fullstack.sleep.dto.SleepRequestDTO;
import com.noom.interview.fullstack.sleep.dto.SleepResponseDTO;
import com.noom.interview.fullstack.sleep.enums.Feeling;
import com.noom.interview.fullstack.sleep.exceptions.IncorrectDateException;
import com.noom.interview.fullstack.sleep.exceptions.SleepException;
import com.noom.interview.fullstack.sleep.exceptions.UserException;
import com.noom.interview.fullstack.sleep.repositories.SleepRepository;
import com.noom.interview.fullstack.sleep.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SleepServiceTest {

    @Mock
    private SleepRepository sleepRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private SleepService sleepService;

    @Test
    @DisplayName("Should test get all Sleeps data")
    void getAllSleepsTest() {
        List<Sleep> sleepList = createSleepList();

        when(sleepRepository.findAll()).thenReturn(sleepList);
        List<SleepResponseDTO> allSleeps = sleepService.getAllSleeps();

        assertEquals(
                allSleeps,
                sleepList.stream().map(SleepResponseDTO::toSleepDto).collect(Collectors.toList()));
        verify(sleepRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should test get a sleep by id")
    void getSleepByIdTest() {
        Sleep sleep = createSingleSleep();
        UUID id = sleep.getId();
        when(sleepRepository.findById(id)).thenReturn(Optional.of(sleep));

        Optional<SleepResponseDTO> sleepById = sleepService.getSleepById(id);

        assertNotNull(sleepById);
        assertEquals(SleepResponseDTO.toSleepDto(sleep), sleepById.get());
        verify(sleepRepository,  times(1)).findById(sleep.getId());
    }

    @Test
    @DisplayName("Should throw exception when try to get a sleep by id")
    void getSleepByIdThrowExceptionTest() {
        UUID id = UUID.randomUUID();
        when(sleepRepository.findById(id)).thenReturn(Optional.empty());

        SleepException ex = assertThrows(SleepException.class, () -> {
            sleepService.getSleepById(id);
        });
        assertEquals("Sleep not found", ex.getMessage());

    }

    @Test
    @DisplayName("Should test get a sleep by user id")
    void getSleepByUserIdTest() {
        Sleep sleep = createSingleSleep();
        User user = sleep.getUser();

        when(userRepository.findById(sleep.getUser().getId())).thenReturn(Optional.of(user));
        when(sleepRepository.findByUserId(user.getId())).thenReturn(List.of(sleep));

        List<SleepResponseDTO> sleepById = sleepService.getSleepsByUserId(user.getId());

        assertNotNull(sleepById);
        assertEquals(sleepById.size(), 1);
        verify(userRepository, times(1)).findById(user.getId());
        verify(sleepRepository, times(1)).findByUserId(user.getId());
    }

    @Test
    @DisplayName("Should throw exception when try to get a sleep by user id")
    void getSleepByUserIdThrowExceptionTest() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserException ex = assertThrows(UserException.class, () -> {
            sleepService.getSleepsByUserId(id);
        });
        assertEquals("User not found", ex.getMessage());

    }

    @Test
    @DisplayName("Should create sleep successfully")
    void createSleepSuccessfully() {
        User user = new User(UUID.randomUUID(), "Bob", "bob@email.com");
        SleepRequestDTO sleepRequestDTO = createSleepRequestDto();
        when(userRepository.findById(UUID.fromString(sleepRequestDTO.getUserId()))).thenReturn(Optional.of(user));
        when(sleepRepository.save(any(Sleep.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Sleep sleep = sleepService.createSleep(sleepRequestDTO);

        assertNotNull(sleep);
        assertEquals(user, sleep.getUser());
        assertEquals(sleepRequestDTO.getSleepDate(), sleep.getSleepDate());
        assertEquals(sleepRequestDTO.getBedTime().plus(Duration.between(sleepRequestDTO.getBedTime(), sleepRequestDTO.getWakeTime())), sleep.getTotalTimeInBed());
        assertEquals(sleepRequestDTO.getWakeTime().minus(Duration.between(sleepRequestDTO.getBedTime(), sleepRequestDTO.getWakeTime())), sleep.getTimeInBed());
        assertEquals(Feeling.GOOD, sleep.getFeeling());
    }

    @Test
    @DisplayName("Should throw UserException when user not found")
    void createSleepUserNotFound() {
        SleepRequestDTO sleepRequestDTO = createSleepRequestDto();

        when(userRepository.findById(UUID.fromString(sleepRequestDTO.getUserId()))).thenReturn(Optional.empty());

        UserException exception = assertThrows(UserException.class, () -> sleepService.createSleep(sleepRequestDTO));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IncorrectDateException when bed time is before sleep date")
    void createSleepBedTimeBeforeSleepDate() {
        SleepRequestDTO sleepRequestDTO = createSleepRequestDto();
        sleepRequestDTO.setBedTime(LocalDateTime.now().minusDays(1));

        IncorrectDateException exception = assertThrows(IncorrectDateException.class, () -> sleepService.createSleep(sleepRequestDTO));
        assertEquals("Bed time cannot be before Sleep date time", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IncorrectDateException when wake time is before bed time")
    void createSleepWakeTimeBeforeBedTime() {
        SleepRequestDTO sleepRequestDTO = createSleepRequestDto();
        sleepRequestDTO.setWakeTime(sleepRequestDTO.getBedTime().minusHours(1));

        IncorrectDateException exception = assertThrows(IncorrectDateException.class, () -> sleepService.createSleep(sleepRequestDTO));
        assertEquals("Wake time cannot be before bed time", exception.getMessage());
    }

    @Test
    @DisplayName("Should set isDeleted = true")
    void deleteSleepTest() {
        Sleep sleep = createSingleSleep();
        UUID id = sleep.getId();

        when(sleepRepository.findByIdAndIsDeleted(id, false)).thenReturn(Optional.of(sleep));
        sleepService.deleteSleep(id);

        assertTrue(sleep.isDeleted());
        verify(sleepRepository, times(1)).findByIdAndIsDeleted(id, false);
    }

    @Test
    @DisplayName("Should throw exception when try to delete sleep by id")
    void deleteSleepThrowExceptionTest() {
        Sleep sleep = createSingleSleep();
        UUID id = sleep.getId();

        when(sleepRepository.findByIdAndIsDeleted(id, false)).thenReturn(Optional.empty());
        SleepException ex = assertThrows(SleepException.class, () -> {
            sleepService.deleteSleep(id);
        });

        assertFalse(sleep.isDeleted());
        assertEquals("Sleep not found", ex.getMessage());
        verify(sleepRepository, times(1)).findByIdAndIsDeleted(sleep.getId(), false);
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

    private List<Sleep> createSleepList() {
        User user = new User(UUID.randomUUID(), "Bob", "bob@email.com");
        Sleep s1 = new Sleep(UUID.randomUUID(),
                LocalDateTime.of(2025, 5, 5, 23, 0, 0),
                LocalDateTime.of(2025, 5, 5, 22, 20, 0),
                LocalDateTime.of(2025, 5, 6, 8, 30, 0),
                Feeling.GOOD,
                user,
                false);

        Sleep s2 = new Sleep(UUID.randomUUID(),
                LocalDateTime.of(2024, 1, 3, 23, 0, 0),
                LocalDateTime.of(2024, 1, 3, 23, 0, 0),
                LocalDateTime.of(2024, 1, 1, 7, 42, 0),
                Feeling.OK,
                user,
                false);

        return List.of(s1, s2);
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
