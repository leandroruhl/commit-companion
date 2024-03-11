package com.leandroruhl.commitcompanion.listeners;

import discord4j.core.event.domain.Event;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

public interface EventListener<T extends Event> {
    Class<T> getEventType();

    Mono<Void> execute(T event);

    default Mono<Void> handleError(Throwable error) {
        System.out.println("An error occurred: " + error.getMessage());
        return Mono.empty();
    }
}
