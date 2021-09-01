package br.com.juno.integration.api.confrateres.utils.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;


@FacesConverter("converter_localdate")
public class LocalDateFacesConverter implements Converter<Object> {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String stringValue) {
		if (null == stringValue || stringValue.isEmpty()) {
			return null;
		}

		LocalDate localDate;

		try {
			localDate = LocalDate.parse(stringValue, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

		} catch (DateTimeParseException e) {

			throw new ConverterException("O ano deve conter 4 dígitos. Exemplo: 13/11/2015.");
		}

		return localDate;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object localDateValue) {
		if (null == localDateValue) {
			return "";
		}

		String dsRetorno = null;

		// Converter data e hora (LocalDateTime) em somente uma data para exibição em tela.
		if(localDateValue.toString().length() > 10 && localDateValue.toString().contains("T")) {
			String dtDataHora = ((LocalDateTime) localDateValue).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

			dsRetorno = dtDataHora.substring(0, dtDataHora.indexOf(" "));
		} else {
			dsRetorno = ((LocalDate) localDateValue).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		}


		return dsRetorno; //  ((LocalDate) localDateValue).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

	}

}
