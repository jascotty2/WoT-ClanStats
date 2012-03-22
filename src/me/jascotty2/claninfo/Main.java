package me.jascotty2.claninfo;

import java.awt.Desktop;
import java.io.File;
import javax.swing.JOptionPane;
import me.jascotty2.lib.util.Str;

public class Main {

	static long st;
	static String clan = null;
	static GetClan c = null;
	static boolean printDone = false;

	public static void main(String[] args) {
		// clan stats

		if (args.length > 0) {
			clan = Str.concatStr(args, " ");
			st = System.currentTimeMillis();
			c = new GetClan(clan);
			c.run();
			if (!c.isFound) {
				System.out.println("Error: the clan '" + clan + "' was not found");
				return;
			}
		} else {
			clan = JOptionPane.showInputDialog(null, "Clan to Search?",
					"Clan Stats by Jacob Scott", JOptionPane.QUESTION_MESSAGE);
			if (clan == null || clan.length() == 0) {
				return;
			}
			st = System.currentTimeMillis();
			c = new GetClan(clan);//"MAYHM"); //"SNS");//
			c.run();
			while (!c.isFound) {
				clan = JOptionPane.showInputDialog(null,
						"<html><span style='font-weight:bold;color:red;'>Clan '"
						+ "<span style='font-weight:bold;color:blue;'>" + clan + "</span>'"
						+ " was not found</span><br />"
						+ "Clan to Search?</html>", "Clan Stats by Jacob Scott", JOptionPane.QUESTION_MESSAGE);
				if (clan == null || clan.length() == 0) {
					return;
				}
				st = System.currentTimeMillis();
				c.searchTag = clan;
				c.run();
			}
		}

		//}

//		PlayerInfo p = new PlayerInfo();
//		p.playername = "jascotty2";
//		p.playerID = "1001630918";
//		try {
//			p.loadFromWeb("com");
//		} catch (MalformedURLException ex) {
//			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//		} catch (IOException ex) {
//			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//		}
//		System.out.println("player: " + p.playername);
//		System.out.println("registered: " + p.created);
//		System.out.println("days: " + ((System.currentTimeMillis() - p.created.getTime()) / 86400000));
//		System.out.println("battles: " + p.totals.battles + 
//				String.format("  (%.1f) per day)", (float) p.totals.battles / (((new Date()).getTime() - p.created.getTime()) / 86400000)));
//		System.out.println("victories: " + p.totals.victories);
//		System.out.println("GR: " + p.playerRating);
	}

	static void printResults() {
		if (!printDone) {
			printDone = true;
		} else {
			return;
		}
		try {
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
			c.sortPlayersByTankAndRating();
			//for (int tier = 10; tier > 0; --tier) {
			for (PlayerInfo p : c.players) {
				StringBuilder line = new StringBuilder(Str.padRight(p.playername, 25));
				line.append("# ").append(Str.padRight(String.valueOf(p.playerRating), 7));
				line.append("* ").append(Str.padRight(String.valueOf(p.totals.battles), 6)).append(" - ");
				int n = 10;
				for (Tank t : p.getSortedTanks()) {
					line.append(Str.padRight(t.toString(), 25));
					if (--n > 0) {
						line.append(" | ");
					} else {
						break;
					}
				}
				System.out.println(line.toString());
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

			File dir = new File("saves");
			dir.mkdirs();
			File f = new File(dir, c.clanName + " [" + c.clanTag + "].html");
			System.out.println("writing to file: " + f.getPath());
			OutputHTML.writeFile(c, f);

			System.out.println("opening in default browser..");
			Desktop.getDesktop().browse(f.toURI());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Unexpected Error: " + e.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
