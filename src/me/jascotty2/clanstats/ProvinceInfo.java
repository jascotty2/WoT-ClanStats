/**
 * Copyright (C) 2012 Jacob Scott <jascottytechie@gmail.com>
 * Description: Defines a Province
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

import me.jascotty2.lib.io.CheckInput;

public class ProvinceInfo {
	public String name, map, id, mapID;
	public boolean isCapital, isAttacked, combatsRunning;
	public int occupancy, revenue;
	public long battleTime; // when battles typically occur

	public void setBattleTime(String battleTime) {
		this.battleTime = (long) CheckInput.GetDouble(battleTime, 0);
	}

	public void setCombatsRunning(String combatsRunning) {
		this.combatsRunning = CheckInput.GetBoolean(combatsRunning, false);
	}

	public void setIsAttacked(String isAttacked) {
		this.isAttacked = CheckInput.GetBoolean(isAttacked, false);
	}

	public void setIsCapital(String isCapital) {
		this.isCapital = CheckInput.GetBoolean(isCapital, false);
	}

	public void setOccupancy(String occupancy) {
		this.occupancy = CheckInput.GetInt(occupancy, 0);
	}

	public void setRevenue(String revenue) {
		this.revenue = CheckInput.GetInt(revenue, 0);
	}
}
