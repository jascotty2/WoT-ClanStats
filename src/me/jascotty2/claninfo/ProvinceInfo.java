package me.jascotty2.claninfo;

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
