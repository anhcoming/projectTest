package com.viettel.hstd.security.sso.app;

import lombok.Getter;
import lombok.Setter;
import viettel.passport.client.UserToken;

@Getter
@Setter
public class SSORestReponse {
    private int status;
    private String statusCode;
    private String message;
    private UserToken userToken;
    private Long interviewSessionId;
}
