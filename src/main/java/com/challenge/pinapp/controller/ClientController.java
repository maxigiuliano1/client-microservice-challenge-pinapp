package com.challenge.pinapp.controller;

import com.challenge.pinapp.dto.ClientRequestDTO;
import com.challenge.pinapp.dto.ClientResponseDTO;
import com.challenge.pinapp.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/clients")
@RequiredArgsConstructor
@RestController
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponseDTO> createClient(@Valid @RequestBody ClientRequestDTO clientRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clientService.createClient(clientRequestDTO));
    }

    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(clientService.findAll());
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String,Double>> getMetrics() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(clientService.getMetrics());
    }
}
