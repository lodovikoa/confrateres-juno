package br.com.juno.integration.api.confrateres.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Properties;

import br.com.juno.integration.api.confrateres.exception.NegocioException;
import lombok.Getter;

public class Uteis {

	@Getter static private String dsAmbiente;

	// Retorna Data atual com hora
	public static LocalDateTime DataHoje() {	
		return LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
	}

	// Adicionar dias em uma data
	public static LocalDate AdcionarDiasNaData(LocalDate data, int qtdeDias) {
		LocalDate dataNova = data.plusDays(qtdeDias);	
		return dataNova;
	}

	public static void buscarAmbiente() {
		try {

			Properties props = new Properties();
			FileInputStream file = new FileInputStream(System.getenv("path_tomcat") + "/confrateres.properties");
			
			props.load(file);
			
			dsAmbiente = props.getProperty("prop.ambiente");
			
		} catch (FileNotFoundException e) {
			throw new NegocioException("Erro ao tentar ler arquivo de configuracões, confrateres.properties em: " + System.getenv("path_tomcat"));
		} catch (IOException e) {
			throw new NegocioException("Erro ao tentar ler arquivo de 'props.load(file)' arquivo confrateres.properties em: " + System.getenv("path_tomcat"));
		}
	}

}
