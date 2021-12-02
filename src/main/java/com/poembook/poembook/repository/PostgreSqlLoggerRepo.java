package com.poembook.poembook.repository;

import com.poembook.poembook.entities.log.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface PostgreSqlLoggerRepo extends JpaRepository<Log, Long> {
    List<Log> findAllByLogType(String logType);

    List<Log> findAllByLogTimeBefore(Date logTime);

    List<Log> findAllByMessageContains(String text);
}
