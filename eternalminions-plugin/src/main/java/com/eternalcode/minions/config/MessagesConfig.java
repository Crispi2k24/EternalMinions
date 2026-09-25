package com.eternalcode.minions.config;

import com.eternalcode.multification.notice.Notice;
import java.nio.file.Path;

public final class MessagesConfig extends ConfigurationFile {

    @Override
    public Path resolve(Path dataDirectory) {
        return dataDirectory.resolve("messages.yml");
    }

    public Notice noPermission = Notice.chat(prefix() + "<red>Nie masz uprawnień do tej komendy.");
    public Notice playerNotFound = Notice.chat(prefix() + "<red>Nie znaleziono gracza.");
    public Notice playerOnly = Notice.chat(prefix() + "<red>Tej komendy może użyć tylko gracz.");
    public Notice correctUsage = Notice.chat(prefix() + "<gray>Poprawne użycie: <aqua>{USAGE}");
    public Notice correctUsageHead = Notice.chat(prefix() + "<gray>Poprawne użycie:");
    public Notice correctUsageEntry = Notice.chat("<dark_gray>-</dark_gray> <aqua>{USAGE}");
    public Notice reloadCompleted = Notice.chat(prefix() + "<green>Konfiguracja przeładowana.");

    public Notice minionToolUpdated = Notice.chat(prefix() + "<green>Narzędzie minionka zostało zmienione.");
    public Notice minionStorageCollected = Notice.chat(prefix() + "<green>Zabrano zawartość magazynu minionka.");
    public Notice minionPickedUp = Notice.chat(
        prefix() + "<green>Minionek podniesiony. <dark_gray>({MINION_LIMIT_CURRENT}/{MINION_LIMIT_MAX})"
    );
    public Notice minionOwnerRequired = Notice.chat(prefix() + "<red>Tym minionkiem zarządza tylko jego właściciel.");
    public Notice minionNotFound = Notice.chat(prefix() + "<red>Tego minionka już nie ma.");
    public Notice minionPlaced = Notice.chat(
        prefix() + "<green>Minionek postawiony. <dark_gray>({MINION_LIMIT_CURRENT}/{MINION_LIMIT_MAX})"
    );
    public Notice minionItemReceived = Notice.chat(prefix() + "<green>Otrzymałeś minionka.");
    public Notice minionPlacementBlocked = Notice.chat(prefix() + "<red>Tutaj nie postawisz minionka.");
    public Notice blockPlacementBlockedByMinion = Notice.chat(prefix() + "<red>Nie postawisz bloku na minionku.");
    public Notice minionTypeUnknown = Notice.chat(prefix() + "<red>Nie ma takiego rodzaju minionka.");
    public Notice addonReceived = Notice.chat(prefix() + "<green>Otrzymałeś przedmiot dla minionka.");
    public Notice addonUnknown = Notice.chat(prefix() + "<red>Nie ma takiego paliwa ani modułu.");
    public Notice addonWrongSlot = Notice.chat(prefix() + "<red>Ten przedmiot nie pasuje do tego pola.");
    public Notice minionLimitReached = Notice.chat(
        prefix() + "<red>Osiągnąłeś limit minionków <dark_gray>({MINION_LIMIT_CURRENT}/{MINION_LIMIT_MAX})<red>."
    );
    public Notice upgradePurchased = Notice.chat(prefix() + "<green>Ulepszenie kupione.");
    public Notice upgradeMaxed = Notice.chat(prefix() + "<red>To ulepszenie jest już na najwyższym poziomie.");
    public Notice upgradeRequiresLevel = Notice.chat(prefix() + "<red>Ten minionek ma za niski poziom na to ulepszenie.");
    public Notice upgradeCannotAfford = Notice.chat(prefix() + "<red>Nie stać cię na to ulepszenie.");

    public Notice chestLinkStart = Notice.chat(prefix() + "<gray>Kliknij PPM skrzynię, aby podłączyć ją do minionka.");
    public Notice chestLinked = Notice.chat(prefix() + "<green>Skrzynia podłączona do minionka.");
    public Notice chestUnlinked = Notice.chat(prefix() + "<green>Skrzynia odłączona od minionka.");
    public Notice chestLinkTooFar = Notice.chat(prefix() + "<red>Ta skrzynia jest za daleko od minionka.");
    public Notice chestLinkUnsupported = Notice.chat(prefix() + "<red>Minionka nie da się podłączyć pod ten pojemnik.");
    public Notice chestLinkExpired = Notice.chat(prefix() + "<red>Minął czas na wybranie skrzyni.");
    public Notice chestLinkDestroyed =
        Notice.chat(prefix() + "<red>Skrzynia podłączona do twojego minionka została zniszczona.");

    public Notice minionRotated = Notice.chat(prefix() + "<green>Minionek obrócony.");

    public Notice skinPurchased = Notice.chat(
        prefix() + "<green>Kupiłeś wygląd {SKIN}<green>. Możesz go zakładać na wszystkie swoje minionki."
    );
    public Notice skinCannotAfford = Notice.chat(prefix() + "<red>Nie stać cię na ten wygląd.");
    public Notice skinNotReady = Notice.chat(prefix() + "<red>Wczytujemy twoje wyglądy, spróbuj za chwilę.");

    private static String prefix() {
        return "<white><bold>MINIONKI > </bold>";
    }
}
