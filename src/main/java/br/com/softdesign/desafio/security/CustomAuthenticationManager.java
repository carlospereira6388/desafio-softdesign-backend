package br.com.softdesign.desafio.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.softdesign.desafio.model.AppUser;
import br.com.softdesign.desafio.repository.UserRepository;

public class CustomAuthenticationManager implements AuthenticationManager {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public CustomAuthenticationManager(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        AppUser user = userRepository.findByEmail(auth.getPrincipal().toString());
        if (user == null || !passwordEncoder.matches(auth.getCredentials().toString(), user.getPassword())) {
            throw new BadCredentialsException("E-mail/senha inválidos");
        }
        return auth;
    }
}
