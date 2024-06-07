package br.com.myMediaConverter.dto;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDto {
	
	private String fileName;
	private File file;

}
