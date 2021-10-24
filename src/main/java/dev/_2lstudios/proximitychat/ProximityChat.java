package dev._2lstudios.proximitychat;

import org.bukkit.plugin.java.JavaPlugin;

import dev._2lstudios.proximitychat.listeners.AsyncPlayerChatListener;

public class ProximityChat extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(new AsyncPlayerChatListener(this), this);
    }

}
