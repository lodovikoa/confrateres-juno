package br.com.juno.integration.api.confrateres.model;

import java.io.Serializable;

import br.com.juno.integration.api.model.Charge;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class AgoChargeRecibo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Getter @Setter private AgoRecibo agoRecibo;
	@Getter @Setter Charge charge;

}
