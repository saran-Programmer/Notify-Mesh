package com.notifymesh.notificationservice.infrastructure.repository;

import com.notifymesh.notificationservice.infrastructure.entity.PriorityTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriorityRepository extends JpaRepository<PriorityTable, Long> {

    Optional<PriorityTable> findByName(String name);
}
