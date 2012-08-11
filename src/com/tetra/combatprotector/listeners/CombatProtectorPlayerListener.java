/* Combat Protector, Prevents users from logging out /teleporting in combat.
 Copyright (C) 2012  Evan Cleary

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.*/
package com.tetra.combatprotector.listeners;

import com.tetra.combatprotector.CombatProtector;
import com.tetra.combatprotector.Configuration;
import com.tetra.combatprotector.handlers.MarkHandler;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class CombatProtectorPlayerListener implements Listener {

    public CombatProtector plugin;
    Configuration config = new Configuration();

    public CombatProtectorPlayerListener(CombatProtector instance) {
        plugin = instance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player evtplayer = event.getPlayer();
        try {
            MarkHandler MH = plugin.markHandlers.get(evtplayer);
            if (MH.checkTagged(evtplayer)) {
                plugin.getServer().broadcastMessage(
                        evtplayer.getName() + ChatColor.DARK_RED
                        + " has combat logged... what a wuss. ");
                evtplayer.setHealth(0);
                evtplayer.remove();
            }
            MH.safeOn(evtplayer);
            if (plugin.playerlist.contains(evtplayer)) {
                plugin.combatlogs.remove(plugin.playerlist.indexOf(evtplayer));
                plugin.playerlist.remove(evtplayer);
            }
        } catch (Exception ex) {
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKick(PlayerKickEvent event) {
        try {
            MarkHandler MH = plugin.markHandlers.get(event.getPlayer());
            if (MH.checkTagged(event.getPlayer())) {
                MH.safeOn(event.getPlayer());
            }
            if (plugin.playerlist.contains(event.getPlayer())) {
                plugin.combatlogs.remove(plugin.playerlist.indexOf(event.getPlayer()));
                plugin.playerlist.remove(event.getPlayer());
                event.getPlayer().remove();
            }
        } catch (Exception ex) {
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (config.getWelcomeMessage()) {
            event.getPlayer().sendMessage(
                    ChatColor.GOLD + "This server runs CombatProtector revision: "
                    + plugin.info.getVersion() + ". Powered by Division Studios.");
        }
        plugin.playerlist.add(event.getPlayer());
        plugin.combatlogs.add(new ArrayList<String>());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (config.getEnabled()) {
            try {
                MarkHandler MH = plugin.markHandlers.get(event.getPlayer());
                if (MH.checkTagged(event.getPlayer())) {
                    MH.sendTimeRemain();
                    if (!event.isCancelled()) {
                        double dist = event.getTo().distance(event.getFrom());
                        if (dist >= 1) {
                            if (event.getCause() == TeleportCause.ENDER_PEARL) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        try {
            MarkHandler MH = plugin.markHandlers.get(event.getPlayer());
            if (MH.checkTagged(event.getPlayer()) && !event.getMessage().contains("/cblog")) {
                MH.sendTimeRemain();
                event.setCancelled(true);
            }
        } catch (Exception ex) {
        }
    }
}
