package com.rbc.ex.model;

import java.math.BigDecimal;

/**
 * ExecutedOrder model class.
 */
public class ExecutedOrder {

    private Order order;
    private Order latestOrder;
    private BigDecimal exePrice;

    public ExecutedOrder(Order order, Order latestOrder, BigDecimal exePrice) {
        this.order = order;
        this.latestOrder = latestOrder;
        this.exePrice = exePrice;
    }

    public Order getOrder() {
        return order;
    }

    public Order getLatestOrder() {
        return latestOrder;
    }

    public BigDecimal getExePrice() {
        return exePrice;
    }

    @Override
    public String toString() {
        return "ExecutedOrder{" +
                "order=" + order +
                ", latestOrder=" + latestOrder +
                ", exePrice=" + exePrice +
                '}';
    }
}

