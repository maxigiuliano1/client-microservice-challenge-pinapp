package com.challenge.pinapp.service;

import com.challenge.pinapp.dto.ClientRequestDTO;
import com.challenge.pinapp.dto.ClientResponseDTO;
import com.challenge.pinapp.entity.Client;
import com.challenge.pinapp.event.producer.ClientProducer;
import com.challenge.pinapp.mapper.ClientMapper;
import com.challenge.pinapp.repository.ClientRepository;
import com.challenge.pinapp.util.MetricCalculator;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ClientServiceImpl implements ClientService{

    private final ClientMapper clientMapper;
    private final ClientRepository clientRepository;
    private final ClientProducer clientProducer;
    private final MeterRegistry meterRegistry;

    // También se puede utilizar la anotación RequiredArgsConstructor de lombok y quitar este constructor
    ClientServiceImpl(ClientMapper clientMapper, ClientRepository clientRepository, ClientProducer clientProducer, MeterRegistry meterRegistry) {
        this.clientMapper = clientMapper;
        this.clientRepository = clientRepository;
        this.clientProducer = clientProducer;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public ClientResponseDTO createClient(ClientRequestDTO clientRequestDTO) {
        Client client = clientMapper.toEntity(clientRequestDTO);
        Client savedClient = clientRepository.save(client);
        ClientResponseDTO clientResponseDTO = clientMapper.toResponseDto(savedClient);

        // Enviamos evento de cliente creado a kafka
        clientProducer.sendClientCreatedEvent(clientResponseDTO);

        // Incrementar metrica en prometheus
        meterRegistry.counter("clients.created.count").increment();

        return clientResponseDTO;
    }

    @Override
    public List<ClientResponseDTO> findAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toResponseDto)
                .toList();
    }

    @Override
    public Map<String, Double> getMetrics() {
        List<Client> clients = clientRepository.findAll();

        if (clients.isEmpty()) {
            // registrar metricas
            meterRegistry.gauge("clients.average.age", 0.0);
            meterRegistry.gauge("clients.age.variance", 0.0);
            meterRegistry.gauge("clients.age.stddev", 0.0);

            return Map.of(
                    "averageAge", 0.0,
                    "ageVariance", 0.0,
                    "standardDeviationOfAge", 0.0
            );
        }

        Double averageAgeClients = MetricCalculator.averageAge(clients);
        Double clientsAgeVariance = MetricCalculator.varianceOfAge(clients,averageAgeClients);
        Double standardDeviationOfAge = Math.sqrt(clientsAgeVariance);

        // registrar métricas en 0
        meterRegistry.gauge("clients.average.age", averageAgeClients);
        meterRegistry.gauge("clients.age.variance", clientsAgeVariance);
        meterRegistry.gauge("clients.age.stddev", standardDeviationOfAge);

        return Map.of(
                "averageAge", averageAgeClients,
                "ageVariance", clientsAgeVariance,
                "standardDeviationOfAge", standardDeviationOfAge
        );
    }
}
