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

	public static void main(String[] args) {
		if (args.length == 1) {

			c = new GetTournamentTeam("75655", "StalkersofMayhem");
			c.callback = m;
			c.isFound = true;
			c.max_threads = 5;

			c.run();

			if (!c.isFound) {
				System.out.println("not found: " + c.requestData);
				return;
			} else {
				System.out.println("found: " + c.requestData);
				System.out.println("name: " + c.clanName);
				System.out.println("last modified: " + c.created);
				System.out.println("members: " + c.numPlayers);
				System.out.println("owner: " + c.ownerID + "  (" + c.ownerName + ")");
				System.out.println("ID: " + c.clanID);

//				System.out.println("Players: ");
//				for(PlayerInfo p : c.players) {
//					System.out.println(p.playername);
//				}
			}
			st = System.currentTimeMillis();
			c.lookupPlayers();
			return;
		}
		// clan stats
//		c = new GetClan("StalkersofMayhem");
//		c.clanName = "StalkersofMayhem Season 1";
//
//		for (String pn : new String[]{"Borgle", "bragi365", 
//			"Chaos7_s", "Dinotank", "Foobis", "Icansee", 
//			"jascotty2", "johnnyybravo", "johnt69", "Juker008", 
//			"l77ogan", "MaxShadow", "MissBehaving", "MLVDVIP", 
//			"MortalEnigma", "MrWar123", "Oxmathus", "Scotty_123", 
//			"StalkerofMayhem", "white91mustang"}) {
//			PlayerInfo p = new PlayerInfo();
//			p.loadStats(pn, "com");
//			if (p.playerID != null) {
//				c.players.add(p);
//			}
//		}
//		

		st = System.currentTimeMillis();

		if (args.length > 0) {
			clan = Str.concatStr(args, " ");
			st = System.currentTimeMillis();
			c = new GetClan(clan);
			c.callback = m;
			c.run();
			if (!c.isFound) {
				System.out.println("Error: the clan '" + clan + "' was not found");
				return;
			}
			if (args.length > 1) {
				// second arg being save dir
				saveDir = args[1];
			}
			if (args.length > 2) {
				// third is if to debug text and open browser
				deamonMode = CheckInput.GetBoolean(args[2], false);
			}
		} else {
			clan = JOptionPane.showInputDialog(null, "Clan to Search, or Tournament URL",
					"Clan Stats by Jacob Scott", JOptionPane.QUESTION_MESSAGE);
			if (clan == null || clan.length() == 0) {
				return;
			}
			do {
				st = System.currentTimeMillis();
				c = !clan.toLowerCase().startsWith("http://") ? new GetClan(clan)
						: new GetTournamentTeam(clan, "");//"MAYHM"); //"SNS");//
				if (c instanceof GetTournamentTeam) {
					if (!((GetTournamentTeam) c).findEventID()) {
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
							+ "Clan to Search?</html>", "Clan Stats by Jacob Scott", JOptionPane.QUESTION_MESSAGE);
					if (clan == null || clan.length() == 0) {
						break;
					}
					st = System.currentTimeMillis();
					c.searchTag = clan;
					c.run();
				}
			} while (!c.isFound);
		}
		c.callback = m;
		c.lookupPlayers();
	}

	@Override
	public void ScanDone() {

		try {
			if (!deamonMode) {
				long end = System.currentTimeMillis();


				System.out.println("results: (completed " + (c.numPlayers + 2) + " queries in " + ((end - st) / 1000) + " seconds)");
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
			} else {
				c.sortPlayersByTankAndRating();
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
			File dir = new File(saveDir);
			if (!saveDir.isEmpty() && !saveDir.equals(".")) {
				dir.mkdirs();
			}
			if (c instanceof GetTournamentTeam) {
				dir = new File(dir, "teams" + File.separator + ((GetTournamentTeam) c).eventID
						+ (((GetTournamentTeam) c).eventName.isEmpty() ? "" : "-" + ((GetTournamentTeam) c).eventName));
				dir.mkdirs();
			}
			File f = new File(dir, c.clanName + " [" + c.clanTag + "].html");
			System.out.println("writing to file: " + f.getPath());
			OutputHTML.writeFile(c, f, !deamonMode);
			if (!deamonMode) {
				System.out.println("opening in default browser..");
				Desktop.getDesktop().browse(f.toURI());
			}
		} catch (Exception e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage(), e);
			JOptionPane.showMessageDialog(null, "Unexpected Error: " + e.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		}
		printDone = true;
	}
}
