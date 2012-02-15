/* Combat Protector, Prevents users from logging out/teleporting in combat.
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
package com.tetra.combatprotector;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.tetra.combatprotector.listeners.CombatProtectorEntityListener;
import com.tetra.combatprotector.listeners.CombatProtectorPlayerListener;

import java.util.ArrayList;
import java.util.logging.Logger;

public class CombatProtector extends JavaPlugin {
	public ArrayList<Player> combatlogon = new ArrayList<Player>();
	public ArrayList<Player> playerlist = new ArrayList<Player>();
	public ArrayList<ArrayList<String>> combatlogs = new ArrayList<ArrayList<String>>();
	public ArrayList<Player> safeLogoutList = new ArrayList<Player>();
	public ArrayList<MarkHandler> markHandler = new ArrayList<MarkHandler>();
	private CombatLogger CL = new CombatLogger(this);
	CombatProtectorEntityListener cpel;
	CombatProtectorPlayerListener cppl;
	Logger log = Logger.getLogger("Minecraft");
	public PluginDescriptionFile info;
	Configuration config = new Configuration();

	public void onEnable() {
		info = this.getDescription();
		log.info("CombatProtector has been activated");
		cpel = new CombatProtectorEntityListener(this);
		cppl = new CombatProtectorPlayerListener(this);
		config.setConfDefaults();
		initLists();
		if (!config.getEnabled()) {
			this.getServer().getPluginManager().disablePlugin(this);
		}
	}

	public void onDisable() {
		log.info("Shutting down CombatProtector");
	}

	public void initLists() {
		Player[] plist = this.getServer().getOnlinePlayers();
		for (int i = 0; i < plist.length; i++) {
			playerlist.add(plist[i]);
			combatlogs.add(new ArrayList<String>());

		}
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}
		if (cmd.getName().equalsIgnoreCase("combatlist")) {
			if (player == null) {
				sender.sendMessage("This command can only be run by a player");
			} else {
				int Total = 0;
				Total = safeLogoutList.size();
				for (Player p : safeLogoutList) {
					sender.sendMessage(p.getDisplayName());
				}
				if (Total == 0) {
					player.sendMessage("The lists are empty");
				}

			}
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("cblog")) {
			if (config.getCLEnabled()) {
				if (player == null) {
					sender.sendMessage("This command can only be run by a player");
				} else {
					if (args.length == 0) {
						CL.GetCombatLog(player);
					}
					if (args.length > 0) {
						if (args[0].equals("on")) {
							if (!combatlogon.contains(player)) {
								combatlogon.add(player);
								player.sendMessage("CombatLog is now on");
							} else {
								combatlogon.remove(player);
								player.sendMessage("CombatLog is now off");
							}
						}
						if (args[0].equals("off")) {
							if (combatlogon.contains(player)) {
								combatlogon.remove(player);
								player.sendMessage("CombatLog is now off");
							} else {
								combatlogon.add(player);
								player.sendMessage("CombatLog is now on");
							}
						}
						if (!args[0].equals("on") && !args[0].equals("off")) {
							player.sendMessage(cmd.getUsage());
						}
					}
				}
				return true;
			}
		}
		// this hasn't happened the a value of false will be returned.
		return false;
	}
}
