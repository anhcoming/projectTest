package com.viettel.hstd.service.imp;

import com.viettel.hstd.entity.hstd.SysLogEntity;
import com.viettel.hstd.security.AuthenticationFacade;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;


public class BaseService {
    @Autowired
    private SysLogService sysLogService;

    @Autowired
    AuthenticationFacade authenticationFacade;

    protected void addLog(String tableName, String content, String data) {
        SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
        String workstation = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getRemoteAddr();
        SysLogEntity entity = new SysLogEntity();
        entity.setTableName(tableName);
        entity.setWorkStation(workstation);
        entity.setContent(content);
        entity.setData(data);
        entity.setSysUserId(sSoResponse.getSysUserId());
        if (sSoResponse != null) {
            entity.setFullName(sSoResponse.getFullName());
            entity.setLoginName(sSoResponse.getLoginName());
        }
        sysLogService.create(entity);
    }

    protected void addLog(String content) {
        Class<?> enclosingClass = getClass().getEnclosingClass();
        String fullClassName = "";
        if (enclosingClass != null) {
            fullClassName = enclosingClass.getName();
        } else {
            fullClassName = getClass().getName();
        }

        String onlyClassName = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);

        SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
        String workstation = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getRemoteAddr();
        SysLogEntity entity = new SysLogEntity();
        entity.setTableName(onlyClassName);
        entity.setWorkStation(workstation);
        entity.setContent(content);
        entity.setSysUserId(sSoResponse.getSysUserId());
        if (sSoResponse != null) {
            entity.setFullName(sSoResponse.getFullName());
            entity.setLoginName(sSoResponse.getLoginName());
        }

        sysLogService.create(entity);
    }
}
