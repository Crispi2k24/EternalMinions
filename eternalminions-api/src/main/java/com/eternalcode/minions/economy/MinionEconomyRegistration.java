package com.eternalcode.minions.economy;

public interface MinionEconomyRegistration extends AutoCloseable {

    void unregister();

    boolean isRegistered();

    @Override
    default void close() {
        this.unregister();
    }
}
