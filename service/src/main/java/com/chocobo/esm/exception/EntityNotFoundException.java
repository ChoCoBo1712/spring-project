package com.chocobo.esm.exception;

public class EntityNotFoundException extends RuntimeException {

    private final long entityId;

    public EntityNotFoundException(long entityId) {
        this.entityId = entityId;
    }

    public long getEntityId() {
        return entityId;
    }
}
