/**
 * Copyright (C) 2012 Jacob Scott <jascottytechie@gmail.com>
 * Description: Output information in HTML format
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class OutputHTML {

	static String template = null;

	static void loadRes(String folder) throws Exception {
		String dir = folder.length() > 0 && !folder.endsWith(File.separator) ? folder + File.separator : folder;
		template = getResFile("template.htm");
		extractFile("style.css", dir, true);
		extractFile("boxover.js", dir, false);
		extractFile("bg.jpg", dir + "images", false);
		extractFile("bg-emblem-profile.png", dir + "images", false);
		extractFile("cont-img-mask.png", dir + "images", false);
		extractFile("ui-bg-tile.jpg", dir + "images", false);
		extractFile("wot-logo.png", dir + "images", false);
	}

	static void extractFile(String filen, boolean overwrite) {
		extractFile(filen, "", overwrite);
	}

	static void extractFile(String filen, String dir, boolean overwrite) {
		String outFile = dir.length() > 0 && !dir.endsWith(File.separator) ? dir + File.separator + filen : dir + filen;
		if (!overwrite && (new File(outFile)).exists()) {
			return;
		}
		File d = new File(dir);
		if (d.exists() && !d.isDirectory()) {
			return;
		}
		if (!d.exists()) {
			d.mkdirs();
		}
		InputStream input = OutputHTML.class.getResourceAsStream("/me/jascotty2/clanstats/res/" + filen);
		if (input != null) {
			FileOutputStream output = null;

			try {
				output = new FileOutputStream(outFile);
				byte[] buf = new byte[8192];
				int length = 0;

				while ((length = input.read(buf)) > 0) {
					output.write(buf, 0, length);
				}
			} catch (Exception e) {
				Logger.getAnonymousLogger().log(Level.SEVERE, null, e);
			} finally {
				try {
					if (input != null) {
						input.close();
					}
				} catch (Exception e) {
				}
				try {
					if (output != null) {
						output.close();
					}
				} catch (Exception e) {
				}
			}
		}
	}

	private static String getResFile(String filen) {
		StringWriter writer = new StringWriter();
		InputStream in = null;
		try {
			URL res = OutputHTML.class.getResource("/me/jascotty2/clanstats/res/" + filen);
			if (res == null) {
				Logger.getAnonymousLogger().log(Level.WARNING, "can't find " + filen + " in plugin JAR file"); //$NON-NLS-1$
				return null;
			}
			URLConnection resConn = res.openConnection();
			resConn.setUseCaches(false);
			in = resConn.getInputStream();
			if (in == null) {
				Logger.getAnonymousLogger().log(Level.WARNING, "can't get input stream from " + res); //$NON-NLS-1$
			} else {
				InputStreamReader inr = new InputStreamReader(in);
				char[] buffer = new char[4096];
				int n = 0;
				while (-1 != (n = inr.read(buffer))) {
					writer.write(buffer, 0, n);
				}
				inr.close();
			}
		} catch (Exception ex) {
			Logger.getAnonymousLogger().log(Level.SEVERE, null, ex);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
				}
			}
		}
		return writer.toString();
	}

	public static void writeFile(GetClan c, String dir) throws Exception {
		File f;
		if (dir != null) {
			File d = new File(dir);
			d.mkdirs();
			f = new File(dir, c.clanName + " [" + c.clanTag + "].html");
		} else {
			f = new File(c.clanName + " [" + c.clanTag + "].html");
		}
		writeFile(c, f);
	}

	public static void writeFile(GetClan c, File f) throws Exception {
		loadRes(f.getParent());
		BufferedWriter out = null;
		FileWriter outStream = null;
		try {
			outStream = new FileWriter(f);
			out = new BufferedWriter(outStream);
		} catch (Exception ex) {
			Logger.getLogger(OutputHTML.class.getName()).log(Level.SEVERE, "Error Opening output file ", ex);
			JOptionPane.showMessageDialog(null, "Error Opening output file: " + ex.getMessage(),
					"IO Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		SimpleDateFormat crformat = new SimpleDateFormat("MMM dd yyy");
		SimpleDateFormat genformat = new SimpleDateFormat("EE, MMM dd yyy  HH:mm zzz");
		SimpleDateFormat lastformat = new SimpleDateFormat("MM/dd/yyy HH:mm");

		c.sortPlayersByTankAndRating();

		// prepare data
		String server = "http://worldoftanks." + c.server;
		StringBuilder tierTableData = new StringBuilder("\n");
		for (int i = 9; i > 0; --i) {
			int lastH = 0, last2 = 0, last24 = 0, num = 0;
			for (PlayerInfo p : c.players) {
				if (p.maxEffectiveTier == i + 1) {
					++num;
					float hago = ((System.currentTimeMillis() - p.lastbattle.getTime()) / 3600000);
					if (hago <= 24) {
						if (hago <= 1) {
							++lastH;
						}
						if (hago <= 2) {
							++last2;
						}
						++last24;
					}
				}
			}
			tierTableData.append("<tr title=\"header=[Active Tier ").
					append(i + 1).
					append("'s:]" + "body=[").
					append(last2).append(" / ").append(last24).append(" / ").append(num).
					append("]\"><td>").
					append(String.valueOf(i + 1)).
					append("</td><td>").
					append(String.valueOf(c.maxTiers[i])).
					append("</td><td>").
					append(String.valueOf(c.totalTiers[i])).
					append("</td>\n");
		}

		//ArrayList<Tank> allTanks = new ArrayList<Tank>();
		HashMap<Tank, ArrayList<PlayerInfo>> tankPlayers = new HashMap<Tank, ArrayList<PlayerInfo>>();

		StringBuilder playerTanksTableData = new StringBuilder("\n");
		int last24 = 0, last2 = 0, lastH = 0;
		for (PlayerInfo p : c.players) {
			// get tanks
			for (Tank t : p.tankBattles.keySet()) {
				//if(!allTanks.contains(t)) allTanks.add(t);
				if (!tankPlayers.containsKey(t)) {
					tankPlayers.put(t, new ArrayList<PlayerInfo>());
				}
				tankPlayers.get(t).add(p);
			}
			float hago = p.lastbattle == null ? Float.MAX_VALUE : ((System.currentTimeMillis() - p.lastbattle.getTime()) / 3600000);
			if (hago <= 24) {
				++last24;
			}
			playerTanksTableData.append("<tr");
			if (hago <= 2) {
				++last2;
				playerTanksTableData.append(" class=\"online\"");
			}
			playerTanksTableData.append("><td class=\"n\" ").
					append("title=\"header=[Player Stats: (").
					append(p.playername).
					append(")]  body=[");
			if (p.is_banned) {
				playerTanksTableData.append("(Player is Banned)<br>");
			}
			if (p.created == null) {
				playerTanksTableData.append("(Closed Account)");
			} else {
				playerTanksTableData.append("GR: ").
						append(String.valueOf(p.playerRating)).
						append("<br>Battles: ").append(String.valueOf(p.totals.battles)).
						append("<br>Last Battle: ").append(p.lastbattle == null ? "- ? -" : lastformat.format(p.lastbattle));
			}
			playerTanksTableData.append("]\">").append("<a href=\"").append(server).append("/community/accounts/").
					append(p.playerID).append("/\">");
			if (p.is_banned) {
				playerTanksTableData.append("<del>").append(p.playername).append("</del>");
			} else {
				playerTanksTableData.append(p.playername);
			}
			playerTanksTableData.append("</a>");
			if (hago <= 1) {
				playerTanksTableData.append("<div class=\"tm\" style=\"background-color: #009900;\">").
						append(String.valueOf((int) hago)).append("</div>");
				++lastH;
			} else if (hago <= 2) {
				playerTanksTableData.append("<div class=\"tm\" style=\"background-color: #669900; color: #000;\">").
						append(String.valueOf((int) hago)).append("</div>");
			} else if (hago <= 5) {
				playerTanksTableData.append("<div class=\"tm\" style=\"background-color: #99CC00; color: #000;\">").
						append(String.valueOf((int) hago)).append("</div>");
			} else if (hago <= 12) {
				playerTanksTableData.append("<div class=\"tm\" style=\"background-color: #996600;\">").
						append(String.valueOf((int) hago)).append("</div>");
			} else if (hago <= 24) {
				playerTanksTableData.append("<div class=\"tm\" style=\"background-color: #990000;\">").
						append(String.valueOf((int) hago)).append("</div>");
			} else if (hago <= 99) {
				playerTanksTableData.append("<div class=\"tm\" style=\"background-color: #330000;\">").
						append(String.valueOf((int) hago)).append("</div>");
			}
			playerTanksTableData.append("</td>");
			int n = 10;
			for (Tank t : p.getSortedTanks()) {
				// ö = \u00F6 = &#246;
				// ä = \u00E4 = &#228;
				playerTanksTableData.append("<td");
				if (t.type == TankType.SPG) {
					playerTanksTableData.append(" class=\"spg\"");
				}
				playerTanksTableData.append("><div title=\"header=[").
						append(t.name.replace("\u00F6", "&#246;").replace("\u00E4", "&#228;")).
						append("] body=[(Tier ").
						append(t.tier);
				if (t.type != TankType.UNKNOWN) {
					playerTanksTableData.append(" ").append(t.type.getName());
				}
				playerTanksTableData.append(")<br>").
						append(String.valueOf(p.tankBattles.get(t).battles)).
						append(" battles]\">").
						append(t.name.replace("\u00F6", "&#246;")).
						append("</div></td>");
				if (--n <= 0) {
					break;
				}
			}
			playerTanksTableData.append("</tr>\n");
		}

		// end player tanks


		StringBuilder tanksTableData = new StringBuilder("\n");
		//for (final Map.Entry<Tank, ArrayList<PlayerInfo>> v : tankPlayers.entrySet()) {
		
		Tank[] tanks = tankPlayers.keySet().toArray(new Tank[0]);
		Arrays.sort(tanks, new Comparator<Tank>() {

			@Override
			public int compare(Tank o1, Tank o2) {
				return o1.effectiveTier() != o2.effectiveTier()
						? o2.effectiveTier() - o1.effectiveTier()
						: o2.type.ordinal() - o1.type.ordinal();
			}
		});
		
		for (final Tank t : tanks) {
			tanksTableData.append("<tr");
			
			PlayerInfo[] players = tankPlayers.get(t).toArray(new PlayerInfo[0]);
			Arrays.sort(players, new Comparator<PlayerInfo>() {

				@Override
				public int compare(PlayerInfo o1, PlayerInfo o2) {
					int p1 = o1.tankBattles.get(t).battles;
					int p2 = o2.tankBattles.get(t).battles;
					return p1 != p2
							? p2 - p1
							: o2.playerRating - o1.playerRating;
				}
			});

			int hr2 = 0, hr24 = 0;
			for (int i = 0; i < 10 && i < players.length; ++i){ //PlayerInfo p : tankPlayers.get(t)) {
				PlayerInfo p = players[i];
				float hago = p.lastbattle == null ? Float.MAX_VALUE : ((System.currentTimeMillis() - p.lastbattle.getTime()) / 3600000);
				if (hago <= 24) {
					++hr24;
				}
				if (hago <= 2) {
					++hr2;
				}
			}
			if (hr2 > 0) {
				tanksTableData.append(" class=\"online").
						append(t.type == TankType.SPG ? " spg" : "").
						append("\"");
			} else if (t.type == TankType.SPG) {
				tanksTableData.append(" class=\"spg\"");
			}
			// ö = \u00F6 = &#246;
			// ä = \u00E4 = &#228;
			tanksTableData.append("><td title=\"header=[Tier ").
					append(t.tier).
					append("<br>").
					append(t.type.getPropername()).
					append("<br>").
					append(hr2).append(" / ").append(hr24).append(" / ").append(tankPlayers.get(t).size()).
					append(")]  body=[").
					append(t.name.replace("\u00F6", "&#246;").replace("\u00E4", "&#228;")).
					append("]\">").
					append(t.name.replace("\u00F6", "&#246;").replace("\u00E4", "&#228;")).
					append("</td>");

			for (int i = 0; i < 10 && i < players.length; ++i) {
				PlayerInfo p = players[i];
				float hago = p.lastbattle == null ? Float.MAX_VALUE : ((System.currentTimeMillis() - p.lastbattle.getTime()) / 3600000);
				if (hago <= 24) {
					++last24;
				}
				tanksTableData.append("<td class=\"");
				if (hago <= 2) {
					tanksTableData.append("online ");
				}
				tanksTableData.append("n\" ").
						append("title=\"header=[Player Stats: (").
						append(p.playername).
						append(")]  body=[");
				if (p.is_banned) {
					tanksTableData.append("(Player is Banned)<br>");
				}
				if (p.created == null) {
					tanksTableData.append("(Closed Account)");
				} else {
					tanksTableData.append("GR: ").
							append(String.valueOf(p.playerRating)).
							append("<br>Battles: ").append(String.valueOf(p.totals.battles)).
							append("<br>Last Battle: ").append(p.lastbattle == null ? "- ? -" : lastformat.format(p.lastbattle));
				}
				tanksTableData.append("]\">").append("<a href=\"").append(server).append("/community/accounts/").
						append(p.playerID).append("/\">");
				if (p.is_banned) {
					tanksTableData.append("<del>").append(p.playername).append("</del>");
				} else {
					tanksTableData.append(p.playername);
				}
				tanksTableData.append("</a>");
				if (hago <= 1) {
					tanksTableData.append("<div class=\"tm\" style=\"background-color: #009900;\">").
							append(String.valueOf((int) hago)).append("</div>");
					++lastH;
				} else if (hago <= 2) {
					tanksTableData.append("<div class=\"tm\" style=\"background-color: #669900; color: #000;\">").
							append(String.valueOf((int) hago)).append("</div>");
				} else if (hago <= 5) {
					tanksTableData.append("<div class=\"tm\" style=\"background-color: #99CC00; color: #000;\">").
							append(String.valueOf((int) hago)).append("</div>");
				} else if (hago <= 12) {
					tanksTableData.append("<div class=\"tm\" style=\"background-color: #996600;\">").
							append(String.valueOf((int) hago)).append("</div>");
				} else if (hago <= 24) {
					tanksTableData.append("<div class=\"tm\" style=\"background-color: #990000;\">").
							append(String.valueOf((int) hago)).append("</div>");
				} else if (hago <= 99) {
					tanksTableData.append("<div class=\"tm\" style=\"background-color: #330000;\">").
							append(String.valueOf((int) hago)).append("</div>");
				}
				tanksTableData.append("</td>");
			}
			tanksTableData.append("</tr>\n");
		}

		// end tanks

		// now write :)
		try {
			out.write(template.replace("%%%%%%server%%%%%%", server).
					replace("%%%%%%ClanName%%%%%%", nonNull(c.clanName)).
					replace("%%%%%%ClanID%%%%%%", nonNull(c.clanID)).
					replace("%%%%%%ClanTAG%%%%%%", nonNull(c.clanTag)).
					replace("%%%%%%ClanEmblem%%%%%%", nonNull(c.emblemURL)).
					replace("%%%%%%Generated%%%%%%", genformat.format(new Date())).
					replace("%%%%%%OwnerID%%%%%%", nonNull(c.ownerID)).
					replace("%%%%%%OwnerName%%%%%%", nonNull(c.ownerName)).
					replace("%%%%%%ClanCreated%%%%%%", c.created == null ? "?" : crformat.format(c.created)).
					replace("%%%%%%Members%%%%%%", String.valueOf(c.numPlayers)).
					replace("%%%%%%Provinces%%%%%%", String.valueOf(c.provinces.size())).
					replace("%%%%%%Income%%%%%%", String.valueOf(c.clanIncome)).
					replace("%%%%%%TierTableData%%%%%%", tierTableData.toString()).
					replace("%%%%%%TanksTableData%%%%%%", tanksTableData.toString()).
					replace("%%%%%%PlayerTanksTableData%%%%%%", playerTanksTableData.toString()).
					replace("%%%%%%Active1%%%%%%", String.valueOf(lastH)).
					replace("%%%%%%Active2%%%%%%", String.valueOf(last2)).
					replace("%%%%%%Active24%%%%%%", String.valueOf(last24)));
		} catch (IOException ex) {
			Logger.getLogger(OutputHTML.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			if (out != null) {
				try {
					out.close();
					outStream.close();
				} catch (IOException ex) {
				}
			}
		}
	}

	static String nonNull(String v) {
		return v == null ? "" : v;
	}
//	public static void main(String[] args) throws Exception {
//		GetClan c = new GetClan("MAYHM");
//		c.clanName = "Stalkers of Mayhem";
//		c.created = new Date("Dec 03 2011");
//		c.numPlayers = 90;
//		c.clanTag = "MAYHM";
//		c.ownerName = "StalkerofMayhem";
//		c.ownerID = "1001454066";
//		c.clanID = "1000002537";
//		c.emblemURL = "http://worldoftanks.com/dcont/clans/emblems/1000002537/emblem_64x64.png";
//		c.maxTiers = new int[]{90, 89, 88, 88, 85, 80, 63, 55, 23, 10};
//		c.totalTiers = new int[]{310, 903, 827, 657, 524, 332, 190, 152, 41, 14};
//
//		PlayerInfo p = new PlayerInfo();
//		p.playerID = c.ownerID;
//		p.playername = "StalkerofMayhem";
//		p.loadFromWeb("com");
//		
//		c.players.add(p);
//
//		File dir = new File("saves");
//		dir.mkdirs();
//		File f = new File(dir, c.clanName + " [" + c.clanTag + "].html");
//		System.out.println("writing to file: " + f.getPath());
//
//		writeFile(c, f);
//	}
//	public static void main(String[] args) {
//		loadRes();
//		System.out.println("css: " + (css != null && !css.isEmpty()));
//		System.out.println("js: " + (js != null && !js.isEmpty()));
//		if(css != null && !css.isEmpty() && js != null && !js.isEmpty()) {
//		System.out.println("css: " + css.substring(0, 100));
//		System.out.println("js: " + js.substring(0, 100));
//		}
//	}
}
