package fr.jchaline.cora.supervision.chart.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import fr.jchaline.cora.supervision.chart.dao.ActionDao;
import fr.jchaline.cora.supervision.chart.domain.Action;

@Transactional(readOnly = true)
@Service
public class ActionService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionService.class);
	
	private static final DateTimeFormatter DATE_TIME_FORMATER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	
	private List<Action> dumbCache = null;
	
	@Value("${log.server}")
	private String server;
	
	@Value("${log.login}")
	private String login;
	
	@Value("${log.password}")
	private String password;

	@Value("${log.directory}")
	private String repertoire;
	
	@Value("${log.fichierPattern}")
	private String fichierPattern;
	
	@Autowired
	private SSHService sshService;

	@Autowired
	private ActionDao actionDao;
	
	/**
	 * Chargement dans la bdd des logs supervision
	 * @throws JSchException 
	 * @throws SftpException 
	 * @throws IOException 
	 */
	@Transactional(readOnly = false)
	public List<Action> updateActionFromServer() throws JSchException, SftpException, IOException {
		
		actionDao.deleteAll();
		
		List<Action> findListAction = findListAction();
		
		findListAction.forEach(a -> actionDao.save(a));
		
		dumbCache = findListAction;
		
		return findListAction;
	}

	private List<Action> findListAction() throws JSchException, SftpException, IOException {
		List<Action> listAction = new ArrayList<Action>();
		
		final String strUrlPattern = "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}):\\d{2},\\d{3} +\\/([\\w\\.]+)\\.do";
		Pattern urlPattern = Pattern.compile(strUrlPattern);

		final String strAvgTimePattern = ".*Temps d'execution moyen \\(ms\\) : (\\d+)";
		Pattern avgTimePattern = Pattern.compile(strAvgTimePattern) ;

		final String strRepartitionPattern = ".*Repartition.*: (\\d+ - \\d+ - \\d+ - \\d+ - \\d+)";
		Pattern repartitionPattern = Pattern.compile(strRepartitionPattern) ;

		ChannelSftp sftpChannel = sshService.buildChannel(server, login, password);

		sftpChannel.cd(repertoire);
		@SuppressWarnings("unchecked")
		Stream<LsEntry> map = sftpChannel.ls(repertoire).stream().map(e -> ((LsEntry) e));
		map = map.filter(e -> e.getFilename().matches(fichierPattern));
		List<LsEntry> entries = map.collect(Collectors.toList());
		
		boolean readData = false;
		int i = 1;
		for (LsEntry entry : entries) {
			LOGGER.info("{} sur {} : {}", i++, entries.size(), entry.getFilename());
			
			InputStream stream = sftpChannel.get(entry.getFilename());
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			
			String currentUrl = "";
			String currentDateTime = "";
			Action currentAction = null;
			
			String line;
			while ((line = br.readLine()) != null) {
				Matcher matcherUrl = urlPattern.matcher(line);
				if (matcherUrl.find()) {
					readData = true;
					currentDateTime = matcherUrl.group(1);
					currentUrl = matcherUrl.group(2);
					
					if (currentAction != null) {
						listAction.add(currentAction);
					}
					
					//nouvelle url, nouvelle action
					currentAction = new Action();
					currentAction.setUrl(currentUrl);
					currentAction.setDateExtraction(LocalDateTime.parse(currentDateTime, DATE_TIME_FORMATER));
				} else if (readData) {
					Matcher matcherAvgTime = avgTimePattern.matcher(line);
					Matcher matcherRepartition = repartitionPattern.matcher(line);
					if (matcherAvgTime.find()) {
						String avgTime = matcherAvgTime.group(1);
						currentAction.setMaxTime(1000);
						currentAction.setMinTime(1000);
						currentAction.setTotalTime(1000);
						currentAction.setAvgTime(Integer.valueOf(avgTime));
					} else if (matcherRepartition.find()) {
						String repartition = matcherRepartition.group(1);
						String[] split = repartition.split(" - ");
						int[] array = Arrays.stream(split).mapToInt(Integer::parseInt).toArray();
						currentAction.setRepartition(array);
					}
				}
			}
			
			if (currentAction != null) {
				listAction.add(currentAction);
			}
			
			br.close();
		}
		sftpChannel.exit();
		sftpChannel.getSession().disconnect();
		return listAction;
	}
	
	public List<Action> joinOnSameDay(List<Action> actions) {
		actions.stream().collect(Collectors.groupingBy(Action::getDateExtraction));
		return null;
	}
	
	public List<Action> evolutionUrl(String url, LocalDate debut, LocalDate fin) {
		
		List<Action> collect = new ArrayList<>();
		
		if (dumbCache != null) {
			Comparator<Action> byDate = (e1, e2) -> e1.getDateExtraction().compareTo(e2.getDateExtraction());
			
			collect = dumbCache.stream().filter(a -> a.getUrl().equals(url)).sorted(byDate).collect(Collectors.toList());
		}
		
		return collect;
	}

}
