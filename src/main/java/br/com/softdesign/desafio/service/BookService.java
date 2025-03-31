package br.com.softdesign.desafio.service;

import br.com.softdesign.desafio.dto.BookDTO;
import br.com.softdesign.desafio.dto.BookResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
	
    BookResponseDTO createBook(BookDTO bookDTO);
    Page<BookResponseDTO> listBooks(String author, Integer year, String genre, Pageable pageable);
    BookResponseDTO getBookDetail(Long id);
    BookResponseDTO rentOrReturnBook(Long id);
    void deleteBook(Long id);
}
