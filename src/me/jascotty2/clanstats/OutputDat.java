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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class OutputDat {

	public static void writeFile(GetClan c, File f) throws Exception {
		BufferedWriter out = null;
		FileWriter outStream = null;
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
			out.write("generated: " + System.currentTimeMillis() + "\n");
			out.write("# (" + (new Date()) + ")\n");
			out.write("created: " + c.created.getTime() + "\n");
			out.write("# (" + c.created + ")\n");
			out.write("members: " + c.numPlayers + "\n");
			out.write("owner: " + c.ownerID + "\n#  (" + c.ownerName + ")\n");
			out.write("clanNum: " + c.clanID + "\n");
			out.write("emblem: " + c.emblemURL + "\n");
			if(c instanceof GetTournamentTeam) {
				out.write("maxTier:\n");
				out.write("\ttotal: " + ((GetTournamentTeam)c).max + "\n");
				out.write("\tHT: " + ((GetTournamentTeam)c).maxHT + "\n");
				out.write("\tMT: " + ((GetTournamentTeam)c).maxMT + "\n");
				out.write("\tLT: " + ((GetTournamentTeam)c).maxLT + "\n");
				out.write("\tTD: " + ((GetTournamentTeam)c).maxTD + "\n");
				out.write("\tSPG: " + ((GetTournamentTeam)c).maxSPG + "\n");
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
}
