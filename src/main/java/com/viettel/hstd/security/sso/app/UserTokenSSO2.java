package com.viettel.hstd.security.sso.app;

import org.apache.log4j.Logger;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

public class UserTokenSSO2 {
    private static Logger log;
    private String sub;
    private Long status;
    private String staffCode;
    private Long deptId;
    private String fullName;
    private String userName;
    private Long userId;
    private Long positionId;
    private String cellPhone;
    private Long exp;
    private Long iat;
    private String jti;
    private String email;

    static {
        UserTokenSSO2.log = Logger.getLogger(UserTokenSSO2.class);
    }

    public static UserTokenSSO2 initWithJSON(final String jsonString) throws JSONException {
        final UserTokenSSO2 userTokenSSO2 = new UserTokenSSO2();
        final JSONObject obj = new JSONObject(jsonString);
        userTokenSSO2.setSub(obj.getString("sub"));
        userTokenSSO2.setStatus("".equals(getFirstStringArray(obj, "status")) ? 0L : Long.valueOf(getFirstStringArray(obj, "status")));
        userTokenSSO2.setStaffCode(getFirstStringArray(obj, "staffCode"));
        userTokenSSO2.setDeptId("".equals(getFirstStringArray(obj, "deptId")) ? 0L : Long.valueOf(getFirstStringArray(obj, "deptId")));
        userTokenSSO2.setFullName(getFirstStringArray(obj, "fullName"));
        userTokenSSO2.setUserName(getFirstStringArray(obj, "userName"));
        userTokenSSO2.setUserId("".equals(getFirstStringArray(obj, "userId")) ? 0L : Long.valueOf(getFirstStringArray(obj, "userId")));
        userTokenSSO2.setPositionId("".equals(getFirstStringArray(obj, "positionId")) ? 0L : Long.valueOf(getFirstStringArray(obj, "positionId")));
        userTokenSSO2.setExp(obj.getLong("exp"));
        userTokenSSO2.setIat(obj.getLong("iat"));
        userTokenSSO2.setJti(obj.getString("jti"));
        userTokenSSO2.setEmail(getFirstStringArray(obj, "email"));
        userTokenSSO2.setCellPhone(getFirstStringArray(obj, "phoneNumber"));
        return userTokenSSO2;
    }

    private static String getFirstStringArray(final JSONObject obj, final String name) {
        try {
            final JSONArray arr = obj.getJSONArray(name);
            final int i = 0;
            if (i < arr.length()) {
                return arr.getString(i);
            }
            return "";
        }
        catch (Exception ex) {
            UserTokenSSO2.log.error("getFirstStringArray ERROR: ", ex);
            return "";
        }
    }

    public String getSub() {
        return this.sub;
    }

    public void setSub(final String sub) {
        this.sub = sub;
    }

    public Long getStatus() {
        return this.status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public String getStaffCode() {
        return this.staffCode;
    }

    public void setStaffCode(final String staffCode) {
        this.staffCode = staffCode;
    }

    public Long getDeptId() {
        return this.deptId;
    }

    public void setDeptId(final Long deptId) {
        this.deptId = deptId;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public Long getPositionId() {
        return this.positionId;
    }

    public void setPositionId(final Long positionId) {
        this.positionId = positionId;
    }

    public Long getExp() {
        return this.exp;
    }

    public void setExp(final Long exp) {
        this.exp = exp;
    }

    public Long getIat() {
        return this.iat;
    }

    public void setIat(final Long iat) {
        this.iat = iat;
    }

    public String getJti() {
        return this.jti;
    }

    public void setJti(final String jti) {
        this.jti = jti;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getCellPhone() {
        return this.cellPhone;
    }

    public void setCellPhone(final String cellPhone) {
        this.cellPhone = cellPhone;
    }
}
