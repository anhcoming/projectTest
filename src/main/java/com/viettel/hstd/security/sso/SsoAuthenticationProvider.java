package com.viettel.hstd.security.sso;

import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.exception.AccessDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SsoAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    SSoService sSoService;
    @Autowired
    Message message;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String ticket = (String) authentication.getCredentials();

        if (authentication.getCredentials().toString().contains("app_")){
            return new UsernamePasswordAuthenticationToken(authentication.getCredentials().toString(), authentication.getCredentials().toString(), new ArrayList<>());
        }

        SSoResponse ssoResponse = sSoService.loadUserFromTicket(ticket);
        if (ssoResponse == null) {
            throw new AccessDeniedException(message.getMessage("message.auth.invalid_ticket"));
        }
        //Authentication auth = new SsoAuthentication(ssoResponse, ticket);

        return new SsoAuthentication(ssoResponse, ticket);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(SsoAuthentication.class);
    }
}
