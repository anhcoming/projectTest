package com.viettel.hstd.core.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;

public class AuthenticateUtils implements Serializable {
    public AuthenticateUtils() {
        super();
    }

    public static String encryptText(String sInputText) {
//        try {
//            MessageDigest m = MessageDigest.getInstance("MD5");
//            m.reset();
//            m.update(sInputText.getBytes());
//            byte[] digest = m.digest();
//            BigInteger bigInt = new BigInteger(1, digest);
//            String sHashText = bigInt.toString(16);
//            return sHashText;
//        } catch (Exception e) {
//            return null;
//        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(sInputText);
    }

}
