/**
 * Copyright (C) 2012 Jacob Scott <jascottytechie@gmail.com>
 * Description: Methods to obtain information about a tournament and team
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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.jascotty2.lib.io.CheckInput;

public class GetTournamentTeam extends GetClan {

	String eventID = "", regEventID = "", tournamentID = "",
			eventName = "";
	boolean needsPassword = false;
	int maxLT = 7, maxMT = 10, maxHT = 10, maxTD = 9, maxSPG = 8;
	//boolean active = false;

	public GetTournamentTeam(String tournamentID, String tag) {
		super(tag);
		if (tournamentID.toLowerCase().startsWith("http://")) {
			try {
				// eg. http://worldoftanks.com/uc/tournaments/36-Halbe_Post_meridiem_Challenge/
				String url = tournamentID;
				if (tournamentID.endsWith("/")) {
					tournamentID = tournamentID.substring(0, tournamentID.length() - 1);
				} else {
					url += "/";
				}
				tournamentID = tournamentID.substring(tournamentID.lastIndexOf('/') + 1);
				if (tournamentID.contains("-")) {
					tournamentID = tournamentID.substring(0, tournamentID.indexOf("-"));
				}
				// now that have the ID, try to load the registrations page to find the eventID
				if (url.contains("registrations")) {
					// reset to format, just in case
					url = url.substring(0, url.indexOf("registrations"));
					System.out.println(url);
				}
				url += "registrations/";
				regEventID = getEventID(url + (CheckInput.GetInt(tournamentID, 3) - 2) + "-Registration/");
				eventID = getEventID(url + (CheckInput.GetInt(tournamentID, 3) - 3) + "-Registration/");
				this.tournamentID = tournamentID;
				findName();
			} catch (Exception e) {
				tournamentID = "";
			}
		}
		this.tournamentID = tournamentID;
	}

	private String getEventID(String url) {
		String page = QueryParser.getPage(url);
		if (page != null) {
			int i = page.indexOf("/uc/teams/?event_id=");
			if (i != -1) {
				i += "/uc/teams/?event_id=".length() - 1;
				StringBuilder n = new StringBuilder(page.charAt(i));
				for (++i; i < page.length(); ++i) {
					if (Character.isDigit(page.charAt(i))) {
						n.append(page.charAt(i));
					} else {
						break;
					}
				}
				return n.toString();
			}
		}
		return "";
	}

	public final boolean findName() {
		// now find the eventID
		String ret = QueryParser.get("http://worldoftanks." + server + "/uc/tournaments/?json=1");
//			System.out.println(ret);
//			List<Map<String, Object>> data = QueryParser.getItemLists("items", ret);
//			
		if (ret != null) {
			List<Map<String, Object>> data = QueryParser.getItemLists("items", ret);
			for (Map<String, Object> dat : data) {
				if (dat.get("id").toString().equals(tournamentID)) {
					for(String k : dat.keySet()) {
						System.out.println(k + ": " + dat.get(k));
					}
					eventName = (String) dat.get("title");
					emblemURL = (String) dat.get("logo_url");
					if(emblemURL != null && !emblemURL.isEmpty()) {
						emblemURL = "http://worldoftanks." + server + emblemURL;
					}
					//  maxLT = 7, maxMT = 10, maxHT = 10, maxTD = 9, maxSPG = 8;
					String desc = QueryParser.getDataWithoutTags(
							((String) dat.get("description")).replace("\\r", "\r").replace("\\n", "\n"));
					maxHT = getMax(desc, "Heavy tank - ", maxHT);
					maxMT = getMax(desc, "Medium tank - ", maxMT);
					maxLT = getMax(desc, "Light tank - ", maxLT);
					maxTD = getMax(desc, "Tank Destroyer - ", maxTD);
					maxSPG = getMax(desc, "SPG - ", maxSPG);
					return true;
				}
			}
		}
		return false;
	}

	private int getMax(String data, String search, int def) {
		int i = data.indexOf(search);
		if (i != -1) {
			StringBuilder n = new StringBuilder();
			for (; i < data.length(); ++i) {
				if (Character.isDigit(data.charAt(i))) {
					n.append(data.charAt(i));
				} else if (n.length() > 0) {
					break;
				}
			}
			return CheckInput.GetInt(n.toString(), def);
		}
		return def;
	}

	@Override
	public void run() {
		try {
			isFound = false;
			stopped = false;
			requestData = "";
			ownerName = "";
			ownerID = "";
			clanName = clanTag = emblemURL = null;
			numPlayers = 0;
			if (eventID.isEmpty() || searchTag.isEmpty()) {
				return;
			}
			// now ask for the info
			requestData = QueryParser.get("http://worldoftanks." + server
					+ "/uc/teams/?event_id=" + eventID + "&type=table&order_by=name&limit=10&search=" + searchTag);

			// data should be ok if returned success
			if (requestData == null || !requestData.contains("\"result\":\"success\"")) {
				return;
			}
			List<Map<String, Object>> data = QueryParser.getItemLists("items", requestData);
			if (data == null || data.isEmpty()) {
				requestData = QueryParser.get("http://worldoftanks." + server
						+ "/uc/teams/?event_id=" + regEventID + "&type=table&order_by=name&limit=10&search=" + searchTag);

				// data should be ok if returned success
				if (requestData == null || !requestData.contains("\"result\":\"success\"")) {
					return;
				}
				data = QueryParser.getItemLists("items", requestData);
				if (data == null || data.isEmpty()) {
					return;
				}
			}
			Map<String, Object> dat = data.get(0);

			// extract info from results
			ownerID = dat.get("team_owner_id").toString();
			needsPassword = (Boolean) dat.get("needpassword");
			clanName = (String) dat.get("name");
			numPlayers = (Integer) dat.get("member_count");
			clanID = dat.get("id").toString();

			String started = dat.get("state_updated_at").toString();
			if (started != null && CheckInput.IsDouble(started)) {
				created = new Date((long) CheckInput.GetDouble(started, 0) * 1000);
			}
			loadMembers((List<Map<String, Object>>) dat.get("members"));

			isFound = clanName != null
					&& clanID != null
					&& numPlayers != 0;
		} catch (Exception ex) {
			Logger.getLogger(GetClan.class.getName()).log(Level.SEVERE, null, ex);
			isFound = false;
		}
	}

	@Override
	public void getProvinces() {
		provinces.clear();
		clanIncome = 0;
	}

	@Override
	public void getPlayers() {
		players.clear();
		for (int i = 0; i < 10; ++i) {
			totalTiers[i] = maxTiers[i] = 0;
		}
		requestData = QueryParser.get("http://worldoftanks." + server
				+ "/uc/teams/?event_id=" + eventID + "&type=table&limit=1&order_by=name&search=" + searchTag);

		// data should be ok if returned success
		if (requestData == null || !requestData.contains("\"result\":\"success\"")) {
			return;
		}
		List<Map<String, Object>> data = QueryParser.getItemLists("items", requestData);
		if (data == null || data.isEmpty()) {
			return;
		}
		Map<String, Object> dat = data.get(0);

		loadMembers((List<Map<String, Object>>) dat.get("members"));
	}

	private void loadMembers(List<Map<String, Object>> members) {
		players.clear();
		for (int i = 0; i < 10; ++i) {
			totalTiers[i] = maxTiers[i] = 0;
		}

		for (Map<String, Object> dat : members) {
			PlayerInfo p = new PlayerInfo();
			p.playername = (String) dat.get("name");
			p.playerID = dat.get("id").toString();
			players.add(p);
		}
	}

	public void applyTierLimits() {
		for (int i = 0; i < 10; ++i) {
			totalTiers[i] = maxTiers[i] = 0;
		}
		for (PlayerInfo p : players) {
			for (Tank t : p.tankBattles.keySet().toArray(new Tank[0])) {
				if (t.type == TankType.HEAVY) {
					if (t.tier > maxHT) {
						p.tankBattles.remove(t);
					}
				} else if (t.type == TankType.MEDIUM) {
					if (t.tier > maxMT) {
						p.tankBattles.remove(t);
					}
				} else if (t.type == TankType.LIGHT) {
					if (t.tier > maxLT) {
						p.tankBattles.remove(t);
					}
				} else if (t.type == TankType.TD) {
					if (t.tier > maxTD) {
						p.tankBattles.remove(t);
					}
				} else if (t.type == TankType.SPG) {
					if (t.tier > maxSPG) {
						p.tankBattles.remove(t);
					}
				}
			}
		}
		for (PlayerInfo p : players) {
			p.maxEffectiveTier = 0;
			for (Tank t : p.tankBattles.keySet()) {
				if (t.tier > 0 && t.tier <= 10) {
					++p.tanksByTier[t.tier - 1];
					if (t.effectiveTier() > p.maxEffectiveTier) {
						p.maxEffectiveTier = t.tier;
					}
				}
			}
			for (int i = 0; i < 10; ++i) {
				if (p.tanksByTier[i] > 0) {
					totalTiers[i] += p.tanksByTier[i];
					++maxTiers[i];
				}
			}
		}
	}
}
