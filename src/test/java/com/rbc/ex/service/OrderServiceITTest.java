package com.rbc.ex.service;

import com.rbc.ex.config.AppConfig;
import com.rbc.ex.model.BuySellIndicator;
import com.rbc.ex.model.Order;
import com.rbc.ex.model.User;
import com.rbc.ex.util.IdGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.rbc.ex.BaseTestOrder.RIC;
import static org.junit.Assert.assertEquals;

/**
 * Integration test for OrderService.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class OrderServiceITTest {

    private List<Order> orders = new ArrayList<>();
    @Autowired
    private OrderService orderService;
    @Autowired
    private IdGenerator idGenerator;

    private static final int ARRAY_LENGTH = 5;


    @Before
    public void setUp() throws URISyntaxException, IOException {
        Stream<String> lines = Files.lines(Paths.get(ClassLoader.getSystemResource("orders.txt").toURI()));
        lines.forEach(line -> {
            String[] split = line.split("\\|");
            assertEquals(ARRAY_LENGTH, split.length);
            User user = new User(Long.parseLong(split[4]));
            Order order = new Order(idGenerator, BuySellIndicator.valueOf(split[0]), split[2], Long.parseLong(split[1]), new BigDecimal(split[3]), LocalDateTime.now(), user);
            orders.add(order);
        });
    }

    @Test
    public void shouldAddAllOrdersSuccessfully() {
        ExpectedResult.initialiseExpectedResults();
        for(int i = 0; i < orders.size(); i++) {
            orderService.addOrder(orders.get(i));
            Map<Long, ExpectedResult> expectedResultMap = ExpectedResult.getExpectedResultMap();
            ExpectedResult expectedResult = expectedResultMap.get(Long.valueOf(i+1));
            assertOpenInt(expectedResult.getOpenIntBuy(), orderService.getOpenInterest(orders.get(i).getRic(), BuySellIndicator.BUY));
            assertOpenInt(expectedResult.getOpenIntSell(), orderService.getOpenInterest(orders.get(i).getRic(), BuySellIndicator.SELL));
            assertAvgExePrice(expectedResult.getAvgExecPrice(), orderService.getAvgExecutionPrice(RIC));
            assertExeQuantity(expectedResult.getU1ExeQty(), orderService.getExecutedQuantity(RIC, new User(1)));
            assertExeQuantity(expectedResult.getU2ExeQty(), orderService.getExecutedQuantity(RIC, new User(2)));
        }
    }
    private void assertOpenInt(Map<BigDecimal, Long> expectedValue , Map<BigDecimal, Long> openIntResult){
        assertEquals(expectedValue, openIntResult);
    }

    private void assertAvgExePrice(BigDecimal expectedValue, BigDecimal actualValue) {
        assertEquals(expectedValue, actualValue);
    }

    private void assertExeQuantity(long expectedValue, long actualValue) {
        assertEquals(expectedValue, actualValue);
    }
}
