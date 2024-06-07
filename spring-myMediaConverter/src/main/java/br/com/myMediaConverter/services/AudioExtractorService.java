package br.com.myMediaConverter.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import br.com.myMediaConverter.dto.FileDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AudioExtractorService {

	private final int BITRATE = 128;	

	public void extractAudioToDirectory(String videoUrl) {

		String outputDirectory = "C:/Users/Israel/Desktop/Nova/";
		try {
			// Comando para executar o YT-DLP via linha de comando
			String command = "yt-dlp --extract-audio --audio-format mp3 "
					+ "--audio-quality " + BITRATE + "K " 
					+ "-o " + outputDirectory 
					+ "%(title)s.%(ext)s " + videoUrl;
		
			criarNovaPasta(outputDirectory);
			
			// Executar o comando e capturar a saída
			Process process = Runtime.getRuntime().exec(command);

			// Aguardar o término do processo
			process.waitFor();

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void extractAudioToDirectoryWithTime(String videoUrl, String startTime, String endTime) {
		
		String outputDirectory = "C:/Users/Israel/Desktop/Nova/";
		try {
			
			// Comando para executar o YT-DLP via linha de comando
			 String command = String.format("yt-dlp --extract-audio --audio-format mp3 --audio-quality %dK " +
	                    "--download-sections \"*%s-%s\" -o \"%s%%(title)s.%%(ext)s\" %s",
	                    BITRATE, startTime, endTime, outputDirectory, videoUrl);
			
			criarNovaPasta(outputDirectory);
			
			// Executar o comando e capturar a saída
			Process process = Runtime.getRuntime().exec(command);
			
			// Aguardar o término do processo
			process.waitFor();
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public FileDto extractAndDownloadAudio(String videoUrl) throws IOException, InterruptedException {
		
		String[] commandTitle = {"yt-dlp", "--get-title", "%(title)s.%(ext)s", videoUrl};
		Process processTitle = Runtime.getRuntime().exec(commandTitle);
		processTitle.waitFor();
		
		// Lê a saída do comando
		InputStream inputStream = processTitle.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        
        // Extrai o título do vídeo da saída
        String videoTitle = reader.readLine().trim();
        reader.close();
        
        // Substitui caracteres especiais por espaço em branco e mantém apenas um espaço entre as palavras
        videoTitle = videoTitle.replaceAll("[*|\\\\%&/#@\"]", " ").replaceAll("\\s+", " ");
			
		// Constrói o comando para extrair o áudio usando yt-dlp
        String[] command = {"yt-dlp", "--extract-audio", "--audio-format", "mp3", "-o", videoTitle, videoUrl};

		// Executar o comando
		Process process = Runtime.getRuntime().exec(command);
		process.waitFor();
	
        // Verifica se o arquivo de áudio foi criado
        File audioFile = new File(videoTitle+".mp3");
        if (!audioFile.exists()) {
            throw new IOException("Erro ao baixar o áudio.");
        }

        // Cria um arquivo temporário e copia o conteúdo do arquivo de áudio para ele
        File tempFile = File.createTempFile(videoTitle, ".mp3");
        FileUtils.copyFile(audioFile, tempFile);
        
        // Deleta o arquivo de áudio original
        audioFile.delete();
        
        FileDto fileDto = new FileDto();
        fileDto.setFile(tempFile);
        fileDto.setFileName(videoTitle);

        return fileDto;
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
