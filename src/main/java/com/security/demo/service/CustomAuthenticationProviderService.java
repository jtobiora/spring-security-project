package com.security.demo.service;

import com.security.demo.model.Role;
import com.security.demo.model.User;
import com.security.demo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.security.core.AuthenticationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class CustomAuthenticationProviderService implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authenticationToken = null;

        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        Optional<User> user = userService.findByEmail(email);
        if(user != null) {
            User u = user.get();
            if(email.equals(u.getEmail()) && BCrypt.checkpw(password,u.getPassword())) {
                Collection<GrantedAuthority> grantedAuthorities = getGrantedAuthorities(u);
                authenticationToken = new UsernamePasswordAuthenticationToken(
                        new org.springframework.security.core.userdetails.User(email, password, grantedAuthorities), password, grantedAuthorities);
            }
        } else {
            throw new UsernameNotFoundException("User name "+ email+" not found");
        }

        return authenticationToken;
    }

    private Collection<GrantedAuthority> getGrantedAuthorities(User user) {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        Role role = user.getRoles().iterator().next();
        if(role.getName().equals("admin")) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return grantedAuthorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
