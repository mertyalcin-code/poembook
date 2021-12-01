package com.poembook.poembook.repository;

import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.entities.log.PostgreSqlLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface PostgreSqlLoggerRepo extends JpaRepository<PostgreSqlLog,Long> {
List<PostgreSqlLog> findAllByLogType(String logType);
List<PostgreSqlLog> findAllByLogTimeBefore(Date logTime);
List<PostgreSqlLog> findAllByMessageContains(String text);
}
