package com.viettel.hstd.security.sso;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.config.TmpMapper;
import com.viettel.hstd.constant.VPSConstant;
import com.viettel.hstd.core.properties.SsoProperties;
import com.viettel.hstd.core.utils.ResourceReader;
import com.viettel.hstd.core.utils.StringUtils;
import com.viettel.hstd.entity.vps.EmployeeVhrEntity;
import com.viettel.hstd.entity.vps.VhrFutureOrganizationEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.exception.UnauthorizedAccessException;
import com.viettel.hstd.repository.vps.EmployeeVhrRepository;
import com.viettel.hstd.repository.vps.SysUserRepository;
import com.viettel.hstd.repository.vps.VhrFutureOrganizationRepository;
import com.viettel.hstd.security.sso.app.SSORestReponse;
import com.viettel.ktts.vps.VPSServiceWrapper;
import com.viettel.ktts.vps.VpsUserToken;
import com.viettel.vps.webservice.AuthorizedData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import viettel.passport.client.UserToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.viettel.hstd.constant.VPSConstant.HSTD_USER_ROLE_CODE;

@Service
@Slf4j
public class SSoService {
    @Autowired
    SsoProperties ssoProperties;

    @Autowired
    TmpMapper tmpMapper;

    @Autowired
    Message message;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Environment environment;

    @Autowired
    SysUserRepository sysUserRepository;

    @Autowired
    VhrFutureOrganizationRepository organizationRepository;

    @Autowired
    Gson gson;

    @Value("${app.sso.domain-code}")
    private String appCode;

    @Value("${app.vps.domain-code}")
    private String vpsAppCode; // Đặt riêng để tránh bị ngộp dữ liệu từ VPS

    public SSoResponse loadUserFromTicket(String ticket) {
        // TODO: Delete fake data
        System.out.println("Create fake data in SSOService");
        Resource resource1 = new ClassPathResource("fake_data/vpn1.json");
        Resource resource2 = new ClassPathResource("fake_data/vpn2.json");
        String fake1 = ResourceReader.asString(resource1);
        String fake2 = ResourceReader.asString(resource2);

        String json;

        if (Arrays.asList(environment.getActiveProfiles()).contains("prod")) {
            json = fake2;
        } else {
            json = fake1;
        }
        SSoResponse ssoResponseFake = new Gson().fromJson(json, SSoResponse.class);

        try {
            System.out.println("Ticket is: " + ticket);
            System.out.println("Setup service ticket and another config like domainCode, ipWan");
            String ip = "127.0.0.1";
            String ipwan = "127.0.0.1";

            ServiceTicket stValidator = new ServiceTicket();
            stValidator.setCasValidateUrl(ssoProperties.getValidateUrl());
            stValidator.setServiceTicket(ticket);
            stValidator.setService(ssoProperties.getService());
            stValidator.setDomainCode(ssoProperties.getDomainCode());
            stValidator.setIpWan(ipwan);
            System.out.println("Start to validate ticket receive from front-end");
            stValidator.validate();

            UserToken usr = stValidator.getUserToken();
            VpsUserToken vpsUserToken = getVpsUserToken(usr.getStaffCode());

            System.out.println("Day la userToken: ");
            System.out.println(gson.toJson(vpsUserToken));

            String sb = "Login|" +
                    usr.getUserName() + "|" +
                    "SUCCESS";
            System.out.println(sb);
            if (vpsUserToken != null) {
                SSoResponse data = tmpMapper.convertValue(vpsUserToken, SSoResponse.class);
                data.setUserName(usr.getUserName());
                data.setStaffCode(usr.getStaffCode());
                data.setDepartmentId(usr.getDeptId());
                data.setPositionId(usr.getPositionId());
//                data.setUserID(usr.getUserID());
                data.setUserID(usr.getUserID());
                data.setTicket(ticket);
                data.setIsActive(vpsUserToken.getAuthorizedData().getUser().getStatus() > 0);
                data.setLoginName(vpsUserToken.getAuthorizedData().getUser().getLoginName());
//                data.setGrantedAuthority(getGranted(vpsUserToken.getAuthorizedData()));

                fillVPSAdditionalInfo(data);

                return data;
            } else if (Arrays.asList(environment.getActiveProfiles()).contains("prod") || Arrays.asList(environment.getActiveProfiles()).contains("prod-real")) {
                throw new UnauthorizedAccessException(message.getMessage("message.vps_error"));
            } else {
                fillVPSAdditionalInfo(ssoResponseFake);
                return ssoResponseFake;
            }

        } catch (
                Throwable e) {
            if (Arrays.asList(environment.getActiveProfiles()).contains("prod") || Arrays.asList(environment.getActiveProfiles()).contains("prod-real")) {
                e.printStackTrace();
                throw new UnauthorizedAccessException(message.getMessage("message.vps_error"));
            } else throw new UnauthorizedAccessException(message.getMessage("message.vps_error"));
        }

    }

    @Autowired
    EmployeeVhrRepository employeeVhrRepository;

    public void fillVPSAdditionalInfo(SSoResponse data) {
        Long sysUserId = data.getSysUserId();
        System.out.println("Start getting roles of user");
        Set<String> roleSet = sysUserRepository.getRoleOfUser(sysUserId, vpsAppCode);
        if (roleSet.size() == 0) roleSet.add(HSTD_USER_ROLE_CODE);
        log.debug(gson.toJson(roleSet));
        System.out.println("Start getting permissions of role");
        Set<String> permissionSet = new HashSet<>();
        roleSet.forEach(role -> {
            permissionSet.addAll(sysUserRepository.getPermissionOfRole(role, vpsAppCode));
        });
        log.debug(gson.toJson(permissionSet));
        System.out.println("Start getting domain of role_user");
        Set<Long> domainDataSet = sysUserRepository.getAllDomainData(sysUserId, VPSConstant.PROVINCE_DOMAIN_TYPE_ID);

        EmployeeVhrEntity employeeVhrEntity = employeeVhrRepository
                .findFirstByEmployeeCode(data.getStaffCode())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhân viên"));
        data.setEmail(employeeVhrEntity.getEmail());
        data.setPhoneNumber(employeeVhrEntity.getMobileNumber());
        data.setEmployeeId(employeeVhrEntity.getEmployeeId());
        data.setEmployeeCode(employeeVhrEntity.getEmployeeCode());
        VhrFutureOrganizationEntity organizationEntity = organizationRepository
                .findById(employeeVhrEntity.getOrganizationId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phòng ban đơn vị"));

        data.setOrganizationId(employeeVhrEntity.getOrganizationId());
        String unitIdString = StringUtils.dividedPathIntoArray(organizationEntity.getPath(), VPSConstant.UNIT_ORGANIZATION_LEVEL);
        String departmentIdString = StringUtils.dividedPathIntoArray(organizationEntity.getPath(), VPSConstant.DEPARTMENT_ORGANIZATION_LEVEL);
        if (unitIdString == null || departmentIdString == null) {
            throw new NotFoundException("Không tìm thấy phòng ban đơn vị");
        }
        try {
            data.setUnitId(Long.parseLong(unitIdString));
            data.setUnitName(organizationEntity.getOrgNameLevel2());
            data.setDepartmentId(Long.parseLong(departmentIdString));
            data.setDepartmentName(organizationEntity.getOrgNameLevel3());
        } catch (NumberFormatException ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            log.error(stackTrace);
        }
        data.setRoleSet(roleSet);
        data.setPermissionSet(permissionSet);
        data.setDomainDataSet(domainDataSet);
    }

    public VpsUserToken getVpsUserToken(String staffCode) throws Throwable {
        log.debug("Đây là appCode: {}", appCode);
        log.debug("Đây là vpsAppCode: {}", vpsAppCode);
        if (staffCode != null && !staffCode.isEmpty()) {
            System.out.println("get info from viettel vps service ");
            System.out.println("VPS:call du lieu sang vps, content: vpsUrl=" + ssoProperties.getVpsServiceUrl() + ",staffCode=" + staffCode + ",domain_code=" + vpsAppCode);
            VpsUserToken data = new VpsUserToken(VPSServiceWrapper.getAuthorizedData(ssoProperties.getVpsServiceUrl(), staffCode, vpsAppCode, null));
            if ((data.getErrorCode() == null)) {
                System.out.println("Gọi dữ liệu sang VPS thành công");
                if ((data.getParentMenu() == null) || (data.getParentMenu().isEmpty())) {
                    System.out.println("Loi khi goi sang vps");
                    System.out.println("nguoi dung chua duoc phan quyen voi");

                } else {
                    System.out.println("authen code = 2");
                }
            } else {
                System.out.println("Loi khi goi sang vps");
            }
            return data;
        } else {
            log.error("Staff code invalid!");
            return null;
        }
    }

    public ArrayList<GrantedAuth> getGranted(AuthorizedData authorizedData) {
        System.out.println("D+==================================");
//        System.out.println(new Gson().toJson(authorizedData));
        if (authorizedData == null) new HashSet<>();
        assert authorizedData != null;
        return (ArrayList<GrantedAuth>) authorizedData.getGrantedMenus()
                .stream()
                .map(menuBO -> new GrantedAuth(menuBO.getCode(), menuBO.getUrl()))
                .collect(Collectors.toList());
    }
}
