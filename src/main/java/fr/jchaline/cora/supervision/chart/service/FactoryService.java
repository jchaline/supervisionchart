package fr.jchaline.cora.supervision.chart.service;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.jchaline.cora.supervision.chart.dao.ApplicationDao;
import fr.jchaline.cora.supervision.chart.dao.ServeurDao;
import fr.jchaline.cora.supervision.chart.domain.Application;
import fr.jchaline.cora.supervision.chart.domain.Serveur;

@Transactional(readOnly = true)
@Service
public class FactoryService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FactoryService.class);
	
	@Autowired
	private ApplicationDao applicationDao;
	
	@Autowired
	private ServeurDao serveurDao;

	@Transactional(readOnly = false)
	public void generateData(Resource resourceConfig) {
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<Application>> mapType = new TypeReference<List<Application>>() {};
		try {
			List<Application> readValue = mapper.readValue(resourceConfig.getInputStream(), mapType);
			for (Application a : readValue) {
				applicationDao.save(a);
				
				for (Serveur s : a.getServeurs()) {
					s.setApplication(a);
					serveurDao.save(s);
				}
				
				applicationDao.save(a);
				
				LOGGER.info(a.getLibelle() + " save");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
