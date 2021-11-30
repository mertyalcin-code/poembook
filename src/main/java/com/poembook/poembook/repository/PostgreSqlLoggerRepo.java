package com.poembook.poembook.repository;

import com.poembook.poembook.entities.log.PostgreSqlLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostgreSqlLoggerRepo extends JpaRepository<PostgreSqlLog,Long> {
List<PostgreSqlLog> findAllByLogType(String logType);
List<PostgreSqlLog> findAllByMessageContains(String text);
}
