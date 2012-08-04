/**
 * Copyright (C) 2012 Jacob Scott <jascottytechie@gmail.com>
 * Description: Information about a Player
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.jascotty2.lib.io.CheckInput;
import me.jascotty2.lib.util.ArrayManip;

public class PlayerInfo {

	public String playername, playerID, clan, position, lastPlayed;
	public BattleStats totals = new BattleStats(), ratingStats = new BattleStats();
	public int playerRating;
	public Map<Tank, BattleStats> tankBattles = new HashMap<Tank, BattleStats>();
	// 0 == total, 1-5 == tank types, 6 == top tier
	public int tanksByTier[][] = new int[7][10], maxTier[] = new int[7], maxEffectiveTier[] = new int[7];
	public static TankType[] typeOrder = new TankType[]{
		null, TankType.HEAVY, TankType.MEDIUM, TankType.LIGHT, TankType.TD, TankType.SPG};
	public int battlesByType[] = new int[6];
	public boolean is_banned = false;
	public Date created, memberSince, lastbattle;

	public void setCreated(String created) {
		if (created != null && CheckInput.IsDouble(created)) {
			this.created = new Date((long) CheckInput.GetDouble(created, 0) * 1000);
		}
	}

	public void setLastbattle(String lastbattle) {
		if (lastbattle != null && CheckInput.IsDouble(lastbattle)) {
			this.lastbattle = new Date((long) CheckInput.GetDouble(lastbattle, 0) * 1000);
		}
	}

	public void setMemberSince(String memberSince) {
		if (memberSince != null && CheckInput.IsDouble(memberSince)) {
			this.memberSince = new Date((long) CheckInput.GetDouble(memberSince, 0) * 1000);
		}
	}

	public void loadStats(String search, String serverExt) {
		// lookup player id
		String res = QueryParser.get("http://worldoftanks." + serverExt
				+ "/community/accounts/?type=table&offset=0&limit=100&order_by=name&search="
				+ search + "&echo=2&id=clans_index");

		if (search != null) {
			List<Object> data = QueryParser.getItemLists("items", res);

			if (data != null && !data.isEmpty()) {
				Object p = null;
				if(data.size() > 1) {
					for(Object o : data) {
						if(o instanceof Map && search.equalsIgnoreCase((String)((Map<String, Object>)o).get("name"))) {
							p = o;
							break;
						}
					}
				} else {
					p = data.get(0);
				}
				if (p != null && p instanceof Map) {
					Map<String, Object> dat = (Map<String, Object>) p;
					//if (CheckInput.GetInt(QueryParser.getData(res, "filtered_count"), 0) == 1) {
					playerID = dat.get("id").toString();
					playername = (String) dat.get("name");
					clan = (String) dat.get("abbreviation");
					totals.totalExp = (Integer) dat.get("exp");
					totals.battles = (Integer) dat.get("battles");
					totals.victories = (Integer) dat.get("wins");
					String crDate = dat.get("created_at").toString(); // eg, "2011-12-14"
					try {
						SimpleDateFormat crformat = new SimpleDateFormat("yyy-MM-dd");
						created = crformat.parse(crDate);
					} catch (ParseException ex) {
						Logger.getLogger(PlayerInfo.class.getName()).log(Level.SEVERE, null, ex);
					}
					//}
				}
			}
		}
	}

	public void loadFromWeb(String serverExt) {
		if (playerID == null) {
			if (playername != null) {
				loadStats(playername, serverExt);
			} else {
				return;
			}
		}
		memberSince = null;
		clan = null;
		if (playerID == null) {
			return;
		}
		String data = QueryParser.getPage("http://worldoftanks." + serverExt + "/community/accounts/" + playerID + "/");
		if (data == null) {
			return;
		}

		String numberNW = (serverExt.equals("com") || serverExt.equals("eu")) ? "td-number-nowidth" : "td-number";

		int start = data.indexOf("Registered");
		if (start < 0) {
			if (data.indexOf("Player profile is closed.") != -1) {
				created = null;
			}
			return;
		}
		setCreated(QueryParser.getStatTimestamp(data, "Registered", start));
		setLastbattle(QueryParser.getStatTimestamp(data, "Data as of", start));
		if (data.substring(start).contains("In Clan")) {
			String cl = data.substring(data.indexOf("[", start) + 1, data.indexOf("]", start));
			clan = QueryParser.getDataWithoutTags(cl);
			setMemberSince(QueryParser.getStatTimestamp(data, "Enrolled", start));
		}
		totals.battles = QueryParser.getStatInt(data, "Battles Participated", numberNW, start);
		totals.victories = QueryParser.getStatInt(data, "Victories", numberNW, start);
		totals.losses = QueryParser.getStatInt(data, "Defeats", numberNW, start);
		totals.survived = QueryParser.getStatInt(data, "Battles Survived", numberNW, start);
		totals.destroyed = QueryParser.getStatInt(data, "Destroyed", numberNW, start);
		totals.spotted = QueryParser.getStatInt(data, "Detected", numberNW, start);
		totals.hitRatio = QueryParser.getStatInt(data, "Hit Ratio", numberNW, start);
		totals.damage = QueryParser.getStatInt(data, "Damage", numberNW, start);
		totals.captured = QueryParser.getStatInt(data, "Capture Points", numberNW, start);
		totals.defense = QueryParser.getStatInt(data, "Defense Points", numberNW, start);
		totals.totalExp = QueryParser.getStatInt(data, "Total Experience", numberNW, start);
		totals.avgExp = QueryParser.getStatInt(data, "Average Experience per Battle", numberNW, start);
		totals.maxExp = QueryParser.getStatInt(data, "Maximum Experience per Battle", numberNW, start);
		start = data.indexOf("Rating", start);
		playerRating = CheckInput.GetInt(QueryParser.getDoubleStatVal(data, "Global Rating", 1, start), Integer.MAX_VALUE);
		ratingStats.hitRatio = CheckInput.GetInt(QueryParser.getDoubleStatVal(data, "Victories/Battles", 1, start), Integer.MAX_VALUE);
		ratingStats.avgExp = CheckInput.GetInt(QueryParser.getDoubleStatVal(data, "Average Experience per Battle", 1, start), Integer.MAX_VALUE);
		ratingStats.victories = CheckInput.GetInt(QueryParser.getDoubleStatVal(data, "Victories", 1, start), Integer.MAX_VALUE);
		ratingStats.battles = CheckInput.GetInt(QueryParser.getDoubleStatVal(data, "Battles Participated", 1, start), Integer.MAX_VALUE);
		ratingStats.captured = CheckInput.GetInt(QueryParser.getDoubleStatVal(data, "Capture Points", 1, start), Integer.MAX_VALUE);
		ratingStats.damage = CheckInput.GetInt(QueryParser.getDoubleStatVal(data, "Damage", 1, start), Integer.MAX_VALUE);
		ratingStats.defense = CheckInput.GetInt(QueryParser.getDoubleStatVal(data, "Defense Points", 1, start), Integer.MAX_VALUE);
		ratingStats.destroyed = CheckInput.GetInt(QueryParser.getDoubleStatVal(data, "Targets Destroyed", 1, start), Integer.MAX_VALUE);
		ratingStats.spotted = CheckInput.GetInt(QueryParser.getDoubleStatVal(data, "Targets Detected", 1, start), Integer.MAX_VALUE);
		ratingStats.totalExp = CheckInput.GetInt(QueryParser.getDoubleStatVal(data, "Total Experience", 1, start), Integer.MAX_VALUE);

		start = data.indexOf("Vehicles", start);
		// vehicle tier labels start in this data cell class
		start = data.indexOf("<span class=\"level\">", start);
		while (start != -1) {
			start += "<span class=\"level\">".length();
			String tier = QueryParser.getCellData(data, start).trim();
			// vehicle names start in this data cell class
			start = data.indexOf("<td class=\"value\">", start);
			String v = QueryParser.getCellData(data, start).trim();
			String[] val = QueryParser.getDoubleStat(data, v, start);
			if (val != null) {
				if (v.equals("M3 Stuart")) {
					// need to verify which M3 Stuart..
					String cell = data.substring(start, data.indexOf(v, start));
					if (cell.toLowerCase().contains("ussr")) {
						v = "M3 Stuart II";
					}
				}
				Tank t = new Tank(tierStrToTier(tier), v);
				addTank(t, CheckInput.GetInt(val[0], 0), CheckInput.GetInt(val[1], 0));


			}
			start = data.indexOf("<span class=\"level\">", start);
		}
	}

	public void addTank(Tank t, int battles, int wins) {
		if (tankBattles.containsKey(t)) {
			BattleStats b = tankBattles.get(t);
			battlesByType[t.type.ordinal()] += battles - b.battles;
			b.battles = battles;
			b.victories = wins;
		} else {
			BattleStats b = new BattleStats();
			b.battles = battles;
			b.victories = wins;
			tankBattles.put(t, b);
			battlesByType[t.type.ordinal()] += battles;

			if (t.tier > 0 && t.tier <= 10) {
				if (t.effectiveTier() > maxEffectiveTier[0]) {
					maxEffectiveTier[0] = t.effectiveTier();
				}
				++tanksByTier[0][t.tier - 1];
				if (t.tier > maxTier[0]) {
					maxTier[0] = t.tier;
				}
				int v = ArrayManip.indexOf(typeOrder, t.type);
				if (v > 0) {
					++tanksByTier[v][t.tier - 1];
					if (t.tier > maxTier[v]) {
						maxTier[v] = t.tier;
					}
					if (t.effectiveTier() > maxEffectiveTier[v]) {
						maxEffectiveTier[v] = t.effectiveTier();
					}
				}
				++tanksByTier[6][t.effectiveTier() - 1];
				if (t.effectiveTier() > maxTier[6]) {
					maxTier[6] = t.effectiveTier();
				}
			}
		}
	}

	public Tank[] getSortedTanks() {
		Tank[] tanks = tankBattles.keySet().toArray(new Tank[0]);
		Arrays.sort(tanks, new Comparator<Tank>() {

			@Override
			public int compare(Tank o1, Tank o2) {
				//return o1.tier != o2.tier ? o2.tier - o1.tier : o1.name.compareToIgnoreCase(o2.name);
				return o1.effectiveTier() != o2.effectiveTier()
						? o2.effectiveTier() - o1.effectiveTier()
						: tankBattles.get(o2).battles - tankBattles.get(o1).battles;//: o1.name.compareToIgnoreCase(o2.name);
			}
		});
		return tanks;
	}

	private int tierStrToTier(String num) {
		for (int i = 0; i < Tank.tiers.length; ++i) {
			if (num.equalsIgnoreCase(Tank.tiers[i])) {
				return i + 1;
			}
		}
		return 0;
	}
}
