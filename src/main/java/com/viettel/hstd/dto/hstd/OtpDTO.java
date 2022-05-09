package com.viettel.hstd.dto.hstd;

import com.viettel.hstd.constant.OtpType;

import javax.validation.constraints.NotNull;

public class OtpDTO {
    public static class OtpRequest {
        public Long otpId;
        public String otpCode;
        public String name;
        public String content;
    }

    public static class OtpResponse {
        public Long otpId;
        public String otpCode;
        public String name;
        public String content;
    }

    public static class OtpCreateRequest {
        @NotNull(message = "Không được để trống loại OTP")
        public OtpType otpType = OtpType.UNKNOWN;
        public String content;
    }

    public static class OtpCreateResponse {
        public Long otpId;
        public Long duration;
    }

    public static class OtpSubmitResponse {
        public boolean result = false;
        public String message;
    }
}