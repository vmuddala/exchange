package com.rbc.ex.service;

import com.rbc.ex.dao.OrderDao;
import com.rbc.ex.model.BuySellIndicator;
import com.rbc.ex.model.ExecutedOrder;
import com.rbc.ex.model.Order;
import com.rbc.ex.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OrderService class.
 */
public class OrderServiceImpl implements OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);

    private OrderDao orderDao;

    public OrderServiceImpl(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    public Order addOrder(Order order) {
        moveOrder(order, findMatchingOrders(order));
        return order;
    }

    @Override
    public Map<BigDecimal, Long> getOpenInterest(String ric, BuySellIndicator direction) {
        LOG.debug("OpenInterest RIC {}, direction {}", ric, direction);
        Map<BigDecimal, Long> result = new HashMap<>();
        Optional<Set<Order>> openOrders = orderDao.getOpenOrdersByRicAndDirection(ric, direction);
        if(openOrders.isPresent()) {
            openOrders.get().stream().collect(Collectors.groupingBy(Order::getPrice)).entrySet().stream().forEach(entry -> {
                result.put(entry.getKey(), entry.getValue().stream().mapToLong(Order::getQuantity).sum());
            });
        }
        return result;
    }

    @Override
    public BigDecimal getAvgExecutionPrice(String ric) {
        LOG.debug("AvgExecutionPrice RIC {}", ric);
        BigDecimal tq = BigDecimal.ZERO;
        BigDecimal tqp = BigDecimal.ZERO;
        for(ExecutedOrder exOrder: orderDao.getExecutedOrders()) {
            BigDecimal orderQty = new BigDecimal(exOrder.getLatestOrder().getQuantity());
            tqp = tqp.add(exOrder.getExePrice().multiply(orderQty));
            tq = tq.add(orderQty);
        }
        if(tq.compareTo(BigDecimal.ZERO) != 0) {
            return tqp.divide(tq, 4, BigDecimal.ROUND_DOWN);
        }
       return BigDecimal.ZERO;
    }

    @Override
    public long getExecutedQuantity(String ric, User user) {
        LOG.debug("ExecutedQuantity for RIC {}, user {}", ric, user);
        List<Order> matchedOrders = new ArrayList<>();
        orderDao.getExecutedOrders().stream().forEach(e -> {
            if (e.getOrder().getRic().equalsIgnoreCase(ric) && e.getOrder().getUser().getUserId() == user.getUserId()) {
                matchedOrders.add(e.getOrder());
            }
            if (e.getLatestOrder().getRic().equalsIgnoreCase(ric) && e.getLatestOrder().getUser().getUserId() == user.getUserId()) {
                matchedOrders.add(e.getLatestOrder());
            }
        });
        return matchedOrders.stream().mapToLong(order ->
                        { if(order.getDirection() == BuySellIndicator.SELL){ return order.getQuantity() * -1;}
                            return order.getQuantity();}).sum();
    }

    private Optional<Order> findMatchingOrders(Order order) {
        Optional<Set<Order>> orders = orderDao.getOppDirecOpenOrdersByRicAndOppDirection(order.getRic(), order.getDirection());
        if(orders.isPresent()) {
            for(Order next : orders.get()) {
                int priceCompResult = order.getPrice().compareTo(next.getPrice());
                if(order.getDirection() == BuySellIndicator.BUY) {
                    if( priceCompResult >= 0 && next.getQuantity() == order.getQuantity()) {
                        return Optional.of(next);
                    }
                } else {
                    if(priceCompResult <= 0 && next.getQuantity() == order.getQuantity()) {
                        return Optional.of(next);
                    }
                }
            }
        }
        return Optional.empty();
    }

    private void moveOrder(Order order, Optional<Order> matchedOrder) {
        if(matchedOrder.isPresent()) {
            orderDao.removeOpenOrder(matchedOrder.get());
            orderDao.addToExecutedOrders(new ExecutedOrder(matchedOrder.get(), order, order.getPrice()));
        } else {
            orderDao.addToOpenOrders(order);
        }
    }
}
