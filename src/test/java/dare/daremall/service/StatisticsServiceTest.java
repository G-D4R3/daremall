package dare.daremall.service;

import dare.daremall.statistics.StatisticsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class StatisticsServiceTest {

    @Autowired
    StatisticsService statisticsService;

    @Test
    public void 주문조회() {
        // given
        statisticsService.findAllOrderStatisticsWeek();
        // when

        // then

    }
}