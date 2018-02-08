package fr.jchaline.cora.supervision.chart.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import fr.jchaline.cora.supervision.chart.domain.Action;
import fr.jchaline.cora.supervision.chart.service.ActionService;

@RestController
@RequestMapping(value = "/chart", method = RequestMethod.GET)
public class ChartController extends AbstractSupervisionChartController {
	
	@Autowired
	private ActionService actionService;
	
	@RequestMapping(value = "/updateAction", method = RequestMethod.POST)
	public boolean updateAction(@RequestParam String server) {
		try {
			actionService.updateActionFromServer();
			return true;
		} catch (JSchException | SftpException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//soit pour une meme url l'évolution temps moyen sur plusieurs jours
	@RequestMapping("/evolutionUrl")
	public Map<String, List<Action>> evolutionUrl(@RequestParam List<String> serverList, @RequestParam String url, @RequestParam LocalDate dateDebut, @RequestParam LocalDate dateFin) {
		return actionService.evolutionUrl(serverList, url, dateDebut, dateFin);
	}
	
	/**
	 * Liste des différentes URL sur une période donnée
	 * @param dateDebut
	 * @param dateFin
	 * @return
	 */
	@RequestMapping("/listAction")
	public List<Action> listActionAvg(@RequestParam String server, @RequestParam LocalDate dateDebut, @RequestParam LocalDate dateFin) {
		return actionService.listActionAvg(server, dateDebut, dateFin);
	}
	
	//soit pour l'ensemble des requete le temps moyen à date 
}
