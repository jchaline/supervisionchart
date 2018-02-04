package fr.jchaline.cora.supervision.chart.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.jchaline.cora.supervision.chart.dao.ApplicationDao;
import fr.jchaline.cora.supervision.chart.domain.Application;

@Transactional(readOnly = true)
@Service
public class ApplicationService {
	
	@Autowired
	private ApplicationDao applicationDao;
	
	public List<Application> findAll() {
		return applicationDao.findAll();
	}
}
