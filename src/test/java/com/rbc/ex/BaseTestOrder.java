package com.rbc.ex;

import com.rbc.ex.model.BuySellIndicator;
import com.rbc.ex.model.Order;
import com.rbc.ex.model.User;
import com.rbc.ex.util.IdGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Base Test class.
 */
public class BaseTestOrder {

    private IdGenerator idGenerator = new IdGenerator();
    public static final String RIC = "VOD.L";

    public Order createOrder(String ric, BuySellIndicator direction) {
        Order order = new Order(idGenerator, direction, ric, Long.MAX_VALUE, BigDecimal.TEN, LocalDateTime.now(), new User(1));
        return order;
    }
}
