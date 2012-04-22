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

import com.tetra.combatprotector.handlers.MarkHandler;
import com.tetra.combatprotector.handlers.StreamHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.tetra.combatprotector.listeners.CombatProtectorEntityListener;
import com.tetra.combatprotector.listeners.CombatProtectorPlayerListener;

import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.ChatColor;

public class CombatProtector extends JavaPlugin {

    public ArrayList<Player> combatlogon = new ArrayList<Player>();
    public ArrayList<Player> playerlist = new ArrayList<Player>();
    public ArrayList<ArrayList<String>> combatlogs = new ArrayList<ArrayList<String>>();
    public ArrayList<Player> safeLogoutList = new ArrayList<Player>();
    public ArrayList<MarkHandler> markHandler = new ArrayList<MarkHandler>();
    public ArrayList<CombatStream> combatStreams = new ArrayList<CombatStream>();
    private CombatLogger CL = new CombatLogger(this);
    CombatProtectorEntityListener cpel;
    CombatProtectorPlayerListener cppl;
    Logger log = Logger.getLogger("Minecraft");
    public PluginDescriptionFile info;
    public Configuration config = new Configuration();
    StreamHandler SH;

    @Override
    public void onEnable() {
        info = this.getDescription();
        log.info("CombatProtector has been activated");
        cpel = new CombatProtectorEntityListener(this);
        cppl = new CombatProtectorPlayerListener(this);
        config.setConfDefaults();
        SH = new StreamHandler(this);
        initLists();
        if (!config.getEnabled()) {
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
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

    @Override
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
                    sender.sendMessage(p.getName());
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
        if (cmd.getName().equalsIgnoreCase("cstream")) {
            if (player == null) {
                sender.sendMessage("[CombatProtector] This command requires a player.");
            } else {
                if (args.length == 1) {
                    Player target = this.getServer().getPlayer(args[0]);
                    if (target != null) {
                        if (!SH.hasStream(target, player)) {
                            combatStreams.add(new CombatStream(this, target, player));
                            player.sendMessage(ChatColor.GREEN + "[CombatProtector] You are now streaming: " + target.getName() + "'s combat data.");
                            return true;
                        } else {
                            combatStreams.remove(SH.getStream(target, player));
                            player.sendMessage(ChatColor.RED + "[CombatProtector] You are no-longer streaming: " + target.getName() + "'s combat data.");
                            return true;
                        }
                    }
                } else{
                    player.sendMessage(ChatColor.RED + "Incorrect number of arguements.");
                    player.sendMessage(ChatColor.RED+ cmd.getUsage());
                }
            }
        }
        // this hasn't happened the a value of false will be returned.
        return false;
    }
}
