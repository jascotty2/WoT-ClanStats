/**
 * Copyright (C) 2012 Jacob Scott <jascottytechie@gmail.com>
 * Description: Provides methods for getting information about clans or tournament teams
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

import java.awt.Desktop;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import me.jascotty2.lib.io.CheckInput;
import me.jascotty2.lib.util.Str;

public class Main implements GetClan.ScanCallback {

	static Main m = new Main();
	static long st;
	static String clan = null;
	static GetClan c = null;
	static boolean printDone = false, deamonMode = false;
	static String saveDir = "saves";
	static String tournSaveDir = "";
	static String fileName = "%c[%a].html";

	public static void main(String[] args) {
//		if (args.length == 0) {
//			main(new String[]{"stalkers", "saves", "true", "%c.dat", "http://worldoftanks.com/uc/tournaments/36-Halbe_Post_meridiem_Challenge/"});
//			return;
//		}

		st = System.currentTimeMillis();

		if (args.length > 0) {
			clan = args[0];
			if (args.length > 1) {
				// second arg being save dir
				tournSaveDir = saveDir = args[1];
			}
			if (args.length > 2) {
				// third is if to debug text and open browser
				deamonMode = CheckInput.GetBoolean(args[2], false);
			}
			if (args.length > 3) {
				// fourth is the output filename (if ends in .dat, outputs all data)
				fileName = args[3];
			}
			st = System.currentTimeMillis();
			if (args.length > 4) {
				// 5th is fot tournament URL, if applicable
				c = new GetTournamentTeam(args[4], clan);
			} else {
				c = new GetClan(clan);
			}
			c.callback = m;
			c.run();
			if (!c.isFound) {
				System.out.println("Error: the clan '" + clan + "' was not found");
				return;
			}
		} else {
			do {
				clan = JOptionPane.showInputDialog(null, "Clan to Search, or Tournament URL",
						"Clan Stats by Jacob Scott", JOptionPane.QUESTION_MESSAGE);
				if (clan == null || clan.length() == 0) {
					return;
				}
				st = System.currentTimeMillis();
				c = !clan.toLowerCase().startsWith("http://") ? new GetClan(clan)
						: new GetTournamentTeam(clan, "");//"MAYHM"); //"SNS");//
				if (c instanceof GetTournamentTeam) {
					if (((GetTournamentTeam) c).eventID.isEmpty()) {
						clan = JOptionPane.showInputDialog(null,
								"<html><span style='font-weight:bold;color:red;'>Tournament '"
								+ "<span style='font-weight:bold;color:blue;'>" + clan + "</span>'"
								+ " was not found (was that the correct link?)</span><br />"
								+ "Clan to Search, or Tournament URL:</html>", "Clan Stats by Jacob Scott", JOptionPane.QUESTION_MESSAGE);
						if (clan == null || clan.length() == 0) {
							return;
						} else {
							continue;
						}
					} else {
						clan = JOptionPane.showInputDialog(null, "Tournament Team to Search:",
								"Clan Stats by Jacob Scott", JOptionPane.QUESTION_MESSAGE);
						if (clan == null || clan.length() == 0) {
							continue;
						}
						st = System.currentTimeMillis();
						c.searchTag = clan;
					}
				}
				c.run();
				while (!c.isFound) {
					clan = JOptionPane.showInputDialog(null,
							"<html><span style='font-weight:bold;color:red;'>"
							+ (c instanceof GetTournamentTeam ? "Team" : "Clan")
							+ " '<span style='font-weight:bold;color:blue;'>" + clan
							+ "</span>' was not found</span><br />"
							+ (c instanceof GetTournamentTeam ? "Tournament Team" : "Clan")
							+ " to Search?</html>", "Clan Stats by Jacob Scott", JOptionPane.QUESTION_MESSAGE);
					if (clan == null || clan.length() == 0) {
						break;
					}
					st = System.currentTimeMillis();
					c.searchTag = clan;
					c.run();
				}
			} while (!c.isFound);
			if (c instanceof GetTournamentTeam) {
				fileName = "%c.html";
			}
		}
		c.callback = m;
		c.lookupPlayers();
	}

	@Override
	public void ScanDone() {

		try {
			if (!deamonMode) {
				if (c instanceof GetTournamentTeam) {
					((GetTournamentTeam) c).applyTierLimits();
				}
				long end = System.currentTimeMillis();


				System.out.println("results: (scan completed in " + ((end - st) / 1000) + " seconds)");
				System.out.println("");
				System.out.println("name: " + c.clanName);
				System.out.println("tag: " + c.clanTag);
				System.out.println("created: " + c.created);
				System.out.println("members: " + c.numPlayers);
				System.out.println("owner: " + c.ownerID + "  (" + c.ownerName + ")");
				System.out.println("clanNum: " + c.clanID);
				System.out.println("emblem: " + c.emblemURL);

				System.out.println("provinces: " + c.provinces.size());
				System.out.println("income: " + c.clanIncome);

				System.out.println("total by tier: ");
				for (int i = 0; i < 10; ++i) {
					System.out.println((i + 1) + " - " + c.totalTiers[i]);
				}
				System.out.println("max by tier: ");
				for (int i = 0; i < 10; ++i) {
					System.out.println((i + 1) + " - " + c.maxTiers[i]);
				}

				System.out.println("potential lineup: "); // (no tier limits)
				int num = 1;
				c.sortPlayersByTankAndRating();
				//for (int tier = 10; tier > 0; --tier) {
				for (PlayerInfo p : c.players) {
					StringBuilder line = new StringBuilder(Str.padRight(String.valueOf(num++) + ":", 3)
							+ Str.padRight(p.playername, 25));
					line.append("# ").append(Str.padRight(String.valueOf(p.playerRating), 7));
					line.append("* ").append(Str.padRight(String.valueOf(p.totals.battles), 6)).append(" - ");
					int n = 10;
					for (Tank t : p.getSortedTanks()) {
						line.append(Str.padRight(t.toString(), 25)).append(Str.padLeft(t.effectiveTier() + "(" + p.tankBattles.get(t).battles + ")", 6));
						if (--n > 0) {
							line.append(" | ");
						} else {
							break;
						}
					}
					System.out.println(line.toString());
				}
			}

			/*
			// output for php test page
			System.out.println("$players = array(");
			for (PlayerInfo p : c.players) {
			StringBuilder line = (new StringBuilder("\t\"")).append(p.playername).append("\" => array (\n");
			line.append("\t\t\"rating\" => ").append(String.valueOf(p.playerRating)).append(",\n");
			line.append("\t\t\"battles\" => ").append(String.valueOf(p.totals.battles)).append(",\n");
			line.append("\t\t\"tanks\" => array(\n\t\t\t");
			int n  = p.tankBattles.size();
			for (Tank t : p.getSortedTanks()) {
			line.append("\"").append(t.name).append("\" => ").append(t.tier);
			if (--n > 0) {
			line.append(", ");
			} else {
			line.append(")),\n");
			}
			}
			System.out.println(line.toString());
			}
			System.out.println("\t);");
			 */
			File dir;
			File f;
			String fn = fileName.replace("%c", c.clanName == null ? "" : c.clanName).
					replace("%a", c.clanTag == null ? "" : c.clanTag);
			if (c instanceof GetTournamentTeam) {
				if (tournSaveDir.isEmpty()) {
					dir = new File(saveDir + File.separator + "teams"
							+ File.separator + ((GetTournamentTeam) c).tournamentID
							+ (((GetTournamentTeam) c).eventName.isEmpty() ? ""
							: "-" + ((GetTournamentTeam) c).eventName.replace(" ", "_")));
				} else {
					dir = new File(tournSaveDir.replace("%i", ((GetTournamentTeam) c).tournamentID).
							replace("%n", ((GetTournamentTeam) c).eventName.replace(" ", "_")));
				}
				dir.mkdirs();
				f = new File(dir, fn);
			} else {
				dir = new File(saveDir);
				dir.mkdirs();
				f = new File(dir, fn);
			}
			
			if (c instanceof GetTournamentTeam) {
				if (tournSaveDir.isEmpty()) {
					dir = new File(dir, "teams" + File.separator + ((GetTournamentTeam) c).tournamentID
							+ (((GetTournamentTeam) c).eventName.isEmpty() ? "" : "-" + ((GetTournamentTeam) c).eventName.replace(" ", "_")));
				} else {
					dir = new File(dir, "teams" + File.separator + ((GetTournamentTeam) c).tournamentID
							+ (((GetTournamentTeam) c).eventName.isEmpty() ? "" : "-" + ((GetTournamentTeam) c).eventName.replace(" ", "_")));
				}
				dir.mkdirs();
				f = new File(dir, fn);
			} else {
				f = new File(dir, fn);
			}
			if (!deamonMode) {
				System.out.println("writing to file: " + f.getPath());
			}
			if (fn.toLowerCase().endsWith(".htm") || fn.toLowerCase().endsWith(".html")) {
				OutputHTML.writeFile(c, f, !deamonMode);
				if (!deamonMode) {
					System.out.println("opening in default browser..");
					Desktop.getDesktop().browse(f.toURI());
				}
			} else {
				OutputDat.writeFile(c, f);
			}
		} catch (Exception e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage(), e);
			JOptionPane.showMessageDialog(null, "Unexpected Error: " + e.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		}
		printDone = true;
	}
}
