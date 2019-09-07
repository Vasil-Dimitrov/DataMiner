package com.dataminer;

import java.util.Arrays;

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

	public static void shortStart(String[] args) {
		SpringApplication.run(DataMinerApplication.class, args);
	}

	public static void longStart(String[] args) {
		ApplicationContext ctx = SpringApplication.run(DataMinerApplication.class, args);

		System.out.println("Beans:");
		String[] beanNames = ctx.getBeanDefinitionNames();
		Arrays.sort(beanNames);
		for (String beanName : beanNames) {
			System.out.println(beanName);
		}
	}

}
