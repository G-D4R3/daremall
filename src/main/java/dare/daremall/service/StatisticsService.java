package dare.daremall.service;

import dare.daremall.domain.Order;
import dare.daremall.domain.OrderItem;
import dare.daremall.domain.OrderStatus;
import dare.daremall.domain.statistics.ItemStatistics;
import dare.daremall.domain.statistics.OrderStatistics;
import dare.daremall.repository.ItemRepository;
import dare.daremall.repository.ItemStatisticsRepository;
import dare.daremall.repository.OrderStatisticsRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatisticsService {

    private final OrderStatisticsRepository orderStatisticsRepository;
    private final ItemStatisticsRepository itemStatisticsRepository;
    private final ItemRepository itemRepository;

    public OrderStatistics findOrderStatistics(LocalDate date) {
        return orderStatisticsRepository.findOne(date).orElse(null);
    }

    @Transactional
    public List<OrderStatistics> findAllOrderStatisticsWeek() {
        List<OrderStatistics> allWeek = new ArrayList<>();
        LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = LocalDate.now().with(DayOfWeek.SUNDAY);

        for(LocalDate date = startOfWeek; date.isBefore(endOfWeek.plusDays(1)); date = date.plusDays(1)) {
            OrderStatistics findStatistics = orderStatisticsRepository.findOne(date).orElse(null);
            if(findStatistics==null) {
                OrderStatistics newStatistics = new OrderStatistics();
                newStatistics.setDate(date);
                newStatistics.setOrderQuantity(0L);
                newStatistics.setRevenue(0L);
                orderStatisticsRepository.save(newStatistics);
                findStatistics = orderStatisticsRepository.findOne(date).orElse(null);
            }
            allWeek.add(findStatistics);
        }
        return allWeek;
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

    @Transactional
    public List<ItemStatistics> findItemStatistics(Long itemId) {
        List<ItemStatistics> allWeek = new ArrayList<>();
        LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = LocalDate.now().with(DayOfWeek.SUNDAY);

        for(LocalDate date = startOfWeek; date.isBefore(endOfWeek.plusDays(1)); date = date.plusDays(1)) {
            ItemStatistics findStatistics = itemStatisticsRepository.findStatistics(itemId, date).orElse(null);
            if(findStatistics==null) {
                ItemStatistics newStatistics = new ItemStatistics();
                newStatistics.setItem(itemRepository.findById(itemId).orElse(null));
                newStatistics.setDate(date);
                newStatistics.setSalesCount(0L);
                newStatistics.setRevenue(0L);
                itemStatisticsRepository.save(newStatistics);
                findStatistics = itemStatisticsRepository.findStatistics(itemId, date).orElse(null);
            }
            allWeek.add(findStatistics);
        }
        return allWeek;
    }

    public void updateItemStatistics(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        if(order.getStatus().equals(OrderStatus.PAY)) {
            for(OrderItem item : orderItems) {
                ItemStatistics itemStatistics = itemStatisticsRepository.findStatistics(item.getItem().getId(), order.getOrderDate().toLocalDate()).orElse(null);
                if(itemStatistics == null) {
                    itemStatistics = new ItemStatistics();
                    itemStatistics.setItem(item.getItem());
                    itemStatistics.setDate(order.getOrderDate().toLocalDate());
                    itemStatistics.setSalesCount((long)item.getCount());
                    itemStatistics.setRevenue((long)item.getTotalPrice());
                }
                else {
                    itemStatistics.setSalesCount(itemStatistics.getSalesCount() + item.getCount());
                    itemStatistics.setRevenue(itemStatistics.getRevenue() + item.getTotalPrice());
                }
                itemStatisticsRepository.save(itemStatistics);
            }
        }
        else if(order.getStatus().equals(OrderStatus.CANCEL)) {
            for(OrderItem item : orderItems) {
                ItemStatistics itemStatistics = itemStatisticsRepository.findStatistics(item.getItem().getId(), order.getOrderDate().toLocalDate()).orElse(null);
                itemStatistics.setRevenue(itemStatistics.getRevenue() - item.getTotalPrice());
                itemStatisticsRepository.save(itemStatistics);
            }
        }
    }
}
