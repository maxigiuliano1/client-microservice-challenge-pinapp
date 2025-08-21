package com.challenge.pinapp.repository;

import com.challenge.pinapp.entity.ClientEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientEventRepository extends JpaRepository<ClientEvent, Long> {
}
