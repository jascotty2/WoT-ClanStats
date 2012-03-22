package me.jascotty2.claninfo;

public class Tank {
	public String name;
	public int tier;
	TankType type = TankType.UNKNOWN;

	public Tank() {
	}

	public Tank(int tier, String name) {
		this.tier = tier;
		this.name = name;
		type = TankType.fromTankName(name);
		if(type == TankType.UNKNOWN) {
			System.out.println("unknown tank: " + name);
		}
	}

	@Override
	public String toString() {
		return tier + ": " + name;
	}
	
	public int effectiveTier() {
		// just in general.. nothing specific (yet..)
		if(type == TankType.HEAVY || type == TankType.MEDIUM) return tier;
		if(type == TankType.TD) return tier + 1;
		if(type == TankType.SPG 
				|| type == TankType.LIGHT) return (tier + 2) > 10 ? 10 : tier + (tier > 4 ? 2 : 1);
		return tier;
	}
	
}


