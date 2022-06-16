package dare.daremall.service;

import dare.daremall.domain.Order;
import dare.daremall.domain.OrderStatus;
import dare.daremall.domain.statistics.OrderStatistics;
import dare.daremall.repository.OrderStatisticsRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatisticsService {

    private final OrderStatisticsRepository orderStatisticsRepository;

    public OrderStatistics findOrderStatistics(LocalDate date) {
        return orderStatisticsRepository.findOne(date).orElse(null);
    }

    public List<OrderStatistics> findAllOrderStatistics() {
        return orderStatisticsRepository.findAll();
    }

    public void updateOrderStatistics(Order order) {
        OrderStatistics find = orderStatisticsRepository.findOne(order.getOrderDate().toLocalDate()).orElse(null);
        if(order.getStatus() == OrderStatus.PAY){
            if(find == null) {
                OrderStatistics newStatistics = new OrderStatistics();
                newStatistics.setDate(order.getOrderDate().toLocalDate());
                newStatistics.setOrderQuantity(1L);
                newStatistics.setRevenue((long) order.getTotalPrice());
                orderStatisticsRepository.save(newStatistics);
            }
            else {
                find.setOrderQuantity(find.getOrderQuantity()+1);
                find.setRevenue(find.getRevenue() + order.getTotalPrice());
                orderStatisticsRepository.save(find);
            }
        }
        if(order.getStatus() == OrderStatus.CANCEL) {
            if(find!=null) {
                find.setOrderQuantity(find.getOrderQuantity()-1);
                find.setRevenue(find.getRevenue() - order.getTotalPrice());
                orderStatisticsRepository.save(find);
            }
        }
    }
}
