package com.noom.interview.fullstack.sleep.domain;

import com.noom.interview.fullstack.sleep.enums.Feeling;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Sleep {

    @Id
    @GeneratedValue
    private UUID id;
    private LocalDateTime sleepDate;
    private LocalDateTime timeInBed;
    private LocalDateTime totalTimeInBed;

    @Enumerated(EnumType.STRING)
    private Feeling feeling;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

