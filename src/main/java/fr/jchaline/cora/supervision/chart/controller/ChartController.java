package fr.jchaline.cora.supervision.chart.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
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
	public boolean updateAction() {
		try {
			actionService.updateActionFromServer();
			return true;
		} catch (JSchException | SftpException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@RequestMapping(value = "/ajax", method = RequestMethod.POST)
	public Map<String, String> getSearchResultViaAjax(@RequestBody Map<String, String> search) {

		//logic
		return search;
	}
	
	//soit pour une meme url l'évolution temps moyen sur plusieurs jours
	@RequestMapping("/evolutionUrl")
	public List<Action> evolutionUrl(@RequestParam String url) {
		return actionService.evolutionUrl(url, LocalDate.now(), LocalDate.now());
	}
	
	//soit pour l'ensemble des requete le temps moyen à date 
}
