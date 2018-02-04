package fr.jchaline.cora.supervision.chart.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.jchaline.cora.supervision.chart.domain.Application;


public interface ApplicationDao extends JpaRepository<Application, Long> {
	
}
