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

import java.util.ArrayList;
import me.jascotty2.lib.util.ArrayManip;
import me.jascotty2.lib.util.Str;

public class Tank {

	public String name;
	public int tier;
	public TankType type = TankType.UNKNOWN;
	public final static String tiers[] = new String[]{"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
// <editor-fold defaultstate="collapsed" desc="Tank Names">
	public final static String[][] heavyTanks = {
		new String[]{},
		new String[]{
			"T2 Medium Tank"
		},
		new String[]{
			"M2 Medium Tank"
		},
		// 4
		new String[]{
			"PzKpfw B2 740 (f)",
			"B1"
		},
		// 5
		new String[]{
			"KV", "KV-1", "KV-220 Beta-Test", "Churchill", "KV-220",
			"T14", "T1 heavy",
			"BDR G1B"
		},
		// 6
		new String[]{
			"KV-1S", "KV-2", "T-150",
			"M6",
			"ARL 44"
		},
		// 7
		new String[]{
			"IS", "KV-3",
			"PzKpfw VI Tiger", "PzKpfw VI Tiger (P)",
			"T29",
			"AMX M4(1945)"
		},
		// 8
		new String[]{
			"IS-3", "IS-6", "KV-4", "KV-5",
			"PzKpfw VIB Tiger II", "VK 4502 (P) Ausf. A", "Löwe",
			"T34", "T32", "M6A2E1",
			"AMX 50 100",
			"WZ-111"
		},
		// 9
		new String[]{
			"ST-I", "IS-8",
			"VK 4502 (P) Ausf. B", "E-75",
			"M103",
			"AMX 50 120"
		},
		// 10
		new String[]{
			"IS-4", "IS-7",
			"Maus", "E-100",
			"T110E5",
			"AMX 50B"
		}};
	public final static String[][] mediumTanks = {
		new String[]{},
		new String[]{},
		// 3
		new String[]{
			"PzKpfw S35 739 (f)",
			"D2"
		},
		// 4
		new String[]{
			"T-28", "A-32",
			"PzKpfw III",
			"M3 Lee"
		},
		// 5
		new String[]{
			"T-34", "Matilda",
			"PzKpfw IV", "PzKpfw III/IV", "T-25", "PzKpfw IV Hydraulic",
			"M4 Sherman", "M7", "Ram-II", "M4A2E4"
		},
		// 6
		new String[]{
			"T-34-85",
			"VK 3601 (H)", "VK 3001 (H)", "VK 3001 (P)", "PzKpfw V-IV", "PzKpfw V-IV Alpha",
			"M4A3E8 Sherman", "M4A3E2"
		},
		// 7
		new String[]{
			"T-43", "KV-13",
			"PzKpfw V Panther", "VK 3002 (DB)",
			"T20"
		},
		// 8
		new String[]{
			"T-44",
			"Panther II",
			"M26 Pershing", "T26E4 SuperPershing",
			"Type 59"
		},
		// 9
		new String[]{
			"T-54",
			"E-50",
			"M46 Patton",
			"Lorraine 40 t"
		},
		new String[]{
			"T-62A",
			"E-50 Ausf.M",
			"M48A1",
			"Bat Chatillon 25 t"
		}
	};
	public final static String[][] lightTanks = {
		new String[]{
			"MS-1",
			"Leichttraktor",
			"T1 Cunningham",
			"RenaultFT"
		},
		new String[]{
			"BT-2", "T-26", "MkVII Tetrarch",
			"PzKpfw 35 (t)", "PzKpfw II", "PzKpfw 38H735 (f)",
			"M2 Light Tank", "T2 Light Tank",
			"Hotchkiss H35", "D1"
		},
		// 3
		new String[]{
			"BT-7", "T-46", "BT-SV", "M3 Stuart II", "T-127",
			"PzKpfw 38 (t)", "PzKpfw III Ausf. A", "PzKpfw II Luchs", "PzKpfw II Ausf. J", "T-15",
			"M3 Stuart", "MTLS-1G14", "M22 Locust",
			"AMX 38"
		},
		// 4
		new String[]{
			"A-20", "T-50", "Valentine",
			"VK 1602 Leopard", "PzKpfw 38 nA",
			"M5 Stuart",
			"AMX 40"
		},
		// 5
		new String[]{
			"T-50-2",
			"VK 2801",
			"M24 Chaffee",
			"ELC AMX"
		},
		// 6
		new String[]{
			"AMX 12t",
			"Type 62"
		},
		// 7
		new String[]{
			"AMX 13 75"
		},
		// 8
		new String[]{
			"AMX 13 90"
		},
		new String[]{},
		new String[]{}
	};
	public final static String[][] artillery = {
		new String[]{},
		// 2
		new String[]{
			"SU-18",
			"Sturmpanzer I Bison",
			"T57",
			"RenaultBS"
		},
		// 3
		new String[]{
			"SU-26",
			"Sturmpanzer II", "Wespe",
			"M37",
			"Lorraine39 L AM"
		},
		// 4
		new String[]{
			"SU-5",
			"Grille",
			"M7 Priest",
			"105 leFH18B2", "AMX 105AM"
		},
		// 5
		new String[]{
			"SU-8",
			"Hummel",
			"M41", "AMX 13 F3 AM"
		},
		// 6
		new String[]{
			"S-51", "SU-14",
			"GW Panther",
			"M12", "Lorraine155 50"
		},
		// 7
		new String[]{
			"Object 212",
			"GW Tiger",
			"M40/M43", "Lorraine155 51"
		},
		// 8
		new String[]{
			"Object 261",
			"GW Typ E",
			"T92", "Bat Chatillon 155"
		},
		new String[]{},
		new String[]{}
	};
	public final static String[][] tankDestroyers = {
		new String[]{},
		new String[]{
			"AT-1",
			"Panzerjäger I",
			"T18",
			"RenaultFT AC"
		},
		// 3
		new String[]{
			"SU-76",
			"Marder II",
			"T82",
			"FCM36 Pak40",
			"Renault UE 57"
		},
		// 4
		new String[]{
			"SU-85B",
			"Hetzer",
			"T40", "M8A1",
			"Somua SAu-40" // was "Somua S-40"
		},
		// 5
		new String[]{
			"SU-85", "SU-85I",
			"StuG III",
			"M10 Wolverine", "T49",
			"S-35 CA"
		},
		// 6
		new String[]{
			"SU-100",
			"JagdPz IV", "Dicker Max",
			"M36 Slugger", "M18 Hellcat",
			"ARL V39"
		},
		// 7
		new String[]{
			"SU-152",
			"Jagdpanther",
			"T25 AT", "T25/2",
			"AMX AC Mle.1946"
		},
		// 8
		new String[]{
			"ISU-152",
			"Ferdinand", "JagdPanther II", "8.8 cm Pak 43 JagdTiger",
			"T28", "T28 Prototype",
			"AMX AC Mle. 1948"
		},
		// 9
		new String[]{
			"Object 704",
			"Jagdtiger",
			"T30", "T95",
			"AMX 50 Foch"
		},
		new String[]{
			"Object 268",
			"JagdPz E-100",
			"T110E4", "T110E3",
			"AMX-50 Foch (155)"
		}
	};
// <editor-fold defaultstate="collapsed" desc="All Tanks, in order from website">
	// (kept for compatibility with older mysql tables)
	private static String[] allTanks = new String[]{
		/// heavy tanks
		// soviet
		"KV",
		"KV-1",
		"KV-220 Beta-Test",
		"Churchill",
		"KV-220",
		"KV-1S",
		"KV-2",
		"T-150",
		"IS",
		"KV-3",
		"IS-3",
		"IS-6",
		"KV-4",
		"KV-5",
		"ST-I",
		"IS-8",
		"IS-4",
		"IS-7",
		// German
		"PzKpfw B2 740 (f)",
		"PzKpfw VI Tiger",
		"PzKpfw VI Tiger (P)",
		"PzKpfw VIB Tiger II",
		"VK 4502 (P) Ausf. A",
		"Löwe",
		"VK 4502 (P) Ausf. B",
		"E-75",
		"Maus",
		"E-100",
		// American
		"T14",
		"T1 heavy",
		"M6",
		"T29",
		"T34",
		"T32",
		"M6A2E1",
		"M103",
		"T110E5",
		// French
		"B1",
		"BDR G1B",
		"ARL 44",
		"AMX M4(1945)",
		"AMX 50 100",
		"AMX 50 120",
		"AMX 50B",
		// Chinese
		"WZ-111",
		/// medium tanks
		// soviet
		"T-28",
		"A-32",
		"T-34",
		"Matilda",
		"T-34-85",
		"T-43",
		"KV-13",
		"T-44",
		"T-54",
		"T-62A",
		// German
		"PzKpfw S35 739 (f)",
		"PzKpfw III",
		"PzKpfw IV",
		"PzKpfw III/IV",
		"T-25",
		"PzKpfw IV Hydraulic",
		"VK 3601 (H)",
		"VK 3001 (H)",
		"VK 3001 (P)",
		"PzKpfw V-IV",
		"PzKpfw V-IV Alpha",
		"PzKpfw V Panther",
		"VK 3002 (DB)",
		"Panther II",
		"E-50",
		"E-50 Ausf.M",
		// American
		"T2 Medium Tank",
		"M2 Medium Tank",
		"M3 Lee",
		"M4 Sherman",
		"M7",
		"Ram-II",
		"M4A2E4",
		"M4A3E8 Sherman",
		"M4A3E2",
		"T20",
		"M26 Pershing",
		"T26E4 SuperPershing",
		"M46 Patton",
		"M48A1",
		// French
		"D2",
		"Lorraine 40 t",
		"Bat Chatillon 25 t",
		// Chinese ;)
		"Type 59",
		/// light tanks
		// soviet
		"MS-1",
		"BT-2",
		"T-26",
		"MkVII Tetrarch",
		"BT-7",
		"T-46",
		"BT-SV",
		"M3 Stuart II",
		"T-127",
		"A-20",
		"T-50",
		"Valentine",
		"T-50-2",
		// German
		"Leichttraktor",
		"PzKpfw 35 (t)",
		"PzKpfw II",
		"PzKpfw 38H735 (f)",
		"PzKpfw 38 (t)",
		"PzKpfw III Ausf. A",
		"PzKpfw II Luchs",
		"PzKpfw II Ausf. J",
		"T-15",
		"VK 1602 Leopard",
		"PzKpfw 38 nA",
		"VK 2801",
		// American
		"T1 Cunningham",
		"M2 Light Tank",
		"T2 Light Tank",
		"M3 Stuart",
		"MTLS-1G14",
		"M22 Locust",
		"M5 Stuart",
		"M24 Chaffee",
		// French
		"RenaultFT",
		"Hotchkiss H35",
		"D1",
		"AMX 38",
		"AMX 40",
		"ELC AMX",
		"AMX 12t",
		"AMX 13 75",
		"AMX 13 90",
		// Chinese
		"Type 62",
		/// artillery
		// soviet
		"SU-18",
		"SU-26",
		"SU-5",
		"SU-8",
		"S-51",
		"SU-14",
		"Object 212",
		"Object 261",
		// German
		"Sturmpanzer I Bison",
		"Sturmpanzer II",
		"Wespe",
		"Grille",
		"Hummel",
		"GW Panther",
		"GW Tiger",
		"GW Typ E",
		// American
		"T57",
		"M37",
		"M7 Priest",
		"M41",
		"M12",
		"M40/M43",
		"T92",
		// French
		"RenaultBS",
		"Lorraine39 L AM",
		"105 leFH18B2",
		"AMX 105AM",
		"AMX 13 F3 AM",
		"Lorraine155 50",
		"Lorraine155 51",
		"Bat Chatillon 155",
		/// tank destroyers
		// soviet
		"AT-1",
		"SU-76",
		"SU-85B",
		"SU-85",
		"SU-85I",
		"SU-100",
		"SU-152",
		"ISU-152",
		"Object 704",
		"Object 268",
		// German
		"Panzerjäger I",
		"Marder II",
		"Hetzer",
		"StuG III",
		"JagdPz IV",
		"Dicker Max",
		"Jagdpanther",
		"Ferdinand",
		"JagdPanther II",
		"8.8 cm Pak 43 JagdTiger",
		"Jagdtiger",
		"JagdPz E-100",
		// American
		"T18",
		"T82",
		"T40",
		"M8A1",
		"M10 Wolverine",
		"T49",
		"M36 Slugger",
		"M18 Hellcat",
		"T25 AT",
		"T25/2",
		"T28",
		"T28 Prototype",
		"T30",
		"T95",
		"T110E4", 
		"T110E3",
		// French
		"RenaultFT AC",
		"FCM36 Pak40",
		"Renault UE 57",
		"Somua SAu-40",
		"S-35 CA",
		"ARL V39",
		"AMX AC Mle.1946",
		"AMX AC Mle. 1948",
		"AMX 50 Foch",
		"AMX-50 Foch (155)"
	};
// </editor-fold>
	private static String[] allHeavyTanks, allMediumTanks, allLightTanks, allArtillery, allTankDestroyers;

	public static String[] getAllHeavies() {
		if (allHeavyTanks == null) {
			allHeavyTanks = ArrayManip.arrayConcat(heavyTanks[0], heavyTanks[1],
					heavyTanks[2], heavyTanks[3], heavyTanks[4], heavyTanks[5],
					heavyTanks[6], heavyTanks[7], heavyTanks[8], heavyTanks[9]);
		}
		return allHeavyTanks;
	}

	public static String[] getAllMediums() {
		if (allMediumTanks == null) {
			allMediumTanks = ArrayManip.arrayConcat(mediumTanks[0], mediumTanks[1],
					mediumTanks[2], mediumTanks[3], mediumTanks[4], mediumTanks[5],
					mediumTanks[6], mediumTanks[7], mediumTanks[8], mediumTanks[9]);
		}
		return allMediumTanks;
	}

	public static String[] getAllLights() {
		if (allLightTanks == null) {
			allLightTanks = ArrayManip.arrayConcat(lightTanks[0], lightTanks[1],
					lightTanks[2], lightTanks[3], lightTanks[4], lightTanks[5],
					lightTanks[6], lightTanks[7], lightTanks[8], lightTanks[9]);
		}
		return allLightTanks;
	}

	public static String[] getAllArtillery() {
		if (allArtillery == null) {
			allArtillery = ArrayManip.arrayConcat(artillery[0], artillery[1],
					artillery[2], artillery[3], artillery[4], artillery[5],
					artillery[6], artillery[7], artillery[8], artillery[9]);
		}
		return allArtillery;
	}

	public static String[] getAllTankDestroyers() {
		if (allTankDestroyers == null) {
			allTankDestroyers = ArrayManip.arrayConcat(tankDestroyers[0], tankDestroyers[1],
					tankDestroyers[2], tankDestroyers[3], tankDestroyers[4], tankDestroyers[5],
					tankDestroyers[6], tankDestroyers[7], tankDestroyers[8], tankDestroyers[9]);
		}
		return allTankDestroyers;
	}

	public static String[] getAllTanks() {
		if (allTanks == null) {
			allTanks = ArrayManip.arrayConcat(
					getAllHeavies(),
					getAllMediums(),
					getAllLights(),
					getAllArtillery(),
					getAllTankDestroyers());
		}
		return allTanks;
	}
// </editor-fold>
	private static ArrayList<String> unknownErrorMsgs = new ArrayList<String>();

	public Tank() {
	}

	public Tank(int tier, String name) {
		this.tier = tier;
		// for some reason, the 'K' in KV-4 is not a ascii K
		// (char code 1050)
		if (name.equals("КV-4")) {
			this.name = "KV-4";
		} else {
			this.name = name;
		}
		type = TankType.fromTankName(this.name);
		if (type == TankType.UNKNOWN) {
			if (!unknownErrorMsgs.contains(name)) {
				unknownErrorMsgs.add(name);
				System.out.println("unknown tank: " + name);
			}
		} else if (tier == 0) {
			this.tier = tierByName(this.name);
		}
	}

	@Override
	public String toString() {
		return tier + ": " + name;
	}

	public int effectiveTier() {
		return _effectiveTier(type, tier);
	}

	private static int _effectiveTier(TankType type, int tier) {
		// just in general.. nothing specific (yet..)
		if (type == TankType.SPG
				|| type == TankType.LIGHT) {
			return (tier + 2) >= 10 ? 10 : tier + (tier > 4 ? 2 : 1);
		}
		return tier;
	}

	public static int tierByName(String name) {
		TankType type = TankType.fromTankName(name);
		String tankClass[][];
		if (type == TankType.HEAVY) {
			tankClass = heavyTanks;
		} else if (type == TankType.MEDIUM) {
			tankClass = mediumTanks;
		} else if (type == TankType.LIGHT) {
			tankClass = lightTanks;
		} else if (type == TankType.TD) {
			tankClass = tankDestroyers;
		} else if (type == TankType.SPG) {
			tankClass = artillery;
		} else {
			return 0;
		}
		for (int i = 0; i < 10; ++i) {
			if (Str.isIn(name, tankClass[i])) {
				return i + 1;
			}
		}
		return 0;
	}

	public static int effectiveTierByName(String name) {
		TankType type = TankType.fromTankName(name);
		String tankClass[][];
		if (type == TankType.HEAVY) {
			tankClass = heavyTanks;
		} else if (type == TankType.MEDIUM) {
			tankClass = mediumTanks;
		} else if (type == TankType.LIGHT) {
			tankClass = lightTanks;
		} else if (type == TankType.TD) {
			tankClass = tankDestroyers;
		} else if (type == TankType.SPG) {
			tankClass = artillery;
		} else {
			return 0;
		}
		for (int i = 0; i < 10; ++i) {
			if (Str.isIn(name, tankClass[i])) {
				return _effectiveTier(type, i + 1);
			}
		}
		return 0;
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
