package com.challenge.pinapp.mapper;

import com.challenge.pinapp.dto.ClientResponseDTO;
import com.challenge.pinapp.entity.ClientEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;

@Mapper(componentModel = "spring", imports = LocalDate.class)
public interface ClientEventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clientId", expression = "java(clientResponseDTO.id())")
    @Mapping(target = "consumedAt", expression = "java(LocalDate.now())")
    ClientEvent toEntity(ClientResponseDTO clientResponseDTO);
}
