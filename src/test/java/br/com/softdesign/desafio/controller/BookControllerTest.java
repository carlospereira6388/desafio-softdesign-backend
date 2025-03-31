package br.com.softdesign.desafio.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.softdesign.desafio.dto.BookDTO;
import br.com.softdesign.desafio.dto.BookResponseDTO;
import br.com.softdesign.desafio.service.BookService;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private BookService bookService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @WithMockUser
    void shouldCreateBook() throws Exception {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("Livro Teste");
        bookDTO.setAuthor("Autor Teste");
        bookDTO.setYear(2025);
        bookDTO.setGenre("Ficção");
        
        BookResponseDTO responseDTO = new BookResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setTitle("Livro Teste");
        responseDTO.setAuthor("Autor Teste");
        responseDTO.setYear(2025);
        responseDTO.setGenre("Ficção");
        responseDTO.setStatus("AVAILABLE");
        
        when(bookService.createBook(any(BookDTO.class))).thenReturn(responseDTO);
        
        mockMvc.perform(post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookDTO)))
                .andExpect(status().isCreated());
    }
}
