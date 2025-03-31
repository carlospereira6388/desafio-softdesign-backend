package br.com.softdesign.desafio.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserResponseDTO {
	
	@ApiModelProperty(position = 0)
	private String name;
	
	@ApiModelProperty(position = 1)
	private String email;
}