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

import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

public class Configuration {

	private YamlConfiguration fc = new YamlConfiguration();

	public void setConfDefaults() {
		System.out.println("Loading config...");
		try {
			fc.load("plugins/CombatProtector/config.yml");
		} catch (Exception e) {
			System.out.println("Creating config...");
		}
		if (!fc.contains("general.enabled")) {
			fc.set("general.enabled", true);
		}
		if (!fc.contains("general.combattimeout")) {
			fc.set("general.combattimeout", 7);
		}
		if (!fc.contains("general.combatlogger")) {
			fc.set("general.combatlog", false);
		}
		try {
			fc.save("plugins/CombatProtector/config.yml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done loading config!");
	}

	public boolean getEnabled() {
		try {
			fc.load("plugins/CombatProtector/config.yml");
			return fc.getBoolean("general.enabled", false);
		} catch (Exception e) {
			setConfDefaults();
		}
		return false;
	}

	public double getCombatTimeOut() {
		try {
			fc.load("plugins/CombatProtector/config.yml");
			return fc.getDouble("general.combattimeout", 7);
		} catch (Exception e) {
			setConfDefaults();
		}
		return 7;
	}

	public boolean getCLEnabled() {
		try {
			fc.load("plugins/CombatProtector/config.yml");
			return fc.getBoolean("general.combatlogger", false);
		} catch (Exception e) {
			setConfDefaults();
		}
		return false;
	}
}
