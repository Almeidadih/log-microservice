package com.logistica.fiscal.outbox;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface OutboxEventRepository extends ReactiveCrudRepository<OutboxEvent, UUID> {
}
