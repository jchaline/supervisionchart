package fr.jchaline.cora.supervision.chart.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Data;

@MappedSuperclass
@Data
public abstract class AbstractEntity {

	@Id
	@GeneratedValue
	@Column(unique = true, nullable = false)
	private Long id;
	
	/**
	 * Date de creation de l'objet
	 */
	@Column
	private LocalDateTime dateCreate = LocalDateTime.now();

	/**
	 * Date de creation de l'objet
	 */
	@Column
	private LocalDateTime dateMaj = LocalDateTime.now();
}
