/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tetra.combatprotector.handlers;

import com.tetra.combatprotector.CombatProtector;
import com.tetra.combatprotector.CombatStream;
import org.bukkit.entity.Player;

/**
 *
 * @author Evan
 */
public class StreamHandler {
    
    CombatProtector _CP;
    
    public StreamHandler(CombatProtector instance) {
        this._CP = instance;
    }
    
    public void addStream(Player target, Player receiver) {
        _CP.combatStreams.add(new CombatStream(_CP, target, receiver));
    }
    
    public void disableStream(Player target, Player receiver) {
        if (hasStream(target, receiver)) {
            _CP.combatStreams.remove(getStream(target, receiver));
        }
    }
    
    public boolean hasStream(Player target, Player receiver) {
        for (CombatStream cs : _CP.combatStreams) {
            if (cs.getPlayer() == target) {
                if (cs.getReceiver() == receiver) {
                    return true;
                }
            }
        }
        return false;
    }
        public boolean hasStream(Player target) {
        for (CombatStream cs : _CP.combatStreams) {
            if (cs.getPlayer() == target) {
                return true;
            }
        }
        return false;
    }
    
    public CombatStream getStream(Player target, Player receiver) {
        for (CombatStream cs : _CP.combatStreams) {
            if (cs.getPlayer() == target) {
                if (cs.getReceiver() == receiver) {
                    return cs;
                }
            }
        }
        return null;
    }
}
