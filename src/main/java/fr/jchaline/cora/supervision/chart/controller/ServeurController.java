package fr.jchaline.cora.supervision.chart.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.jchaline.cora.supervision.chart.domain.Serveur;
import fr.jchaline.cora.supervision.chart.service.ServeurService;

@RestController
@RequestMapping(value = "/serveur", method = RequestMethod.GET)
public class ServeurController extends AbstractSupervisionChartController {
	
	@Autowired
	private ServeurService serveurService;
	
	@RequestMapping(value = "/findByApplication", method = RequestMethod.GET)
	public List<Serveur> findByApplication() {
		return serveurService.findAll();
	}
}
