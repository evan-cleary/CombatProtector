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
package com.tetra.combatprotector.handlers;

import com.tetra.combatprotector.CombatProtector;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.tetra.combatprotector.util.Util;

public class MarkHandler implements Runnable {

    private Player player;
    private CombatProtector cp;
    private int comTimer = -1;
    private long started;
    private long delay;
    Calendar c;

    public MarkHandler(Player p, CombatProtector instance) {
        this.cp = instance;
        this.player = p;
    }

    private void initTimer(long delay) {
        c = new GregorianCalendar();
        this.started = c.getTimeInMillis();
        this.delay = delay;
    }

    private void updateTimer(long delay) {
        c = new GregorianCalendar();
        this.started = c.getTimeInMillis();
        this.delay = delay;
    }

    @Override
    public void run() {
        if (player == null || !player.isOnline()
                || player.getLocation() == null) {
            cancel();
            return;
        }

        long now = System.currentTimeMillis(); // Gets current time to compare
        // with start and delay
        if (now > started + delay) {
            if (checkTagged(player)) {
                safeOn(player);
            }
        }
    }

    public void cancel() {
        cancel(false);
    }

    public void cancel(boolean notifyUser) {
        if (comTimer == -1) {
            return;
        }
        try {
            cp.getServer().getScheduler().cancelTask(comTimer);
            if (notifyUser) {
                player.sendMessage(ChatColor.GREEN
                        + "You are no longer marked for combat.");
            }
        } finally {
            comTimer = -1;
        }
    }

    public void safeOn(Player p) {
        if (checkTagged(p)) {
            cp.safeLogoutList.remove(p);
            cp.markHandler.remove(this);
            cancel(true);
        }
    }

    public void safeOff(Player p) {
        cp.safeLogoutList.add(p);
        cp.markHandler.add(this);
        double delayd = cp.config.getCombatTimeOut();
        cancel();
        c = new GregorianCalendar();
        c.add(Calendar.SECOND, (int) delayd);
        c.add(Calendar.MILLISECOND, (int) ((delayd * 1000.0) % 1000.0));
        player.sendMessage(ChatColor.RED
                + "You have been marked for combat for: "
                + Util.formatDateDiff(c.getTimeInMillis()));
        initTimer((long) (delayd * 1000.0));
        comTimer = cp.getServer().getScheduler().scheduleSyncRepeatingTask(cp, this, 10, 10);
    }

    public boolean checkTagged(Player p) {
        if (cp.safeLogoutList.contains(p)) {
            return true;
        }
        return false;
    }

    public void sendTimeRemain() {
        double delayd = cp.config.getCombatTimeOut();
        c = new GregorianCalendar();
        c.setTimeInMillis(started);
        c.add(Calendar.SECOND, (int) delayd);
        c.add(Calendar.MILLISECOND, (int) ((delayd * 1000.0) % 1000.0));
        player.sendMessage(ChatColor.RED
                + "You must wait "
                + Util.formatDateDiff(c.getTimeInMillis()) + " before performing this action.");
    }

    public void refreshTimer(Player p) {
        double delayd = cp.config.getCombatTimeOut();
        updateTimer((long) (delayd * 1000.0));
    }
}
