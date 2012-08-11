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
package com.tetra.combatprotector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CombatLogger {

    private CombatProtector plugin;
    Configuration config = new Configuration();

    public CombatLogger(CombatProtector instance) {
        this.plugin = instance;
    }

    public void AddEntry(Player receiver, Player damager, int damage,
            String weapon) {
        ArrayList<String> CombatLog = plugin.combatlogs.get(plugin.playerlist.indexOf(receiver));
        Date curtime = new Date();
        SimpleDateFormat df = new SimpleDateFormat("H:mm:ss.SSS");
        String ts = df.format(curtime);
        String LogInfo = ("[" + ChatColor.GOLD + ts + ChatColor.WHITE + "] " + damager.getName() + " hit you for " + damage
                + " with " + weapon);
        CombatLog.add(0, LogInfo);
        if (CombatLog.size() == 12) {
            CombatLog.remove(11);
        }
        if (checkLiveLog(receiver)) {
            receiver.sendMessage(LogInfo);
        }
    }

    public boolean checkLiveLog(Player sender) {
        if (plugin.combatlogon.contains(sender)) {
            return true;
        }
        return false;
    }

    public void GetCombatLog(Player sender) {
        ArrayList<String> CombatLog = plugin.combatlogs.get(plugin.playerlist.indexOf(sender));
        sender.sendMessage(ChatColor.DARK_AQUA
                + "______________________CombatLog______________________");
        for (String log : CombatLog) {
            sender.sendMessage(log);
        }
        sender.sendMessage(ChatColor.DARK_AQUA
                + "_____________________________________________________");
    }
}
