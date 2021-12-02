package com.poembook.poembook.controller;

import com.poembook.poembook.business.abstracts.LoggerService;
import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.entities.log.Log;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/log")
@AllArgsConstructor
@PreAuthorize("hasAuthority('superAdmin')")
public class LogController {
    LoggerService loggerService;

    @GetMapping("/logs")
    DataResult<List<Log>> findAllLogs() {
        return loggerService.findAll();
    }

    @GetMapping("/types")
    DataResult<List<String>> listLogTypes() {
        return loggerService.listLogTypes();
    }

    @GetMapping("/logs/{type}")
    DataResult<List<Log>> findAllByLogType(@PathVariable String type) {
        return loggerService.findAllByLogType(type);
    }

    @GetMapping("/delete-all")
    Result deleteAllLogs() {
        return loggerService.deleteAllLogs();
    }

    @GetMapping("/delete-type/{type}")
    Result deleteByType(@PathVariable String type) {
        return loggerService.deleteLogsByLogType(type);
    }

    @GetMapping("/delete-except-this-week")
    Result deleteAllLogsExceptThisWeek() {
        return loggerService.deleteAllLogsExceptThisWeek();
    }
}
