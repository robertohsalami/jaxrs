package br.com.alura.loja;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.alura.loja.modelo.Projeto;

public class ProjetoTest {
	
	private HttpServer servidor;
	private WebTarget target;
	private Client client;

	@Before
	public void iniciarServidor() {
		servidor = Servidor.inicializaServidor();
		ClientConfig config = new ClientConfig();
		config.register(new LoggingFilter());	
		client = ClientBuilder.newClient(config);
		target = client.target("http://localhost:8080");	
	}
	
	@After
	public void derrubaServidor() {
		servidor.stop();
	}
	
	@Test
	public void testaQueBuscarUmProjetoTrazOProjetoEsperado() {		
		Projeto projeto = target.path("/projetos/1").request().get(Projeto.class);		
		Assert.assertEquals(1, projeto.getId());
	}
	
	@Test
	public void testeQueAdicionaUmProjetoNoServidor() {		
		Projeto projeto = new Projeto("Projeto JAX-RS", 10, 2017);		
		Entity<Projeto> entity = Entity.entity(projeto, MediaType.APPLICATION_XML);
		Response response = target.path("/projetos").request().post(entity);
		Assert.assertEquals(201, response.getStatus());
		
		String location = response.getHeaderString("Location");
		Projeto projetoCriado = client.target(location).request().get(Projeto.class);
		Assert.assertEquals("Projeto JAX-RS", projetoCriado.getNome());
	}

}
