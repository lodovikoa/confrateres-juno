package br.com.juno.integration.api;

import java.math.BigDecimal;
import java.util.List;

import br.com.juno.integration.api.services.JunoApiManager;
import br.com.juno.integration.api.services.request.charge.ChargeCreateRequest;
import br.com.juno.integration.api.services.request.charge.ChargeCreateRequest.Billing;
import br.com.juno.integration.api.services.request.charge.ChargeCreateRequest.Billing.Address;
import br.com.juno.integration.api.services.request.charge.ChargeCreateRequest.Charge;

public class ObterTokenAutorizacao {

	public static void main(String[] args) {

		// Verificar se o accessToken é válido, se não obter novo token.
		String accessToken = JunoApiManager.getAuthorizationService().getToken();
		System.out.println(accessToken);



		ChargeCreateRequest request = createChargeRequest();

		List<br.com.juno.integration.api.model.Charge> createdCharges = JunoApiManager.getChargeService().create(request);

		br.com.juno.integration.api.model.Charge createdCharge = createdCharges.get(0);


	}

	private static ChargeCreateRequest createChargeRequest() {
		ChargeCreateRequest.Charge charge = new Charge("CobrancaTest");
		charge.setAmount(BigDecimal.valueOf(100.0D));
		ChargeCreateRequest.Billing billing = new Billing();
		billing.setName("João Mane Doe da Silva Junior");
		billing.setDocument("06085371950");
		billing.setEmail("lodoviko@gmail.com");
		billing.setNotify(true);
		ChargeCreateRequest.Billing.Address address = new Address();
		address.setStreet("Rua Alguma coisa");
		address.setNumber("12");
		address.setCity("Curitiba");
		address.setState("PR");
		address.setPostCode("81270350");
		billing.setAddress(address);

		ChargeCreateRequest request = new ChargeCreateRequest(charge, billing);
		return request;
	}

}
