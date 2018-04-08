package com.rbc.ex.config;

import com.rbc.ex.dao.OrderDao;
import com.rbc.ex.service.OrderService;
import com.rbc.ex.service.OrderServiceImpl;
import com.rbc.ex.util.IdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Application configuration class.
 */
@Configuration
public class AppConfig {

    @Bean
    public OrderService orderService(){
        return new OrderServiceImpl(orderDao());
    }

    @Bean
    public OrderDao orderDao() {
        return new OrderDao();
    }

    @Bean
    public IdGenerator idGenerator() {
        return new IdGenerator();
    }
}
