package br.com.softdesign.desafio.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.softdesign.desafio.dto.BookDTO;
import br.com.softdesign.desafio.dto.BookResponseDTO;
import br.com.softdesign.desafio.exception.CustomException;
import br.com.softdesign.desafio.model.Book;
import br.com.softdesign.desafio.model.BookStatus;
import br.com.softdesign.desafio.repository.BookRepository;
import br.com.softdesign.desafio.service.BookService;
import br.com.softdesign.desafio.service.RedisService;
import br.com.softdesign.desafio.utils.Mapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final Mapper mapper;
    private final RedisService redisService;
    
    @Override
    public BookResponseDTO createBook(BookDTO bookDTO) {
        Book book = mapper.convert(bookDTO, Book.class);
        book.setStatus(BookStatus.AVAILABLE);
        Book savedBook = bookRepository.save(book);
        String redisKey = "book:" + savedBook.getId();
        redisService.save(redisKey, savedBook, 10, TimeUnit.MINUTES);
        return mapper.convert(savedBook, BookResponseDTO.class);
    }
    
    @Override
    public Page<BookResponseDTO> listBooks(String author, Integer year, String genre, Pageable pageable) {
        Page<Book> booksPage = bookRepository.findAll((root, query, cb) -> {
            var predicates = cb.conjunction();
            if (author != null && !author.isBlank()) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%"));
            }
            if (year != null) {
                predicates = cb.and(predicates, cb.equal(root.get("year"), year));
            }
            if (genre != null && !genre.isBlank()) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("genre")), "%" + genre.toLowerCase() + "%"));
            }
            return predicates;
        }, pageable);
        return booksPage.map(book -> mapper.convert(book, BookResponseDTO.class));
    }
    
    @Override
    public BookResponseDTO getBookDetail(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new CustomException("Livro não encontrado", HttpStatus.NOT_FOUND));
        return mapper.convert(book, BookResponseDTO.class);
    }

    @Override
    public BookResponseDTO rentOrReturnBook(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new CustomException("Livro não encontrado", HttpStatus.NOT_FOUND));
        
        book.setStatus(book.getStatus() == BookStatus.AVAILABLE ? BookStatus.RENTED : BookStatus.AVAILABLE);
        Book updatedBook = bookRepository.save(book);
        return mapper.convert(updatedBook, BookResponseDTO.class);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new CustomException("Livro não encontrado", HttpStatus.NOT_FOUND));
        if (book.getStatus() == BookStatus.RENTED) {
            throw new CustomException("Livro alugado não pode ser excluído", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        bookRepository.delete(book);
    }
}
