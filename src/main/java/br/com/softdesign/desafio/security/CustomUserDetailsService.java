package br.com.softdesign.desafio.security;

import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.softdesign.desafio.exception.CustomException;
import br.com.softdesign.desafio.model.AppUser;
import br.com.softdesign.desafio.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final AppUser user = userRepository.findByEmail(email);
        if (user == null) {
            throw new CustomException("E-mail '" + email + "' não encontrado.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return org.springframework.security.core.userdetails.User.withUsername(email)
                .password(user.getPassword())
                .authorities(Collections.emptyList())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
    
    public AppUser getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
