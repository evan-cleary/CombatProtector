/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tetra.combatprotector;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Evan
 */
public class CombatStream {

    CombatProtector _CP;
    Player _target;
    Player _receiver;

    public CombatStream(CombatProtector instance, Player target, Player receiver) {
        this._target = target;
        this._CP = instance;
        this._receiver = receiver;
    }

    public Player getPlayer() {
        return _target;
    }

    public Player getReceiver() {
        return _receiver;
    }

    public void sendOutgoing(Player target, Player damagee, int dmg, String wep) {
        if (_receiver.isOnline()) {
            _receiver.sendMessage("[" + ChatColor.DARK_AQUA + "cstream:" + ChatColor.RED + _target.getName() + ChatColor.WHITE + "] " + target.getName() + " hit " + damagee.getName() + " for " + dmg + " with " + wep);
        }
    }

    public void sendReceiving(Player target, Player damager, int dmg, String wep) {
        if (_receiver.isOnline()) {
            _receiver.sendMessage("[" + ChatColor.DARK_AQUA + "cstream:" + ChatColor.RED + _target.getName() + ChatColor.WHITE + "] " + damager.getName() + " hit " + target.getName() + " for " + dmg + " with " + wep);

        }
    }

    public void sendDeath(Player victim, Player attacker) {
        if (_receiver.isOnline()) {
            _receiver.sendMessage("[" + ChatColor.DARK_AQUA + "cstream:" + ChatColor.RED + _target.getName() + ChatColor.WHITE + "] " + attacker.getName() + " has slain " + victim.getName());

        }
    }
}
