package br.com.softdesign.desafio.controller;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.softdesign.desafio.dto.BookDTO;
import br.com.softdesign.desafio.dto.BookResponseDTO;
import br.com.softdesign.desafio.service.BookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("/api/v1/books")
@Api(tags = "Books")
@PreAuthorize("isAuthenticated()")
public class BookController {

    private final BookService bookService;
    
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    
    @PostMapping
    @ApiOperation(
        value = "Cadastrar um novo livro",
        response = BookResponseDTO.class,
        authorizations = { @Authorization(value = "apiKey") }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Livro criado com sucesso"),
        @ApiResponse(code = 400, message = "Solicitação inválida")
    })
    public ResponseEntity<BookResponseDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
        BookResponseDTO createdBook = bookService.createBook(bookDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }
    
    @GetMapping
    @ApiOperation(
        value = "Listar livros com filtros e paginação",
        response = BookResponseDTO.class,
        responseContainer = "Page",
        authorizations = { @Authorization(value = "apiKey") }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Lista de livros obtida com sucesso")
    })
    public ResponseEntity<Page<BookResponseDTO>> listBooks(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String genre,
            Pageable pageable) {
        Page<BookResponseDTO> books = bookService.listBooks(author, year, genre, pageable);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/{id}")
    @ApiOperation(
        value = "Detalhar livro",
        response = BookResponseDTO.class,
        authorizations = { @Authorization(value = "apiKey") }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Detalhes do livro obtidos com sucesso"),
        @ApiResponse(code = 404, message = "Livro não encontrado")
    })
    public ResponseEntity<BookResponseDTO> getBookDetail(@PathVariable Long id) {
        BookResponseDTO book = bookService.getBookDetail(id);
        return ResponseEntity.ok(book);
    }
    
    @PatchMapping("/{id}/rent")
    @ApiOperation(
        value = "Alugar/Devolver livro",
        response = BookResponseDTO.class,
        authorizations = { @Authorization(value = "apiKey") }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Status do livro atualizado com sucesso"),
        @ApiResponse(code = 404, message = "Livro não encontrado"),
        @ApiResponse(code = 422, message = "Operação não permitida")
    })
    public ResponseEntity<BookResponseDTO> rentOrReturnBook(@PathVariable Long id) {
        BookResponseDTO updatedBook = bookService.rentOrReturnBook(id);
        return ResponseEntity.ok(updatedBook);
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation(
        value = "Excluir livro",
        authorizations = { @Authorization(value = "apiKey") }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Livro excluído com sucesso"),
        @ApiResponse(code = 404, message = "Livro não encontrado"),
        @ApiResponse(code = 422, message = "Livro alugado não pode ser excluído")
    })
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
