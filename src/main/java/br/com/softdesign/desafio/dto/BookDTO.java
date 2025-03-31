package br.com.softdesign.desafio.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookDTO {
    
    @NotBlank(message = "Título é obrigatório")
    private String title;
    
    @NotBlank(message = "Autor é obrigatório")
    private String author;
    
    @NotNull(message = "Ano é obrigatório")
    private Integer year;
    
    @NotBlank(message = "Gênero é obrigatório")
    private String genre;
}
