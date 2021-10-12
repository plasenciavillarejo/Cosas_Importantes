package com.cosas.importantes.script.crontab;

import org.quartz.impl.JobDetailImpl;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import es.chsegura.persistenciageiser.app.Prueba;
import static org.quartz.JobBuilder.*;

import org.junit.Test;

public class AppTest {

	@Test
	public void testPrueba() throws SchedulerException {

		// Creacion de una instacia de Scheduler
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		System.out.println("Iniciando Scheduler...");
		scheduler.start();

		// Creacion una instacia de JobDetail
		JobDetailImpl jobDetail = (JobDetailImpl) newJob(Prueba.class).withIdentity("job1_1", "jGroup1").build();

		// Se ejecuta a las 12 de la mañana -> 0 0 12 * * ?
		CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger3", "group1")
				.withSchedule(CronScheduleBuilder.cronSchedule("/10 * * * * ?")).forJob("job1_1", "jGroup1").build();

		// Registro dentro del Scheduler
		scheduler.scheduleJob(jobDetail, trigger);

//	 while (true) {
//		 try {
//			Thread.sleep(60000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	 }

		// Detenemos la ejecución de la
		// instancia de Scheduler
		// scheduler.shutdown();

	}
}
