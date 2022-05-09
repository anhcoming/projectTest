package com.viettel.hstd.security;


import com.viettel.hstd.security.sso.SSoResponse;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImp implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of(0L);
        }
        if (authentication.getPrincipal() == "anonymousUser") {
            return Optional.of(0L);
        }
        return Optional.of(((SSoResponse) authentication.getPrincipal()).getSysUserId());
    }
}
