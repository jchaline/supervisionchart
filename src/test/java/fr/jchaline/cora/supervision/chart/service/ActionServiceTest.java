package fr.jchaline.cora.supervision.chart.service;

import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.jchaline.cora.supervision.chart.dao.ActionDao;
import fr.jchaline.cora.supervision.chart.domain.Action;

@RunWith( MockitoJUnitRunner.class )
public class ActionServiceTest {
	
	@Mock
	private ActionDao dao;
	
	@InjectMocks
	private ActionService service = new ActionService();
	
	@Test
	public void evolutionUrl() {
		int[] repart = {2, 3, 4, 2, 1};
		Action a1 = new Action("lx01", "test", 200, repart, 100, 20, 30, 22, LocalDateTime.now().minusDays(1).minusMinutes(243));
		Action a2 = new Action("lx01", "test", 400, repart, 100, 20, 30, 22, LocalDateTime.now().minusDays(1));
		Action a3 = new Action("lx01", "test", 300, repart, 100, 20, 30, 22, LocalDateTime.now().minusDays(2));
		
		List<Action> origin = Arrays.asList(a1, a2, a3);
		List<Action> joinOnDay = Arrays.asList(a1, a2, a3);
		
		when( dao.findAll() ).thenReturn( origin );
		
		
		Assert.assertSame(origin, joinOnDay);
	}

}
