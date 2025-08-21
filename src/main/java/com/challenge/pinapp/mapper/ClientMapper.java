package com.challenge.pinapp.mapper;

import com.challenge.pinapp.dto.ClientRequestDTO;
import com.challenge.pinapp.dto.ClientResponseDTO;
import com.challenge.pinapp.entity.Client;
import com.challenge.pinapp.util.LifeExpectancyCalculator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/* Indico a MapStruct que la clase de impl generada tiene que ser un componente de spring.
   Spring se encarga de gestionar el ciclo de vida del bean, con esto podemos inyectarlo en servicios o controladores,
    usando @Autowired o inyección de dependencia por constructor.
 */
@Mapper(componentModel = "spring", imports = {LifeExpectancyCalculator.class})
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    Client toEntity(ClientRequestDTO clientRequestDTO);

    @Mapping(target = "estimatedDeathDate",
            expression = "java(LifeExpectancyCalculator.calculateEstimatedDeathDate(client.getBirthDate()))")
    ClientResponseDTO toResponseDto(Client client);
}
