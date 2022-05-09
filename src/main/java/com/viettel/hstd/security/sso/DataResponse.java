package com.viettel.hstd.security.sso;

import com.viettel.ktts.vps.VpsUserToken;
import viettel.passport.client.UserToken;

/**
 *
 * @author chienpv
 */
public class DataResponse {
    private UserToken ssoUser;
    private VpsUserToken vpsUser;

    public UserToken getSsoUser() {
        return ssoUser;
    }

    public void setSsoUser(UserToken ssoUser) {
        this.ssoUser = ssoUser;
    }

    public VpsUserToken getVpsUser() {
        return vpsUser;
    }

    public void setVpsUser(VpsUserToken vpsUser) {
        this.vpsUser = vpsUser;
    }
    
}
