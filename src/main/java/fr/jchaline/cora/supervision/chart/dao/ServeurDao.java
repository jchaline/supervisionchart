package fr.jchaline.cora.supervision.chart.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.jchaline.cora.supervision.chart.domain.Serveur;

public interface ServeurDao extends JpaRepository<Serveur, Long> {
	
}
