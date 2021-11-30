package com.poembook.poembook.business.concretes;

import com.poembook.poembook.business.abstracts.LoggerService;
import com.poembook.poembook.constant.enumaration.Log;
import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.ErrorDataResult;
import com.poembook.poembook.core.utilities.result.SuccessDataResult;
import com.poembook.poembook.entities.log.PostgreSqlLog;
import com.poembook.poembook.repository.PostgreSqlLoggerRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class PostgreSqlLogger implements LoggerService {
    private PostgreSqlLoggerRepo postgreSqlLoggerRepo;
    @Override
    public void log(String logType, String message) {
        PostgreSqlLog postgreSqlLog = new PostgreSqlLog();
        postgreSqlLog.setLogType(logType);
        postgreSqlLog.setMessage(message);
        postgreSqlLog.setLogTime(new Date());
        postgreSqlLoggerRepo.save(postgreSqlLog);
    }

    @Override
    public DataResult<List<PostgreSqlLog>> findAll() {
        List<PostgreSqlLog> logs = postgreSqlLoggerRepo.findAll();
        if(logs.size()<1){
            return new ErrorDataResult<>(" Log bulunamadı");
        }
        return new SuccessDataResult<>(logs,"Loglar listelendi");
    }

    @Override
    public DataResult<List<PostgreSqlLog>> findAllByLogType(String logType) {
        List<PostgreSqlLog> logs = postgreSqlLoggerRepo.findAllByLogType(logType);
        if(logs==null){
            return new ErrorDataResult<>(" Log bulunamadı");
        }
    return new SuccessDataResult<>(logs,"Loglar listelendi");
    }

    @Override
    public DataResult<List<PostgreSqlLog>> searchLogMessages(String text) {
        List<PostgreSqlLog> logs = postgreSqlLoggerRepo.findAllByMessageContains(text);
        if(logs==null){
            return new ErrorDataResult<>(" Log bulunamadı");
        }
        return new SuccessDataResult<>(logs);
    }

    @Override
    public DataResult<List<String>> listLogTypes() {
        List<String> types = new ArrayList<>();
     for(Log type :Log.class.getEnumConstants()){
         types.add(type.toString());
     }

            return new SuccessDataResult<>(types,"listed");
    }
}
