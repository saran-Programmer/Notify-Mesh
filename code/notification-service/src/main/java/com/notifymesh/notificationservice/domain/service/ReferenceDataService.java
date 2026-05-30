package com.notifymesh.notificationservice.domain.service;

import com.notifymesh.notificationservice.constants.CacheNames;
import com.notifymesh.notificationservice.exception.NotFoundException;
import com.notifymesh.notificationservice.infrastructure.entity.ChannelType;
import com.notifymesh.notificationservice.infrastructure.entity.PriorityTable;
import com.notifymesh.notificationservice.infrastructure.repository.ChannelTypeRepository;
import com.notifymesh.notificationservice.infrastructure.repository.PriorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReferenceDataService {

    private final ChannelTypeRepository channelTypeRepository;
    
    private final PriorityRepository priorityRepository;

    @Cacheable(value = CacheNames.CHANNEL, key = "#name")
    public ChannelType getChannelByName(String name) {
        return channelTypeRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("channel type '" + name + "' not configured"));
    }

    @Cacheable(value = CacheNames.PRIORITY, key = "#name")
    public PriorityTable getPriorityByName(String name) {
        return priorityRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("priority '" + name + "' not configured"));
    }
}
