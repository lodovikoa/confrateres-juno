package br.com.juno.integration.api.confrateres.utils.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.juno.integration.api.confrateres.exception.NegocioException;


@FacesConverter("converter_localdatetime")
public class LocalDateTimeFacesConverter implements Converter<Object> {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String stringValue) {
		if (null == stringValue || stringValue.isEmpty()) {
			return null;
		}
		LocalDateTime localDateTime = null;
		try {
			// Se a String conter somente a data com 10 caracteres, execmplo "2017-01-28", incluir a hora no formato "T00:00:00" para permitir transformar em LocalDateTime
			if(stringValue.length() <= 10){		
				LocalDate localDate = LocalDate.parse(stringValue.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
				localDateTime = LocalDateTime.parse(localDate.toString() + "T00:00:00");
			} else{
				localDateTime = LocalDateTime.parse(stringValue.trim(),	DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZoneId.systemDefault()));
			}

		} catch (DateTimeParseException e) {
			if(stringValue.length() <= 10)
				throw new NegocioException("O formato da data e hora deve ser 15/11/2015.");
			else
				throw new NegocioException("O formato da data e hora deve ser 15/11/2015 12:00:00.");
		}

		return localDateTime;


	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object localDateTimeValue) {
		if (null == localDateTimeValue) {
			return "";
		}

		return ((LocalDateTime) localDateTimeValue).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZoneId.systemDefault()));
	}

}
