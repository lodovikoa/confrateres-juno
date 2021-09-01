package br.com.juno.integration.api.confrateres.utils.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

@FacesConverter("converter_cpf")
public class CpfConverter implements Converter<Object> {

	// Referencia: https://pablonobrega.wordpress.com/2009/08/10/implementando-converter-e-validator-de-cpf/
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
		
		String dsCpf = value;
		if(StringUtils.isNotBlank(value)) {
			dsCpf = value.replaceAll("[^0-9]", "");
		}
		
		return dsCpf;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
		
		String dsCpf = ((String)value).replaceAll("[^0-9]", "");
		if(StringUtils.isNotBlank(dsCpf) && dsCpf.length() == 11) {
			dsCpf = dsCpf.substring(0, 3) + "." + dsCpf.substring(3, 6) + "." + dsCpf.substring(6, 9) + "-" + dsCpf.substring(9, 11);
		}
		
		return dsCpf;
	}

}
