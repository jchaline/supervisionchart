package fr.jchaline.cora.supervision.chart.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.jchaline.cora.supervision.chart.domain.Application;
import fr.jchaline.cora.supervision.chart.service.ApplicationService;

@RestController
@RequestMapping(value = "/application", method = RequestMethod.GET)
public class ApplicationController extends AbstractSupervisionChartController {
	
	@Autowired
	private ApplicationService applicationService;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<Application> list() {
		List<Application> findAll = applicationService.findAll();
		return findAll;
	}
}
