package com.dataminer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Main class of DataMiner
 *
 * @author Vasil.Dimitrov^2
 *
 */
@SpringBootApplication
// може да се добави (scanBasePackages = {"hello", "controller", "hibernate"}) за да се добавят допълнителни пакети за сканиране, които не са в същия пакет като този клас
public class DataMinerApplication {

	public static void main(String[] args) {
		boolean shortStart = true;

		if (shortStart) {
			shortStart(args);
		} else {
			longStart(args);
		}
	}

	public static ApplicationContext shortStart(String[] args) {
		return SpringApplication.run(DataMinerApplication.class, args);
	}

	/**
	 * Method used for listing all the beans at the start of the application.
	 * 
	 * @param args
	 */
	public static void longStart(String[] args) {
		ApplicationContext ctx = shortStart(args);

		System.out.println("Beans:");
		String[] beanNames = ctx.getBeanDefinitionNames();
		Arrays.sort(beanNames);
		for (String beanName : beanNames) {
			System.out.println(beanName);
		}
	}

}
