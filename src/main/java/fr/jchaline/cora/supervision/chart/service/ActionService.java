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
import java.util.Map;
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

		final String strAvgTimePattern = ".*Temps d'execution moyen \\(ms\\)\\s+:\\s+(\\d+)";
		Pattern avgTimePattern = Pattern.compile(strAvgTimePattern) ;

		final String strMiniTimePattern = ".*Temps d'execution mini \\(ms\\)\\s+:\\s+(\\d+)";
		Pattern miniTimePattern = Pattern.compile(strMiniTimePattern) ;

		final String strMaxiTimePattern = ".*Temps d'execution maxi \\(ms\\)\\s+:\\s+(\\d+)";
		Pattern maxiTimePattern = Pattern.compile(strMaxiTimePattern) ;
		
		final String strTotalTimePattern = ".*Temps d'execution total \\(ms\\)\\s+:\\s+(\\d+)";
		Pattern totalTimePattern = Pattern.compile(strTotalTimePattern) ;

		final String strNbExecutionPattern = ".*Nombre de demande\\s+:\\s+(\\d+)";
		Pattern nbExecutionPattern = Pattern.compile(strNbExecutionPattern) ;

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
			LOGGER.info("Fichier {} sur {} : {}", i++, entries.size(), entry.getFilename());
			
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
				}
				else if (readData) {
					Matcher matcherAvgTime = avgTimePattern.matcher(line);
					Matcher matcherRepartition = repartitionPattern.matcher(line);
					Matcher matcherMini = miniTimePattern.matcher(line);
					Matcher matcherMaxi = maxiTimePattern.matcher(line);
					Matcher matcherTotal = totalTimePattern.matcher(line);
					Matcher matcherNbExec = nbExecutionPattern.matcher(line);
					if (matcherAvgTime.find()) {
						String avgTime = matcherAvgTime.group(1);
						currentAction.setAvgTime(Integer.valueOf(avgTime));
					}
					else if (matcherRepartition.find()) {
						String repartition = matcherRepartition.group(1);
						String[] split = repartition.split(" - ");
						int[] array = Arrays.stream(split).mapToInt(Integer::parseInt).toArray();
						currentAction.setRepartition(array);
					}
					else if (matcherMini.find()) {
						String minTime = matcherMini.group(1);
						currentAction.setMinTime(Integer.valueOf(minTime));
					}
					else if (matcherMaxi.find()) {
						String maxTime = matcherMaxi.group(1);
						currentAction.setMaxTime(Integer.valueOf(maxTime));
					}
					else if (matcherTotal.find()) {
						String totalTime = matcherTotal.group(1);
						currentAction.setTotalTime(Integer.valueOf(totalTime));
					}
					else if (matcherNbExec.find()) {
						String nbExec = matcherNbExec.group(1);
						currentAction.setNb(Integer.valueOf(nbExec));
					}
				}
			}
			
			if (currentAction != null) {
				currentAction.setTotalTime(currentAction.getNb() * currentAction.getAvgTime());
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

	public List<String> listUrl(LocalDate firstDay, LocalDate lastDay) {
		List<String> collect = new ArrayList<>();
		
		if (dumbCache != null) {
			collect = dumbCache.parallelStream()
					.filter(e -> e.getDateExtraction().toLocalDate().isAfter(firstDay))
					.filter(e -> e.getDateExtraction().toLocalDate().isBefore(lastDay))
					.map(e -> e.getUrl())
					.distinct().collect(Collectors.toList());
		}
		
		return collect;
	}

	/**
	 * Liste des différentes URL contactés durant la période donnée avec temps moyen de réponse
	 * @param dateDebut
	 * @param dateFin
	 * @return
	 */
	public List<Action> listActionAvg(LocalDate firstDay, LocalDate lastDay) {
		List<Action> result = new ArrayList<>();
		
		if (dumbCache != null) {
			Map<String, List<Action>> grouped = dumbCache.parallelStream()
					.filter(e -> e.getDateExtraction().toLocalDate().isAfter(firstDay))
					.filter(e -> e.getDateExtraction().toLocalDate().isBefore(lastDay))
					.collect(Collectors.groupingBy(Action::getUrl, Collectors.toList()));
			Stream<Action> calcul = grouped.entrySet().parallelStream().map(e -> {
				int[] repartition = new int[5];
				int totalTime = 0;
				int minTime  = Integer.MAX_VALUE;
				int maxTime = 0;
				int avgTime = 0;
				int nb = 0;
				
				for (Action a : e.getValue()) {
					for (int i=0; i<repartition.length; i++) {
						repartition[i] += a.getRepartition()[i];
					}
					nb += a.getNb();
					totalTime += a.getTotalTime();
					if (a.getMinTime() < minTime) minTime = a.getMinTime();
					if (a.getMaxTime() > maxTime) maxTime = a.getMaxTime();
					avgTime += (a.getAvgTime() * a.getNb());
				}
				avgTime /= nb;

				Action action = new Action(e.getKey(), e.getValue().size(), repartition, totalTime, minTime, maxTime, avgTime, LocalDateTime.now());
				return action;
			});
			result = calcul.collect(Collectors.toList());
		}
		
		return result;
	}

}
