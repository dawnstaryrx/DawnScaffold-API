package com.gooodh.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String QUEUE_NAME = "test.queue";
    public static final String EXCHANGE_NAME = "test.exchange";
    public static final String ROUTING_KEY = "test.routing";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true); // durable=true 表示持久化
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
}
