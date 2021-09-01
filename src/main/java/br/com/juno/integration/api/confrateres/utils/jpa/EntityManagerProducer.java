package br.com.juno.integration.api.confrateres.utils.jpa;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Session;

@ApplicationScoped
public class EntityManagerProducer {
	
	private EntityManagerFactory factory;
	
	public EntityManagerProducer() {
		this.factory = Persistence.createEntityManagerFactory("Confrateres00");
	}
	
	@Produces
	@RequestScoped
	public Session createEntityManager(){
		return (Session) factory.createEntityManager();
	}
	
	public void closeEntityManager(@Disposes EntityManager manager){
		manager.close();
	}

}
