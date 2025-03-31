package br.com.softdesign.desafio.service;

import br.com.softdesign.desafio.dto.AuthResponseDTO;
import br.com.softdesign.desafio.dto.UserDataDTO;

public interface UserService {
	
    AuthResponseDTO login(String email, String password);
    AuthResponseDTO register(UserDataDTO userDataDTO);
}
