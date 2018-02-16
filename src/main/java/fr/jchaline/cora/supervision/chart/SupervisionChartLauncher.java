package fr.jchaline.cora.supervision.chart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;

import fr.jchaline.cora.supervision.chart.service.FactoryService;

@SpringBootApplication
public class SupervisionChartLauncher {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SupervisionChartLauncher.class);

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(SupervisionChartLauncher.class, args);
		
		Resource resourceConfig = ctx.getResource("classpath:servers.json");
		
		FactoryService factory = ctx.getBean(FactoryService.class);
		factory.generateData(resourceConfig);
		
		LOGGER.info("SupervisionChart Start successfull, wait for http layer ...");
	}
}
