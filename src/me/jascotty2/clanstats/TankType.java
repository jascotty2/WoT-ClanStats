/**
 * Copyright (C) 2012 Jacob Scott <jascottytechie@gmail.com>
 * Description: Definition for all known tank names in World of Tanks
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

import me.jascotty2.lib.util.Str;

public enum TankType {

	HEAVY("Heavy", "Heavy Tank", "HT"),
	MEDIUM("Medium", "Medium Tank", "MT"),
	LIGHT("Light", "Light Tank", "LT"),
	SPG("Artillery", "Artillery", "SPG"),
	TD("Tank Destroyer", "Tank Destroyer", "TD"),
	UNKNOWN("?", "Unknown..", "?");
	private String name, propername, abbr;

	private TankType(String name, String propername, String abbr) {
		this.name = name;
		this.propername = propername;
		this.abbr = abbr;
	}

	public String getName() {
		return name;
	}

	public String getPropername() {
		return propername;
	}

	public String getAbbr() {
		return abbr;
	}
	
	public static TankType fromTankName(String tank) {
		if (Str.isIn(tank, Tank.getAllHeavies())) {
			return TankType.HEAVY;
		} else if (Str.isIn(tank, Tank.getAllMediums())) {
			return TankType.MEDIUM;
		} else if (Str.isIn(tank, Tank.getAllLights())) {
			return TankType.LIGHT;
		} else if (Str.isIn(tank, Tank.getAllArtillery())) {
			return TankType.SPG;
		} else if (Str.isIn(tank, Tank.getAllTankDestroyers())) {
			return TankType.TD;
		}
		return TankType.UNKNOWN;
	}

}
