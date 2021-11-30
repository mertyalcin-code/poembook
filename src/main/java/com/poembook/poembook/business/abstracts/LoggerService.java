package com.poembook.poembook.business.abstracts;

import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.entities.log.PostgreSqlLog;

import java.util.List;

public interface LoggerService {
    void log(String logType,String message);
    DataResult<List<PostgreSqlLog>> findAll(); // bağımlı olduk
    DataResult<List<PostgreSqlLog>> findAllByLogType(String logType); // bağımlı olduk
    DataResult<List<PostgreSqlLog>> searchLogMessages(String text); // bağımlı olduk

    DataResult<List<String>> listLogTypes();

}
