package com.viettel.hstd.security.sso;


import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import viettel.passport.client.UserToken;
import viettel.passport.util.SecureURL;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class ServiceTicket {

    private static Logger log = Logger.getLogger(ServiceTicket.class);

    private String casValidateUrl;

    private String proxyCallbackUrl;

    private String st;

    private String service;

    private String pgtIou;

    private String user;

    private String errorMessage;

    private String entireResponse;

    private boolean renew = false;

    private boolean successfulAuthentication;

    private String domainCode;

    private UserToken userToken;

    private String cookie;

    private String ipWan;

    private static long TIME_OUT = 18000L;

    public static final long ACTION_TIMEOUT = 60000L;

    public void setCasValidateUrl(String x) {
        this.casValidateUrl = x;
    }

    public String getCasValidateUrl() {
        return this.casValidateUrl;
    }

    public void setProxyCallbackUrl(String x) {
        this.proxyCallbackUrl = x;
    }

    public void setRenew(boolean b) {
        this.renew = b;
    }

    public String getProxyCallbackUrl() {
        return this.proxyCallbackUrl;
    }

    public void setServiceTicket(String x) {
        this.st = x;
    }

    public void setService(String x) {
        this.service = x;
    }

    public String getUser() {
        return this.user;
    }

    public String getPgtIou() {
        return this.pgtIou;
    }

    public boolean isAuthenticationSuccesful() {
        return this.successfulAuthentication;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public String getResponse() {
        return this.entireResponse;
    }

    public String getDomainCode() {
        return this.domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    public UserToken getUserToken() {
        return this.userToken;
    }

    public void setUserToken(UserToken userToken) {
        this.userToken = userToken;
    }

    protected void clear() {
        this.user = this.pgtIou = this.errorMessage = null;
        this.successfulAuthentication = false;
    }

    public void validate() throws IOException, ParserConfigurationException {
        if (this.casValidateUrl == null || this.st == null) {
            throw new IllegalStateException("must set validation URL and ticket");
        }
        clear();
        StringBuilder sb = new StringBuilder();
        sb.append(this.casValidateUrl);
        if (this.casValidateUrl.indexOf('?') == -1) {
            sb.append('?');
        } else {
            sb.append('&');
        }
        sb.append("service=").append(this.service).append("&ticket=").append(this.st);
        if (this.proxyCallbackUrl != null) {
            sb.append("&pgtUrl=").append(this.proxyCallbackUrl);
        }
        if (this.renew) {
            sb.append("&renew=true");
        }
        sb.append("&domainCode=").append(this.domainCode);
        sb.append("&ipWan=").append(this.ipWan);
        String url = sb.toString();
        log.info("Start validate for URL: " + url);
        long start = System.currentTimeMillis();
        SecureURL secure = new SecureURL();
        if ((((this.cookie != null) ? 1 : 0) & ("".equals(this.cookie) ? 0 : 1)) != 0) {
            this.entireResponse = secure.retrieve(url + "JSESSIONID=" + this.cookie);
        } else {
            this.entireResponse = secure.retrieve(url);
        }

        System.out.println(this.entireResponse);

        long elapse = System.currentTimeMillis() - start;
        log.info("Receive validate response for service ticket [" + this.st
                + "], response time = " + elapse);
        if (elapse > 60000L) {
            log.warn("Action timeout: Too long time to validate ticket [" + this.st
                    + "], response time is " + elapse);
        }
        try {
            parseXMLResponse(this.entireResponse);
        } catch (SAXException ex) {
            log.error("Error when parse xml response, respone data:\n\"" + this.entireResponse + "\"", ex);
        } finally {
            elapse = System.currentTimeMillis() - start;
            log.info("Finish validate service ticket [" + this.st + "], elapse time = " + elapse);
            if (elapse > 60000L) {
                log.error("Action timeout: Too long time to validate user, elapse time = "
                        + elapse);
            }
        }
    }

    public void parseXMLResponse(String response) throws ParserConfigurationException, SAXException, IOException {
        this.userToken = UserTokenParse.parseXMLResponse(response);
        if (this.userToken != null) {
            this.user = this.userToken.getUserName();
            this.pgtIou = this.userToken.getFullName();
            this.successfulAuthentication = true;
            log.info("Authenticate successful for username ["
                    + this.userToken.getUserName() + "] for service ticket ["
                    + this.st + "]");
        } else {
            this.user = null;
            this.pgtIou = null;
            this.successfulAuthentication = false;
            log.info("Authenticate failure for service ticket ["
                    + this.st + "], respone data:\n\"" + response + "\"");
        }
    }

    public String getCookie() {
        return this.cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getIpWan() {
        return this.ipWan;
    }

    public void setIpWan(String ipWan) {
        this.ipWan = ipWan;
    }
}
