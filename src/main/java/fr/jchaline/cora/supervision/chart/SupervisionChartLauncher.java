package fr.jchaline.cora.supervision.chart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SupervisionChartLauncher {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SupervisionChartLauncher.class);

	public static void main(String[] args) {
		SpringApplication.run(SupervisionChartLauncher.class, args);
		
		
		LOGGER.info("SupervisionChart Start successfull, wait for http layer ...");
	}
}
