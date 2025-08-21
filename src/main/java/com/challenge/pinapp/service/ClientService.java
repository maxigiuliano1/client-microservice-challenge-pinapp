package com.challenge.pinapp.service;

import com.challenge.pinapp.dto.ClientRequestDTO;
import com.challenge.pinapp.dto.ClientResponseDTO;

import java.util.List;
import java.util.Map;

public interface ClientService {
    ClientResponseDTO createClient(ClientRequestDTO clientRequestDTO);
    List<ClientResponseDTO> findAll();
    Map<String, Double> getMetrics();
}
