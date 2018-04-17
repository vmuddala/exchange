package com.rbc.ex.dao;

import com.rbc.ex.BaseTestOrder;
import com.rbc.ex.model.BuySellIndicator;
import com.rbc.ex.model.ExecutedOrder;
import com.rbc.ex.model.Order;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.rbc.ex.model.BuySellIndicator.BUY;
import static com.rbc.ex.model.BuySellIndicator.SELL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for Order DAO.
 */
public class OrderDaoTest extends BaseTestOrder {

    private OrderDao orderDao = new OrderDao();


    @Test
    public void shouldAddBuyOrderSuccessfully() {
        Order order = createOrder(RIC, BUY);
        orderDao.addToOpenOrders(order);
        assertAddedOrders(order);
    }

    @Test
    public void shouldAddSellOrderSuccessfully() {
        Order order = createOrder(RIC, SELL);
        orderDao.addToOpenOrders(order);
        assertAddedOrders(order);
    }

    @Test
    public void shouldReturnBuyOrderWhenDirectionIsSell() {
        orderDao.addToOpenOrders(createOrder(RIC, SELL));
        orderDao.addToOpenOrders(createOrder(RIC, BUY));
        Optional<Set<Order>> oppOrder = orderDao.getOppDirecOpenOrdersByRicAndOppDirection(RIC, SELL);
        assertOppOrders(oppOrder, BUY, RIC);
    }

    @Test
    public void shouldReturnSellOrderWhenDirectionIsBuy() {
        orderDao.addToOpenOrders(createOrder(RIC, SELL));
        orderDao.addToOpenOrders(createOrder(RIC, BUY));
        Optional<Set<Order>> oppOrder = orderDao.getOppDirecOpenOrdersByRicAndOppDirection(RIC, BUY);
        assertOppOrders(oppOrder, SELL, RIC);
    }

    @Test
    public void shouldRemoveOpenOrderSuccessfully() {
        Order order = createOrder(RIC, BUY);
        orderDao.addToOpenOrders(order);
        orderDao.removeOpenOrder(order);
        Optional<Set<Order>> orders = orderDao.getOpenOrdersByRicAndDirection(RIC, BUY);
        assertTrue(orders.isPresent());
        assertTrue(orders.get().isEmpty());
    }

    @Test
    public void shouldAddToExecutionsSuccessfully(){
        Order matching = createOrder(RIC, BUY);
        Order order = createOrder(RIC, BUY);
        orderDao.addToExecutedOrders(new ExecutedOrder(matching, order, order.getPrice()));
        List<ExecutedOrder> executedOrders = orderDao.getExecutedOrders();
        assertFalse(executedOrders.isEmpty());
        assertEquals(order.getPrice(), executedOrders.get(0).getExePrice());
    }

    private void assertAddedOrders(Order order) {
        Optional<Set<Order>> orders = orderDao.getOpenOrdersByRicAndDirection(order.getRic(), order.getDirection());
        assertTrue(orders.isPresent());
        Order next = orders.get().iterator().next();
        assertEquals(order.getOrderId(), next.getOrderId());
    }

    private void assertOppOrders(Optional<Set<Order>> oppOrder, BuySellIndicator direction, String ric) {
        assertTrue(oppOrder.isPresent());
        Order next = oppOrder.get().iterator().next();
        assertEquals(direction, next.getDirection());
        assertEquals(ric, next.getRic());
    }

}
