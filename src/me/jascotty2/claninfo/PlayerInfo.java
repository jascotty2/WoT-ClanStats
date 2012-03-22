package me.jascotty2.claninfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.jascotty2.lib.io.CheckInput;

public class PlayerInfo {

	private final static String tiers[] = new String[]{"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
	public String playername, playerID, clan, position, joined, lastPlayed;
	public BattleStats totals = new BattleStats(), globalRating = new BattleStats();
	public int playerRating;
	public Map<Tank, BattleStats> tankBattles = new HashMap<Tank, BattleStats>();
	public int[] tanksByTier = new int[10];
	public int maxEffectiveTier;
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

	public void loadFromWeb(String serverExt) throws MalformedURLException, IOException {
		URL url = new URL("http://worldoftanks." + serverExt + "/community/accounts/" + playerID + "/");
		URLConnection con = url.openConnection();
		//con.setRequestProperty("Accept-Language", "en-us;q=0.5,en;q=0.3");
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; es-ES; rv:1.9.2.8) Gecko/20100722 Firefox/3.6.8");
		con.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
		con.setRequestProperty("Accept-Language", "en-us;q=0.5,en;q=0.3");
		con.setRequestProperty("Accept-Encoding", "paco");
		con.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		con.setRequestProperty("Connection", "close");
		con.setRequestProperty("X-Requested-With", "XMLHttpRequest");


		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF8"));

//		BufferedReader in = new BufferedReader(new InputStreamReader(
//				new FileInputStream("D:\\Jacob\\Programs\\Java\\ClanTracker\\jascotty2.htm")));

		String tmp;
		StringBuilder res = new StringBuilder();
		while ((tmp = in.readLine()) != null) {
			res.append(tmp);
		}

		String data = res.toString();

		String numberNW = (serverExt.equals("com") || serverExt.equals("eu")) ? "td-number-nowidth" : "td-number";

		int start = data.indexOf("Registered");
		setCreated(getStatTimestamp(data, "Registered", start));
		setLastbattle(getStatTimestamp(data, "Data as of", start));
		totals.battles = getStatInt(data, "Battles Participated", numberNW, start);
		totals.victories = getStatInt(data, "Victories", numberNW, start);
		totals.losses = getStatInt(data, "Defeats", numberNW, start);
		totals.survived = getStatInt(data, "Battles Survived", numberNW, start);
		totals.destroyed = getStatInt(data, "Destroyed", numberNW, start);
		totals.spotted = getStatInt(data, "Detected", numberNW, start);
		totals.hitRatio = getStatInt(data, "Hit Ratio", numberNW, start);
		totals.damage = getStatInt(data, "Damage", numberNW, start);
		totals.captured = getStatInt(data, "Capture Points", numberNW, start);
		totals.defense = getStatInt(data, "Defense Points", numberNW, start);
		totals.totalExp = getStatInt(data, "Total Experience", numberNW, start);
		totals.avgExp = getStatInt(data, "Average Experience per Battle", numberNW, start);
		totals.maxExp = getStatInt(data, "Maximum Experience per Battle", numberNW, start);
		start = data.indexOf("Rating", start);
		playerRating = CheckInput.GetInt(getDoubleStatVal(data, "Global Rating", 1, start), Integer.MAX_VALUE);
		globalRating.victories = CheckInput.GetInt(getDoubleStatVal(data, "Victories/Battles", 1, start), Integer.MAX_VALUE);
		globalRating.avgExp = CheckInput.GetInt(getDoubleStatVal(data, "Average Experience per Battle", 1, start), Integer.MAX_VALUE);
		globalRating.victories = CheckInput.GetInt(getDoubleStatVal(data, "Victories", 1, start), Integer.MAX_VALUE);
		globalRating.battles = CheckInput.GetInt(getDoubleStatVal(data, "Battles Participated", 1, start), Integer.MAX_VALUE);
		globalRating.captured = CheckInput.GetInt(getDoubleStatVal(data, "Capture Points", 1, start), Integer.MAX_VALUE);
		globalRating.damage = CheckInput.GetInt(getDoubleStatVal(data, "Damage", 1, start), Integer.MAX_VALUE);
		globalRating.defense = CheckInput.GetInt(getDoubleStatVal(data, "Defense Points", 1, start), Integer.MAX_VALUE);
		globalRating.destroyed = CheckInput.GetInt(getDoubleStatVal(data, "Targets Destroyed", 1, start), Integer.MAX_VALUE);
		globalRating.spotted = CheckInput.GetInt(getDoubleStatVal(data, "Targets Detected", 1, start), Integer.MAX_VALUE);
		globalRating.totalExp = CheckInput.GetInt(getDoubleStatVal(data, "Total Experience", 1, start), Integer.MAX_VALUE);

		start = data.indexOf("Vehicles", start);
		// vehicle tier labels start in this data cell class
		start = data.indexOf("<span class=\"level\">", start);
		while (start != -1) {
			start += "<span class=\"level\">".length();
			String tier = getCellData(data, start).trim();
			// vehicle names start in this data cell class
			start = data.indexOf("<td class=\"value\">", start);
			String v = getCellData(data, start).trim();
			String[] val = getDoubleStat(data, v, start);
			if (val != null) {
				Tank t = new Tank(tierStrToTier(tier), v);
				BattleStats b = new BattleStats();
				b.battles = CheckInput.GetInt(val[0], 0);
				b.victories = CheckInput.GetInt(val[1], 0);
				tankBattles.put(t, b);
				if (t.tier > 0 && t.tier <= 10) {
					++tanksByTier[t.tier - 1];
					if (t.effectiveTier() > maxEffectiveTier) {
						maxEffectiveTier = t.tier;
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
						: o1.name.compareToIgnoreCase(o2.name);
			}
		});
		return tanks;
	}

	private int getStatInt(String html, String title, String divider, int start) {
		String ret = getStat(html, title, divider, start);
		if (ret != null) {
			if (ret.contains("(")) {
				ret = ret.substring(0, ret.indexOf("("));
			}
			return CheckInput.GetInt(ret.replace("%", ""), -1);
		}
		return -1;
	}

	private String getStat(String html, String title, String divider, int start) {
		int i = html.indexOf(title, start);
		if (i != -1) {
			i = html.indexOf("<td class=\"" + divider + "\">", i);
			if (i != -1) {
				i += ("<td class=\"" + divider + "\">").length();
				int end = html.indexOf("</td>", i);
				if (end != -1) {
					return html.substring(i, end).replace(" ", "").replace("&nbsp;", "");
				}
			}
		}
		return null;
	}

	private String getStatTimestamp(String html, String title, int start) {
		int i = html.indexOf(title, start);
		if (i != -1) {
			i = html.indexOf("data-timestamp=\"", i);
			if (i != -1) {
				i += ("data-timestamp=\"").length();
				int end = html.indexOf("\"", i);
				if (end != -1) {
					return html.substring(i, end);
				}
			}
		}
		return null;
	}

	private String getCellData(String html, int start) {
		try {
			return getDataWithoutTags(html.substring(start, getAssertedIndex(html, "</td>", start)));
		} catch (Exception ex) {
		}
		return null;
	}

	private String getDataWithoutTags(String data) {
		data = data.replace("&nbsp;", " ");
		if (data.contains("<")) {
			StringBuilder dat = new StringBuilder();
			boolean in_tag = false;
			for (char c : data.toCharArray()) {
				if (c == '<') {
					in_tag = true;
				} else if (c == '>') {
					in_tag = false;
				} else if (!in_tag) {
					dat.append(c);
				}
			}
			return dat.toString();
		}
		return data;
	}

	private String getDoubleStatVal(String html, String title, int num, int start) {
		String val[] = getDoubleStat(html, title, start);
		return val != null && val.length > num ? val[num] : null;
	}

	private String[] getDoubleStat(String html, String title, int start) {
		try {
			int i = getAssertedIndex(html, title, start);
			i = getAssertedIndex(html, "</td>", i);
			i += ("</td>").length();
			// now search for two other data cells, before the next row
			int max = getAssertedIndex(html, "</tr>", i);
			// first
			i = getAssertedIndex(html, "<td", i);
			i = getAssertedIndex(html, ">", i) + 1;
			String s1 = html.substring(i, getAssertedIndex(html, "</td>", i)).replace(" ", "").replace("&nbsp;", "");
			i = getAssertedIndex(html, "<td", i);
			i = getAssertedIndex(html, ">", i) + 1;
			String s2 = html.substring(i, getAssertedIndex(html, "</td>", i)).replace(" ", "").replace("&nbsp;", "");
			return (i > max) ? null : new String[]{getDataWithoutTags(s1), getDataWithoutTags(s2)};
		} catch (Exception ex) {
			Logger.getAnonymousLogger().log(Level.WARNING, "", ex);
		}
		return null;
	}

	private int getAssertedIndex(String str, String search, int start) throws Exception {
		int i = str.indexOf(search, start);
		if (i == -1) {
			throw new Exception(String.format("\"%s\" not found in string", search));
		}
		return i;
	}

	private int tierStrToTier(String num) {
		for (int i = 0; i < tiers.length; ++i) {
			if (num.equalsIgnoreCase(tiers[i])) {
				return i + 1;
			}
		}
		return 0;
	}
}
