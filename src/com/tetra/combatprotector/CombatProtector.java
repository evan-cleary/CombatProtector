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
import com.tetra.combatprotector.listeners.CombatProtectorEntityListener;
import com.tetra.combatprotector.listeners.CombatProtectorPlayerListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class CombatProtector extends JavaPlugin {

    public ArrayList<Player> combatlogon = new ArrayList<Player>();
    public ArrayList<Player> playerlist = new ArrayList<Player>();
    public ArrayList<ArrayList<String>> combatlogs = new ArrayList<ArrayList<String>>();
    public Map<Player, MarkHandler> markHandlers = new HashMap<Player, MarkHandler>();
    private CombatLogger CL = new CombatLogger(this);
    CombatProtectorEntityListener cpel;
    CombatProtectorPlayerListener cppl;
    static final Logger log = Logger.getLogger("Minecraft");
    public PluginDescriptionFile info;
    public Configuration config = new Configuration();

    @Override
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
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Queue(), 20l, 20l);
    }

    @Override
    public void onDisable() {
        log.info("Shutting down CombatProtector");
        for (MarkHandler mh : markHandlers.values()) {
            mh.safeOn(mh.getPlayer());
        }
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
            } else {
                sender.sendMessage("Combat logging is not enabled.");
                return true;
            }
        }
        // this hasn't happened the a value of false will be returned.
        return false;
    }

    public class Queue implements Runnable {

        @Override
        public void run() {
            for (MarkHandler mh : markHandlers.values()) {
                mh.run();
            }
        }
    }
}
