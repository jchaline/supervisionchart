package fr.jchaline.cora.supervision.chart.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.jchaline.cora.supervision.chart.dao.ApplicationDao;
import fr.jchaline.cora.supervision.chart.dao.ServeurDao;
import fr.jchaline.cora.supervision.chart.domain.Application;
import fr.jchaline.cora.supervision.chart.domain.Serveur;

@Transactional(readOnly = true)
@Service
public class FactoryService {
	
	@Autowired
	private ApplicationDao applicationDao;
	
	@Autowired
	private ServeurDao serveurDao;

	@Transactional(readOnly = false)
	public void generateData() {
		Application application = new Application("omega", new ArrayList<>());
		applicationDao.save(application);
		
		Serveur serveur = new Serveur("lx01omega", "192.168.0.10", "jeremy", "galaxxie", 22, "/opt/logappli", application);
		serveurDao.save(serveur);

		Serveur serveur2 = new Serveur("lx02omega", "192.168.0.10", "jeremy", "galaxxie", 22, "/opt/logappli2", application);
		serveurDao.save(serveur2);

		Serveur serveur3 = new Serveur("lx03omega", "192.168.0.10", "jeremy", "galaxxie", 22, "/opt/logappli3", application);
		serveurDao.save(serveur3);
		
		application.getServeurs().add(serveur);
		application.getServeurs().add(serveur2);
		application.getServeurs().add(serveur3);
		
		applicationDao.save(application);
	}
}
