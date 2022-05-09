package com.viettel.hstd.service.imp;


import com.viettel.hstd.constant.OtpType;
import com.viettel.hstd.dto.hstd.OtpDTO;
import com.viettel.hstd.entity.hstd.OtpEntity;

import com.viettel.hstd.entity.hstd.SysConfigEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.OtpRepository;
import com.viettel.hstd.repository.hstd.SysConfigRepository;
import com.viettel.hstd.security.AuthenticationFacade;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.OtpService;
import com.viettel.hstd.service.inf.SmsMessageService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import static com.viettel.hstd.constant.HSDTConstant.SYS_CONFIG_OTP_DURATION_CODE;

@Service
public class OtpServiceImp extends BaseService implements OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private SmsMessageService smsMessageService;

    @Autowired
    private SysConfigRepository sysConfigRepository;

    @Override
    public OtpDTO.OtpCreateResponse create(OtpDTO.OtpCreateRequest request) {
        SSoResponse soResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
        OtpDTO.OtpCreateResponse createResponse = new OtpDTO.OtpCreateResponse();

        OtpEntity otpEntity = new OtpEntity();
        otpEntity.setOtpCode(createOtp2());
        otpEntity.setName(request.content);
        otpEntity.setOtpType(request.otpType);
        otpEntity.setContent("OTP tạo bởi " + soResponse.getFullName() + " (" + soResponse.getEmployeeCode() + ") ");

        SysConfigEntity sysConfigEntity = sysConfigRepository.findFirstByConfigKey(SYS_CONFIG_OTP_DURATION_CODE).orElse(null);
        Long duration = 90L;

        if (sysConfigEntity != null) {
            try {
                duration = Long.parseLong(sysConfigEntity.getConfigValue());
            } catch (Exception ex) {
                duration = 90L;
            }
        } else {
            duration = 90L;
        }

        otpEntity.setDurtaion(duration);
        otpEntity = otpRepository.save(otpEntity);
        createResponse.otpId = otpEntity.getOtpId();
        createResponse.duration = otpEntity.getDurtaion();

        if (soResponse.getPhoneNumber() == null) throw new NotFoundException("Tài khoản này chưa có số điện thoại");
        String message = "Ma OTP cua ban la " + otpEntity.getOtpCode() + ". Vui long khong cung cap cho bat ky ai ma OTP nay.";
        switch (request.otpType) {
            case PROBATIONARY_CONTRACT: {
                message = "TCT CP Cong trinh Viettel thong bao: D/c vao app Ho so dien tu de ky hop dong Thu viec va nhap ma OTP " + otpEntity.getOtpCode() + " xac nhan dong y. Tran trong!";
            break;
            }
            case LABOR_CONTRACT: {
                message = "TCT CP Cong trinh Viettel thong bao: D/c vao app Ho so dien tu de ky hop dong lao dong va nhap ma OTP " + otpEntity.getOtpCode() + " xac nhan dong y. Tran trong!";
            break;
            }
            case TERMINATE_CONTRACT: {
                message = "TCT CP Cong trinh Viettel thong bao: D/c vao app Ho so dien tu de tao don xin nghi viec va nhap ma OTP " + otpEntity.getOtpCode() + " xac nhan dong y. Tran trong!";
            break;
            }
        }
        smsMessageService.sendSmsMessage(soResponse.getPhoneNumber(), message);

        return createResponse;
    }

    @Override
    public OtpDTO.OtpSubmitResponse submit(OtpDTO.OtpRequest request) {
        OtpDTO.OtpSubmitResponse submitResponse = new OtpDTO.OtpSubmitResponse();
        OtpEntity otpEntity = otpRepository.findById(request.otpId).orElse(null);
        if (otpEntity == null) {
            submitResponse.result = false;
            submitResponse.message = "OTP chưa được tạo";
            return submitResponse;
        }
        if (!otpEntity.getIsActive()) {
            submitResponse.result = false;
            submitResponse.message = "OTP đã được sử dụng";
            return submitResponse;
        }
        if (!otpEntity.getOtpCode().equals(request.otpCode)) {
            submitResponse.result = false;
            submitResponse.message = "Sai mã OTP";
            return submitResponse;
        }
        if (otpEntity.getCreatedAt().plusSeconds(otpEntity.getDurtaion()).isBefore(LocalDateTime.now())) {
            submitResponse.result = false;
            submitResponse.message = "Hết hạn OTP";
            return submitResponse;
        }

        submitResponse.result = true;
        submitResponse.message = "Thành công";
        otpEntity.setIsActive(false);
        otpRepository.save(otpEntity);

        return submitResponse;
    }

    private String createOtp1() {
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HHmmdd");

        return dateTimeFormatter.format(ldt);
    }

    private String createOtp2() {
        return RandomStringUtils.randomNumeric(6);
    }
}