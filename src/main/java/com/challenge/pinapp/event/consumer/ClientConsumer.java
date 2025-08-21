package com.challenge.pinapp.event.consumer;

import com.challenge.pinapp.dto.ClientResponseDTO;
import com.challenge.pinapp.entity.ClientEvent;
import com.challenge.pinapp.mapper.ClientEventMapper;
import com.challenge.pinapp.repository.ClientEventRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ClientConsumer {
    public static final Logger LOG = LoggerFactory.getLogger(ClientConsumer.class);

    private final ClientEventMapper clientEventMapper;
    private final ClientEventRepository clientEventRepository;

    @KafkaListener(topics = "${topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ClientResponseDTO clientResponseDTO) {
        LOG.info("Consumiendo cliente con datos: {}", clientResponseDTO.toString());

        ClientEvent clientEvent = clientEventMapper.toEntity(clientResponseDTO);
        clientEventRepository.save(clientEvent);

        LOG.info("Cliente consumido guardado correctamente.");
    }
}
