package com.challenge.pinapp.service;

import com.challenge.pinapp.dto.ClientRequestDTO;
import com.challenge.pinapp.dto.ClientResponseDTO;
import com.challenge.pinapp.entity.Client;
import com.challenge.pinapp.event.producer.ClientProducer;
import com.challenge.pinapp.mapper.ClientMapper;
import com.challenge.pinapp.repository.ClientRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceImplTest {
    @Mock
    private ClientMapper clientMapper;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ClientProducer clientProducer;
    @Mock
    private MeterRegistry meterRegistry;
    @Mock
    private Counter counter;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    void givenClientServiceImpl_whenCreateClient_thenSaveAndReturnResponseDTO() {
        // Etapa de preparar (Arrange)
        ClientRequestDTO requestDTO = ClientRequestDTO.builder()
                .firstName("Maximiliano")
                .lastName("Giuliano")
                .age(30)
                .birthDate(LocalDate.of(1994,12,23))
                .build();

        Client client = Client.builder()
                .id(1L)
                .firstName("Maximiliano")
                .lastName("Giuliano")
                .age(30)
                .birthDate(LocalDate.of(1994,12,23))
                .build();

        ClientResponseDTO responseDTO = ClientResponseDTO.builder()
                .id(1L)
                .firstName("Maximiliano")
                .lastName("Giuliano")
                .age(30)
                .birthDate(LocalDate.of(1994,12,23))
                .estimatedDeathDate(LocalDate.of(2074,12,23))
                .build();

        when(clientMapper.toEntity(requestDTO)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);
        when(meterRegistry.counter(anyString(), any(String[].class))).thenReturn(counter);
        when(clientMapper.toResponseDto(client)).thenReturn(responseDTO);

        // Etapa de Actuar (Act)
        ClientResponseDTO result = clientService.createClient(requestDTO);

        // Etapa de Afirmar (Assert)
        assertNotNull(result);
        assertEquals("Maximiliano", result.firstName());
        verify(clientRepository, times(1)).save(client);
        verify(clientProducer, times(1)).sendClientCreatedEvent(responseDTO);
    }

    @Test
    void givenClientServiceImpl_whenFindAllClients_thenReturnAllClients() {
        // Arrange
        Client client = Client.builder()
                .id(1L)
                .firstName("Maximiliano")
                .lastName("Giuliano")
                .age(30)
                .birthDate(LocalDate.of(1994,12,23))
                .build();

        ClientResponseDTO response = ClientResponseDTO.builder()
                .id(1L)
                .firstName("Maximiliano")
                .lastName("Giuliano")
                .age(30)
                .birthDate(LocalDate.of(1994,12,23))
                .estimatedDeathDate(LocalDate.of(2074,12,23))
                .build();

        when(clientRepository.findAll()).thenReturn(List.of(client));
        when(clientMapper.toResponseDto(client)).thenReturn(response);

        // Act
        List<ClientResponseDTO> result = clientService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Maximiliano", result.getFirst().firstName());
    }

    @Test
    void givenClientServiceImpl_whenGetMetrics_thenReturnAverageVarianceStandardDeviationMetrics() {
        // Arrange
        Client client1 = Client.builder()
                .id(1L)
                .firstName("Maximiliano")
                .lastName("Giuliano")
                .age(30)
                .birthDate(LocalDate.of(1994,12,23))
                .build();

        Client client2 = Client.builder()
                .id(2L)
                .firstName("Shirley")
                .lastName("Giuliano")
                .age(29)
                .birthDate(LocalDate.of(1996,8,5))
                .build();

        when(clientRepository.findAll()).thenReturn(List.of(client1,client2));

        // Act
        Map<String, Double> result = clientService.getMetrics();

        // Assert
        assertEquals(29.5, result.get("averageAge"));
        assertTrue(result.get("standardDeviationOfAge") > 0);
    }
}
