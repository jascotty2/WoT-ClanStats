package me.jascotty2.clanstats;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.jascotty2.lib.io.CheckInput;

public class GetTournamentTeam extends GetClan {

	String eventID = "", tournamentID,
			eventName = "";
	boolean needsPassword = false;
	//boolean active = false;

	public GetTournamentTeam(String tournamentID, String tag) {
		super(tag);
		if (tournamentID.toLowerCase().startsWith("http://")) {
			try {
				// eg. http://worldoftanks.com/uc/tournaments/36-Halbe_Post_meridiem_Challenge/
				if (tournamentID.endsWith("/")) {
					tournamentID = tournamentID.substring(0, tournamentID.length() - 1);
				}
				tournamentID = tournamentID.substring(tournamentID.lastIndexOf('/') + 1);
				tournamentID = tournamentID.substring(0, tournamentID.indexOf("-"));
			} catch (Exception e) {
				tournamentID = "";
			}
		}
		this.tournamentID = tournamentID;
	}

	public boolean findEventID() {
		if (!tournamentID.isEmpty()) {
			// now find the eventID
			String ret = QueryParser.get("http://worldoftanks." + server + "/uc/tournaments/?json=1");
			List<Map<String, Object>> data = QueryParser.getItemLists("items", ret);
			for (Map<String, Object> dat : data) {
				System.out.println("Tournament: ");
				for (String k : dat.keySet()) {
					System.out.println("\t" + k + ": " + dat.get(k));
				}
			}
		}
		return false;
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
			if (eventID.isEmpty()) {
				if (!findEventID()) {
					return;
				}
			}
			// now ask for the info
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
		GetTournamentTeam t = new GetTournamentTeam("http://worldoftanks.com/uc/tournaments/36-Halbe_Post_meridiem_Challenge/", "StalkersofMayhem");
		System.out.println("found? " + t.findEventID());

	}
}
