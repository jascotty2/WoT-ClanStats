
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
		if(Str.isIn(tank, 
				/// Heavies
				// soviet
				"KV", 
				"KV-220 Beta-Test",
				"Churchill",
				"KV-220",
				"KV-1S",
				"KV-3",
				"IS",
				"IS-3",
				"KV-5",
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
				"AMX 50B")) {
			return TankType.HEAVY;
		} else if(Str.isIn(tank, 
				/// Mediums
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
				"M46 Patton",
				// French
				"D2",
				"Lorraine 40 t",
				"Bat Chatillon 25 t",
				// Chinese ;)
				"Type 59")) {
			return TankType.MEDIUM;
		} else if(Str.isIn(tank, 
				// Light Tanks
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
				"AMX 12t",
				"AMX 13 75",
				"AMX 13 90")) {
			return TankType.LIGHT;
		} else if(Str.isIn(tank, 
				/// SPGs
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
				"105 leFH18B2"
				)) {
			return TankType.SPG;
		} else if(Str.isIn(tank, 
				/// Tank Destroyers
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
				// German
				"Panzerjäger I",
				"Marder II",
				"Hetzer",
				"StuG III",
				"JagdPz IV",
				"Jagdpanther",
				"Ferdinand",
				"Jagdtiger",
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
				// French
				"FCM36 Pak40"
				)) {
			return TankType.TD;
		}
		return TankType.UNKNOWN;
	}
}
