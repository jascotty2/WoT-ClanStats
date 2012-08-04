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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import me.jascotty2.lib.util.ArrayManip;

public class OutputHTML {

	static String template = null;
	static SimpleDateFormat crformat = new SimpleDateFormat("MMM dd yyy");
	static SimpleDateFormat genformat = new SimpleDateFormat("EE, MMM dd yyy  HH:mm zzz");
	static SimpleDateFormat lastformat = new SimpleDateFormat("MM/dd/yyy HH:mm");

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
//		loadRes(f.getParent());
		loadRes(f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(File.separator)));
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

		// prepare data
		String server = "http://worldoftanks." + c.server;
		
		// total tanks by tier
		StringBuilder tierTableData[] = new StringBuilder[7];
		for (int j = 0; j < 7; ++j) {
			tierTableData[j] = new StringBuilder("\n");
			for (int i = 9; i > 0; --i) {
				int max = 0, total = 0;
				int last1 = 0, last2 = 0, last24 = 0, num = 0;
				for (PlayerInfo p : c.players) {
					total += p.tanksByTier[j][i];
					if (p.tanksByTier[j][i] > 0) {
						++max;
					}
					if (p.maxTier[j] == i + 1) {
						++num;
						float hago = ((c.generatedTime - p.lastbattle.getTime()) / 3600000);
						if (hago <= 24) {
							if (hago <= 1) {
								++last1;
							}
							if (hago <= 2) {
								++last2;
							}
							++last24;
						}
					}
				}
				if(j + 1 < 7) {
				tierTableData[j].append("<tr title=\"header=[Active Tier ").
						append(i + 1).
						append("'s:]body=[").
						append(last1).append(" / ").append(last2).append(" / ").append(last24).
						append("]\"><td>").
						append(String.valueOf(i + 1)).
						append("</td><td>").
						append(num).
						append("</td><td>").
						append(String.valueOf(max)).
						append("</td><td>").
						append(String.valueOf(total)).
						append("</td>\n");
				} else {
					tierTableData[j].append("<tr title=\"header=[Active ");
					if(i == 9) tierTableData[j].append("Top Tier: ");
					else tierTableData[j].append("Max - ").append(String.valueOf(9-i)).append(": ");
					tierTableData[j].append(i + 1).
						append("]body=[").
						append(last1).append(" / ").append(last2).append(" / ").append(last24).
						append("]\"><td>");
					if(i == 9) tierTableData[j].append("Max");
					else tierTableData[j].append("-").append(String.valueOf(9-i));
					tierTableData[j].append("</td><td>").
						append(num).
						append("</td><td>").
						append(String.valueOf(max)).
						append("</td><td>").
						append(String.valueOf(total)).
						append("</td>\n");
				}
			}
		}
		// end total tanks by tier
		
		// player lineup
		c.sortPlayersByTankAndRating();
		
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
			float hago = p.lastbattle == null ? Float.MAX_VALUE : ((c.generatedTime - p.lastbattle.getTime()) / 3600000);
			if (hago <= 24) {
				++last24;
			}
			playerTanksTableData.append("<tr");
			if (hago <= 2) {
				++last2;
				playerTanksTableData.append(" class=\"online\"");
			}
			if (hago <= 1) {
				++lastH;
			}
			playerTanksTableData.append(">").append(getPlayerNameCell(p, server, hago));

			//int n = 10;
			for (Tank t : p.getSortedTanks()) {
				// ö = \u00F6 = &#246;
				// ä = \u00E4 = &#228;
				int v = ArrayManip.indexOf(PlayerInfo.typeOrder, t.type);
				if (v > 0 && t.tier == p.maxTier[v]) {
					playerTanksTableData.append(getTankCell(t, p.tankBattles.get(t).battles));
	//				if (--n <= 0) {
	//					break;
	//				}
				}
			}
			playerTanksTableData.append("</tr>\n");
		}

		// end player lineup

		// start tank table

		StringBuilder tanksTableData = new StringBuilder("\n");
		//for (final Map.Entry<Tank, ArrayList<PlayerInfo>> v : tankPlayers.entrySet()) {

		Tank[] tanks = tankPlayers.keySet().toArray(new Tank[0]);
		Arrays.sort(tanks, new Comparator<Tank>() {

			@Override
			public int compare(Tank o1, Tank o2) {
				return o1.effectiveTier() != o2.effectiveTier()
						? o2.effectiveTier() - o1.effectiveTier()
						: o1.type.ordinal() - o2.type.ordinal();
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
			for (int i = 0; i < 10 && i < players.length; ++i) { //PlayerInfo p : tankPlayers.get(t)) {
				PlayerInfo p = players[i];
				float hago = p.lastbattle == null ? Float.MAX_VALUE : ((c.generatedTime - p.lastbattle.getTime()) / 3600000);
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
					append("]  body=[").
					append(t.name.replace("\u00F6", "&#246;").replace("\u00E4", "&#228;")).
					append("<br>").
					append(hr2).append(" / ").append(hr24).append(" / ").append(tankPlayers.get(t).size()).
					append("]\"><div>").
					append(t.name.replace("\u00F6", "&#246;").replace("\u00E4", "&#228;")).
					append("</div></td>");

			for (int i = 0; i < 10 && i < players.length; ++i) {
				PlayerInfo p = players[i];
				float hago = p.lastbattle == null ? Float.MAX_VALUE : ((c.generatedTime - p.lastbattle.getTime()) / 3600000);
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
							append("<br>Battles: ").append(String.valueOf(p.tankBattles.get(t).battles)).
							append("<br>Wins: ").append(String.valueOf(p.tankBattles.get(t).victories)).
							append(" (").append((int) Math.round(((double) p.tankBattles.get(t).victories / p.tankBattles.get(t).battles) * 100)).append("%)").
							// not on the public page per-tank
							//append("<br>Hit Ratio: ").append(String.valueOf(p.tankBattles.get(t).hitRatio)).append("%)").
							append("<br>Last Battle: ").append(p.lastbattle == null ? "- ? -" : lastformat.format(p.lastbattle));
				}
				tanksTableData.append("]\">").append("<a href=\"").append(server).append("/community/accounts/").
						append(p.playerID).append("/\">");
				if (p.is_banned) {
					tanksTableData.append("<del>").append(p.playername).append("</del>");
				} else {
					tanksTableData.append(p.playername);
				}
				tanksTableData.append("</a>").append(getHourBox((int) hago)).append("</td>");
			}
			tanksTableData.append("</tr>\n");
		}

		// end tank table

		// start best guess

		StringBuilder bestGuess[] = {new StringBuilder("\n"), new StringBuilder("\n"), new StringBuilder("\n")};
		int maxHours[] = {2, 24, Integer.MAX_VALUE};
		for (int i = 0; i < 3; ++i) {
			// build list of players that may show up
//			System.out.println("run " + i + " (" + maxHours[i] + " hours)");
			ArrayList<PlayerInfo> players;
			if (maxHours[i] < Integer.MAX_VALUE) {
				players = new ArrayList<PlayerInfo>();
				for (PlayerInfo p : c.players) {
					float hago = p.lastbattle == null ? Float.MAX_VALUE : ((c.generatedTime - p.lastbattle.getTime()) / 3600000);
					if (hago < maxHours[i]) {
						players.add(p);
					}
				}
			} else {
				players = c.players;
			}
			// list of tanks type players
			ArrayList<PlayerInfo> specialPlayers[] = new ArrayList[5];
			// what tank type is stored in each index (matching PlayerInfo.typeOrder)
			int tankIndicies[] = new int[]{5, 2, 4, 3, 1};
			/*
			// first order of business: determine # of arty (2 is optimal, 3 is best)
			// then compile simmilar list of mediums, TD, and lights (in that order) 
			int count = 0;
			for (int run = 0; run <= 1; ++run) {
				for (int ti = 0; ti < tankIndicies.length; ++ti) {
					specialPlayers[ti] = new ArrayList<PlayerInfo>();
					for (int tier = 9; tier > 0; --tier) {
						for (PlayerInfo p : players) {
							if (!(p.is_banned || p.created == null) && p.tanksByTier[tankIndicies[ti]][tier] > 0) {
								for (int pi = ti; pi >= 0; --pi) {
									if (specialPlayers[pi].contains(p)) {
										break;
									} else if (pi == 0) {
										++count;
										specialPlayers[ti].add(p);
										System.out.println("tier " + (tier + 1) + " "
												+ PlayerInfo.typeOrder[tankIndicies[ti]].getPropername()
												+ ": " + p.playername);
										int bestI = 0, bestN = p.battlesByType[0];
										for (int j = 0; j < 6; ++j) {
											if (p.battlesByType[j] > bestN) {
												bestI = j;
												bestN = p.battlesByType[j];
											}
										}
										System.out.println("(best at: " + TankType.values()[bestI].getPropername() + " (" + bestN + " battles))");
									}
								}
							}
							if (run == 0 && (ti != tankIndicies.length - 1
									? specialPlayers[ti].size() > 6 : specialPlayers[ti].size() > 20)) {
								break;
							}
						}
					}
				}
				if (count >= 25 || c.players.size() == count) {
					break;
				}
			}
			// todo: now examine the list
			// eg. some players better (more battles) in another class, eg 4 in tier 6 arty, but 200 in tier 10)
			// also, if low on tier 10 ht, some in other lists may instead opt for heavies 
			//		(lower-tier arty isn't as bad as lower-tier arty)
			 */
			// search through tanks, find what tank type is best at vs highest tier tank
			for(int j = 0; j < specialPlayers.length; ++j) {
				specialPlayers[j] = new ArrayList<PlayerInfo>();
			}
			for (PlayerInfo p : players) {
				int bestI = 0, bestN = p.battlesByType[0];
				for (int j = 0; j < 6; ++j) {
					if (p.battlesByType[j] > bestN) {
						bestI = j;
						bestN = p.battlesByType[j];
					}
				}
				if(p.maxEffectiveTier[ArrayManip.indexOf(PlayerInfo.typeOrder, TankType.values()[bestI])] 
						>= p.maxEffectiveTier[0] - 1) {
					specialPlayers[ArrayManip.indexOf(tankIndicies, ArrayManip.indexOf(PlayerInfo.typeOrder, TankType.values()[bestI]))].add(p);
				} else { 
					// best tank up front
					for (int j = 0; j < 5; ++j) {
						if (p.maxEffectiveTier[0] == p.maxEffectiveTier[j + 1]) {
							specialPlayers[ArrayManip.indexOf(tankIndicies, TankType.values()[j].ordinal() + 1)].add(p);
							break;
						}
					}
				}
			}
			// list in 'normal' order
			for (int ind = 0; ind < 5; ++ind) {
				int sp = ArrayManip.indexOf(tankIndicies, ind + 1);
				for (PlayerInfo p : specialPlayers[sp].subList(0, 
						ind == 0 ? (specialPlayers[sp].size() > 15 ? 15 : specialPlayers[sp].size()) 
						: (specialPlayers[sp].size() > 4 ? 4 : specialPlayers[sp].size()))) {
					float hago = p.lastbattle == null ? Float.MAX_VALUE : ((c.generatedTime - p.lastbattle.getTime()) / 3600000);
					bestGuess[i].append("<tr");
					if (hago <= 2) {
						bestGuess[i].append(" class=\"online\"");
					}
					bestGuess[i].append(">");
					bestGuess[i].append(getPlayerNameCell(p, server, hago));


					int n = 4, tier = 0;
					for (Tank t : p.getSortedTanks()) {
						// first sort out the tanks that match tier & type that was added as
						if (t.type == PlayerInfo.typeOrder[ind + 1] && (tier == 0 || t.tier == tier)) {
							tier = t.tier;
//							System.out.println(p.playername + ": tier " + t.tier + " " + PlayerInfo.typeOrder[ind + 1].getPropername() + " - " + t.name);
							bestGuess[i].append(getTankCell(t, p.tankBattles.get(t).battles));
							if (--n <= 0) {
								break;
							}
						}
					}
					if (n > 0) {
						// now fill rest with other tanks
						// only show top-tier tanks
						boolean used[] = new boolean[]{false, false, false, false, false, false};
						for (Tank t : p.getSortedTanks()) {
							if (t.type != PlayerInfo.typeOrder[ind + 1]
									&& !used[t.type.ordinal()]) {
								if (p.maxEffectiveTier[0] <= t.effectiveTier() + 3) {
									bestGuess[i].append(getTankCell(t, p.tankBattles.get(t).battles));
								}
								if (--n <= 0) {
									break;
								}
								used[t.type.ordinal()] = true;
							}
						}
					}
					bestGuess[i].append("</tr>\n");
				}
			}
		}

		// end best guess

		// now write :)
		try {
			out.write(template.replace("%%%%%%server%%%%%%", server).
					replace("%%%%%%ClanName%%%%%%", nonNull(c.clanName)).
					replace("%%%%%%ClanID%%%%%%", nonNull(c.clanID)).
					replace("%%%%%%ClanTAG%%%%%%", nonNull(c.clanTag)).
					replace("%%%%%%ClanEmblem%%%%%%", nonNull(c.emblemURL)).
					replace("%%%%%%Generated%%%%%%", genformat.format(new Date(c.generatedTime))).
					replace("%%%%%%OwnerID%%%%%%", nonNull(c.ownerID)).
					replace("%%%%%%OwnerName%%%%%%", nonNull(c.ownerName)).
					replace("%%%%%%ClanCreated%%%%%%", c.created == null ? "?" : crformat.format(c.created)).
					replace("%%%%%%Members%%%%%%", String.valueOf(c.numPlayers)).
					replace("%%%%%%Provinces%%%%%%", String.valueOf(c.provinces.size())).
					replace("%%%%%%Battles%%%%%%", String.valueOf(c.battles.size())).
					replace("%%%%%%Income%%%%%%", String.valueOf(c.clanIncome)).
					replace("%%%%%%TierTableData_All%%%%%%", tierTableData[0].toString()).
					replace("%%%%%%TierTableData_HT%%%%%%", tierTableData[1].toString()).
					replace("%%%%%%TierTableData_MT%%%%%%", tierTableData[2].toString()).
					replace("%%%%%%TierTableData_LT%%%%%%", tierTableData[3].toString()).
					replace("%%%%%%TierTableData_TD%%%%%%", tierTableData[4].toString()).
					replace("%%%%%%TierTableData_SPG%%%%%%", tierTableData[5].toString()).
					replace("%%%%%%TierTableData_MAX%%%%%%", tierTableData[6].toString()).
					replace("%%%%%%TanksTableData%%%%%%", tanksTableData.toString()).
					replace("%%%%%%PlayerTanksTableData%%%%%%", playerTanksTableData.toString()).
					replace("%%%%%%GuessTanksTable1%%%%%%", bestGuess[0].toString()).
					replace("%%%%%%GuessTanksTable2%%%%%%", bestGuess[1].toString()).
					replace("%%%%%%GuessTanksTable3%%%%%%", bestGuess[2].toString()).
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

	static String getHourBox(int hours) {
		if (hours <= 1) {
			return "<div class=\"tm\" style=\"background-color: #009900;\">" + hours + "</div>";
		} else if (hours <= 2) {
			return "<div class=\"tm\" style=\"background-color: #669900; color: #000;\">" + hours + "</div>";
		} else if (hours <= 5) {
			return "<div class=\"tm\" style=\"background-color: #99CC00; color: #000;\">" + hours + "</div>";
		} else if (hours <= 12) {
			return "<div class=\"tm\" style=\"background-color: #996600;\">" + hours + "</div>";
		} else if (hours <= 24) {
			return "<div class=\"tm\" style=\"background-color: #990000;\">" + hours + "</div>";
		} else if (hours <= 99) {
			return "<div class=\"tm\" style=\"background-color: #330000;\">" + hours + "</div>";
		}
		return "";
	}

	static String getTankCell(Tank t, int battles) {
		// ö = \u00F6 = &#246;
		// ä = \u00E4 = &#228;
		StringBuilder cell = new StringBuilder();
		cell.append("<td");
		if (t.type == TankType.SPG) {
			cell.append(" class=\"spg\"");
		}
		cell.append("><div title=\"header=[").
				append(t.name.replace("\u00F6", "&#246;").replace("\u00E4", "&#228;")).
				append("] body=[(Tier ").
				append(t.tier);
		if (t.type != TankType.UNKNOWN) {
			cell.append(" ").append(t.type.getName());
		}
		cell.append(")<br>").
				append(String.valueOf(battles)).
				append(" battles]\">").
				append(t.name.replace("\u00F6", "&#246;")).
				append("</div></td>");
		return cell.toString();
	}

	static String getPlayerNameCell(PlayerInfo p, String serverExt) {
		return getPlayerNameCell(p, serverExt, p.lastbattle == null ? Float.MAX_VALUE : ((System.currentTimeMillis() - p.lastbattle.getTime()) / 3600000));
	}

	static String getPlayerNameCell(PlayerInfo p, String serverExt, float hago) {
		StringBuilder name = new StringBuilder();
		name.append("<td class=\"n\" ").
				append("title=\"header=[Player Stats: (").
				append(p.playername).
				append(")]  body=[");
		if (p.is_banned) {
			name.append("(Player is Banned)<br>");
		}
		if (p.created == null) {
			name.append("(Closed Account)");
		} else {
			int bestI = 0, bestN = p.battlesByType[0];
			for (int j = 0; j < 6; ++j) {
				if (p.battlesByType[j] > bestN) {
					bestI = j;
					bestN = p.battlesByType[j];
				}
			}
			name.append("GR: ").
					append(String.valueOf(p.playerRating)).
					append("<br>Battles: ").append(String.valueOf(p.totals.battles)).
					append("<br>").append(TankType.values()[bestI].getPropername()).append(": ").append(String.valueOf(bestN)).
					append("<br>Last Battle: ").append(p.lastbattle == null ? "- ? -" : lastformat.format(p.lastbattle));
		}
		name.append("]\">").append("<a href=\"").append(serverExt).append("/community/accounts/").
				append(p.playerID).append("/\">");
		if (p.is_banned) {
			name.append("<del>").append(p.playername).append("</del>");
		} else {
			name.append(p.playername);
		}
		name.append("</a>").append(getHourBox((int) hago)).append("</td>");
		return name.toString();
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
