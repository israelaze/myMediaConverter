package br.com.myMediaConverter.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.myMediaConverter.dto.FileDto;
import br.com.myMediaConverter.services.AudioExtractorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "Aúdio Útils")
@RequestMapping(value = "/api/audioController")
public class AudioController {

	private final AudioExtractorService service;

	@GetMapping("/extractToDirectory")
	@Operation(summary = "Extrair 1 ou mais aúdios para o diretório escolhido")
	public ResponseEntity<String> extractAudioToDirectory(@RequestParam List<String> urls) throws IOException {

		if (urls.isEmpty()) {
			return ResponseEntity.badRequest().body("NENHUMA URL FOI INSERIDA!");
		}

		for (String videoUrl : urls) {
			
			try {
				
				service.extractAudioToDirectory(videoUrl);

			} catch (Exception e) {
				log.error("ERRO: " + e.getMessage());
				return ResponseEntity.internalServerError().build();
			}

		}
		String s = "";
		if (urls.size() > 1) {
			s = "S";
		}

		return ResponseEntity.ok().body(urls.size() + " AÚDIO" + s + " EXTRAÍDO" + s + " COM SUCESSO!");

	}
	
	@GetMapping("/extractToDirectoryWIthTime")
	@Operation(summary = "Extrair 1 aúdio com intervalo de tempo específico")
	public ResponseEntity<String> extractAudioToDirectoryWithTime(@RequestParam String videoUrl, @RequestParam String startTime, @RequestParam String endTime) throws IOException {
		
		if (videoUrl.isEmpty()) {
			return ResponseEntity.badRequest().body("NENHUMA URL FOI INSERIDA!");
		}

		try {
			
			service.extractAudioToDirectoryWithTime(videoUrl, startTime, endTime);
			
		} catch (Exception e) {
			log.error("ERRO: " + e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
			
		return ResponseEntity.ok().body("AÚDIO EXTRAÍDO COM SUCESSO!");
			
		
		
	}

	@GetMapping("/downloadAudio")
	@Operation(summary = "Fazer o download do aúdio")
	public ResponseEntity<Resource> downloadAudio(@RequestParam String videoUrl) {

		try {
		
			FileDto dto = service.extractAndDownloadAudio(videoUrl);

			// Prepara o arquivo para ser enviado como resposta
			FileSystemResource resource = new FileSystemResource(dto.getFile());
						
			// Define os cabeçalhos da resposta para indicar que é um arquivo para download
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData("attachment", dto.getFileName()+".mp3");
			headers.setContentLength(resource.contentLength());

			log.info("DOWNLOAD EFETUADO COM SUCESSO!");

			// Retorna o arquivo como parte da resposta
			return new ResponseEntity<>(resource, headers, HttpStatus.OK);

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
