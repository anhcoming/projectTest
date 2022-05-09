package com.viettel.hstd.service.imp;

import com.viettel.hstd.dto.hstd.SmsMessageConfig;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.exception.UnauthorizedAccessException;
import com.zaxxer.hikari.HikariConfig;
import liquibase.pro.packaged.A;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import com.viettel.hstd.service.inf.SmsMessageService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

@Service
@Slf4j
public class SmsMessageServiceImp implements SmsMessageService {

    @Autowired
    SmsMessageConfig smsMessageConfig;

    @Autowired
    Environment environment;

    @Override
    public String sendSmsMessage(String phoneNumber, String message) {
        try {

            String reqId = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String resMT = sendSMS(reqId, formatPhone(phoneNumber), message);
            if (Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
                resMT = sendSMS(reqId, formatPhone("84961185888"), message);
            } else {
                resMT = sendSMS(reqId, formatPhone(phoneNumber), message);
            }


            System.out.println("sendMT " + resMT);

            String status = getValue(resMT, "<result>", "</result>");

            if (status.equals("1")) {
                log.info("Send message successful");
                return "OK";
            } else {
                log.info("Send message unsuccessful");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String formatPhone(String msisdn) {
        if (msisdn.startsWith("0")) {
            msisdn = "84" + msisdn.substring(1);
        }
        if (!msisdn.startsWith("84")) {
            msisdn = "84" + msisdn;
        }
        return msisdn;
    }


    public String sendSMS(String reqId, String msisdn, String content) throws Exception {
        String user = smsMessageConfig.getUser();//phần này sẽ đọc từ config trên yaml
        String pass = smsMessageConfig.getPass();//phần này sẽ đọc từ config trên yaml
        String CPCode = smsMessageConfig.getCPCode();//phần này sẽ đọc từ config trên yaml
        String ServiceID = smsMessageConfig.getServiceID();//phần này sẽ đọc từ config trên yaml

        String data = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:impl=\"http://impl.bulkSms.ws/\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <impl:wsCpMt>\n"
                + "         <!--Optional:-->\n"
                + "         <User>" + user + "</User>\n"
                + "         <!--Optional:-->\n"
                + "         <Password>" + pass + "</Password>\n"
                + "         <!--Optional:-->\n"
                + "         <CPCode>" + CPCode + "</CPCode>\n"
                + "         <!--Optional:-->\n"
                + "         <RequestID>" + reqId + "</RequestID>\n"
                + "         <!--Optional:-->\n"
                + "         <UserID>" + msisdn + "</UserID>\n"
                + "         <!--Optional:-->\n"
                + "         <ReceiverID>" + msisdn + "</ReceiverID>\n"
                + "         <!--Optional:-->\n"
                + "         <ServiceID>" + ServiceID + "</ServiceID>\n"
                + "         <!--Optional:-->\n"
                + "         <CommandCode>bulksms</CommandCode>\n"
                + "         <!--Optional:-->\n"
                + "         <Content>" + content + "</Content>\n"
                + "         <!--Optional:-->\n"
                + "         <ContentType>0</ContentType>\n"
                + "      </impl:wsCpMt>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";

        return bulkSMSService(data);
    }

    public String bulkSMSService(String data) throws Exception {
        String url = smsMessageConfig.getUrl();//phần này sẽ đọc từ config trên yaml
        trustAllHttpsCertificates();
        fixHttpsHandler();
        URL sendUrl = new URL(url);
        URLConnection urlCon = sendUrl.openConnection();
        urlCon.setDoOutput(true);
        urlCon.setDoInput(true);

        HttpsURLConnection conn = (HttpsURLConnection) urlCon;
        HttpsURLConnection.setDefaultHostnameVerifier(new VpHostnameVerifier());

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
        conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
        conn.setRequestProperty("SOAPAction", "");
        conn.setConnectTimeout(3000);

        PrintStream ps = new PrintStream(conn.getOutputStream(), true, "utf-8");
        ps.write(data.getBytes("utf-8"));
        ps.flush();

        StringBuilder sb;
        try (InputStream is = conn.getInputStream()) {
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String str;
            sb = new StringBuilder();
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
        }
        return sb.toString();

    }

    public static String getValue(String xml, String openTag, String closeTag) {
        try {
            int f = xml.indexOf(openTag) + openTag.length();
            int l = xml.indexOf(closeTag);
            return f <= l ? xml.substring(f, l) : "";
        } catch (Exception e) {
            return "-1";
        }
    }

    public void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new MiTM();
        trustAllCerts[0] = tm;
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    public static void fixHttpsHandler() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            SSLContext mySSLContext = SSLContext.getInstance("TLSv1.3");
            mySSLContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(mySSLContext.getSocketFactory());

            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class MiTM implements TrustManager, X509TrustManager {

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        }
    }

    public class VpHostnameVerifier implements HostnameVerifier {

        public boolean verify(String urlHostName, SSLSession session) {
            return true;
        }
    }
}
