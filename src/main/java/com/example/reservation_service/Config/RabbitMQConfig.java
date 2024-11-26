package com.example.reservation_service.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

    // Define a queue for sending messages
    @Bean
    public Queue passengerQueue() {
        return new Queue("passengerQueue", true);  // durable queue
    }

    // Define a topic exchange
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("passengerExchange", true, false);  // durable exchange, non-auto delete
    }

    // Define the custom MessageConverter for serializing PassengerCredentialsDTO to JSON
    @Bean
    public MessageConverter messageConverter() {
        return new CustomMessageConverter(); // Your custom message converter
    }

    // Configure RabbitTemplate to use the custom MessageConverter
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());  // Set custom converter here
        return rabbitTemplate;
    }

    // Bind the queue to the exchange
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(passengerQueue())
                .to(exchange())
                .with("passengerRoutingKey"); // Set the routing key for binding the queue and exchange
    }
}
