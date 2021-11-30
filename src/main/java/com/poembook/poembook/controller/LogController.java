package com.poembook.poembook.controller;

import com.poembook.poembook.business.abstracts.LoggerService;
import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.entities.log.PostgreSqlLog;
import com.poembook.poembook.entities.users.User;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/log")
@AllArgsConstructor
//@PreAuthorize("hasAuthority('superAdmin')")
public class LogController {
    LoggerService loggerService;
    @GetMapping("/logs")
    DataResult<List<PostgreSqlLog>> findAllLogs(){
        return loggerService.findAll();
    }
    @GetMapping("/types")
    DataResult<List<String>> listLogTypes(){
        return loggerService.listLogTypes();
    }
    @GetMapping("/logs/{type}")
    DataResult<List<PostgreSqlLog>> findAllByLogType(@PathVariable String type){
        return loggerService.findAllByLogType(type);
    }
}
