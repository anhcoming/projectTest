package com.viettel.hstd.security.sso.app;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.keys.AesKey;
import viettel.passport.client.UserToken;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class SSOServiceUtils {
    private static Logger log;
    private static final HostnameVerifier DO_NOT_VERIFY;

    static {
        SSOServiceUtils.log = Logger.getLogger(SSOServiceUtils.class);
        DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(final String hostname, final SSLSession session) {
                return true;
            }
        };
    }

    private static void trustAllHosts() {
        final TrustManager[] trustAllCerts = { new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override
            public void checkClientTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {
            }
        } };
        try {
            final SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        catch (Exception e) {
            SSOServiceUtils.log.error("trustAllHosts ERROR:", e);
        }
    }

    public static SSORestReponse loginByAPIs(final String params, final String url) {
        String result = "";
        OutputStreamWriter out = null;
        BufferedReader in = null;
        HttpURLConnection conn = null;
        final SSORestReponse response = new SSORestReponse();
        try {
            trustAllHosts();
            final URL realUrl = new URL(null, url);
            if (realUrl.getProtocol().toLowerCase().equals("https")) {
                final HttpsURLConnection https = (HttpsURLConnection)realUrl.openConnection();
                https.setHostnameVerifier(SSOServiceUtils.DO_NOT_VERIFY);
                conn = https;
            }
            else {
                conn = (HttpURLConnection)realUrl.openConnection();
            }
            conn.setRequestMethod("POST");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(params);
            out.flush();
            if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
                response.setStatus(200);
                response.setStatusCode("SUCCESS");
                in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            }
            else {
                if (conn.getResponseCode() != 401) {
                    throw new IOException("INTERNAL_SERVER_ERROR");
                }
                response.setStatusCode("LOGIN_FAIED");
                response.setStatus(401);
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            }
            String line;
            while ((line = in.readLine()) != null) {
                result = String.valueOf(result) + line;
            }
            response.setMessage(result);
        }
        catch (Exception e) {
            response.setStatus(0);
            response.setMessage("sendHtpps error");
            response.setStatusCode("ERROR");
            SSOServiceUtils.log.error("sendHtpps ERROR:", e);
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
            catch (IOException ex) {
                SSOServiceUtils.log.error("sendHtpps ERROR:", ex);
            }
            return response;
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
            catch (IOException ex) {
                SSOServiceUtils.log.error("sendHtpps ERROR:", ex);
            }
        }
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        catch (IOException ex) {
            SSOServiceUtils.log.error("sendHtpps ERROR:", ex);
        }
        return response;
    }

    private static SSORestReponse standardRespone(final SSORestReponse response) {
        if (response.getStatus() == 200) {
            return response;
        }
        if (response.getMessage().equals("InvalidLoginLocationException")) {
            response.setStatusCode("InvalidLoginLocationException");
        }
        if (response.getMessage().equals("AccountNotActiveException")) {
            response.setStatusCode("AccountNotActiveException");
        }
        if (response.getMessage().equals("AccountExpireException")) {
            response.setStatusCode("AccountExpireException");
        }
        if (response.getMessage().equals("FirstLoginException")) {
            response.setStatusCode("FirstLoginException");
        }
        if (response.getMessage().equals("MaxTempLockException")) {
            response.setStatusCode("MaxTempLockException");
        }
        if (response.getMessage().equals("PasswordExpiredException")) {
            response.setStatusCode("PasswordExpiredException");
        }
        if (response.getMessage().equals("AccountExpiredException")) {
            response.setStatusCode("AccountExpiredException");
        }
        if (response.getMessage().equals("AccountLockedException")) {
            response.setStatusCode("AccountLockedException");
        }
        if (response.getMessage().equals("AccountLockedException")) {
            response.setStatusCode("AccountLockedException");
        }
        if (response.getMessage().equals("UnresolvedPrincipalException")) {
            response.setStatusCode("UnresolvedPrincipalException");
        }
        if (response.getMessage().equals("FailedLoginException")) {
            response.setStatusCode("FailedLoginException");
        }
        else {
            response.setStatusCode("FailedLoginException");
        }
        return response;
    }

    public static String getJsonData(final String input) {
        final String signingKey = "ahN47WHSA3-_I7wAcfQ7W2qyTKMeQrbDBYJQoENpGeTs8xLWddVPaMfqgC_e_UboPB9wJluMVC3M8CtoBKt7Ow";
        final String encryptionKey = "rle6pMmf5eWeix5LHm2sil_aP8WWl3IB8RtMWsRw1vs";
        final Key key = new AesKey("ahN47WHSA3-_I7wAcfQ7W2qyTKMeQrbDBYJQoENpGeTs8xLWddVPaMfqgC_e_UboPB9wJluMVC3M8CtoBKt7Ow".getBytes(StandardCharsets.UTF_8));
        final JsonWebSignature jws = new JsonWebSignature();
        String result = "";
        try {
            jws.setCompactSerialization(input);
            jws.setKey(key);
            if (!jws.verifySignature()) {
                throw new Exception("JWT verification failed");
            }
            final byte[] decodedBytes = Base64.decodeBase64(jws.getEncodedPayload().getBytes(StandardCharsets.UTF_8));
            final String decodedPayload = new String(decodedBytes, StandardCharsets.UTF_8);
            final JsonWebEncryption jwe = new JsonWebEncryption();
            final JsonWebKey jsonWebKey = JsonWebKey.Factory.newJwk("\n{\"kty\":\"oct\",\n \"k\":\"rle6pMmf5eWeix5LHm2sil_aP8WWl3IB8RtMWsRw1vs\"\n}");
            jwe.setCompactSerialization(decodedPayload);
            jwe.setKey(new AesKey(jsonWebKey.getKey().getEncoded()));
            result = jwe.getPlaintextString();
        }
        catch (Exception e) {
            SSOServiceUtils.log.error("getJsonData ERROR:", e);
        }
        return result;
    }

    public static SSORestReponse validate(final String username, final String password) {
        String appCode = "CTCT";
        String ticketServiceUrl = "https://10.255.58.201:8002/sso/v1/tickets";
        try {
            final String params = "username=" + URLEncoder.encode(username, "utf-8") + "&password=" + URLEncoder.encode(password, "utf-8") + "&token=true&appCode=" + appCode;
            final SSORestReponse response = standardRespone(loginByAPIs(params, ticketServiceUrl));
            if ("SUCCESS".equals(response.getStatusCode())) {
                response.setUserToken(extractUserToken(response.getMessage(), appCode));
            }
            return response;
        }
        catch (Exception e) {
            SSOServiceUtils.log.error("lay thong tin tu VsaadminService.wsdl error: ", e);
            return null;
        }
    }

    public static UserToken extractUserToken(final String jwtResult, final String appCode) {
        try {
            final UserToken userToken = new UserToken();
            final String jsonString = getJsonData(jwtResult);
            final UserTokenSSO2 userTokenSSO2 = UserTokenSSO2.initWithJSON(jsonString);
            userToken.setStatus(userTokenSSO2.getStatus());
            userToken.setStaffCode(userTokenSSO2.getStaffCode());
            userToken.setDeptId((long)userTokenSSO2.getDeptId());
            userToken.setFullName(userTokenSSO2.getFullName());
            userToken.setUserName(userTokenSSO2.getUserName());
            userToken.setUserID((long)userTokenSSO2.getUserId());
            userToken.setEmail(userTokenSSO2.getEmail());
            userToken.setCellphone(userTokenSSO2.getCellPhone());
            return userToken;
        }
        catch (Exception ex) {
            SSOServiceUtils.log.error("Extract user token from JWT ERROR: ", ex);
            return null;
        }
    }
}
