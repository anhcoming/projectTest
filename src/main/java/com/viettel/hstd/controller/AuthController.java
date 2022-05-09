package com.viettel.hstd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.viettel.hstd.constant.VPSConstant;
import com.viettel.hstd.core.anotation.LogEndpoint;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.constant.ConstantConfig;
import com.viettel.hstd.core.dto.AccountLoginDTO;
import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.TicketDTO;
import com.viettel.hstd.core.properties.SsoProperties;
import com.viettel.hstd.core.utils.StringUtils;
import com.viettel.hstd.dto.hstd.RecruiteeAccountDTO;
import com.viettel.hstd.entity.hstd.EmployeeVhrTempEntity;
import com.viettel.hstd.entity.vps.PositionEntity;
import com.viettel.hstd.entity.vps.SysUserEntity;
import com.viettel.hstd.entity.vps.VhrFutureOrganizationEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.EmployeeVhrTempRepository;
import com.viettel.hstd.repository.hstd.RecruiteeAccountRepository;
import com.viettel.hstd.repository.vps.PositionRepository;
import com.viettel.hstd.repository.vps.SysUserRepository;
import com.viettel.hstd.repository.vps.VhrFutureOrganizationRepository;
import com.viettel.hstd.security.JwtTokenUtils;
import com.viettel.hstd.security.sso.*;
import com.viettel.hstd.security.sso.app.SSORestReponse;
import com.viettel.hstd.security.sso.app.SSOServiceUtils;
import com.viettel.hstd.service.inf.RecruiteeAccountService;
import com.viettel.hstd.util.VOConfig;
import com.viettel.security.PassTranformer;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import viettel.passport.client.UserToken;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.util.*;

import static com.viettel.hstd.constant.VPSConstant.HSTD_USER_ROLE_CODE;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication")
@Slf4j
public class AuthController {
    final
    AuthenticationManager authenticationManager;
    final
    SSoService sSoService;
    final
    SsoProperties ssoProperties;
    final
    JwtTokenUtils jwtTokenUtils;
    final
    ObjectMapper objectMapper;
    final
    Message message;
    final
    Gson gson;
    private final RecruiteeAccountService recruiteeAccountService;
    private final EmployeeVhrTempRepository employeeVhrTempRepository;

    final
    VhrFutureOrganizationRepository organizationRepository;

    private final SysUserRepository sysUserRepository;

    @Value("${app.vps.domain-code}")
    private String vpsAppCode; // Đặt riêng để tránh bị ngộp dữ liệu từ VPS

    final
    Environment environment;

    @Autowired
    private RecruiteeAccountRepository recruiteeAccountRepository;
    @Autowired
    private VOConfig voConfig;

    private final PositionRepository positionRepository;

    @Autowired
    private  VhrFutureOrganizationRepository vhrFutureOrganizationRepository;

    public AuthController(PositionRepository positionRepository, JwtTokenUtils jwtTokenUtils, AuthenticationManager authenticationManager, Environment environment, SSoService sSoService, SsoProperties ssoProperties, ObjectMapper objectMapper, Message message, Gson gson, RecruiteeAccountService recruiteeAccountService, EmployeeVhrTempRepository employeeVhrTempRepository, VhrFutureOrganizationRepository organizationRepository, SysUserRepository sysUserRepository) {
        this.positionRepository = positionRepository;
        this.jwtTokenUtils = jwtTokenUtils;
        this.authenticationManager = authenticationManager;
        this.environment = environment;
        this.sSoService = sSoService;
        this.ssoProperties = ssoProperties;
        this.objectMapper = objectMapper;
        this.message = message;
        this.gson = gson;
        this.recruiteeAccountService = recruiteeAccountService;
        this.employeeVhrTempRepository = employeeVhrTempRepository;
        this.organizationRepository = organizationRepository;
        this.sysUserRepository = sysUserRepository;
    }


    @GetMapping("/test")
    public ModelAndView test() {
        String ssoUrl = "/";
        try {
            ssoUrl = ssoProperties.getLoginUrl() + "?appCode=" + ssoProperties.getDomainCode() + "&service=" + URLEncoder.encode(ssoProperties.getService(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(ssoUrl);
        return new ModelAndView("redirect:" + ssoUrl);
    }

    @PostMapping
    @LogEndpoint(name = "Đăng nhập hệ thống")
    public BaseResponse<String> doPost(@Valid @RequestBody TicketDTO request) {
        try {
            String ticket = request.ticket;
            if (ticket == null || ticket.isEmpty())
                throw new BadCredentialsException(message.getMessage("message.unauthorized"));
            Authentication authentication = authenticationManager.authenticate(new SsoAuthentication(ticket));
            if (authentication.isAuthenticated()) SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println(gson.toJson((SSoResponse) authentication.getPrincipal()));
            String token = jwtTokenUtils.generateToken((SSoResponse) authentication.getPrincipal());
            return new BaseResponse
                    .ResponseBuilder<String>()
                    .success(token);
        } catch (Exception ex) {
            throw new BadCredentialsException(ex.getMessage());
        }
    }

    @PostMapping("/app-login")
    public BaseResponse<String> appLogin(@RequestBody AccountLoginDTO accountLogin) {
        try {
            SSoResponse sSoResponse = null;
            if (accountLogin.userName.startsWith(ConstantConfig.defaultAccountPrefix)) {
                //là tài khoản nội bộ hệ thống. select trong DB
                RecruiteeAccountDTO.RecruiteeAccountResponse recruiteeAccountResponse = recruiteeAccountService.validate(accountLogin.userName, accountLogin.password);
                if (recruiteeAccountResponse == null) {
                    return new BaseResponse
                            .ResponseBuilder<String>()
                            .failed(null, "Sai tên đăng nhập hoặc mật khẩu");
                }
                sSoResponse = new SSoResponse();
                sSoResponse.setUserID(recruiteeAccountResponse.recruiteeAccountId);
//                sSoResponse.setGrantedAuthority(createDefaultMENUForHSTDUser());
                sSoResponse.getRoleSet().add(HSTD_USER_ROLE_CODE);
                sSoResponse.setUserName(recruiteeAccountResponse.loginName);

                if (recruiteeAccountResponse.interviewSessionCvEntity != null
                        && recruiteeAccountResponse.interviewSessionCvEntity.cvEntity != null) {
                    sSoResponse.setId(recruiteeAccountResponse.interviewSessionCvEntity.interviewSessionCvId);
                    sSoResponse.setFullName(recruiteeAccountResponse.interviewSessionCvEntity.cvEntity.fullName);
                    sSoResponse.setEmail(recruiteeAccountResponse.interviewSessionCvEntity.cvEntity.email);
                    sSoResponse.setPhoneNumber(recruiteeAccountResponse.interviewSessionCvEntity.cvEntity.phoneNumber);
                    sSoResponse.setOrganizationId(recruiteeAccountResponse.interviewSessionCvEntity.interviewSessionEntity.unitId);

                    EmployeeVhrTempEntity employeeVhrTempEntity = employeeVhrTempRepository.findFirstByInterviewSessionCvId(recruiteeAccountResponse.interviewSessionCvEntity.interviewSessionCvId)
                            .orElse(null);

                    if (employeeVhrTempEntity != null) {
                        sSoResponse.setSysUserId(employeeVhrTempEntity.getEmployeeId());
                        sSoResponse.setEmployeeId(employeeVhrTempEntity.getEmployeeId());
                        sSoResponse.setEmployeeCode(employeeVhrTempEntity.getEmployeeCode());
                        sSoResponse.setEmployeeVhrTempId(employeeVhrTempEntity.getEmployeeVhrTempId());
                        sSoResponse.setPhoneNumber(employeeVhrTempEntity.getMobileNumber());
                        sSoResponse.setInterviewSessionCvId(employeeVhrTempEntity.getInterviewSessionCvId());
                        sSoResponse.setPositionId(employeeVhrTempEntity.getPositionId());
                        sSoResponse.setPositionName(employeeVhrTempEntity.getPositionName());

                        if (employeeVhrTempEntity.getOrganizationId() != null) {
                            VhrFutureOrganizationEntity organizationEntity = organizationRepository.findById(Long.parseLong(employeeVhrTempEntity.getOrganizationId()))
                                    .orElse(null);

                            if (organizationEntity.getOrgLevelManage() >= 2) {
                                String unitIdString = StringUtils.dividedPathIntoArray(organizationEntity.getPath(), VPSConstant.UNIT_ORGANIZATION_LEVEL);

                                sSoResponse.setUnitId(Long.parseLong(unitIdString));
                                sSoResponse.setUnitName(organizationEntity.getOrgNameLevel2());

                                if (organizationEntity.getOrgLevelManage() >= 3) {
                                    String departmentIdString = StringUtils.dividedPathIntoArray(organizationEntity.getPath(), VPSConstant.DEPARTMENT_ORGANIZATION_LEVEL);

                                    sSoResponse.setDepartmentId(Long.parseLong(departmentIdString));
                                    sSoResponse.setDepartmentName(organizationEntity.getOrgNameLevel3());
                                }
                            }

                        }
                    }

                }

                sSoResponse.setIsHsdtAccount(true);

                sSoResponse.setUserInfoId(recruiteeAccountResponse.recruiteeAccountId);

            } else {
                //là tài khoản nhân sự. xác thực SSO
                log.info("Đang đăng nhập bằng tài khoản SSO của Viettel");
                PassTranformer.setInputKey(voConfig.ca_encrypt_key);// set key to encrypt password voffice
                String encryptedPass = PassTranformer.encrypt(accountLogin.password);

                SSORestReponse responseRest = null;
                if (Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
                    // Fake login
                    responseRest = fakeLogin(accountLogin.userName, accountLogin.password);
                } else {
                    responseRest = SSOServiceUtils.validate(accountLogin.userName, accountLogin.password);
                }
                log.info("Tai khoan {} dang dang nhap", accountLogin.userName);

                log.info("Fill normal data");
                if (responseRest.getUserToken() != null) {
                    log.debug("userToken không null, bắt đầu fill data");
                    sSoResponse = new SSoResponse();
                    sSoResponse.setUserName(responseRest.getUserToken().getUserName());
                    sSoResponse.setFullName(responseRest.getUserToken().getFullName());
                    sSoResponse.setStaffCode(responseRest.getUserToken().getStaffCode());
                    sSoResponse.setEmployeeCode(responseRest.getUserToken().getStaffCode());
                    sSoResponse.setLoginName(responseRest.getUserToken().getUserName());
                    sSoResponse.setDepartmentId(responseRest.getUserToken().getDeptId());
                    sSoResponse.setPositionId(responseRest.getUserToken().getPositionId());
                    sSoResponse.setEncryptedPw(encryptedPass);

                    Long positionId = responseRest.getUserToken().getPositionId();
                    if (positionId != null) {
                        sSoResponse.setPositionId(positionId);
                        PositionEntity positionEntity = positionRepository.findById(positionId).orElse(null);
                        if (positionEntity != null) {
                            sSoResponse.setPositionName(positionEntity.getPositionName());
                        }
                    }

                    sSoResponse.setUserID(responseRest.getUserToken().getUserID());
                    SSORestReponse finalResponseRest = responseRest;
                    Long sysUserId = sysUserRepository.getSysUserIdByLoginName(responseRest.getUserToken().getStaffCode())
                            .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với tên đăng nhập " + finalResponseRest.getUserToken().getStaffCode()));

                    sSoResponse.setSysUserId(sysUserId);

                    log.debug("Bắt đầu fill đơn vị phòng ban, quyền,...");


//                    if (responseRest.getUserToken().getRolesList() != null) {
//                        Set<String> lstRole = new HashSet<>();
//                        for (RoleToken item : responseRest.getUserToken().getRolesList()) {
//                            lstRole.add(item.getRoleCode());
//                        }
//                        sSoResponse.setRoleSet(lstRole);
//                    }
                    sSoService.fillVPSAdditionalInfo(sSoResponse);

                    sSoResponse.setIsHsdtAccount(false);
                } else {
                    log.info("User token null");
                }
            }
            if (sSoResponse == null) {
                return new BaseResponse
                        .ResponseBuilder<String>()
                        .failed(null, "Sai tên đăng nhập hoặc mật khẩu");
            }
            //Authentication authentication = authenticationManager.authenticate(new SsoAuthentication("app_" + userName));

            //if (authentication.isAuthenticated())
            //    SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtTokenUtils.generateToken(sSoResponse);

            return new BaseResponse
                    .ResponseBuilder<String>()
                    .success(token);
        } catch (Exception e) {
            throw new BadCredentialsException(e.getMessage());
        }
    }

    private SSORestReponse fakeLogin(String loginName, String password) {
        SSORestReponse ssoRestReponse = new SSORestReponse();
        if (!password.equals("123456")) {
            throw new BadRequestException("Sai tên đăng nhập hoặc mật khẩu");
        }

        SysUserEntity sysUserEntity = sysUserRepository.findFirstByLoginName(loginName)
                .orElseThrow(() -> new BadRequestException("Sai tên đăng nhập hoặc mật khẩu"));

        ssoRestReponse.setUserToken(objectMapper.convertValue(sysUserEntity, UserToken.class));
        ssoRestReponse.getUserToken().setUserName(loginName);
        ssoRestReponse.getUserToken().setStaffCode(loginName);

        return ssoRestReponse;
    }
}
