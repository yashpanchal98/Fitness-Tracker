package com.fitness.activityService.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMqConfig {
    
    // Defining Queue
    @Bean
    public Queue activityQueue(){
        return new Queue("activity.queue", true); // true = durable means rabbit mq restarts the queue wont get restart it remains
    }

    @Bean
    public DirectExchange fitnessExchange() {
        return new DirectExchange("fitness.exchange");
    }

    @Bean
    public Binding binding(Queue activityQueue, DirectExchange fitnessExchange) {
        // This is the "Glue" that forces RabbitMQ to create the exchange
        return BindingBuilder.bind(activityQueue).to(fitnessExchange).with("activity.tracking");
    }

    // converts java obj to json before sending into rabbitMQ
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
