package com.poembook.poembook.business.abstracts;

import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.entities.log.Log;

import java.util.List;

public interface LoggerService {
    void log(String logType, String message);

    DataResult<List<Log>> findAll(); // bağımlı olduk

    DataResult<List<Log>> findAllByLogType(String logType); // bağımlı olduk

    Result deleteAllLogs();

    Result deleteLogsByLogType(String logType);

    Result deleteAllLogsExceptThisWeek();

    DataResult<List<String>> listLogTypes();

}
