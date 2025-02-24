package com.eternalcode.core.teleport;

import com.eternalcode.core.notification.NoticeService;
import com.eternalcode.core.notification.NoticeType;
import com.eternalcode.core.shared.PositionAdapter;
import com.eternalcode.core.util.DurationUtil;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.utilities.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeleportTask implements Runnable {

    private final NoticeService noticeService;
    private final TeleportTaskService teleportTaskService;
    private final TeleportService teleportService;
    private final Server server;

    public TeleportTask(NoticeService noticeService, TeleportTaskService teleportTaskService, TeleportService teleportService, Server server) {
        this.noticeService = noticeService;
        this.teleportTaskService = teleportTaskService;
        this.teleportService = teleportService;
        this.server = server;
    }

    @Override
    public void run() {
        List<Teleport> teleportsCopy = new ArrayList<>(this.teleportTaskService.getTeleports());

        for (Teleport teleport : teleportsCopy) {
            Location destinationLocation = PositionAdapter.convert(teleport.getDestinationLocation());
            Location startLocation = PositionAdapter.convert(teleport.getStartLocation());
            UUID uuid = teleport.getUuid();
            Instant teleportMoment = teleport.getTeleportMoment();

            Player player = this.server.getPlayer(uuid);

            if (player == null) {
                continue;
            }

            if (player.getLocation().distance(startLocation) > 0.5) {
                this.teleportTaskService.removeTeleport(uuid);

                this.noticeService.create()
                    .notice(NoticeType.ACTIONBAR, translation -> StringUtils.EMPTY)
                    .notice(translation -> translation.teleport().teleportTaskCanceled())
                    .player(player.getUniqueId())
                    .send();

                continue;
            }

            Instant now = Instant.now();

            if (now.isBefore(teleportMoment)) {
                Duration duration = Duration.between(now, teleportMoment);

                this.noticeService.create()
                    .notice(translation -> translation.teleport().teleportTimerFormat())
                    .placeholder("{TIME}", DurationUtil.format(duration))
                    .player(player.getUniqueId())
                    .send();

                continue;
            }

            this.teleportService.teleport(player, destinationLocation);
            this.teleportTaskService.removeTeleport(uuid);

            this.noticeService.create()
                .notice(translation -> translation.teleport().teleported())
                .player(player.getUniqueId())
                .send();
        }
    }
}

