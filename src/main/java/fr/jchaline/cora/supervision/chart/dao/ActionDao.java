package fr.jchaline.cora.supervision.chart.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.jchaline.cora.supervision.chart.domain.Action;


public interface ActionDao extends JpaRepository<Action, Long> {
	
}
