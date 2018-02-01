package fr.jchaline.cora.supervision.chart.config.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

/**
 * Converter for http Controller endpoint
 * 
 * @author jeremy
 *
 */
public class LocalDateConverter implements Converter<String, LocalDate> {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Override
	public LocalDate convert(String source) {
		if (StringUtils.isBlank(source)) {
			return null;
		}

		return LocalDate.parse(source, DATE_TIME_FORMATTER);
	}

}
