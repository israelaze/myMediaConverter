package br.com.myMediaConverter.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.myMediaConverter.services.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "Video Útils")
@RequestMapping(value = "/api/videoController")
public class VideoController {

	private final VideoService service;

	@GetMapping("/extractToDirectory")
	@Operation(summary = "Extrair 1 ou mais vídeos para o diretório escolhido")
	public ResponseEntity<String> extractVideoToDirectory(@RequestParam List<String> urls) {

		String mensagem = "";
		int countSucesso = 0;
		int countErro = 0;

		if (urls.isEmpty()) {
			return ResponseEntity.badRequest().body("NENHUMA URL FOI INSERIDA!");
		}

		for (String videoUrl : urls) {

			try {

				mensagem = service.extractVideoToDirectory(videoUrl);

				if (mensagem.contains("SUCESSO")) {
					countSucesso++;
				} else {
					countErro++;
				}

			} catch (Exception e) {
				log.error(mensagem + e.getMessage());
				return ResponseEntity.internalServerError().build();
			}
		}

		mensagem = mensagens(urls.size(), countSucesso, countErro);

		return ResponseEntity.ok().body(mensagem);

	}
	
	
	@GetMapping("/extractToDirectoryWithTime")
	@Operation(summary = "Extrair 1 vídeo com delimitador de tempo")
	public ResponseEntity<String> extractVideoToDirectoryWithTime(@RequestParam String videoUrl, @RequestParam String startTime,
            @RequestParam String endTime) {
		
		String mensagem = "";
		
		if (videoUrl.isEmpty()) {
			return ResponseEntity.badRequest().body("NENHUMA URL FOI INSERIDA!");
		}
		
		try {
			
			mensagem = service.extractVideoToDirectoryWithTime(videoUrl, startTime, endTime);
			return ResponseEntity.ok().body(mensagem);
			
		} catch (Exception e) {
			log.error(mensagem + e.getMessage());
			return ResponseEntity.internalServerError().build();
		}					
				
	}
	
	
	private String mensagens(int urlsSize, int countSucesso, int countErro) {
		
		String mensagem = "";
		
		if (urlsSize > 1 && countSucesso == urlsSize) {
			mensagem = "TODOS OS VÍDEOS FORAM BAIXADOS COM SUCESSO!";
		} else if (urlsSize > 1 && countErro == urlsSize) {
			mensagem = "QUE PENA...TODOS OS DOWNLOADS FALHARAM!";
		} else if (urlsSize == 1 && countSucesso == 1) {
			mensagem = "VÍDEO BAIXADO COM SUCESSO!";
		} else if (urlsSize == 1 && countErro == 1) {
			mensagem = "HOUVE UM ERRO AO TENTAR BAIXAR O VÍDEO!";
		} else if (urlsSize > 1 && countSucesso != urlsSize) {
			if (countSucesso == 1) {
				mensagem = "APENAS " + countSucesso + " VÍDEO FOi BAIXADO COM SUCESSO!";
			} else {
				mensagem = "APENAS " + countSucesso + " VÍDEOS FORAM BAIXADOS COM SUCESSO!";
			}
		}
		
		return mensagem;
		
	}

}
