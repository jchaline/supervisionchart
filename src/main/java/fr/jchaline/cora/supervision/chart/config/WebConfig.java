package fr.jchaline.cora.supervision.chart.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import fr.jchaline.cora.supervision.chart.config.converter.LocalDateConverter;

@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

	// work with curl, with command like :
	// curl -H 'Accept: application/json' -H 'Content-type: application/json' -u
	// user:password http://localhost:9080/dweller/list
	// work with browser with apache proxy config
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// registry.addMapping("/**");
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new LocalDateConverter());
	}
}
