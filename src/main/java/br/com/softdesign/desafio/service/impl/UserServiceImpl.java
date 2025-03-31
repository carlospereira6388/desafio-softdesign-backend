package br.com.softdesign.desafio.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.softdesign.desafio.dto.AuthResponseDTO;
import br.com.softdesign.desafio.dto.UserDataDTO;
import br.com.softdesign.desafio.dto.UserResponseDTO;
import br.com.softdesign.desafio.exception.CustomException;
import br.com.softdesign.desafio.model.AppUser;
import br.com.softdesign.desafio.repository.UserRepository;
import br.com.softdesign.desafio.security.JwtTokenProvider;
import br.com.softdesign.desafio.utils.Mapper;
import br.com.softdesign.desafio.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final Mapper mapper;
    
    @Override
    public AuthResponseDTO login(String email, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            AppUser user = userRepository.findByEmail(email);
            UserResponseDTO userResponseDTO = mapper.convert(user, UserResponseDTO.class);
            String token = jwtTokenProvider.generateToken(email);
            return new AuthResponseDTO(true, userResponseDTO, token);
        } catch (Exception e) {
            throw new CustomException("E-mail/senha inválidos", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Override
    public AuthResponseDTO register(UserDataDTO userDataDTO) {
        if (!isPasswordValid(userDataDTO.getPassword())) {
            throw new CustomException("Senha não corresponde aos requisitos mínimos de segurança.", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        
        if (!userRepository.existsByEmail(userDataDTO.getEmail())) {
            userDataDTO.setPassword(passwordEncoder.encode(userDataDTO.getPassword()));
            AppUser user;
            
            try {
                user = mapper.convert(userDataDTO, AppUser.class);
                userRepository.save(user);
            } catch (Exception e) {
                throw new CustomException("Erro no cadastro do usuário", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            
            UserResponseDTO userResponseDTO = mapper.convert(user, UserResponseDTO.class);
            String token = jwtTokenProvider.generateToken(userDataDTO.getEmail());
            return new AuthResponseDTO(true, userResponseDTO, token);
        } else {
            throw new CustomException("O e-mail já está sendo usado", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    
    private boolean isPasswordValid(String password) {
        return password.matches("(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}");
    }
}
