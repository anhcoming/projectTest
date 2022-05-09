package com.viettel.hstd.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.repository.vps.SysUserRepository;
import com.viettel.hstd.security.AuthenticationFacade;
import com.viettel.hstd.service.inf.EmployeeVhrTempService;
import com.viettel.hstd.service.inf.EmployeeVhrService;
import com.viettel.ktts.vps.VpsMenu;
import com.viettel.hstd.core.config.TmpMapper;
import com.viettel.hstd.core.properties.SsoProperties;
import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.dto.vps.SysUserDTO;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.security.sso.SSoService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.viettel.hstd.dto.hstd.EmployeeVhrTempDTO.*;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    SSoService ssoService;
    @Autowired
    SsoProperties ssoProperties;
    @Autowired
    TmpMapper tmpMapper;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private EmployeeVhrTempService employeeVhrTempService;
    @Autowired
    private EmployeeVhrService employeeVhrService;

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    SysUserRepository sysUserRepository;

    @Value("${app.vps.domain-code}")
    private String vpsAppCode; // Đặt riêng để tránh bị ngộp dữ liệu từ VPS

    @PostMapping
    public BaseResponse<?> index() {
        SSoResponse ssoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
        SysUserDTO.UserResponse response = tmpMapper.convertValue(ssoResponse, SysUserDTO.UserResponse.class);
        if (ssoResponse.getGrantedAuthority() == null) ssoResponse.setGrantedAuthority(new ArrayList<>());

        response.permissions = ssoResponse
                .getGrantedAuthority()
                .stream()
                .map(item -> item.authority)
                .collect(Collectors.toSet());
//        response.permissionPages = ssoResponse
//                .getGrantedAuthority()
//                .stream()
//                .filter(item -> !StringUtils.isEmpty(item.url))
//                .map(item -> item.url)
//                .collect(Collectors.toSet());
        response.permissionPages = sysUserRepository.getAllMenuUrlsBySysRoleIn(vpsAppCode, ssoResponse.getRoleSet());
        response.roles = ssoResponse.getRoleSet();

        return new BaseResponse.ResponseBuilder<>().success(response);
    }

    @GetMapping("/vps-menu")
    public BaseResponse<?> getVpsMenu(@Parameter(hidden = true) @AuthenticationPrincipal SSoResponse ssoResponse) throws Throwable {
        List<VpsMenu> vpsMenus = ssoService
                .getVpsUserToken(ssoResponse.getStaffCode())
                .getParentMenu();
        return new BaseResponse.ResponseBuilder<>().success(vpsMenus);
    }

    @GetMapping("/get-profile")
    public BaseResponse<EmployeeVhrTempResponse> getEmailContent() {
        SSoResponse ssoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
        EmployeeVhrTempResponse response = null;
        if (ssoResponse.getIsHsdtAccount() != null && ssoResponse.getIsHsdtAccount()) {
            response = employeeVhrTempService.getProfile();
        } else {
            response = employeeVhrService.getProfile();
        }
        return new BaseResponse
                .ResponseBuilder<EmployeeVhrTempResponse>()
                .success(response);
    }
}
