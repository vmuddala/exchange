package com.rbc.ex.service;

import com.rbc.ex.model.BuySellIndicator;
import com.rbc.ex.model.Order;
import com.rbc.ex.model.User;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Interface for OrderService API.
 */
public interface OrderService {

    /**
     * Adds given order to the orders category
     *
     * @param order the order
     * @return the order
     */
    Order addOrder(Order order);

    /**
     * Gets total quantity of all open orders by price for the given RIC and direction.
     *
     * @param ric the ric
     * @param direction the direction
     * @return the interest key as price, value as total quantity
     */
    Map<BigDecimal, Long> getOpenInterest(String ric, BuySellIndicator direction);

    /**
     * Gets average price of all executions for the given RIC.
     *
     * @param ric the RIC
     * @return the avg price
     */
    BigDecimal getAvgExecutionPrice(String ric);

    /**
     * Gets sum of quantities of all executions for the given RIC and user.
     * SELL: It will be represented in negative
     *
     * @param ric the RIC
     * @param user the user
     * @return the sum of quantities
     */
    long getExecutedQuantity(String ric, User user);
}
