package me.jascotty2.claninfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class OutputHTML {

		static String logo = "data:image/jpeg;base64,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"
			+ "/HX9sVudByytqajISc80pUGygIA4xQldTB/9k=";
	static String css = null;
	static String js = null;
	static String template = null;

	static void loadRes() throws Exception {
		if (css == null) {
			css = getResFile("style.css");
			js = getResFile("boxover.js");
			template = getResFile("template.htm");
			if(css == null || css.isEmpty() 
					|| js == null || js.isEmpty()
					|| template == null || template.isEmpty()) {
				throw new Exception("Unexpected Error extracting files from jar");
			}
		}
	}

	private static String getResFile(String filen) {
		StringWriter writer = new StringWriter();
		InputStream in = null;
		try {
			URL res = OutputHTML.class.getResource("/me/jascotty2/clantracker/res/" + filen);
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

	public static void writeFile(GetClan c, File f) throws Exception {
		loadRes();
		BufferedWriter out = null;
		FileWriter outStream = null;
		try {
			outStream = new FileWriter(f);
			out = new BufferedWriter(outStream);
		} catch (Exception ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
			JOptionPane.showMessageDialog(null, "Error Opening output file: " + ex.getMessage(),
					"IO Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		SimpleDateFormat crformat = new SimpleDateFormat("MMM dd yyy");
		SimpleDateFormat genformat = new SimpleDateFormat("EE, MMM dd yyy  HH:mm zzz");
		SimpleDateFormat lastformat = new SimpleDateFormat("MM/dd/yyy HH:mm");
		
		// prepare data
		String server = "http://worldoftanks." + c.server;
		StringBuilder tierTableData = new StringBuilder("\n");
		for (int i = 9; i > 0; --i) {
			tierTableData.append("<tr><td>").
					append(String.valueOf(i + 1)).
					append("</td><td>").
					append(String.valueOf(c.maxTiers[i])).
					append("</td><td>").
					append(String.valueOf(c.totalTiers[i])).
					append("</td>\n");
		}

		StringBuilder tanksTableData = new StringBuilder("\n");
		for (PlayerInfo p : c.players) {
			tanksTableData.append("<tr><td class=\"n\" ").
					append("title=\"header=[Player Stats: (").
					append(p.playername).append(")]  body=[GR: ").
					append(String.valueOf(p.playerRating)).
					append("<br>Battles: ").append(String.valueOf(p.totals.battles)).
					append("<br>Last Battle: ").append(lastformat.format(p.lastbattle)).
					append("]\"><a href=\"").append(server).append("/community/accounts/").
					append(p.playerID).append("/\">").
					append(p.playername).append("</a>");
			float hago = ((System.currentTimeMillis() - p.lastbattle.getTime()) /  3600000);
			if(hago <= 1) {
				tanksTableData.append("<div class=\"tm\" style=\"background-color: #009900;\">").
						append(String.valueOf((int)hago)).append("</div>");
			} else if(hago <= 2) {
				tanksTableData.append("<div class=\"tm\" style=\"background-color: #669900;\">").
						append(String.valueOf((int)hago)).append("</div>");
			} else if(hago <= 5) {
				tanksTableData.append("<div class=\"tm\" style=\"background-color: #99CC00;\">").
						append(String.valueOf((int)hago)).append("</div>");
			} else if(hago <= 12) {
				tanksTableData.append("<div class=\"tm\" style=\"background-color: #996600;\">").
						append(String.valueOf((int)hago)).append("</div>");
			} else if(hago <= 24) {
				tanksTableData.append("<div class=\"tm\" style=\"background-color: #990000;\">").
						append(String.valueOf((int)hago)).append("</div>");
			} else if(hago <= 99) {
				tanksTableData.append("<div class=\"tm\" style=\"background-color: #330000;\">").
						append(String.valueOf((int)hago)).append("</div>");
			}
			tanksTableData.append("</td>");
			int n = 10;
			for (Tank t : p.getSortedTanks()) {
				// ö = \u00F6 = &#246;
				tanksTableData.append("<td><div title=\"header=[").
						append(t.name.replace("\u00F6", "&#246;")).
						append("] body=[(Tier ").
						append(t.tier);
				if(t.type != TankType.UNKNOWN) {
					tanksTableData.append(" ").append(t.type.getName());
				}
				tanksTableData.append(")]\">").
						append(t.name.replace("\u00F6", "&#246;")).
						append("</div></td>");
				if (--n <= 0) {
					break;
				}
			}
			tanksTableData.append("</tr>\n");
		}
		
		// now write :)
		try {
			out.write(template.replace("%%%%%%css%%%%%%", css).
					replace("%%%%%%js%%%%%%", js).
					replace("%%%%%%LogoImg%%%%%%", logo).
					replace("%%%%%%server%%%%%%", server).
					replace("%%%%%%ClanName%%%%%%", c.clanName).
					replace("%%%%%%ClanID%%%%%%", c.clanID).
					replace("%%%%%%ClanTAG%%%%%%", c.clanTag).
					replace("%%%%%%ClanEmblem%%%%%%", c.emblemURL).
					replace("%%%%%%Generated%%%%%%", genformat.format(new Date())).
					replace("%%%%%%OwnerID%%%%%%", c.ownerID).
					replace("%%%%%%OwnerName%%%%%%", c.ownerName).
					replace("%%%%%%ClanCreated%%%%%%", crformat.format(c.created)).
					replace("%%%%%%Members%%%%%%", String.valueOf(c.numPlayers)).
					replace("%%%%%%Provinces%%%%%%", String.valueOf(c.provinces.size())).
					replace("%%%%%%Income%%%%%%", String.valueOf(c.clanIncome)).
					replace("%%%%%%TierTableData%%%%%%", tierTableData.toString()).
					replace("%%%%%%TanksTableData%%%%%%", tanksTableData.toString()));
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
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
