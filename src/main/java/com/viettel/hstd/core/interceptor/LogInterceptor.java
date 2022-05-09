package com.viettel.hstd.core.interceptor;

import com.viettel.hstd.core.properties.AppProperties;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.imp.LoggerServiceImp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
@Slf4j
public class LogInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    LoggerServiceImp loggerServiceImp;
    @Autowired
    AppProperties appProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();
        LocalDateTime newStartTime = LocalDateTime.now();
        log.trace("\n-------- LogInterception.preHandle --- ");
        log.trace("Request Id: " + requestId);
        log.trace("Request URL: " + request.getRequestURL());
//        log.trace("Start Time: " + System.currentTimeMillis());
        log.trace("New Start Time: " + newStartTime.toString());
//        request.setAttribute("startTime", startTime);
        request.setAttribute("newStartTime", newStartTime);
        request.setAttribute("requestId", requestId);
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.trace("\n-------- LogInterception.afterCompletion --- ");
//        long startTime = (Long) request.getAttribute("startTime");
        LocalDateTime newStartTime = (LocalDateTime) request.getAttribute("newStartTime");
        String requestId = (String) request.getAttribute("requestId");
        log.trace("Request ID " + requestId);
//        long endTime = System.currentTimeMillis();
        LocalDateTime newEndTime = LocalDateTime.now();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName;
        Long userId;
        if (authentication.getPrincipal() == "anonymousUser") {
            userName = "anonymousUser";
            userId = 0L;
            log.trace("User Name " + userName);
            log.trace("User ID " + userId);
            log.trace("Department Id " + "anonymous Department");
        } else {
            SSoResponse sso = (SSoResponse) authentication.getPrincipal();
            userName = sso.getFullName();
            userId = sso.getSysUserId();
            log.trace("User Name " + sso.getFullName());
            log.trace("User ID " + sso.getSysUserId());
            log.trace("Department Id " + sso.getDepartmentId());
        }
        String descriptionEndpoint = "";
        String descriptionCode = "";

        log.trace("function Name: " + descriptionEndpoint);
        log.trace("function Code: " + descriptionCode);
        log.trace("Request URL: " + request.getRequestURL());
        log.trace("Request PATH " + request.getServletPath());
        log.trace("Request METHOD " + request.getMethod());
//        log.trace("Start Time: " + startTime);
        log.trace("New Start Time: " + newStartTime.toString());
//        log.trace("End Time: " + endTime);
        log.trace("New End Time: " + newEndTime.toString());
//        log.trace("Time Taken: " + (endTime - startTime));
        log.trace("New Time Taken: " + newStartTime.until(newEndTime, ChronoUnit.MILLIS) + " ms");
        super.afterCompletion(request, response, handler, ex);
    }
}
