package br.com.softdesign.desafio.dto;

import lombok.Data;

@Data
public class BookResponseDTO {
    
    private Long id;
    private String title;
    private String author;
    private Integer year;
    private String genre;
    private String status;
}