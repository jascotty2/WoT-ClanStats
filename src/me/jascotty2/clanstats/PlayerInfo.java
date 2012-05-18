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

public class PlayerInfo {

	public String playername, playerID, clan, position, lastPlayed;
	public BattleStats totals = new BattleStats(), ratingStats = new BattleStats();
	public int playerRating;
	public Map<Tank, BattleStats> tankBattles = new HashMap<Tank, BattleStats>();
	public int[] tanksByTier = new int[10];
	public int maxEffectiveTier;
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
				+ "/community/accounts/?type=table&offset=0&limit=1&order_by=name&search="
				+ search + "&echo=2&id=clans_index");
		
		if (search != null) {
			List<Map<String, Object>> data = QueryParser.getItemLists("items", res);
			if (data != null && !data.isEmpty()) {
				Map<String, Object> dat = data.get(0);
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
		if(start < 0) {
			if(data.indexOf("Player profile is closed.") != -1) {
				created = null;
			}
			return;
		}
		setCreated(QueryParser.getStatTimestamp(data, "Registered", start));
		setLastbattle(QueryParser.getStatTimestamp(data, "Data as of", start));
		if(data.substring(start).contains("In Clan")) {
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
				if(v.equals("M3 Stuart")){
					// need to verify which M3 Stuart..
					String cell = data.substring(start, data.indexOf(v, start));
					if(cell.toLowerCase().contains("ussr")) {
						v = "M3 Stuart II";
					}
				}
				Tank t = new Tank(tierStrToTier(tier), v);
				BattleStats b = new BattleStats();
				b.battles = CheckInput.GetInt(val[0], 0);
				b.victories = CheckInput.GetInt(val[1], 0);
				tankBattles.put(t, b);
				if (t.tier > 0 && t.tier <= 10) {
					++tanksByTier[t.tier - 1];
					if (t.effectiveTier() > maxEffectiveTier) {
						maxEffectiveTier = t.effectiveTier();
					}
				}
			}
			start = data.indexOf("<span class=\"level\">", start);
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
		for (int i = 0; i < TankType.tiers.length; ++i) {
			if (num.equalsIgnoreCase(TankType.tiers[i])) {
				return i + 1;
			}
		}
		return 0;
	}
}
