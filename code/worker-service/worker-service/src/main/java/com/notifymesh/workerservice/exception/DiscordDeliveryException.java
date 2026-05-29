package com.notifymesh.workerservice.exception;

public class DiscordDeliveryException extends RuntimeException {

    public DiscordDeliveryException(int statusCode, String body) {
        super("Discord API error [" + statusCode + "]: " + body);
    }
}
