package com.viettel.hstd.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.AESDecrypter;
import com.nimbusds.jose.crypto.AESEncrypter;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.gen.OctetSequenceKeyGenerator;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.viettel.hstd.security.sso.SSoResponse;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenUtils {
    public static String JWT_SECRET = "@Ghdc@!@#%^&*()(*&^^%%^&**(";
    // TODO: Production thi chinh lai
    public static final Long JWT_HOLD_TIME = 15 * 60 * 1000L * 100; /*15 pút*/
    public static final long EXPIRATION_TIME_IN_MINUTES = 300;
    public static OctetSequenceKey octetSequenceKey;

    @Autowired
    ObjectMapper objectMapper;

    @PostConstruct
    protected void init() throws JOSEException {
        JWT_SECRET = Base64.getEncoder().encodeToString(JWT_SECRET.getBytes());
        octetSequenceKey = new OctetSequenceKeyGenerator(256)
                .keyID(JWT_SECRET) // give the key some ID (optional)
                .algorithm(JWSAlgorithm.HS256) // indicate the intended key alg (optional)
                .generate();
    }

    public String generateToken1(SSoResponse jwtUserPrincipal) {
        Claims claims = Jwts.claims().setSubject(jwtUserPrincipal.getUserName());
        claims.put("payload", jwtUserPrincipal);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_HOLD_TIME);

        LocalDateTime newNow = LocalDateTime.now();
        LocalDateTime newExpiredDate = newNow.plusDays(1);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(newNow.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(newExpiredDate.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    public SSoResponse validateToken1(String token) {
        if (token == null) return null;
        try {

            Jws<Claims> claims = Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
            if (!claims.getBody().getExpiration().before(new Date())) {
                Object info = claims.getBody().get("payload", Object.class);
                Gson gson = new Gson();
                String infoJson = gson.toJson(info);
                return gson.fromJson(infoJson, SSoResponse.class);
            } else return null;
        } catch (Exception e) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            log.error(stackTrace);
            return null;
        }
    }

    public String generateToken(SSoResponse jwtUserPrincipal) throws NoSuchAlgorithmException, JOSEException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        //Initialize key size
        keyPairGenerator.initialize(2048);
        // Generate the key pair
        KeyPair keyPair = keyPairGenerator.genKeyPair();

        JWTClaimsSet.Builder claimsSet = new JWTClaimsSet.Builder();
        claimsSet.issuer("test-user");
        claimsSet.subject("JWE-Authentication-Example");

        //User specified claims
        claimsSet.claim("payload", jwtUserPrincipal);

        claimsSet.expirationTime(Date.from(LocalDateTime.now().plusMinutes(EXPIRATION_TIME_IN_MINUTES).atZone(ZoneId.systemDefault()).toInstant()));
        claimsSet.jwtID(UUID.randomUUID().toString());

        log.info("Claim Set : \n" + claimsSet.build());

        // Create the JWE header and specify:
        // RSA-OAEP as the encryption algorithm
        // 128-bit AES/GCM as the encryption method
        JWEHeader header = new JWEHeader(JWEAlgorithm.A256KW, EncryptionMethod.A256CBC_HS512);

        // Initialized the EncryptedJWT object
        EncryptedJWT jwt = new EncryptedJWT(header, claimsSet.build());

        // Create an RSA encrypted with the specified public RSA key
        AESEncrypter encrypter1 = new AESEncrypter(octetSequenceKey);

        // Doing the actual encryption
        jwt.encrypt(encrypter1);

        // Serialize to JWT compact form
        String jwtString = jwt.serialize();
        log.info("");
        log.info("========================= Encrypted JWE token ==================================");
        log.info("");

        return jwtString;
    }

    public SSoResponse validateToken(String jwtString) {
        if (jwtString == null) return null;
        try {
            // In order to read back the data from the token using your private RSA key:
            // parse the JWT text string using EncryptedJWT object
            EncryptedJWT jwt = EncryptedJWT.parse(jwtString);

            AESDecrypter decrypter1 = new AESDecrypter(octetSequenceKey);

            // Doing the decryption
            jwt.decrypt(decrypter1);

            // Print out the claims from decrypted token
            System.out.println("======================== Decrypted payload values ===================================");
            System.out.println("");

            System.out.println("Issuer: [ " + jwt.getJWTClaimsSet().getIssuer() + "]");
            System.out.println("Subject: [" + jwt.getJWTClaimsSet().getSubject() + "]");
            System.out.println("Expiration Time: [" + jwt.getJWTClaimsSet().getExpirationTime() + "]");
            System.out.println("Not Before Time: [" + jwt.getJWTClaimsSet().getNotBeforeTime() + "]");
            System.out.println("JWT ID: [" + jwt.getJWTClaimsSet().getJWTID() + "]");

//            System.out.println("Application Id: [" + jwt.getJWTClaimsSet().getClaim("appId") + "]");
//            System.out.println("User Id: [" + jwt.getJWTClaimsSet().getClaim("userId") + "]");
//            System.out.println("Role type: [" + jwt.getJWTClaimsSet().getClaim("role") + "]");
//            System.out.println("Application Type: [" + jwt.getJWTClaimsSet().getClaim("applicationType") + "]");
//            System.out.println("Client Remote Address: [" + jwt.getJWTClaimsSet().getClaim("clientRemoteAddress") + "]");

            System.out.println("");
            System.out.println(
                    "==========================================================================================================");
            if (!jwt.getJWTClaimsSet().getExpirationTime().before(new Date())) {
                Object info = jwt.getJWTClaimsSet().getClaim("payload");
                Gson gson = new Gson();
                String infoJson = gson.toJson(info);
                return gson.fromJson(infoJson, SSoResponse.class);
            } else return null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }


}

