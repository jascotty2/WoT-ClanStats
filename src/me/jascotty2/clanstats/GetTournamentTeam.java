package me.jascotty2.clanstats;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.jascotty2.lib.io.CheckInput;

public class GetTournamentTeam extends GetClan {

	String eventID = "", regEventID = "", tournamentID,
			eventName = "";
	boolean needsPassword = false;
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

	public List<Map<String, Object>> findEvents() {
		// now find the eventID
		String ret = QueryParser.get("http://worldoftanks." + server + "/uc/tournaments/?json=1");
//			System.out.println(ret);
//			List<Map<String, Object>> data = QueryParser.getItemLists("items", ret);
//			for (Map<String, Object> dat : data) {
//				System.out.println("Tournament: ");
//				for (String k : dat.keySet()) {
//					System.out.println("\t" + k + ": " + dat.get(k));
//				}
//			}
		return ret == null ? null : QueryParser.getItemLists("items", ret);
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

	public static void main(String[] args) {
		//GetTournamentTeam t = new GetTournamentTeam("http://worldoftanks.com/uc/tournaments/36-Halbe_Post_meridiem_Challenge/", "StalkersofMayhem");
		GetTournamentTeam t = new GetTournamentTeam("http://worldoftanks.com/uc/tournaments/33-World_of_Tanks_Classic,_Season_I/", "StalkersofMayhem");
		t.run();
		System.out.println("found? " + t.isFound);

	}
}
