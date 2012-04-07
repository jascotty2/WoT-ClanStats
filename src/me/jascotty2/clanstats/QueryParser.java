/**
 * Copyright (C) 2012 Jacob Scott <jascottytechie@gmail.com>
 * Description: Provides methods for obtaining & parsing html and xml queries
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.jascotty2.lib.io.CheckInput;
import me.jascotty2.lib.util.Str;

public class QueryParser {

	public static String get(String url) {
		try {
			URL localURL = new URL(url);
			URLConnection localURLConnection = localURL.openConnection();
			localURLConnection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
			localURLConnection.setRequestProperty("Accept-Language", "en-us;q=0.5,en;q=0.3");
			localURLConnection.setRequestProperty("Accept-Encoding", "paco");
			localURLConnection.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
			localURLConnection.setRequestProperty("Connection", "close");
			localURLConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			BufferedReader localBufferedReader = new BufferedReader(
					new InputStreamReader(localURLConnection.getInputStream(), "UTF8"));
			String tmp;
			StringBuilder res = new StringBuilder();
			while ((tmp = localBufferedReader.readLine()) != null) {
				res.append(tmp);
			}
			return res.toString();
		} catch (FileNotFoundException e) {
			// 404
		} catch (Exception ex) {
			Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public static String getPage(String url) {
		try {
			URL localURL = new URL(url);
			URLConnection localURLConnection = localURL.openConnection();
			localURLConnection.setRequestProperty("Accept-Language", "en-us;q=0.5,en;q=0.3");
			localURLConnection.setRequestProperty("Accept-Encoding", "paco");
			localURLConnection.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
			localURLConnection.setRequestProperty("Connection", "close");
			BufferedReader localBufferedReader = new BufferedReader(
					new InputStreamReader(localURLConnection.getInputStream(), "UTF8"));
			String tmp;
			StringBuilder res = new StringBuilder();
			while ((tmp = localBufferedReader.readLine()) != null) {
				res.append(tmp);
			}
			return res.toString();
		} catch (FileNotFoundException e) {
			// 404
		} catch (IOException ex) {
			if (ex.getMessage() != null && !ex.getMessage().contains("HTTP response code: 403")) {
				Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (Exception ex) {
			Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

//	public static void main(String[] args) {
//		List<Map<String, Object>> data = getItemLists("items", "{\"request_data\":{\"items\":[{\"team_owner_id\":1001380563,\"can_enter\":null,\"url_leave\":\"/uc/teams/5243-StalkersofMayhem/update/?type=delete_user&account_id=-1\",\"members\":[{\"name\":\"Chaos7_s\",\"id\":1001380563},{\"name\":\"jascotty2\",\"id\":1001630918},{\"name\":\"johnt69\",\"id\":1001649907}],\"needpassword\":true,\"id\":5243,\"name\":\"StalkersofMayhem\",\"member_count\":3,\"url\":\"/uc/teams/5243-StalkersofMayhem/\",\"can_leave\":null,\"state\":\"opened\",\"state_updated_at\":1333430189.0,\"url_enter\":\"/uc/teams/5243-StalkersofMayhem/update/?type=new_user&account_id=-1\"}],\"total_count\":106,\"filtered_count\":1,\"offset\":0,\"echo\":0},\"result\":\"success\"}");
//		System.out.println("num values: " + (data != null ? data.size() : 0));
//		if (data != null && data.size() > 0) {
//			System.out.println("data keys: " + Str.concatStr(data.get(0).keySet(), ", "));
//			for(String k : data.get(0).keySet()) {
//				Object o = data.get(0).get(k);
//				if(o instanceof String) {
//					System.out.println("String: " + k + " : " + o);
//				} else if (o instanceof Boolean) {
//					System.out.println("bool: " + k + " : " + o);
//				} else if (o instanceof Integer) {
//					System.out.println("Int: " + k + " : " + o);
//				} else if (o instanceof Double) {
//					System.out.println("Float: " + k + " : " + o);
//				} else if (o instanceof List) {
//					System.out.println("List: " + k + " : " + ((List)o).size());//Str.concatStr(((Map)o).keySet(), ", "));
//					if(((List)o).size() >= 1) {
//						Map<String, Object> ob = (Map<String, Object>) ((List<Map>)o).get(0);
//						for(String k2 : ob.keySet()) {
//							Object o2 = ob.get(k2);
//							if(o2 instanceof String) {
//								System.out.println("String: " + k + " : " + o2);
//							}
//						}
//					}
//				} else if (o == null) {
//					System.out.println("null: " + k);
//				} else {
//					System.out.println("unknown: " + k);
//				}
//			}
//		}
//	}
	/**
	 * returns parsed list portion of a XMLHttpRequest
	 * each element of the array is the whole 
	 * @param key what the key for the list is
	 * @param data the data to parse
	 * @return 
	 */
	public static List<Map<String, Object>> getItemLists(String key, String data) {
		ArrayList<Map<String, Object>> lists = null;
		key = "\"" + key + "\":[";
		int i = data.indexOf(key);
		if (i != -1) {
			try {
				lists = new ArrayList<Map<String, Object>>();
				i += key.length();
				String dat = data.substring(i, getListStrEnd(data, i));
				i = 1;
				while (i > 0 && i + 1 < dat.length()) {
					Map<String, Object> wr = new HashMap<String, Object>();
					while (i + 1 < dat.length() && dat.charAt(i) != '}') {
						// all keys begin with a quote
						i = getAssertedIndex(dat, "\"", i) + 1;
						int end = getAssertedIndex(dat, "\"", i + 1);
						while (dat.charAt(end - 1) == '\\') {
							end = getAssertedIndex(dat, "\"", end + 1);
						}
						String k = dat.substring(i, end);
						Object toAdd = null;
						i = getAssertedIndex(dat, ":", i) + 1;
						while (dat.charAt(i) == ' ') {
							++i;
						}
						if (dat.charAt(i) == '"') {
							++i;
							end = getAssertedIndex(dat, "\"", i);
							while (dat.charAt(end - 1) == '\\') {
								end = getAssertedIndex(dat, "\"", end + 1);
							}
							toAdd = dat.substring(i, end);
							++end;
						} else {
							if (dat.charAt(i) == '[') {
								toAdd = getItemLists(k, data);
								end = getListStrEnd(dat, i + 1);
							} else {
								end = i;
								for (; end < dat.length(); ++end) {
									char c = dat.charAt(end);
									if (c == ',' || c == ':' || c == '[' || c == ']' || c == '}') {
										break;
									}
								}
								String v = dat.substring(i, end);
								if (!v.toLowerCase().equals("null")) {
									if (v.equalsIgnoreCase("false")) {
										toAdd = false;
									} else if (v.equalsIgnoreCase("true")) {
										toAdd = true;
									} else {
										try {
											if (v.contains(".")) {
												toAdd = Double.parseDouble(v);
											} else {
												toAdd = Integer.parseInt(v);
											}
										} catch (Exception e) {
											toAdd = null;
										}
									}
								}
							}
						}
						wr.put(k, toAdd);
						i = end;
					}
					lists.add(wr);
					i = dat.indexOf('{', i);
				}
			} catch (Exception ex) {
				//Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, null, ex);
				System.out.println(Str.getStackStr(ex));
			}
		}
		return lists;
	}

	public static String getItemListStr(String key, String data) {
		key = "\"" + key + "\":[";
		int i = data.indexOf(key);
		if (i != -1) {
			i += key.length();
			return data.substring(i, getListStrEnd(data, i));
		}
		return null;
	}

	protected static int getListStrEnd(String data, int start) {
		int i = start;
		int depth = 1;
		for (; i < data.length(); ++i) {
			// assume no special chars in quotes
			if (data.charAt(i) == ']') {
				if (--depth <= 0) {
					return i;
				}
			} else if (data.charAt(i) == '[') {
				++depth;
			}
		}
		return i;
	}

	public static String getData(String itemList, String tag) {
		if (itemList != null) {
			tag = "\"" + tag + "\":";
			int i = itemList.indexOf(tag);
			if (i != -1) {
				i += tag.length();
				// if starts with a quote, inc by 1 and go to end quote
				// else, continue to the ending comma
//				int end = (requestData.charAt(i) == '"')
//						? requestData.indexOf("\"", ++i)
//						: requestData.indexOf(",", i);
				int end = -1;
				if (itemList.charAt(i) == '"') {
					end = itemList.indexOf("\"", ++i);
				} else {
					int a = itemList.indexOf("}", i);
					int b = itemList.indexOf("]", i);
					int c = itemList.indexOf(",", i);
					// choose the first control char to break at
					end = (a != -1 && a < b && a < c)
							? a : ((b != -1 && b < a && b < c) ? b : c);
				}
				if (end != -1) {
					return itemList.substring(i, end);
				} else {
					return itemList.substring(i);
				}
			}
		}
		return null;
	}

	// html parsing
	public static int getStatInt(String html, String title, String divider, int start) {
		String ret = getStat(html, title, divider, start);
		if (ret != null) {
			if (ret.contains("(")) {
				ret = ret.substring(0, ret.indexOf("("));
			}
			return CheckInput.GetInt(ret.replace("%", ""), -1);
		}
		return -1;
	}

	public static String getStat(String html, String title, String divider, int start) {
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

	public static String getStatTimestamp(String html, String title, int start) {
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

	public static String getCellData(String html, int start) {
		try {
			return getDataWithoutTags(html.substring(start, getAssertedIndex(html, "</td>", start)));
		} catch (Exception ex) {
		}
		return null;
	}

	public static String getDataWithoutTags(String data) {
		data = data.replace("&nbsp;", " ").replace("&amp;", "&");
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

	public static String getDoubleStatVal(String html, String title, int num, int start) {
		String val[] = getDoubleStat(html, title, start);
		return val != null && val.length > num ? val[num] : null;
	}

	public static String[] getDoubleStat(String html, String title, int start) {
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

	private static int getAssertedIndex(String str, String search, int start) throws Exception {
		int i = str.indexOf(search, start);
		if (i == -1) {
			throw new Exception(String.format("\"%s\" not found in string", search));
		}
		return i;
	}
}
