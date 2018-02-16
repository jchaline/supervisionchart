package fr.jchaline.cora.supervision.chart.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Serveur extends AbstractEntity {
	
	@Column(nullable = false)
	private String libelle;
	
	@Column(nullable = false)
	private String url;
	
	@Column(nullable = false)
	private String login;
	
	@Column(nullable = false)
	private String password;
	
	@Column
	private int port;
	
	
	@Column(nullable = false)
	private String directory;
	
	@JsonIgnoreProperties("serveurs")
	@ManyToOne(fetch = FetchType.LAZY)
	private Application application;
}
