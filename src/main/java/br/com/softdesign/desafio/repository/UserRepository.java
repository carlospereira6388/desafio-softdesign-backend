package br.com.softdesign.desafio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.softdesign.desafio.model.AppUser;

public interface UserRepository extends JpaRepository<AppUser, Integer> {
	
    boolean existsByEmail(String email);
    AppUser findByEmail(String email);
}
