package fr.jchaline.cora.supervision.chart.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
public class Action extends AbstractEntity {
	
	@Column(nullable = false)
	private String server;
	
	@Column(nullable = false)
	private String url;
	
	@Column
	private int nb;
	
	@Column
	private int[] repartition;
	
	@Column
	private int totalTime;

	@Column
	private int minTime;

	@Column
	private int maxTime;

	@Column
	private int avgTime;

	/**
	 * Date de creation du log sur le serveur
	 */
	@Column
	private LocalDateTime dateExtraction;

}
