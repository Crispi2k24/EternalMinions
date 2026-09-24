package com.eternalcode.minions.economy;

import java.math.BigDecimal;
import java.util.UUID;

public interface MinionEconomyProvider {

    boolean available();

    boolean withdraw(UUID playerId, BigDecimal amount);

    String format(BigDecimal amount);
}
