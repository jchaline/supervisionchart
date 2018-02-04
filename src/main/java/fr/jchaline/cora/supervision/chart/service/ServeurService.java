package fr.jchaline.cora.supervision.chart.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.jchaline.cora.supervision.chart.dao.ServeurDao;
import fr.jchaline.cora.supervision.chart.domain.Serveur;

@Transactional(readOnly = true)
@Service
public class ServeurService {
	
	@Autowired
	private ServeurDao serveurDao;
	
	public List<Serveur> findAll() {
		return serveurDao.findAll();
	}

	public List<Serveur> findByApplication() {
		return serveurDao.findAll();
	}
}
