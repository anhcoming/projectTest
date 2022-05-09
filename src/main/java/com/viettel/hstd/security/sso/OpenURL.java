package com.viettel.hstd.security.sso;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class OpenURL {
  public String retrieve(String url) throws IOException {
    BufferedReader in = null;
    try {
      trustAllHttpsCertificates();
      URL u = new URL(url);
      HttpsURLConnection.setDefaultHostnameVerifier(new VpHostnameVerifier());
      URLConnection hConn = u.openConnection();
      hConn.setDoInput(true);
      hConn.setAllowUserInteraction(false);
      hConn.setUseCaches(true);
      in = new BufferedReader(new InputStreamReader(hConn.getInputStream(), "UTF8"));
      StringBuffer buf = new StringBuffer();
      String line;
      while ((line = in.readLine()) != null)
        buf.append(line + "\n"); 
      return buf.toString();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } finally {
      if (in != null)
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }  
    } 
  }
  
  private static void trustAllHttpsCertificates() throws Exception {
    TrustManager[] trustAllCerts = new TrustManager[1];
    TrustManager tm = new MiTM();
    trustAllCerts[0] = tm;
    SSLContext sc = SSLContext.getInstance("SSL");
    sc.init(null, trustAllCerts, null);
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
  }
  
  public static class MiTM implements TrustManager, X509TrustManager {
    public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[0];
    }
    
    public boolean isServerTrusted(X509Certificate[] certs) {
      return true;
    }
    
    public boolean isClientTrusted(X509Certificate[] certs) {
      return true;
    }
    
    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {}
    
    public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {}
  }
  
  public static class VpHostnameVerifier implements HostnameVerifier {
    public boolean verify(String urlHostName, SSLSession session) {
      return true;
    }
  }
}

