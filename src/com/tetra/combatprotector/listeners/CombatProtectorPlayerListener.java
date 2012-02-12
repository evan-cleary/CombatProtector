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

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.tetra.combatprotector.CombatProtector;
import com.tetra.combatprotector.Configuration;
import com.tetra.combatprotector.MarkHandler;

public class CombatProtectorPlayerListener implements Listener {
	public CombatProtector plugin;
	Configuration config = new Configuration();

	public CombatProtectorPlayerListener(CombatProtector instance) {
		plugin = instance;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		try {
			MarkHandler MH = plugin.markHandler.get(plugin.safeLogoutList
					.indexOf(event.getPlayer()));
			if (config.getEnabled()) {
				if (MH.checkTagged(event.getPlayer())) {
					plugin.getServer().broadcastMessage(
							event.getPlayer().getDisplayName()
									+ ChatColor.DARK_RED
									+ " has combat logged... what a wuss. ");
					event.getPlayer().setHealth(0);
				}
				MH.safeOn(event.getPlayer());
				if (plugin.playerlist.contains(event.getPlayer())) {
					if (config.getCLEnabled()) {
						plugin.combatlogs.remove(plugin.playerlist
								.indexOf(event.getPlayer()));
					}
					plugin.playerlist.remove(event.getPlayer());
				}
			}
		} catch (Exception ex) {

		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerKick(PlayerKickEvent event) {
		try {
			MarkHandler MH = plugin.markHandler.get(plugin.safeLogoutList
					.indexOf(event.getPlayer()));
			if (config.getEnabled()) {
				if (MH.checkTagged(event.getPlayer())) {
					MH.safeOn(event.getPlayer());
				}
				if (plugin.playerlist.contains(event.getPlayer())) {
					if (config.getCLEnabled()) {
						plugin.combatlogs.remove(plugin.playerlist
								.indexOf(event.getPlayer()));
					}
					plugin.playerlist.remove(event.getPlayer());
				}
			}
		} catch (Exception ex) {

		}

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (config.getEnabled()) {
			event.getPlayer().sendMessage(
					ChatColor.GOLD
							+ "This server runs CombatProtector revision: "
							+ plugin.info.getVersion()
							+ ". Powered by SmashPVP.");
			plugin.playerlist.add(event.getPlayer());
			if (config.getCLEnabled()) {
				plugin.combatlogs.add(new ArrayList<String>());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (config.getEnabled()) {
			try {
				MarkHandler MH = plugin.markHandler.get(plugin.safeLogoutList
						.indexOf(event.getPlayer()));
				if (MH.checkTagged(event.getPlayer())) {
					MH.sendTimeRemain();
					event.setCancelled(true);
				}
			} catch (Exception ex) {

			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPTestlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (config.getEnabled()) {
			try {
				MarkHandler MH = plugin.markHandler.get(plugin.safeLogoutList
						.indexOf(event.getPlayer()));
				if (MH.checkTagged(event.getPlayer())) {
					MH.sendTimeRemain();
					event.setCancelled(true);
				}
			} catch (Exception ex) {

			}
		}
	}
}
