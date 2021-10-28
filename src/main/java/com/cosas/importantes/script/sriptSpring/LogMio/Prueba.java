package com.cosas.importantes.script.sriptSpring.LogMio;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import es.chsegura.persistenciageiser.service.ServicioPrueba;


//public class Prueba implements Job {
public class Prueba{
	

//	public void execute(JobExecutionContext context) throws JobExecutionException {
	@SuppressWarnings("resource")
	public static void main (String[] args) {
		
	/* Se llama al ApplicationContext para cargar los datos al inicar la aplicación spring de forma manual.
	 * Posteriormente se ejecuta el método que tenemos implementando en el ServicioPrueba.java */	
		
		try {
			var ctx = new AnnotationConfigApplicationContext();
			ctx.scan("es.chsegura");
			ctx.refresh();
			ServicioPrueba sp = (ServicioPrueba) ctx.getBean("servicioPrueba");
			sp.probarGeiser();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
};
