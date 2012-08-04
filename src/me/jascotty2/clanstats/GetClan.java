/**
 * Copyright (C) 2012 Jacob Scott <jascottytechie@gmail.com>
 * Description: Methods to obtain information about a clan
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.jascotty2.lib.io.CheckInput;
import me.jascotty2.lib.util.Str;

public class GetClan implements Runnable, Cloneable {

	public String server = "com"; // com == NA, eu == Europe
	public String searchTag; // what is entered as search query
	public String requestData = ""; // what is returned by the query
	public int max_threads = 20; // max lookup threads
	public boolean isFound = false;
	boolean stopped = true; // is the query returned (good) results
	// self explanitory..
	public int numPlayers;
	public String clanName;
	public String clanTag;
	public String emblemURL;
	public String clanID = ""; // unique clan id
	public String ownerID = "", ownerName = "";
	public Date created;
	public long generatedTime;
	public final ArrayList<ProvinceInfo> provinces = new ArrayList<ProvinceInfo>();
	public final ArrayList<PlayerInfo> players = new ArrayList<PlayerInfo>();
	public final ArrayList<BattleInfo> battles = new ArrayList<BattleInfo>();
	public int clanIncome;
	public int totalTiers[] = new int[10],
			maxTiers[] = new int[10];
	// threads to lookup multiple players at once
	Thread[] findThreads;
	// track what players haven't been looked up yet
	final Stack<PlayerInfo> toLookup = new Stack<PlayerInfo>();
	public ScanCallback callback = null;

	public GetClan(String tag) {
		this.searchTag = tag;
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

			// now ask for the info
			requestData = QueryParser.get("http://worldoftanks." + server
					+ "/community/clans/?type=table&offset=0&limit=100&order_by=name&search="
					+ searchTag + "&echo=2&id=clans_index");

			// data should be ok if returned success
			if (requestData == null || !requestData.replace(" ", "").contains("\"result\":\"success\"")) {
				return;
			}
			List<Object> data = QueryParser.getItemLists("items", requestData);
			if (data == null || data.isEmpty()) {
				return;
			}

			Map<String, Object> dat = null;

			if (data.size() > 1) {
				// search for exact
				for (Object o : data) {
					if (o instanceof Map) {
						Map<String, Object> d = (Map<String, Object>) o;
						if (((String) d.get("abbreviation")).equalsIgnoreCase(searchTag)
								|| ((String) d.get("name")).equalsIgnoreCase(searchTag)) {
							dat = d;
							break;
						}
					}

				}
			} else {
				if (data.get(0) instanceof Map) {
					dat = (Map<String, Object>) data.get(0);
				}
			}
			if (dat == null) {
				return;
			}
			// extract info from results
			clanTag = (String) dat.get("abbreviation");
			clanName = (String) dat.get("name");
			clanID = dat.get("id").toString();
			numPlayers = (Integer) dat.get("member_count");
			emblemURL = (String) dat.get("clan_emblem_url");
			ownerID = dat.get("owner_id").toString();
			if (emblemURL != null) {
				emblemURL = "http://worldoftanks." + server + emblemURL;
			}
			String started = dat.get("created_at").toString();
			if (started != null && CheckInput.IsDouble(started)) {
				created = new Date((long) CheckInput.GetDouble(started, 0) * 1000);
			}

			isFound = clanTag != null
					&& clanName != null
					&& clanID != null
					&& numPlayers != 0
					&& emblemURL != null;
			getProvinces();
			getBattles();
			getPlayers();
		} catch (Exception ex) {
			Logger.getLogger(GetClan.class.getName()).log(Level.SEVERE, null, ex);
			isFound = false;
		}
	}

	public void getProvinces() {

		provinces.clear();
		clanIncome = 0;

		String res = QueryParser.get("http://worldoftanks." + server + "/community/clans/"
				+ clanID + "/provinces/?type=table&offset=0&limit=1000&order_by=name&echo=1&id=js-provinces-table");

		if (res == null) {
			isFound = false;
		} else {
			List<Object> data = QueryParser.getItemLists("items", res);
			if (data != null && !data.isEmpty()) {
				try {
					for (Object o : data) {
						if (o instanceof Map) {
							Map<String, Object> dat = (Map<String, Object>) o;
							ProvinceInfo p = new ProvinceInfo();
							p.id = (String) dat.get("id");
							p.name = (String) dat.get("name");
							p.map = (String) dat.get("arena_name");
							p.mapID = dat.get("arena_id").toString();
							p.setBattleTime(dat.get("prime_time").toString());
							p.combatsRunning = (Boolean) dat.get("combats_running");
							p.isAttacked = (Boolean) dat.get("attacked");
							p.isCapital = (Boolean) dat.get("capital");
							p.setOccupancy(dat.get("occupancy_time").toString());
							p.revenue = (Integer) dat.get("revenue");
							clanIncome += p.revenue;
							provinces.add(p);
						}
					}

				} catch (Throwable t) {
					System.out.println("ERROR: " + Str.getStackStr(t));
				}
			}
		}

	}

	public void getBattles() {

		String res = QueryParser.get("http://worldoftanks." + server + "/community/clans/"
				+ clanID + "/battles/?type=table&offset=0&limit=1000&order_by=name&echo=1");
		//System.out.println(res);

		if (res == null) {
			isFound = false;
		} else {
			List<Object> data = QueryParser.getItemLists("items", res);

			if (data != null && !data.isEmpty()) {
				try {
					for (Object o : data) {
						if (o instanceof Map) {
							Map<String, Object> dat = (Map<String, Object>) o;
							BattleInfo b = new BattleInfo();
							if (dat.containsKey("provinces")
									&& dat.get("provinces") instanceof List
									&& ((List) dat.get("provinces")).size() >= 1
									&& ((List) dat.get("provinces")).get(0) instanceof Map) {
								Map<String, Object> p = (Map<String, Object>) ((List) dat.get("provinces")).get(0);
								b.provinceID = (String) p.get("id");
								b.provinceName = (String) p.get("name");
							}
							b.type = (String) dat.get("type");
							b.started = (Boolean) dat.get("started");
							if (dat.get("time") instanceof Integer) {
								b.battleTime = (Integer) dat.get("time");
							}
							if (dat.containsKey("arenas") && dat.get("arenas") instanceof List) {
								//for(String arena : (List<String>) dat.get("arenas")) {
								for (Object arena : (List) dat.get("arenas")) {
									if (arena instanceof String) {
										b.arenas.add((String) arena);
									}
								}
							}

							battles.add(b);
						}
					}
					//System.out.println("--");
				} catch (Throwable t) {
					System.out.println("ERROR: " + Str.getStackStr(t));
				}
			}
		}
	}

	public void getPlayers() {
		players.clear();
		for (int i = 0; i < 10; ++i) {
			totalTiers[i] = maxTiers[i] = 0;
		}

		String res = QueryParser.get("http://worldoftanks." + server
				+ "/community/clans/" + clanID
				+ "/members/?type=table&offset=0&limit=100&order_by=name&search=&echo=1&id=clan_members_index");

		//System.out.println(res);

		if (res == null) {
			isFound = false;
		} else {
			List<Object> data = QueryParser.getItemLists("items", res);

			if (data != null && !data.isEmpty()) {
				for (Object o : data) {
					if (o instanceof Map) {
						Map<String, Object> dat = (Map<String, Object>) o;
						PlayerInfo p = new PlayerInfo();
						p.playername = (String) dat.get("name");
						p.playerID = dat.get("account_id").toString();
						p.position = (String) dat.get("role");
						p.setMemberSince(dat.get("member_since").toString());
						//if(dat.get("banned") != null) 
						p.is_banned = (Boolean) dat.get("banned");
						players.add(p);
					}
				}
			}
		}
	}

	public void lookupPlayers() {
		// track what players haven't been looked up yet
		toLookup.clear();
		toLookup.addAll(players);

		stopped = false;

		// spawn multiple lookup threads
		findThreads = new Thread[players.size() > max_threads ? max_threads : players.size()];
		for (int i = 0; i < findThreads.length; ++i) {
			findThreads[i] = new Thread(new threadedPlayerLookup(toLookup.pop(), i));
			findThreads[i].start();
		}
	}

	private synchronized PlayerInfo getNextLookup() {
		return toLookup.empty() ? null : toLookup.pop();
	}

	private synchronized void scanDone(int threadId) {
		findThreads[threadId] = null;
		if (!stopped) {
			int al = 0;
			for (int i = 0; i < findThreads.length; ++i) {
				if (findThreads[i] != null && findThreads[i].isAlive()) {
					++al;
				}
			}
			//System.out.println("worker thread in " + this.clanTag + " finished (" + al + " left)");
			if (al >= 1) {
				// still waiting on some workers to finish
				return;
			}
			stopped = true;

			for (PlayerInfo p : players) {
				for (int i = 0; i < 10; ++i) {
					if (p.tanksByTier[0][i] > 0) {
						totalTiers[i] += p.tanksByTier[0][i];
						++maxTiers[i];
					}
				}
				if (p.playerID.equals(ownerID)) {
					ownerName = p.playername;
				}
			}

			generatedTime = System.currentTimeMillis();
			if (callback != null) {
				callback.ScanDone();
			}
		}
	}

	class threadedPlayerLookup implements Runnable {

		PlayerInfo player;
		int threadId = -1;

		public threadedPlayerLookup(PlayerInfo player, int threadId) {
			this.player = player;
			this.threadId = threadId;
		}

		@Override
		public void run() {
			try {
				player.loadFromWeb(server);
				while (!stopped && (player = getNextLookup()) != null) {
					player.loadFromWeb(server);
				}
				scanDone(threadId);
			} catch (Exception ex) {
				Logger.getLogger(GetClan.class.getName()).log(Level.SEVERE, null, ex);
				synchronized (toLookup) {
					toLookup.clear();
				}
				scanDone(threadId);
			}
		}
	}

	public void sortPlayersByTankAndRating() {
		Collections.sort(players, new Comparator<PlayerInfo>() {

			@Override
			public int compare(PlayerInfo o1, PlayerInfo o2) {
				if (o1.maxEffectiveTier[0] == o2.maxEffectiveTier[0]) {
					return o1.playerRating - o2.playerRating;
				}
				return o2.maxEffectiveTier[0] - o1.maxEffectiveTier[0];
			}
		});
	}

	@Override
	public GetClan clone() {
		GetClan clone = new GetClan(searchTag);
		clone.server = this.server;
		clone.requestData = this.requestData;
		clone.max_threads = this.max_threads;
		clone.isFound = this.isFound;

		clone.numPlayers = this.numPlayers;
		clone.clanName = this.clanName;
		clone.clanTag = this.clanTag;
		clone.emblemURL = this.emblemURL;
		clone.clanID = this.clanID;
		clone.ownerID = this.ownerID;
		clone.ownerName = this.ownerName;
		clone.created = this.created;
		clone.provinces.addAll(this.provinces);
		clone.players.addAll(this.players);

		clone.clanIncome = this.clanIncome;
		for (int i = 0; i < totalTiers.length; ++i) {
			clone.totalTiers[i] = this.totalTiers[i];
			clone.maxTiers[i] = this.maxTiers[i];
		}
		clone.callback = this.callback;
		return clone;
	}

	public static interface ScanCallback {

		public void ScanDone();
	}
}
