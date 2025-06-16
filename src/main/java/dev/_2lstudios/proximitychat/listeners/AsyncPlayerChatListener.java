package dev._2lstudios.proximitychat.listeners;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import dev._2lstudios.proximitychat.ProximityChat;

public class AsyncPlayerChatListener implements Listener {

    private final ProximityChat plugin;

    private final boolean cancelMessage;
    private final double chatDistance;
    private final String globalPrefix;

    private final String noReaderMessage;
    private final String spyFormat;
    private final String globalFormat;

    public AsyncPlayerChatListener(final ProximityChat plugin) {
        this.plugin = plugin;

        this.cancelMessage = plugin.getConfig().getBoolean("settings.cancel-message");
        this.chatDistance = plugin.getConfig().getDouble("settings.maximum-distance-to-receive");
        this.globalPrefix = plugin.getConfig().getString("settings.global-prefix");

        this.noReaderMessage = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.no-reader"));

        this.spyFormat = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.spy-format"));
        this.globalFormat = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.global-format"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChat(final AsyncPlayerChatEvent e) {
        boolean bypass = false;

        if (e.getMessage().startsWith(this.globalPrefix) && e.getPlayer().hasPermission("proximitychat.global")) {
            bypass = true;
            e.setMessage(e.getMessage().replaceFirst(this.globalPrefix, ""));
            e.setFormat(this.globalFormat.replace("{format}", e.getFormat()).replace("{message}", e.getMessage())
                    .replace("{player}", e.getPlayer().getName()));
        }

        if (!bypass) {
            final Iterator<Player> iterator = e.getRecipients().iterator();
            while (iterator.hasNext()) {
                final Player recipient = iterator.next();
                if (recipient.getWorld() == e.getPlayer().getWorld()) {
                    if (recipient.getLocation().distance(e.getPlayer().getLocation()) > chatDistance) {
                        iterator.remove();
                    }
                } else {
                	iterator.remove();
                }
            }
        }

        if (e.getRecipients().size() == 1 && this.noReaderMessage != null && !this.noReaderMessage.isEmpty()) {
           e.getPlayer().sendMessage(this.noReaderMessage.replace("{message}", e.getMessage()).replace("{player}", e.getPlayer().getName()));
            if (this.cancelMessage) {
                e.setCancelled(true);
            }
        }

        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            if (!e.getRecipients().contains(player) && player.hasPermission("proximitychat.spy")) {
                player.sendMessage(this.spyFormat.replace("{message}", e.getMessage()).replace("{player}",
                        e.getPlayer().getName()));
            }
        }
    }
}
