package me.jascotty2.claninfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.jascotty2.lib.io.CheckInput;

public class GetClan implements Runnable {

	String server = "com"; // com == NA, eu == Europe
	String searchTag; // what is entered as search query
	String requestData = ""; // what is returned by the query
	int max_threads = 20;
	boolean isFound, stopped = true; // is the query returned (good) results
	// self explanitory..
	int numPlayers;
	String clanName;
	String clanTag;
	String emblemURL;
	String clanID = ""; // unique clan id
	String ownerID = "", ownerName = "";
	Date created;
	ArrayList<ProvinceInfo> provinces = new ArrayList<ProvinceInfo>();
	ArrayList<PlayerInfo> players = new ArrayList<PlayerInfo>();
	int clanIncome;
	int totalTiers[] = new int[10],
			maxTiers[] = new int[10];
	// threads to lookup multiple players at once
	Thread[] findThreads;
	// track what players haven't been looked up yet
	final Stack<PlayerInfo> toLookup = new Stack<PlayerInfo>();

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
			URL localURL = new URL("http://worldoftanks." + server
					+ "/community/clans/?type=table&offset=0&limit=1&order_by=name&search="
					+ searchTag + "&echo=2&id=clans_index");

			URLConnection localURLConnection = localURL.openConnection();
			localURLConnection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
			localURLConnection.setRequestProperty("Accept-Language", "en-us;q=0.5,en;q=0.3");
			localURLConnection.setRequestProperty("Accept-Encoding", "paco");
			localURLConnection.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
			localURLConnection.setRequestProperty("Connection", "close");
			localURLConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");

			// and read the results
			BufferedReader localBufferedReader =
					new BufferedReader(new InputStreamReader(localURLConnection.getInputStream(), "UTF8"));
			String tmp;
			StringBuilder res = new StringBuilder();
			while ((tmp = localBufferedReader.readLine()) != null) {
				res.append(tmp);
			}
			requestData = res.toString();

			// data should be ok if returned success
			if (!requestData.contains("\"result\":\"success\"")) {
				return;
			}
			String[] data = getItemLists(requestData);
			if (data == null || data.length == 0) {
				return;
			}

			// extract info from results
			clanTag = getData(data[0], "abbreviation");
			clanName = getData(data[0], "name");
			clanID = getData(data[0], "id");
			numPlayers = CheckInput.GetInt(getData(data[0], "member_count"), 0);
			emblemURL = getData(data[0], "clan_emblem_url");
			ownerID = getData(data[0], "owner_id");
			if (emblemURL != null) {
				emblemURL = "http://worldoftanks." + server + emblemURL;
			}
			String started = getData(data[0], "created_at");
			if (started != null && CheckInput.IsDouble(started)) {
				created = new Date((long) CheckInput.GetDouble(started, 0) * 1000);
			}

			isFound = clanTag != null
					&& clanName != null
					&& clanID != null
					&& numPlayers != 0
					&& emblemURL != null;
			if (isFound) {
				getProvinces();
				getPlayers();
			}
		} catch (MalformedURLException ex) {
			Logger.getLogger(GetClan.class.getName()).log(Level.SEVERE, null, ex);
			isFound = false;
		} catch (IOException ex) {
			Logger.getLogger(GetClan.class.getName()).log(Level.SEVERE, null, ex);
			isFound = false;
		}
	}

	private void getProvinces() throws MalformedURLException, IOException {

		provinces.clear();
		clanIncome = 0;

		URL localURL = new URL("http://worldoftanks." + server + "/community/clans/"
				+ clanID + "/provinces/?type=table&offset=0&limit=1000&order_by=name&search=&echo=1&id=js-provinces-table");
		URLConnection localURLConnection = localURL.openConnection();
		localURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; es-ES; rv:1.9.2.8) Gecko/20100722 Firefox/3.6.8");
		localURLConnection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
		localURLConnection.setRequestProperty("Accept-Language", "en-us;q=0.5,en;q=0.3");
		localURLConnection.setRequestProperty("Accept-Encoding", "paco");
		localURLConnection.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		localURLConnection.setRequestProperty("Connection", "close");
		localURLConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");

		// and read the results
		BufferedReader localBufferedReader =
				new BufferedReader(new InputStreamReader(localURLConnection.getInputStream(), "UTF8"));
		String tmp;
		StringBuilder res = new StringBuilder();
		while ((tmp = localBufferedReader.readLine()) != null) {
			res.append(tmp);
		}

		String[] data = getItemLists(res.toString());

		for (String dat : data) {
			ProvinceInfo p = new ProvinceInfo();
			p.id = getData(dat, "id");
			p.name = getData(dat, "name");
			p.map = getData(dat, "arena_name");
			p.mapID = getData(dat, "arena_id");
			p.setBattleTime(getData(dat, "prime_time"));
			p.setCombatsRunning(getData(dat, "combats_running"));
			p.setIsAttacked(getData(dat, "attacked"));
			p.setIsCapital(getData(dat, "capital"));
			p.setOccupancy(getData(dat, "occupancy_time"));
			p.setRevenue(getData(dat, "revenue"));
			clanIncome += p.revenue;
			provinces.add(p);
		}
	}

	private void getPlayers() throws MalformedURLException, IOException {
		players.clear();
		for (int i = 0; i < 10; ++i) {
			totalTiers[i] = maxTiers[i] = 0;
		}

		URL localURL = new URL("http://worldoftanks." + server
				+ "/community/clans/" + clanID
				+ "/members/?type=table&offset=0&limit=100&order_by=name&search=&echo=1&id=clan_members_index");
		URLConnection localURLConnection = localURL.openConnection();
		localURLConnection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
		localURLConnection.setRequestProperty("Accept-Language", "en-us;q=0.5,en;q=0.3");
		localURLConnection.setRequestProperty("Accept-Encoding", "paco");
		localURLConnection.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		localURLConnection.setRequestProperty("Connection", "close");
		localURLConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
		BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localURLConnection.getInputStream(), "UTF8"));
		String tmp;
		StringBuilder res = new StringBuilder();
		while ((tmp = localBufferedReader.readLine()) != null) {
			res.append(tmp);
		}

		String[] data = getItemLists(res.toString());

		for (String dat : data) {
			PlayerInfo p = new PlayerInfo();
			p.playername = getData(dat, "name");
			p.playerID = getData(dat, "account_id");
			p.position = getData(dat, "role");
			p.setMemberSince(getData(dat, "member_since"));

			players.add(p);
		}
		// track what players haven't been looked up yet
		toLookup.clear();
		toLookup.addAll(players);
		
		// spawn multiple lookup threads
		findThreads = new Thread[players.size() > max_threads ? max_threads : players.size()];
		for (int i = 0; i < findThreads.length; ++i) {
			findThreads[i] = new Thread(new threadedPlayerLookup(toLookup.pop()));
			findThreads[i].start();
		}
	}

	private synchronized PlayerInfo getNextLookup() {
		return toLookup.empty() ? null : toLookup.pop();
	}

	private synchronized void scanDone() {
		if (!stopped) {
			int al = 0;
			for (int i = 0; i < findThreads.length; ++i) {
				if (findThreads[i].isAlive()) {
					++al;
				}
			}
			if (al > 1) {
				// still waiting on some workers to finish
				return;
			}
			stopped = true;

			for (PlayerInfo p : players) {
				for (int i = 0; i < 10; ++i) {
					if (p.tanksByTier[i] > 0) {
						totalTiers[i] += p.tanksByTier[i];
						++maxTiers[i];
					}
				}
				if (p.playerID.equals(ownerID)) {
					ownerName = p.playername;
				}
			}
			Main.printResults();
		}
	}

	class threadedPlayerLookup implements Runnable {

		PlayerInfo player;

		public threadedPlayerLookup(PlayerInfo player) {
			this.player = player;
		}

		@Override
		public void run() {
			try {
				player.loadFromWeb(server);
				while (!stopped && (player = getNextLookup()) != null) {
					player.loadFromWeb(server);
				}
				scanDone();
			} catch (Exception ex) {
				Logger.getLogger(GetClan.class.getName()).log(Level.SEVERE, null, ex);
				synchronized (toLookup) {
					toLookup.clear();
				}
				scanDone();
			}
		}
	}

	public void sortPlayersByTankAndRating() {
		Collections.sort(players, new Comparator<PlayerInfo>() {

			@Override
			public int compare(PlayerInfo o1, PlayerInfo o2) {
				if(o1.maxEffectiveTier == o2.maxEffectiveTier) {
					return o1.playerRating - o2.playerRating;
				}
				return o2.maxEffectiveTier - o1.maxEffectiveTier;
			}
		});
	}

	protected String[] getItemLists(String data) {
		String[] lists = null;
		int i = data.indexOf("\"items\":[");
		if (i != -1) {
			i += "\"items\":[".length();
			ArrayList<String> results = new ArrayList<String>();
			while (data.charAt(i) != ']') {
				if (data.charAt(i) == ',') {
					i += 2;
				}
				// assume no special chars in quotes
				int end = data.indexOf("}", i);
				if (end == -1) {
					break;
				}
				results.add(data.substring(i, end));
				i = end + 1;
			}
			lists = results.toArray(new String[0]);
		}
		return lists;
	}

	protected String getData(String itemList, String tag) {
		if (itemList != null) {
			tag = "\"" + tag + "\":";
			int i = itemList.indexOf(tag);
			if (i != -1) {
				i += tag.length();
				// if starts with a quote, inc by 1 and go to end quote
				// else, continue to the ending comma
//				int end = (requestData.charAt(i) == '"')
//						? requestData.indexOf("\"", ++i)
//						: requestData.indexOf(",", i);
				int end = -1;
				if (itemList.charAt(i) == '"') {
					end = itemList.indexOf("\"", ++i);
				} else {
					int a = itemList.indexOf("}", i);
					int b = itemList.indexOf("]", i);
					int c = itemList.indexOf(",", i);
					// choose the first control char to break at
					end = (a != -1 && a < b && a < c)
							? a : ((b != -1 && b < a && b < c) ? b : c);
				}
				if (end != -1) {
					return itemList.substring(i, end);
				} else {
					return itemList.substring(i);
				}
			}
		}
		return null;
	}
}
