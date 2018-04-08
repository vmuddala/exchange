package com.rbc.ex.dao;

import com.rbc.ex.model.BuySellIndicator;
import com.rbc.ex.model.ExecutedOrder;
import com.rbc.ex.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository class to store orders by category.
 */
public class OrderDao {

    private static final Logger LOG = LoggerFactory.getLogger(OrderDao.class);

    private Map<String, Set<Order>> openBuyOrders = new ConcurrentHashMap<>();
    private Map<String, Set<Order>> openSellOrders = new ConcurrentHashMap<>();
    private List<ExecutedOrder> executedOrders = new ArrayList<>();

    /**
     * Adds order to openOrder list.
     *
     * @param order the order
     */
    public void addToOpenOrders(Order order) {
        LOG.info("Add order {}", order);
        Set<Order> orders = Collections.synchronizedSortedSet(new TreeSet<>());
        orders.add(order);
        if(order.getDirection() == BuySellIndicator.BUY) {
            openBuyOrders.merge(order.getRic(), orders, (v1, v2) -> { v1.addAll(v2); return v1;} );
        } else {
            openSellOrders.merge(order.getRic(), orders, (v1, v2) -> { v1.addAll(v2); return v1;} );
        }
    }

    /**
     * Add executionOrder to executedOrder list.
     *
     * @param executedOrder the executionOrder.
     */
    public void addToExecutedOrders(ExecutedOrder executedOrder) {
        LOG.info("Add order {}", executedOrder);
        executedOrders.add(executedOrder);
    }

    /**
     * Gets Opposite direction orders for the given RIC if it is existed,
     * otherwise it will empty optional.
     *
     * @param ric the ric
     * @param direction the direction
     * @return the orders
     */
    public Optional<Set<Order>> getOppDirecOpenOrdersByRicAndOppDirection(String ric, BuySellIndicator direction) {
        if(direction == BuySellIndicator.SELL) {
            return openBuyOrders.containsKey(ric) ? Optional.of(openBuyOrders.get(ric)) : Optional.empty();
        }
        return openSellOrders.containsKey(ric) ? Optional.of(openSellOrders.get(ric)) : Optional.empty();
    }

    /**
     * Removes the given order from open orders.
     *
     * @param order the order
     */
    public void removeOpenOrder(Order order) {
        Set<Order> removableOrders;
        if(order.getDirection() == BuySellIndicator.BUY) {
            removableOrders = openBuyOrders.get(order.getRic());
        } else {
            removableOrders = openSellOrders.get(order.getRic());
        }
        removableOrders.remove(order);
    }

    /**
     * Gets all executed orders list.
     *
     * @return the executedOrders
     */
    public List<ExecutedOrder> getExecutedOrders() {
        return executedOrders;
    }

    /**
     * Gets set of open orders for the given ric and direction if it is existed,
     * otherwise it will empty optional.
     *
     * @param ric the ric
     * @param direction the direction
     * @return the orders
     */
    public Optional<Set<Order>> getOpenOrdersByRicAndDirection(String ric, BuySellIndicator direction) {
        if(direction == BuySellIndicator.SELL) {
            return openSellOrders.containsKey(ric) ? Optional.of(openSellOrders.get(ric)) : Optional.empty();
        }
        return openBuyOrders.containsKey(ric) ? Optional.of(openBuyOrders.get(ric)) : Optional.empty();
    }

}
