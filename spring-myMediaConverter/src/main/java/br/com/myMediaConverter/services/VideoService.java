package br.com.myMediaConverter.services;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VideoService {
	
	private final String MENSAGEM_SUCESSO = "SUCESSO";
	private final String MENSAGEM_ERRO = "ERRO";
	

	public String extractVideoToDirectory(String videoUrl) {

		String outputDirectory = "C:/Users/Israel/Desktop/Nova/";
		criarNovaPasta(outputDirectory);
		
		try {
			
			String command = "yt-dlp --format [height<=1080] -o "
					+ outputDirectory 
					+ "%(title)s.%(ext)s " 
					+videoUrl;
			
			Process process = Runtime.getRuntime().exec(command);

            // Aguardando o término do processo
            int exitVal = process.waitFor();
            if (exitVal == 0) {
                return MENSAGEM_SUCESSO;
            } else {
                return MENSAGEM_ERRO;
            }
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return MENSAGEM_ERRO;
		}
	}
	
	
	public String extractVideoToDirectoryWithTime(String videoUrl, String startTime, String endTime) {

		String outputDirectory = "C:/Users/Israel/Desktop/Nova/";
		criarNovaPasta(outputDirectory);
		
		try {
			
			String command = String.format("yt-dlp -f 18 "
					+ "\"%s\" --external-downloader ffmpeg --external-downloader-args "
					+ "\"ffmpeg_i:-ss %s -to %s\" -o "
					+ "\"%s/%%(title)s.%%(ext)s\"",
                    videoUrl, startTime, endTime, outputDirectory);
			
			Process process = Runtime.getRuntime().exec(command);
			
            // Aguardando o término do processo
            int exitVal = process.waitFor();
            if (exitVal == 0) {
                return MENSAGEM_SUCESSO;
            } else {
                return MENSAGEM_ERRO;
            }
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			throw new InternalError(MENSAGEM_ERRO);
		}
	}
	
	private void criarNovaPasta(String outputDirectory) {
		
		File directory = new File(outputDirectory);
		
		if(!directory.exists()) {
			
			if(directory.mkdirs()) {
				log.info("Pasta de saída criada em: " + outputDirectory);
			}else {
				log.error("Falha ao criar a pasta de saída");
				return;
			}
		}
	}

}
