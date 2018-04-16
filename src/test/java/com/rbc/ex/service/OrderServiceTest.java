package com.rbc.ex.service;

import com.rbc.ex.BaseTestOrder;
import com.rbc.ex.dao.OrderDao;
import com.rbc.ex.model.ExecutedOrder;
import com.rbc.ex.model.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static com.rbc.ex.model.BuySellIndicator.BUY;
import static com.rbc.ex.model.BuySellIndicator.SELL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for OrderService.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest extends BaseTestOrder {
    @Mock
    private OrderDao orderDao;
    private OrderService orderService;


    @Before
    public void setUp(){
        orderService = new OrderServiceImpl(orderDao);
    }

    @Test
    public void shouldAddTheOrderToOpenOrdersWhenNoMatchFound(){
        //Given
        Order order = createOrder(RIC, SELL);
        when(orderDao.getOppDirecOpenOrdersByRicAndOppDirection(RIC, order.getDirection())).thenReturn(Optional.empty());
        //When
        orderService.addOrder(order);
        //Then
        verify(orderDao).getOppDirecOpenOrdersByRicAndOppDirection(RIC, order.getDirection());
        verify(orderDao).addToOpenOrders(order);
    }

    @Test
    public void shouldRemoveFromOpenOrdersAndAddToExecutedOrdersWhenMatchFound(){
        //Given
        Order order = createOrder(RIC, SELL);
        Set<Order> existingOpenOrders = new HashSet<>();
        Order existingOrder = createOrder(RIC, BUY);
        existingOpenOrders.add(existingOrder);
        when(orderDao.getOppDirecOpenOrdersByRicAndOppDirection(RIC, order.getDirection())).thenReturn(Optional.of(existingOpenOrders));
        ExecutedOrder executedOrder = new ExecutedOrder(existingOrder, order, order.getPrice());
        orderDao.addToExecutedOrders(executedOrder);
        //When
        orderService.addOrder(order);
        //Then
        verify(orderDao).getOppDirecOpenOrdersByRicAndOppDirection(RIC, order.getDirection());
        verify(orderDao).removeOpenOrder(existingOrder);
        verify(orderDao).addToExecutedOrders(executedOrder);
    }

    @Test
    public void shouldReturnAllOpenInterestForTheGivenRicAndDirectionOnly() {
        //Given
        Set<Order> openOrders = new HashSet<>();
        Order order = createOrder(RIC, SELL); openOrders.add(order);
        when(orderDao.getOpenOrdersByRicAndDirection(RIC, SELL)).thenReturn(Optional.of(openOrders));
        //When
        Map<BigDecimal, Long> openInterest = orderService.getOpenInterest(RIC, SELL);
        //Then
        verify(orderDao).getOpenOrdersByRicAndDirection(RIC, SELL);
        assertTrue(openInterest.containsKey(order.getPrice()));
        assertEquals((Long)order.getQuantity(), openInterest.get(order.getPrice()));
    }

    @Test
    public void shouldReturnAvgExecutionPriceForTheGivenRicOnly() {
        //Given
        Order order = createOrder(RIC, SELL);
        Order existingOrder = createOrder(RIC, BUY);
        ExecutedOrder executedOrder = new ExecutedOrder(existingOrder, order, order.getPrice());
        when(orderDao.getExecutedOrders()).thenReturn(Arrays.asList(executedOrder));
        //When
        BigDecimal avgExecutionPrice = orderService.getAvgExecutionPrice(RIC);
        //Then
        verify(orderDao).getExecutedOrders();
        assertEquals(new BigDecimal("10.0000"), avgExecutionPrice);
    }

    @Test
    public void shouldReturnExecutedQuantityForTheGivenRicAndDirections() {
        //Given
        Order order = createOrder(RIC, SELL);
        Order existingOrder = createOrder(RIC, BUY);
        ExecutedOrder executedOrder = new ExecutedOrder(existingOrder, order, order.getPrice());
        when(orderDao.getExecutedOrders()).thenReturn(Arrays.asList(executedOrder));
        //When
        long executedQuantity = orderService.getExecutedQuantity(RIC, order.getUser());
        //Then
        verify(orderDao).getExecutedOrders();
        assertEquals(0L, executedQuantity);
    }

}
