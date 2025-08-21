package com.challenge.pinapp.event.producer;

import com.challenge.pinapp.dto.ClientResponseDTO;
import com.challenge.pinapp.exception.KafkaPublishException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class ClientProducer {
    private static final Logger LOG = LoggerFactory.getLogger(ClientProducer.class);

    private final KafkaTemplate<String, ClientResponseDTO> kafkaTemplate;

    // "${app.kafka.topic.clients}"
    @Value("${topic}")
    private String topic;

    public void sendClientCreatedEvent(ClientResponseDTO clientResponseDTO) {
        LOG.info("Enviando evento de cliente creado: {}", clientResponseDTO);

        CompletableFuture<SendResult<String, ClientResponseDTO>> future = kafkaTemplate.send(topic, clientResponseDTO);

        future.whenCompleteAsync((result, throwable) -> {
            if(throwable != null) {
                throw new KafkaPublishException("Ha ocurrido un error al enviar el evento de cliente creado: " + throwable.getMessage());
            }

            LOG.info("Se ha registrado el cliente: {}, en el tópico: {}",
                    result.getProducerRecord().value().firstName(), result.getRecordMetadata().topic());
        });
    }
}