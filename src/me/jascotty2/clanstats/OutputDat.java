/**
 * Copyright (C) 2012 Jacob Scott <jascottytechie@gmail.com>
 * Description: Output information in a format for other programs to read
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import me.jascotty2.lib.io.CheckInput;
import me.jascotty2.lib.util.Str;

public class OutputDat {

	public static void writeFile(GetClan c, File f) throws Exception {
		BufferedWriter out;
		FileWriter outStream;
		try {
			outStream = new FileWriter(f);
			out = new BufferedWriter(outStream);
		} catch (Exception ex) {
			Logger.getLogger(OutputHTML.class.getName()).log(Level.SEVERE, null, ex);
			JOptionPane.showMessageDialog(null, "Error Opening output file: " + ex.getMessage(),
					"IO Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			out.write("tag: " + OutputHTML.nonNull(c.clanTag) + "\n");
			out.write("name: " + c.clanName + "\n");
			out.write("generated: " + c.generatedTime + "\n");
			out.write("# (" + (new Date()) + ")\n");
			out.write("created: " + c.created.getTime() + "\n");
			out.write("# (" + c.created + ")\n");
			out.write("members: " + c.numPlayers + "\n");
			out.write("owner: " + c.ownerID + "\n#  (" + c.ownerName + ")\n");
			out.write("clanNum: " + c.clanID + "\n");
			out.write("emblem: " + c.emblemURL + "\n");
			if (c instanceof GetTournamentTeam) {
				out.write("maxTier:\n");
				out.write("\ttotal: " + ((GetTournamentTeam) c).max + "\n");
				out.write("\tHT: " + ((GetTournamentTeam) c).maxHT + "\n");
				out.write("\tMT: " + ((GetTournamentTeam) c).maxMT + "\n");
				out.write("\tLT: " + ((GetTournamentTeam) c).maxLT + "\n");
				out.write("\tTD: " + ((GetTournamentTeam) c).maxTD + "\n");
				out.write("\tSPG: " + ((GetTournamentTeam) c).maxSPG + "\n");
			}
			out.write("total provinces: " + c.provinces.size() + "\n");
			out.write("total income: " + c.clanIncome + "\n");

			for (ProvinceInfo p : c.provinces) {
				out.write("province: " + p.id + "\n");
				out.write("\tID: " + p.id + "\n");
				out.write("\tname: " + p.name + "\n");
				out.write("\tmap: " + p.map + "\n");
				out.write("\tmapID: " + p.mapID + "\n");
				out.write("\tincome: " + p.revenue + "\n");
				out.write("\tbattle time: " + p.battleTime + "\n");
				out.write("\toccupancy: " + p.occupancy + "\n");
				if (p.isCapital) {
					out.write("\tcapital: " + p.isCapital + "\n");
				}
			}
			out.write("total by tier: \n");
			for (int i = 0; i < 10; ++i) {
				out.write("\t" + (i + 1) + ": " + c.totalTiers[i] + "\n");
			}
			out.write("max by tier: \n");
			for (int i = 0; i < 10; ++i) {
				out.write("\t" + (i + 1) + ": " + c.maxTiers[i] + "\n");
			}

			c.sortPlayersByTankAndRating();

			out.write("players:\n");

			for (PlayerInfo p : c.players) {
				out.write("\t" + p.playername + ":\n");
				out.write("\t\tID: " + p.playerID + "\n");
				if (p.is_banned) {
					out.write("\t\tBanned from Clan\n");
				}
				if (p.created == null) {
					out.write("\t\tAccount is Closed\n");
				} else {
					out.write("\t\tStarted: " + p.created.getTime() + "\n");
					out.write("\t\t# (" + p.created + ")\n");
					if (p.position != null && !p.position.isEmpty()) {
						out.write("\t\tPosition: " + p.position + "\n");
						out.write("\t\tJoined: " + p.memberSince.getTime() + "\n");
						out.write("\t\t# (" + p.memberSince + ")\n");
					}
					out.write("\t\tLast Battle: " + (p.lastbattle == null ? "0" : p.lastbattle.getTime()) + "\n");
					out.write("\t\t# (" + (p.lastbattle == null ? "unknown" : p.lastbattle) + ")\n");
					out.write("\t\tBattle Results:\n");
					out.write("\t\t\tBattles: " + p.totals.battles + "\n");
					out.write("\t\t\tVictories: " + p.totals.victories + "\n");
					out.write("\t\t\tDefeats: " + p.totals.losses + "\n");
					out.write("\t\t\tSurvived: " + p.totals.survived + "\n");
					out.write("\t\t\tDestroyed: " + p.totals.destroyed + "\n");
					out.write("\t\t\tDetected: " + p.totals.spotted + "\n");
					out.write("\t\t\tHit Ratio: " + p.totals.hitRatio + "\n");
					out.write("\t\t\tDamage: " + p.totals.damage + "\n");
					out.write("\t\t\tCaptured: " + p.totals.captured + "\n");
					out.write("\t\t\tDefense: " + p.totals.defense + "\n");
					out.write("\t\t\tTotal Exp: " + p.totals.totalExp + "\n");
					out.write("\t\t\tAvg Exp: " + p.totals.avgExp + "\n");
					out.write("\t\t\tMax Exp: " + p.totals.maxExp + "\n");
					out.write("\t\tRating: \n");
					out.write("\t\t\tGR: " + p.playerRating + "\n");
					out.write("\t\t\tW/B: " + p.ratingStats.hitRatio + "\n");
					out.write("\t\t\tE/B: " + p.ratingStats.avgExp + "\n");
					out.write("\t\t\tWIN: " + p.ratingStats.victories + "\n");
					out.write("\t\t\tGPL: " + p.ratingStats.battles + "\n");
					out.write("\t\t\tCPT: " + p.ratingStats.captured + "\n");
					out.write("\t\t\tDMG: " + p.ratingStats.damage + "\n");
					out.write("\t\t\tDPT: " + p.ratingStats.defense + "\n");
					out.write("\t\t\tFRG: " + p.ratingStats.destroyed + "\n");
					out.write("\t\t\tSPT: " + p.ratingStats.spotted + "\n");
					out.write("\t\t\tEXP: " + p.ratingStats.totalExp + "\n");
					out.write("\t\tVehicles: \n");
					for (Tank t : p.getSortedTanks()) {
						out.write("\t\t\t" + t.name + ": "
								+ p.tankBattles.get(t).victories + "/" + p.tankBattles.get(t).battles + "\n");
					}
				}
			}

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

	private static boolean isTournamentSave(File f) {
		BufferedReader in;
		FileReader inStream;
		try {
			inStream = new FileReader(f);
			in = new BufferedReader(inStream);

			String line;
			while ((line = in.readLine()) != null) {
				if (line.equals("maxTier:")) {
					return true;
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(OutputHTML.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	public static GetClan loadFile(File f) throws Exception {
		boolean tourn = isTournamentSave(f);
		BufferedReader in;
		FileReader inStream;
		try {
			inStream = new FileReader(f);
			in = new BufferedReader(inStream);
		} catch (Exception ex) {
			Logger.getLogger(OutputHTML.class.getName()).log(Level.SEVERE, null, ex);
			JOptionPane.showMessageDialog(null, "Error Opening output file: " + ex.getMessage(),
					"IO Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		GetClan c = tourn ? new GetTournamentTeam("", "") : new GetClan("");
		String line, lastk = null;
		while ((line = in.readLine()) != null) {
			if (line.trim().startsWith("#")) {
				continue;
			}
			while(line.startsWith("\t")) {
				line = loadElement(c, lastk, line, 1, in);
				if(line == null || line.trim().startsWith("#")) {
					break;
				}
			}
			if (line == null || line.trim().startsWith("#")) {
				continue;
			}
			if (line.contains(":")) {
				String k = line.substring(0, line.indexOf(":")).trim();
				String v = line.substring(line.indexOf(":") + 1).trim();
				if (k.equals("tag")) {
					c.clanTag = v;
				} else if (k.equals("name")) {
					c.clanName = v;
				} else if (k.equals("generated")) {
					c.generatedTime = CheckInput.GetLong(v, 0);
				} else if (k.equals("created")) {
					c.created = new Date(CheckInput.GetLong(v, 0));
				} else if (k.equals("members")) {
					c.numPlayers = CheckInput.GetInt(v, 0);
				} else if (k.equals("owner")) {
					c.ownerID = v;
				} else if (k.equals("clanNum")) {
					c.clanID = v;
				} else if (k.equals("emblem")) {
					c.emblemURL = v;
				} else if (k.equals("total income")) {
					c.clanIncome = CheckInput.GetInt(v, 0);
				}
				lastk = k;
			}
		}
		for(int i = 0; i < 10; ++i) {
			c.totalTiers[i] = c.maxTiers[i] = 0;
		}
		
		for (PlayerInfo p : c.players) {
			for (int i = 0; i < 10; ++i) {
				if (p.tanksByTier[0][i] > 0) {
					c.totalTiers[i] += p.tanksByTier[0][i];
					++c.maxTiers[i];
				}
			}
			if (p.playerID.equals(c.ownerID)) {
				c.ownerName = p.playername;
			}
		}

		return c;
	}

	private static String loadElement(GetClan c, String elem, String lastLine, int depth, BufferedReader in) throws Exception {
		String line = lastLine;
		boolean skipRead = true;
		while (line != null && (skipRead || (line = in.readLine()) != null)) {
			skipRead = false;
			if (line.trim().startsWith("#")) {
				continue;
			}
			if (Str.count(line, "\t") < depth) {
				return line;
			} else if (Str.count(line, "\t") > depth) {
				// todo?
				//loadElement(c, lastk, 1, in);
				return line;
			}
			if (line.contains(":")) {
				String k = line.substring(0, line.indexOf(":")).trim();
				String v = line.substring(line.indexOf(":") + 1).trim();

				if (elem.equals("total by tier")) {
					int t = CheckInput.GetInt(k, 0);
					if (t > 0 && t <= c.totalTiers.length) {
						c.totalTiers[t - 1] = CheckInput.GetInt(v, 0);
					}
				} else if (elem.equals("max by tier")) {
					int t = CheckInput.GetInt(k, 0);
					if (t > 0 && t <= c.maxTiers.length) {
						c.maxTiers[t - 1] = CheckInput.GetInt(v, 0);
					}
				} else if (elem.equals("players")) {
					PlayerInfo newPlayer = new PlayerInfo();
					newPlayer.clan = c.clanTag;
					newPlayer.playername = k;
					line = loadPlayer(newPlayer, depth + 1, in);
					c.players.add(newPlayer);
					skipRead = true;
				}
			}
		}
		return line;
	}
	
	private static String loadPlayer(PlayerInfo pl, int depth, BufferedReader in) throws Exception {
		String line = null, lastk = null;
		while ((line = in.readLine()) != null) {
			if (line.trim().startsWith("#")) {
				continue;
			}
			if (Str.count(line, "\t") < depth) {
				return line;
			} else if (Str.count(line, "\t") > depth) {
				String k = line.substring(0, line.indexOf(":")).trim();
				String v = line.substring(line.indexOf(":") + 1).trim();
				if(lastk.equals("Battle Results")) {
					if(k.equals("Battles")) {
						pl.totals.battles = CheckInput.GetInt(v, 0);
					} else if(k.equals("Victories")) {
						pl.totals.victories = CheckInput.GetInt(v, 0);
					} else if(k.equals("Defeats")) {
						pl.totals.losses = CheckInput.GetInt(v, 0);
					} else if(k.equals("Survived")) {
						pl.totals.survived = CheckInput.GetInt(v, 0);
					} else if(k.equals("Destroyed")) {
						pl.totals.destroyed = CheckInput.GetInt(v, 0);
					} else if(k.equals("Detected")) {
						pl.totals.spotted = CheckInput.GetInt(v, 0);
					} else if(k.equals("Hit Ratio")) {
						pl.totals.hitRatio = CheckInput.GetInt(v, 0);
					} else if(k.equals("Damage")) {
						pl.totals.damage = CheckInput.GetInt(v, 0);
					} else if(k.equals("Captured")) {
						pl.totals.captured = CheckInput.GetInt(v, 0);
					} else if(k.equals("Defense")) {
						pl.totals.defense = CheckInput.GetInt(v, 0);
					} else if(k.equals("Total Exp")) {
						pl.totals.totalExp = CheckInput.GetInt(v, 0);
					} else if(k.equals("Avg Exp")) {
						pl.totals.avgExp = CheckInput.GetInt(v, 0);
					} else if(k.equals("Max Exp")) {
						pl.totals.maxExp = CheckInput.GetInt(v, 0);
					}
				} else if (lastk.equals("Rating")) {
					if(k.equals("GR")) {
						pl.playerRating = CheckInput.GetInt(v, 0);
					} else if(k.equals("W/B")) {
						pl.ratingStats.hitRatio = CheckInput.GetInt(v, 0);
					} else if(k.equals("E/B")) {
						pl.ratingStats.avgExp = CheckInput.GetInt(v, 0);
					} else if(k.equals("WIN")) {
						pl.ratingStats.victories = CheckInput.GetInt(v, 0);
					} else if(k.equals("GPL")) {
						pl.ratingStats.battles = CheckInput.GetInt(v, 0);
					} else if(k.equals("CPT")) {
						pl.ratingStats.captured = CheckInput.GetInt(v, 0);
					} else if(k.equals("DMG")) {
						pl.ratingStats.damage = CheckInput.GetInt(v, 0);
					} else if(k.equals("DPT")) {
						pl.ratingStats.defense = CheckInput.GetInt(v, 0);
					} else if(k.equals("FRG")) {
						pl.ratingStats.destroyed = CheckInput.GetInt(v, 0);
					} else if(k.equals("SPT")) {
						pl.ratingStats.spotted = CheckInput.GetInt(v, 0);
					} else if(k.equals("EXP")) {
						pl.ratingStats.totalExp = CheckInput.GetInt(v, 0);
					}
				} else if (lastk.equals("Vehicles")) {
					Tank t = new Tank(0, k);
					int w = CheckInput.GetInt(v.substring(0, v.indexOf("/")), 0);
					int b = CheckInput.GetInt(v.substring(v.indexOf("/") + 1), 0);
					pl.addTank(t, b, w);
				}
				
			} else if (line.contains(":")) {
				String k = line.substring(0, line.indexOf(":")).trim();
				String v = line.substring(line.indexOf(":") + 1).trim();
				if(k.equals("ID")) {
					pl.playerID = v;
				} else if(k.equals("Started")) {
					pl.created = new Date(CheckInput.GetLong(v, 0));
				} else if(k.equals("Joined")) {
					pl.memberSince = new Date(CheckInput.GetLong(v, 0));
				} else if(k.equals("Last Battle")) {
					pl.lastbattle = new Date(CheckInput.GetLong(v, 0));
				} else if(k.equals("Position")) {
					pl.position = v;
				}
				lastk = k;
			}
		}
		return line;
	}
}
