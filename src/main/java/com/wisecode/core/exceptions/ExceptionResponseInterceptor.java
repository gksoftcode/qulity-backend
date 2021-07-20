package com.wisecode.core.exceptions;

import com.wisecode.core.conf.secuirty.CurrentUser;
import com.wisecode.core.entities.ExceptionLog;
import com.wisecode.core.entities.User;
import com.wisecode.core.repositories.ApplicationSetting;
import com.wisecode.core.repositories.ExceptionLogRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

@RestControllerAdvice
@Log4j2
public class ExceptionResponseInterceptor extends ResponseEntityExceptionHandler {

    @Autowired
    ExceptionLogRepository repository;
    @Autowired
    ApplicationSetting setting;

    @ExceptionHandler
    public ResponseEntity<ExceptionLog> handleException(@CurrentUser User usr, Exception exception, HttpServletRequest request ) {
        ExceptionLog exceptionLog= buildExceptionEntity(exception,request,usr);
        if(setting.getLogEnable()) {
            repository.save(exceptionLog);
        }
        return new ResponseEntity<>(exceptionLog, HttpStatus.BAD_REQUEST);
    }
    private ExceptionLog buildExceptionEntity(Exception exception, HttpServletRequest request, User usr) {
        ExceptionLog exceptionLogEntity= new ExceptionLog();
        exceptionLogEntity.setResponseStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        exceptionLogEntity.setExceptionName(exception.getClass().getCanonicalName());
        exceptionLogEntity.setMessage(exception.getMessage());
        exceptionLogEntity.setUserName(usr!=null?usr.getUsername():"");
        exceptionLogEntity.setUserNumber(usr!=null?usr.getId():-1);
        exceptionLogEntity.setServiceUrl(request.getRequestURI());
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        exceptionLogEntity.setLogTrace(sw.toString());
        return exceptionLogEntity;
    }

}