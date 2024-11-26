package com.example.reservation_service.Config;

import com.example.reservation_service.Dto.PassengerCredentialsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

public class CustomMessageConverter implements MessageConverter {
    private static final Logger logger = LoggerFactory.getLogger(CustomMessageConverter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();  // Jackson ObjectMapper

    public CustomMessageConverter() {
        // You can configure the ObjectMapper here if needed (e.g., for pretty printing, date formats, etc.)
    }

    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        try {
            // Log the message content before converting
            logger.debug("Converting object to message: {}", object);

            // Serialize the DTO object into a JSON byte array
            byte[] body = objectMapper.writeValueAsBytes(object);
            return new Message(body, messageProperties);  // Return the message with body and properties
        } catch (Exception e) {
            throw new MessageConversionException("Error converting object to message", e);
        }
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        try {
            // Log the received message body
            logger.debug("Converting message to object: {}", new String(message.getBody()));

            // Deserialize the JSON byte array back into a PassengerCredentialsDTO
            return objectMapper.readValue(message.getBody(), PassengerCredentialsDTO.class);
        } catch (Exception e) {
            throw new MessageConversionException("Error converting message to object", e);
        }
    }
}
