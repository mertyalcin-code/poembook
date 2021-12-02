package com.poembook.poembook.entities.log;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class PostgreSqlLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long logId;
    private ZonedDateTime logTime;
    private String logType;
    private String message;
}
