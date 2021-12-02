package com.poembook.poembook.business.concretes;

import com.poembook.poembook.business.abstracts.LoggerService;
import com.poembook.poembook.core.utilities.result.*;
import com.poembook.poembook.entities.log.Log;
import com.poembook.poembook.repository.PostgreSqlLoggerRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.poembook.poembook.constant.LoggerConstant.*;

@Service
@AllArgsConstructor
public class PostgreSqlLogger implements LoggerService {
    private PostgreSqlLoggerRepo postgreSqlLoggerRepo;

    @Override
    public void log(String logType, String message) {
        Log log = new Log();
        log.setLogType(logType);
        log.setMessage(message);
        log.setLogTime(LocalDateTime.now().atZone(ZoneId.of("UTC")));
        postgreSqlLoggerRepo.save(log);
    }

    @Override
    public DataResult<List<Log>> findAll() {
        List<Log> logs = postgreSqlLoggerRepo.findAll();
        if (logs.size() < 1) {
            return new ErrorDataResult<>(LOG_NOT_FOUND);
        }
        return new SuccessDataResult<>(logs, LOG_LISTED);
    }

    @Override
    public DataResult<List<Log>> findAllByLogType(String logType) {
        List<Log> logs = postgreSqlLoggerRepo.findAllByLogType(logType);
        if (logs == null) {
            return new ErrorDataResult<>(LOG_NOT_FOUND);
        }
        return new SuccessDataResult<>(logs, LOG_LISTED);
    }

    @Override
    public Result deleteAllLogs() {
        postgreSqlLoggerRepo.deleteAll();
        return new SuccessResult(ALL_LOGS_DELETED);
    }

    @Override
    public Result deleteLogsByLogType(String logType) {
        postgreSqlLoggerRepo.deleteAll(postgreSqlLoggerRepo.findAllByLogType(logType));
        return new SuccessResult(LOGS_DELETED_BY_TYPE);
    }

    @Override
    public Result deleteAllLogsExceptThisWeek() {
        Date date = Date.from((LocalDate.now().minusDays(7)).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        postgreSqlLoggerRepo.deleteAll(postgreSqlLoggerRepo.findAllByLogTimeBefore(date));
        return new SuccessResult(LOGS_DELETED_EXCEPT_THIS_WEEK);
    }

    @Override
    public DataResult<List<String>> listLogTypes() {
        List<String> types = new ArrayList<>();
        for (com.poembook.poembook.constant.enumaration.Log type : com.poembook.poembook.constant.enumaration.Log.class.getEnumConstants()) {
            types.add(type.toString());
        }

        return new SuccessDataResult<>(types, LOG_LISTED);
    }
}
