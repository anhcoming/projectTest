package com.viettel.hstd.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.properties.AppProperties;
import com.viettel.hstd.exception.UnauthorizedAccessException;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.security.sso.SSoService;
import com.viettel.hstd.security.sso.SsoAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    SSoService sSoService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtTokenUtils jwtTokenUtils;
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;
    @Autowired
    AppProperties appProperties;
    @Autowired
    Message message;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveToken(request);
            SSoResponse ssoResponse = jwtTokenUtils.validateToken(token);
            if (ssoResponse == null) throw new UnauthorizedAccessException(message.getMessage("message.unauthorized"));
            Authentication auth = new SsoAuthentication(ssoResponse, ssoResponse.getTicket());
            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            System.out.println("Spring Security Filter Chain Exception:");
            e.printStackTrace();
            resolver.resolveException(request, response, null, e);
        }
    }

    private String resolveToken(HttpServletRequest req) {
        String token = req.getHeader("access-token");
        String bearerToken = token != null ? token : req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else {
            return null;
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return Arrays.stream(appProperties.getAllowUrl())
                .anyMatch(e -> new AntPathMatcher().match(e, request.getServletPath()));
    }
}
