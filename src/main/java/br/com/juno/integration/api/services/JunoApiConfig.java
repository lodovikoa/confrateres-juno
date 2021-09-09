package br.com.juno.integration.api.services;

import org.apache.commons.lang3.StringUtils;

import br.com.juno.integration.api.config.UnirestConfig;
import br.com.juno.integration.api.confrateres.bo.JunoPagamentosBO;
import br.com.juno.integration.api.confrateres.exception.NegocioException;
import br.com.juno.integration.api.confrateres.model.JunoConfig;
import br.com.juno.integration.api.confrateres.utils.Uteis;

public final class JunoApiConfig {

    public static Long token_timeout; // = 60 * 60 * 23 * 1000L ; // = 23h representado em milesseguntos
    public static Long cache_timeout; // = 6 * 60 * 60 * 1000L;

    private String clientId; 
    private String clientSecret; 
    private String resourceToken; 
    private String publicToken;
    private Environment env;
    
    JunoPagamentosBO junoPagamentosBO;

    public JunoApiConfig() {
    	junoPagamentosBO = new JunoPagamentosBO();
    	this.clientId = JunoConfig.getDsClientId();
    	this.clientSecret = JunoConfig.getDsClientSecret();
    	this.resourceToken = JunoConfig.getDsResourceToken();
    	this.publicToken = JunoConfig.getDsPublicToken();
    	JunoApiConfig.token_timeout = JunoConfig.getNnTokenTimeout();
    	JunoApiConfig.cache_timeout = JunoConfig.getDsCacheTimeout();
    	
    	if(Uteis.getDsAmbiente() != null && Uteis.getDsAmbiente().equalsIgnoreCase("HML")) {
    		sandbox();
    	} else if(Uteis.getDsAmbiente() != null && Uteis.getDsAmbiente().equalsIgnoreCase("PRD")) {
    		production();
    	} else {
    		throw new NegocioException("ERRO Não foi possível identificar ambiente - sandbox() ou production().");
    	}
    	
    	
        UnirestConfig.configure();
    }

    public JunoApiConfig production() {
        this.env = new ProductionEnviroment();
        env.setAuthorizationServerEndpoint(JunoConfig.getDsAuthorizationServer());
        env.setResourceServerEndpoint(JunoConfig.getDsResouceServer());
        
        return this;
    }

    public JunoApiConfig sandbox() {
        this.env = new SandboxEnvironment();
        env.setAuthorizationServerEndpoint(JunoConfig.getDsAuthorizationServer());
        env.setResourceServerEndpoint(JunoConfig.getDsResouceServer());
        
        return this;
    }

    public JunoApiConfig development() {
        this.env = new DevelopmentEnviroment();
        env.setAuthorizationServerEndpoint(JunoConfig.getDsAuthorizationServer());
        env.setResourceServerEndpoint(JunoConfig.getDsResouceServer());
        
        return this;
    }

    public JunoApiConfig unitTests() {
        this.env = new UnitTestEnviroment();
        env.setAuthorizationServerEndpoint(JunoConfig.getDsAuthorizationServer());
        env.setResourceServerEndpoint(JunoConfig.getDsResouceServer());
        
        return this;
    }

    public JunoApiConfig setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public JunoApiConfig setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public JunoApiConfig setResourceToken(String resourceToken) {
        this.resourceToken = resourceToken;
        return this;
    }

    public JunoApiConfig setPublicToken(String publicToken) {
        this.publicToken = publicToken;
        return this;
    }

    public Environment getEnv() {
        return env;
    }

    public String getAuthorizationEndpoint() {
        return env.getAuthorizationServerEndpoint();
    }

    public String getResourceEndpoint() {
        return env.getResourceServerEndpoint();
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getResourceToken() {
        return resourceToken;
    }

    public String getPublicToken() {
        return publicToken;
    }

    public boolean hasPublicTokenConfigured() {
        return StringUtils.isNotBlank(publicToken);
    }

    public boolean isCredentialsComplete() {
        return StringUtils.isNoneBlank(clientId, clientSecret, resourceToken);
    }

    public boolean isConfigured() {
        return isCredentialsComplete() && env != null;
    }

    public interface Environment {

        String getAuthorizationServerEndpoint();
        void setAuthorizationServerEndpoint(String url);
        
        String getResourceServerEndpoint();
        void setResourceServerEndpoint(String url);
       

    }

    public static class UnitTestEnviroment implements Environment {

        private static String authorization_server; // = "http://localhost:8888";
        private static String resource_server; // = "http://localhost:8888";

        @Override
        public String getAuthorizationServerEndpoint() {
            return authorization_server;
        }

        @Override
        public String getResourceServerEndpoint() {
            return resource_server;
        }


		@Override
		public void setAuthorizationServerEndpoint(String url) {
			UnitTestEnviroment.authorization_server = url;
			
		}

		@Override
		public void setResourceServerEndpoint(String url) {
			UnitTestEnviroment.resource_server = url;
			
		}

    }

    public static class DevelopmentEnviroment implements Environment {

        private static String authorization_server; // = "http://localhost:8084";
        private static String resource_server; // = "http://localhost:8082";

        @Override
        public String getAuthorizationServerEndpoint() {
            return authorization_server;
        }

        @Override
        public String getResourceServerEndpoint() {
            return resource_server;
        }

		@Override
		public void setAuthorizationServerEndpoint(String url) {
			DevelopmentEnviroment.authorization_server = url;
			
		}

		@Override
		public void setResourceServerEndpoint(String url) {
			DevelopmentEnviroment.resource_server = url;
			
		}

    }

    public static class SandboxEnvironment implements Environment {

        private static String authorization_server; // = "https://sandbox.boletobancario.com/authorization-server";
        private static String resource_server; // = "https://sandbox.boletobancario.com/api-integration";

        @Override
        public String getAuthorizationServerEndpoint() {
            return authorization_server;
        }

        @Override
        public String getResourceServerEndpoint() {
            return resource_server;
        }

		@Override
		public void setAuthorizationServerEndpoint(String url) {
			SandboxEnvironment.authorization_server = url;
			
		}

		@Override
		public void setResourceServerEndpoint(String url) {
			SandboxEnvironment.resource_server = url;
			
		}
        

    }

    public static class ProductionEnviroment implements Environment {

        private static String authorization_server; // = "https://api.juno.com.br/authorization-server";
        private static String resource_server; // = "https://api.juno.com.br";

        @Override
        public String getAuthorizationServerEndpoint() {
            return authorization_server;
        }

        @Override
        public String getResourceServerEndpoint() {
            return resource_server;
        }

		@Override
		public void setAuthorizationServerEndpoint(String url) {
			ProductionEnviroment.authorization_server = url;
			
		}

		@Override
		public void setResourceServerEndpoint(String url) {
			ProductionEnviroment.resource_server = url;
			
		}

    }

}
