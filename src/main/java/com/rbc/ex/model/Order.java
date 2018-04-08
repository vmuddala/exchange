package com.rbc.ex.model;

import com.rbc.ex.util.IdGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Order model class.
 */
public class Order implements Comparable {

    private long orderId;
    private BuySellIndicator direction;
    private String ric;
    private long quantity;
    private BigDecimal price;
    private LocalDateTime orderDate;
    private User user;

    public Order(IdGenerator idGenerator, BuySellIndicator direction, String ric, long quantity, BigDecimal price, LocalDateTime orderDate, User user) {
        this.orderId = idGenerator.getNextId();
        this.direction = direction;
        this.ric = ric;
        this.quantity = quantity;
        this.price = price;
        this.orderDate = orderDate;
        this.user = user;
    }

    public long getOrderId() {
        return orderId;
    }

    public BuySellIndicator getDirection() {
        return direction;
    }

    public String getRic() {
        return ric;
    }

    public long getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return orderId == order.orderId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", direction=" + direction +
                ", ric='" + ric + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", user=" + user +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        if(o != null && o instanceof Order) {
            Order other = (Order)o;
            if(other.getDirection() == BuySellIndicator.SELL) {
                int result = this.getPrice().compareTo(other.getPrice());
                if(result == 0) {
                    return this.orderDate.compareTo(other.getOrderDate());
                } else return result;
            } else {
                int result = other.getPrice().compareTo(this.getPrice());
                if(result == 0) {
                    return this.orderDate.compareTo(other.getOrderDate());
                } else return result;
            }
        }
        return -1;
    }
}
