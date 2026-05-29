package com.notifymesh.workerservice.domain.service.delivery;

import com.notifymesh.workerservice.domain.valueobject.Channel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DeliveryStrategyResolver {

    private final Map<Channel, NotificationSender> strategies;

    public DeliveryStrategyResolver(List<NotificationSender> senders) {
        this.strategies = senders.stream()
                .collect(Collectors.toMap(NotificationSender::channel, Function.identity()));
    }

    public NotificationSender resolve(Channel channel) {
        
        NotificationSender sender = strategies.get(channel);

        if (sender == null) {
            throw new UnsupportedOperationException("No delivery strategy registered for channel: " + channel);
        }
        return sender;
    }
}
