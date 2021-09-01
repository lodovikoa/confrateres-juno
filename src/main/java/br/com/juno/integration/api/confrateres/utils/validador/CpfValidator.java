package br.com.juno.integration.api.confrateres.utils.validador;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang3.StringUtils;

import br.com.juno.integration.api.confrateres.exception.NegocioException;

@FacesValidator("validator_cpf")
public class CpfValidator implements Validator<Object> {

	// Fonte: https://pablonobrega.wordpress.com/2009/08/10/implementando-converter-e-validator-de-cpf/

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		
		String dsCpf = (String) value;
		
		if(StringUtils.isEmpty(dsCpf)) {
			return;
		}
		
		if(!validaCPF(dsCpf)) {
			throw new NegocioException("CPF informado na tela não é válido .");
		}

	}


	private static boolean validaCPF(String dsCpf) {
		if (StringUtils.isBlank(dsCpf) || dsCpf.length() != 11 || isCPFPadrao(dsCpf))
			return false;

		try {
			Long.parseLong(dsCpf);
		} catch (NumberFormatException e) { // CPF não possui somente números
			return false;
		}

		return calcDigVerif(dsCpf.substring(0, 9)).equals(dsCpf.substring(9, 11));
	}

	/**
	 *
	 * @param cpf String valor a ser testado
	 * @return boolean indicando se o usuário entrou com um CPF padrão
	 */
	private static boolean isCPFPadrao(String dsCpf) {
		if (dsCpf.equals("11111111111") 
				|| dsCpf.equals("22222222222")
				|| dsCpf.equals("33333333333")
				|| dsCpf.equals("44444444444")
				|| dsCpf.equals("55555555555")
				|| dsCpf.equals("66666666666")
				|| dsCpf.equals("77777777777")
				|| dsCpf.equals("88888888888")
				|| dsCpf.equals("99999999999")) {

			return true;
		}

		return false;
	}

	private static String calcDigVerif(String num) {
		Integer primDig, segDig;
		int soma = 0, peso = 10;
		for (int i = 0; i < num.length(); i++)
			soma += Integer.parseInt(num.substring(i, i + 1)) * peso--;

		if (soma % 11 == 0 | soma % 11 == 1)
			primDig = new Integer(0);
		else
			primDig = new Integer(11 - (soma % 11));

		soma = 0;
		peso = 11;
		for (int i = 0; i < num.length(); i++)
			soma += Integer.parseInt(num.substring(i, i + 1)) * peso--;

		soma += primDig.intValue() * 2;
		if (soma % 11 == 0 | soma % 11 == 1)
			segDig = new Integer(0);
		else
			segDig = new Integer(11 - (soma % 11));

		return primDig.toString() + segDig.toString();
	}	

}
