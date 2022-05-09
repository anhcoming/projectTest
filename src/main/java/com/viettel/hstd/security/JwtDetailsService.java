package com.viettel.hstd.security;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Primary
public class JwtDetailsService implements UserDetailsService {

//    @Autowired
//    AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        EntityAccount account = accountRepository
//                .findByUserName(username)
//                .orElseThrow(() -> new UsernameNotFoundException("Not found account by " + username));
        return new com.viettel.hstd.security.JwtUserPrincipal(null, getAuthority());
    }

    private Set<GrantedAuthority> getAuthority() {
        Set<GrantedAuthority> grantedAuthority = new HashSet<>();
        grantedAuthority.add(new SimpleGrantedAuthority("ADMIN"));
        return grantedAuthority;
    }
}
