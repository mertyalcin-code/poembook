package com.poembook.poembook.entities.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.poembook.poembook.constant.HttpResponseConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static com.poembook.poembook.constant.HttpResponseConstant.HTTP_RESPONSE_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@SuperBuilder
public class HttpResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = HTTP_RESPONSE_PATTERN, timezone = HttpResponseConstant.HTTP_RESPONSE_TIME_ZONE)
    private ZonedDateTime timeStamp;
    private int httpStatusCode;
    private HttpStatus httpStatus;
    private String reason;
    private String message;

    public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
        this.timeStamp = LocalDateTime.now().atZone(ZoneId.of("UTC+3"));
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.reason = reason;
        this.message = message;
    }


}
