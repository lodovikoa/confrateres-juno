package br.com.juno.integration.api.confrateres.utils.validador;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang3.StringUtils;

import br.com.juno.integration.api.confrateres.exception.NegocioException;


@FacesValidator("validator_cnpj")
public class CnpjValidator implements Validator<Object> {

	// https://pablonobrega.wordpress.com/2009/08/25/implcnpjementando-converter-e-validator-de-cnpj/

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		
		String dsCnpj = (String) value;
		
		if(StringUtils.isBlank(dsCnpj)) {
			return;
		}
		
		if(!validaCnpj(dsCnpj)) {
			throw new NegocioException("CNPJ informado na tela não é válido .");
		}

	}

	private boolean validaCnpj(String dsCnpj) {
		if(StringUtils.isBlank(dsCnpj) || dsCnpj.length() != 14)
			return false;

		try {
			Long.parseLong(dsCnpj);
		} catch (NumberFormatException e) { // CNPJ não possui somente números
			return false;
		}

		int soma = 0;
		String cnpj_calc = dsCnpj.substring(0, 12);

		char chr_cnpj[] = dsCnpj.toCharArray();
		for(int i = 0; i < 4; i++)
			if(chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9)
				soma += (chr_cnpj[i] - 48) * (6 - (i + 1));

		for(int i = 0; i < 8; i++)
			if(chr_cnpj[i + 4] - 48 >= 0 && chr_cnpj[i + 4] - 48 <= 9)
				soma += (chr_cnpj[i + 4] - 48) * (10 - (i + 1));

		int dig = 11 - soma % 11;
		cnpj_calc = (new StringBuilder(String.valueOf(cnpj_calc))).append(dig != 10 && dig != 11 ? Integer.toString(dig) : "0").toString();
		soma = 0;
		for(int i = 0; i < 5; i++)
			if(chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9)
				soma += (chr_cnpj[i] - 48) * (7 - (i + 1));


		for(int i = 0; i < 8; i++)
			if(chr_cnpj[i + 5] - 48 >= 0 && chr_cnpj[i + 5] - 48 <= 9)
				soma += (chr_cnpj[i + 5] - 48) * (10 - (i + 1));

		dig = 11 - soma % 11;
		cnpj_calc = (new StringBuilder(String.valueOf(cnpj_calc))).append(dig != 10 && dig != 11 ? Integer.toString(dig) : "0").toString();

		return dsCnpj.equals(cnpj_calc);

	}
}
