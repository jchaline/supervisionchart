package fr.jchaline.cora.supervision.chart.service;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@Transactional(readOnly = true)
@Service
public class SSHService {
	
	public ChannelSftp buildChannel(String ip, String login, String password) throws JSchException {
		return buildChannel(ip, login, password, 22);
	}
	
	private ChannelSftp buildChannel(String ipServer, String login, String password, int port) throws JSchException {
		final JSch jsch = new JSch();
		final Session session = jsch.getSession(login, ipServer, port);
		session.setPassword(password.getBytes(StandardCharsets.ISO_8859_1));
		
		final Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		
		session.connect();
		
		final Channel channel = session.openChannel("sftp");
		channel.connect();
		final ChannelSftp c = (ChannelSftp) channel;
		return c;
	}

}
