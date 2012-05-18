/**
 * Copyright (C) 2012 Jacob Scott <jascottytechie@gmail.com>
 * Description: Defines a Tank
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.jascotty2.clanstats;

import me.jascotty2.lib.util.ArrayManip;

public class Tank {

	public String name;
	public int tier;
	TankType type = TankType.UNKNOWN;

	public Tank() {
	}

	public Tank(int tier, String name) {
		this.tier = tier;
		// for some reason, the 'K' in KV-4 is not a ascii K
		// (char code 1050)
		if(name.equals("КV-4")) this.name = "KV-4";
		else this.name = name;
		type = TankType.fromTankName(this.name);
		if (type == TankType.UNKNOWN) {
			System.out.println("unknown tank: " + name);
		}
	}

	@Override
	public String toString() {
		return tier + ": " + name;
	}

	public int effectiveTier() {
		// just in general.. nothing specific (yet..)
		if (type == TankType.HEAVY) {
			return tier;
		}
		if (type == TankType.MEDIUM) {
			return (tier + 1) >= 10 ? 10 : tier + (tier > 5 ? 1 : 0);
		}
		if (type == TankType.TD) {
			return tier + 1;
		}
		if (type == TankType.SPG
				|| type == TankType.LIGHT) {
			return (tier + 2) >= 10 ? 10 : tier + (tier > 4 ? 2 : 1);
		}
		return tier;
	}
	
	private static String[] tanks = null;
	public static String[] getAllTanks() {
		if(tanks == null) {
			tanks = ArrayManip.arrayConcat(
					TankType.heavyTanks, 
					TankType.mediumTanks,
					TankType.lightTanks,
					TankType.artillery,
					TankType.tankDestroyers);
		}
		return tanks;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Tank other = (Tank) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		if (this.type != other.type) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
		hash = 97 * hash + (this.type != null ? this.type.hashCode() : 0);
		return hash;
	}
	
}
