package br.com.softdesign.desafio.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.softdesign.desafio.dto.BookDTO;
import br.com.softdesign.desafio.dto.BookResponseDTO;
import br.com.softdesign.desafio.exception.CustomException;
import br.com.softdesign.desafio.model.Book;
import br.com.softdesign.desafio.model.BookStatus;
import br.com.softdesign.desafio.repository.BookRepository;
import br.com.softdesign.desafio.service.impl.BookServiceImpl;
import br.com.softdesign.desafio.utils.Mapper;

class BookServiceTest {

    @Mock private BookRepository bookRepository;
    @Mock private Mapper mapper;
    @Mock private RedisService redisService;
    @InjectMocks private BookServiceImpl bookService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBook() {
        BookDTO dto = new BookDTO();
        Book book = new Book();
        book.setId(1L);
        book.setStatus(BookStatus.AVAILABLE);
        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setStatus(BookStatus.AVAILABLE);
        BookResponseDTO responseDTO = new BookResponseDTO();

        when(mapper.convert(dto, Book.class)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(mapper.convert(savedBook, BookResponseDTO.class)).thenReturn(responseDTO);

        BookResponseDTO result = bookService.createBook(dto);

        assertEquals(responseDTO, result);
        verify(redisService).save("book:1", savedBook, 10, TimeUnit.MINUTES);
    }

    @Test
    void testGetBookDetailSuccess() {
        Book book = new Book();
        book.setId(1L);
        BookResponseDTO response = new BookResponseDTO();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(mapper.convert(book, BookResponseDTO.class)).thenReturn(response);

        BookResponseDTO result = bookService.getBookDetail(1L);

        assertEquals(response, result);
    }

    @Test
    void testGetBookDetailNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        CustomException ex = assertThrows(CustomException.class, () -> bookService.getBookDetail(1L));
        assertEquals("Livro não encontrado", ex.getMessage());
    }

    @Test
    void testRentOrReturnBook() {
        Book book = new Book();
        book.setId(1L);
        book.setStatus(BookStatus.AVAILABLE);
        Book updatedBook = new Book();
        updatedBook.setStatus(BookStatus.RENTED);
        BookResponseDTO response = new BookResponseDTO();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(updatedBook);
        when(mapper.convert(updatedBook, BookResponseDTO.class)).thenReturn(response);

        BookResponseDTO result = bookService.rentOrReturnBook(1L);
        assertEquals(response, result);
    }

    @Test
    void testDeleteBookSuccess() {
        Book book = new Book();
        book.setId(1L);
        book.setStatus(BookStatus.AVAILABLE);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.deleteBook(1L);

        verify(bookRepository).delete(book);
    }

    @Test
    void testDeleteBookRented() {
        Book book = new Book();
        book.setId(1L);
        book.setStatus(BookStatus.RENTED);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        CustomException ex = assertThrows(CustomException.class, () -> bookService.deleteBook(1L));
        assertEquals("Livro alugado não pode ser excluído", ex.getMessage());
    }

    @Test
    void testDeleteBookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () -> bookService.deleteBook(1L));
        assertEquals("Livro não encontrado", ex.getMessage());
    }
}
