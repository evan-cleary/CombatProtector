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

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import com.tetra.combatprotector.CombatProtector;
import com.tetra.combatprotector.Configuration;
import com.tetra.combatprotector.MarkHandler;
import com.tetra.combatprotector.CombatLogger;

public class CombatProtectorEntityListener implements Listener {
	public CombatProtector plugin;
	public CombatLogger CL;
	Configuration config = new Configuration();
	public double TimeOut = 140;

	public CombatProtectorEntityListener(CombatProtector instance) {
		plugin = instance;
		CL = new CombatLogger(instance);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		setTimeOut();
	}

	private void setTimeOut() {
		this.TimeOut = config.getCombatTimeOut() * 20;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event) {
			if (config.getEnabled()) {
				Event eventType = event.getEntity().getLastDamageCause();
				if (event instanceof EntityDamageByEntityEvent) {
					EntityDamageByEntityEvent evt = (EntityDamageByEntityEvent) eventType;
					Entity Damagee = checkSource(evt.getEntity());
					Entity Damager = checkSource(evt.getDamager());
					if ((Damagee != null) && (Damager != null)) {
						final Player p = (Player) Damagee;
						Player a = (Player) Damager;
						if (!evt.isCancelled()) {
							try {
								MarkHandler MH = plugin.markHandler
										.get(plugin.safeLogoutList.indexOf(p));
								MH.refreshTimer(p);
							} catch (Exception ex) {
								MarkHandler MH = new MarkHandler(p, plugin);
								if (!MH.checkTagged(p)) {
									a.sendMessage(ChatColor.RED + "You marked "
											+ p.getName() + " for combat.");
									MH.safeOff(p);
								}
							}
						}
						if (config.getCLEnabled()) {
							CL.AddEntry(p, a, evt.getDamage(), checkWeapon(a));
						}
					}
				}
			}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent event) {
		if (config.getEnabled()) {
			Entity Victim = checkSource(event.getEntity());
			if (Victim != null) {
				Player p = (Player) Victim;
				if (p != null) {
					try {
						MarkHandler MH = plugin.markHandler
								.get(plugin.safeLogoutList.indexOf(p));
						if (MH.checkTagged(p)) {
							MH.safeOn(p);
						}
					} catch (Exception ex) {
					}
				}
			}
		}
	}

	public Entity checkSource(Entity source) {
		if (source instanceof Player) {
			return source;
		}
		if ((source instanceof Projectile)
				&& (((Projectile) source).getShooter() instanceof Player)) {
			return ((Projectile) source).getShooter();
		}
		if ((source instanceof ThrownPotion)
				&& (((ThrownPotion) source).getShooter() instanceof Player)) {
			return ((ThrownPotion) source).getShooter();
		}
		return null;
	}

	public String checkWeapon(Entity source) {
		if (source instanceof Player) {
			String weapon = ((Player) source).getItemInHand().getType().name();
			if (weapon.equals("AIR")) {
				return "FIST";
			} else {
				return weapon;
			}
		}
		return null;
	}
}
