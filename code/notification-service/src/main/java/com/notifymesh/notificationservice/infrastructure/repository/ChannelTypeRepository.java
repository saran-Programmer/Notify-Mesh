package com.notifymesh.notificationservice.infrastructure.repository;

import com.notifymesh.notificationservice.infrastructure.entity.ChannelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChannelTypeRepository extends JpaRepository<ChannelType, Long> {

    Optional<ChannelType> findByName(String name);
}
