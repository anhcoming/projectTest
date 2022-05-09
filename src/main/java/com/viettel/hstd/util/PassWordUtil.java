package com.viettel.hstd.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PassWordUtil {

    private static PassWordUtil instance;

    private PassWordUtil() {
    }

    public synchronized String encrypt(String plaintext) throws Exception {
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException var6) {
            var6.printStackTrace();
        }

        try {
            md.update(plaintext.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException var5) {
            var5.printStackTrace();
        }

        byte[] raw = md.digest();
        String hash = Base64.getEncoder().encodeToString(raw);
        return hash;
    }

    public static synchronized PassWordUtil getInstance() {
        if (instance == null) {
            instance = new PassWordUtil();
        }

        return instance;
    }
}
