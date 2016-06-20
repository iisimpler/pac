package com.pac.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import us.codecraft.webmagic.Page;

import com.google.gson.Gson;
import com.pac.model.Company;
import com.pac.model.Country;
import com.pac.model.Handicap;
import com.pac.model.League;
import com.pac.model.Match;
import com.pac.model.Odds;
import com.pac.model.OddsMap;
import com.pac.model.OverUnder;
import com.pac.model.Team;
import com.pac.model.X1x2;

public class GetUtil {
	/**
	 * 获取走地欧赔
	 */
	public List<X1x2> getX1x2s(Page page) {
		String str = page.getRawText();
		String urlStr = page.getUrl().toString();
		Integer matchId = 545;
		Integer companyId = Integer.valueOf(urlStr.substring(getIndex(urlStr, 2, "=") + 1));
		List<X1x2> x1x2s = new ArrayList<X1x2>();
		
		Matcher mYear = Pattern.compile("\\d+ 年").matcher(str);
		String year = "";
		if (mYear.find()) {
			year = mYear.group().substring(0, 4);
		}
		
		Matcher m = Pattern.compile("class=\"gts\">[\\w\\W]*?</table>").matcher(str);
		m.find();
		m.find();
		if (m.find()) {

			String handicapsInfos = m.group();
			Matcher mhandicapsInfo = Pattern.compile("<tr [\\w\\W]*?</tr>").matcher(handicapsInfos);
			mhandicapsInfo.find();
			while (mhandicapsInfo.find()) {
				
				X1x2 x1x2 = new X1x2();
				
				String handicapsInfo = mhandicapsInfo.group();
				Matcher mInfo = Pattern.compile("<td .*").matcher(handicapsInfo);
				int temp = 0;
				while (mInfo.find()) {
					temp++;
					String info = mInfo.group();
					String content = getText(info, 1, ">", 2, "<");
					switch (temp) {
					case 1:
						x1x2.setGameTime(content);
						break;
					case 2:
						x1x2.setScore(content);
						break;
					case 3:
						x1x2.setHostOdds(content.equals("")?null:Float.valueOf(content));
						break;
					case 4:
						x1x2.setHandicap(content.contains("封")?"封":content);
						break;
					case 5:
						x1x2.setGuestOdds(content.equals("")?null:Float.valueOf(content));
						break;
					case 6:
						x1x2.setTime(year+"-"+getText(info, 1, ">", 3, "<").replace("<br />", " ")+":00");
						break;
					case 7:
						x1x2.setState(content);
						break;
					}
				}
				x1x2.setMatchId(matchId);
				x1x2.setCompanyId(companyId);
				x1x2s.add(x1x2);
			}
		
		}
		System.out.println(new Gson().toJson(x1x2s));
		return x1x2s;
	}
	
	/**
	 * 获取走地大小球盘
	 */
	public List<OverUnder> getOverUnders(Page page) {
		String str = page.getRawText();
		String urlStr = page.getUrl().toString();
		Integer matchId = 545;
		Integer companyId = Integer.valueOf(urlStr.substring(getIndex(urlStr, 2, "=") + 1));
		List<OverUnder> overUnders = new ArrayList<OverUnder>();
		
		Matcher mYear = Pattern.compile("\\d+ 年").matcher(str);
		String year = "";
		if (mYear.find()) {
			year = mYear.group().substring(0, 4);
		}
		
		Matcher m = Pattern.compile("class=\"gts\">[\\w\\W]*?</table>").matcher(str);
		m.find();
		if (m.find()) {
			String handicapsInfos = m.group();
			Matcher mhandicapsInfo = Pattern.compile("<tr [\\w\\W]*?</tr>").matcher(handicapsInfos);
			mhandicapsInfo.find();
			while (mhandicapsInfo.find()) {
				
				OverUnder overUnder = new OverUnder();
				
				String handicapsInfo = mhandicapsInfo.group();
				Matcher mInfo = Pattern.compile("<td .*").matcher(handicapsInfo);
				int temp = 0;
				while (mInfo.find()) {
					temp++;
					String info = mInfo.group();
					String content = getText(info, 1, ">", 2, "<");
					switch (temp) {
					case 1:
						overUnder.setGameTime(content);
						break;
					case 2:
						overUnder.setScore(content);
						break;
					case 3:
						overUnder.setHostOdds(content.equals("")?null:Float.valueOf(content));
						break;
					case 4:
						overUnder.setHandicap(content.contains("封")?"封":content);
						break;
					case 5:
						overUnder.setGuestOdds(content.equals("")?null:Float.valueOf(content));
						break;
					case 6:
						overUnder.setTime(year+"-"+getText(info, 1, ">", 3, "<").replace("<br />", " ")+":00");
						break;
					case 7:
						overUnder.setState(content);
						break;
					}
				}
				overUnder.setMatchId(matchId);
				overUnder.setCompanyId(companyId);
				overUnders.add(overUnder);
			}
		}
		System.out.println(new Gson().toJson(overUnders));
		return overUnders;
	}
	
	/**
	 * 获取走地让球盘
	 */
	public List<Handicap> getHandicaps(Page page) {
		String str = page.getRawText();
		String urlStr = page.getUrl().toString();
		Integer matchId = 545;
		Integer companyId = Integer.valueOf(urlStr.substring(getIndex(urlStr, 2, "=") + 1));
		List<Handicap> handicaps = new ArrayList<Handicap>();
		
		Matcher mYear = Pattern.compile("\\d+ 年").matcher(str);
		String year = "";
		if (mYear.find()) {
			year = mYear.group().substring(0, 4);
		}
		
		Matcher m = Pattern.compile("class=\"gts\">[\\w\\W]*?</table>").matcher(str);
		
		if (m.find()) {
			String handicapsInfos = m.group();
			Matcher mhandicapsInfo = Pattern.compile("<tr [\\w\\W]*?</tr>").matcher(handicapsInfos);
			mhandicapsInfo.find();
			while (mhandicapsInfo.find()) {
				
				Handicap handicap = new Handicap();
				
				String handicapsInfo = mhandicapsInfo.group();
				Matcher mInfo = Pattern.compile("<td .*").matcher(handicapsInfo);
				int temp = 0;
				while (mInfo.find()) {
					temp++;
					String info = mInfo.group();
					String content = getText(info, 1, ">", 2, "<");
					switch (temp) {
					case 1:
						handicap.setGameTime(content);
						break;
					case 2:
						handicap.setScore(content);
						break;
					case 3:
						handicap.setHostOdds(content.equals("")?null:Float.valueOf(content));
						break;
					case 4:
						handicap.setHandicap(content.contains("封")?"封":content);
						break;
					case 5:
						handicap.setGuestOdds(content.equals("")?null:Float.valueOf(content));
						break;
					case 6:
						handicap.setTime(year+"-"+getText(info, 1, ">", 3, "<").replace("<br />", " ")+":00");
						break;
					case 7:
						handicap.setState(content);
						break;
					}
				}
				handicap.setMatchId(matchId);
				handicap.setCompanyId(companyId);
				handicaps.add(handicap);
			}
		}
		System.out.println(new Gson().toJson(handicaps));
		return handicaps;
	}

	/**
	 * 从手机版网页获取赔率
	 */
	public List<Odds> getOddsListFromMobile(Page page) {
		List<Odds> oddsList = new ArrayList<Odds>();
		String str = page.getRawText();

		String urlStr = page.getUrl().toString();
		Integer matchId = Integer.valueOf(getText(urlStr, 1, "=", 1, "&"));
		Integer companyId = Integer.valueOf(urlStr.substring(getIndex(urlStr, 2, "=") + 1));

		Matcher yearInfoMatch = Pattern.compile("\"gray\">\\d+").matcher(str);
		Matcher oddsInfosMatch = Pattern.compile("<table style=\"width:100%;\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"mytable3\">[\\w\\W]*?</table>").matcher(str);

		Integer year = 0;
		if (yearInfoMatch.find()) {
			year = Integer.valueOf("20" + yearInfoMatch.group().substring(7));
		}
		if (oddsInfosMatch.find()) {
			String oddsInfos = oddsInfosMatch.group();
			Matcher oddsInfoMatch = Pattern.compile("<tr[\\w\\W]*?</tr>").matcher(oddsInfos);

			oddsInfoMatch.find();

			String date = "";
			String time = "";

			while (oddsInfoMatch.find()) {
				String oddsInfo = oddsInfoMatch.group();
				Matcher oddsMatch = Pattern.compile("<td[\\w\\W]*?</td>").matcher(oddsInfo);

				Odds odds = new Odds();
				Float hostOdds = null;
				Float drawOdds = null;
				Float guestOdds = null;
				int temp = 0;
				while (oddsMatch.find()) {
					String rate = oddsMatch.group();
					Matcher ratesMatch = Pattern.compile("\\d+\\.\\d+").matcher(rate);
					Matcher dateMatch = Pattern.compile("\\d+\\-\\d+").matcher(rate);
					Matcher timeMatch = Pattern.compile("\\d+\\:\\d+").matcher(rate);

					if (ratesMatch.find()) {
						temp++;
						String rates = ratesMatch.group();

						if (temp == 1) {
							hostOdds = Float.valueOf(rates);
						}
						if (temp == 2) {
							drawOdds = Float.valueOf(rates);
						}
						if (temp == 3) {
							guestOdds = Float.valueOf(rates);
						}
						if (temp == 5) {
							odds.setHostKelly(Float.valueOf(ratesMatch.group()));
							ratesMatch.find();
							odds.setDrawKelly(Float.valueOf(ratesMatch.group()));
							ratesMatch.find();
							odds.setGuestKelly(Float.valueOf(ratesMatch.group()));
						}
					}
					if (dateMatch.find()) {
						date = year + "-" + dateMatch.group();
					}
					if (timeMatch.find()) {
						time = timeMatch.group() + ":00";
					}
				}
				Float returnRate = hostOdds * drawOdds * guestOdds / (hostOdds * drawOdds + hostOdds * guestOdds + drawOdds * guestOdds);

				Float hostRate = returnRate / hostOdds;
				Float drawRate = returnRate / drawOdds;
				Float guestRate = returnRate / guestOdds;

				odds.setHostOdds(hostOdds);
				odds.setDrawOdds(drawOdds);
				odds.setGuestOdds(guestOdds);
				odds.setHostRate(hostRate);
				odds.setDrawRate(drawRate);
				odds.setGuestRate(guestRate);
				odds.setReturnRate(returnRate);
				odds.setTime(date + " " + time);
				odds.setMatchId(matchId);
				odds.setCompanyId(companyId);

				oddsList.add(odds);
			}
		}
		return oddsList;
	}

	/**
	 * 从PC版网页获取赔率，有访问频率限制
	 */
	public List<Odds> getOddsListFromPc(String str) {

		List<Odds> oddsList = new ArrayList<Odds>();

		Matcher m = Pattern.compile("<tr[^\u4e3b]*?</tr>").matcher(str);

		// List<String> oddsTrs = new ArrayList<String>();
		while (m.find()) {
			String oddsTr = m.group(0);
			Odds odds = new Odds();

			Float hostOdds = Float.valueOf(getText(oddsTr, 4, ">", 5, "<"));
			Float drawOdds = Float.valueOf(getText(oddsTr, 10, ">", 11, "<"));
			Float guestOdds = Float.valueOf(getText(oddsTr, 16, ">", 17, "<"));

			Float returnRate = hostOdds * drawOdds * guestOdds / (hostOdds * drawOdds + hostOdds * guestOdds + drawOdds * guestOdds);

			Float hostRate = returnRate / hostOdds;
			Float drawRate = returnRate / drawOdds;
			Float guestRate = returnRate / guestOdds;

			odds.setHostOdds(hostOdds);
			odds.setDrawOdds(drawOdds);
			odds.setGuestOdds(guestOdds);
			odds.setHostRate(hostRate);
			odds.setDrawRate(drawRate);
			odds.setGuestRate(guestRate);
			odds.setReturnRate(returnRate);
			odds.setHostKelly(Float.valueOf(getText(oddsTr, 28, ">", 29, "<")));
			odds.setDrawKelly(Float.valueOf(getText(oddsTr, 30, ">", 31, "<")));
			odds.setGuestKelly(Float.valueOf(getText(oddsTr, 32, ">", 33, "<").trim()));
			odds.setTime("0000-" + getText(oddsTr, 34, ">", 35, "<").replace("&nbsp; ", "") + ":00");

			oddsList.add(odds);
		}
		return oddsList;
	}

	/**
	 * 获取某一博彩公司对某一场比赛所开的赔率的对应关系
	 */
	public List<OddsMap> getOddsMaps(String str) {

		List<OddsMap> oddsMaps = new ArrayList<OddsMap>();

		// 某一比赛的ID
		Matcher mm = Pattern.compile("ScheduleID=\\d+").matcher(str);
		mm.find();
		String matchId = mm.group(0).substring(11);

		// 开始获取给该比赛赔率的对应关系
		Matcher mmm = Pattern.compile("game=Array.*").matcher(str);
		mmm.find();
		String gameInfo = mmm.group(0);

		Matcher m = Pattern.compile("\"[^\\\"]+\"").matcher(gameInfo);
		while (m.find()) {

			String game = m.group(0);
			OddsMap oddsMap = new OddsMap();

			oddsMap.setId(Integer.valueOf(getText(game, 1, "|", 2, "|")));
			oddsMap.setMatchId(Integer.valueOf(matchId));
			oddsMap.setCompanyId(Integer.valueOf(getText(game, 1, "\"", 1, "|")));

			oddsMaps.add(oddsMap);
		}

		return oddsMaps;
	}

	/**
	 * 获取比赛信息
	 */
	public List<Match> getMaths(String str) {

		List<Match> matchs = new ArrayList<Match>();

		String pattern = "jh.*";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(str);

		while (m.find()) {
			String matchInfos = m.group(0);

			int round = Integer.valueOf(getText(matchInfos, 1, "_", 2, "\""));

			String pattern1 = "\\[[^\\[\\]\"]+]";
			Pattern r1 = Pattern.compile(pattern1);
			Matcher m1 = r1.matcher(matchInfos);

			while (m1.find()) {
				String matchinfo = m1.group(0);

				Match match = new Match();
				match.setId(Integer.valueOf(getText(matchinfo, 1, "[", 1, ",")));
				match.setLeagueId(Integer.valueOf(getText(matchinfo, 1, ",", 2, ",")));
				match.setTime(getText(matchinfo, 1, "'", 2, "'") + ":00");

				match.setHostId(Integer.valueOf(getText(matchinfo, 4, ",", 5, ",")));
				match.setGuestId(Integer.valueOf(getText(matchinfo, 5, ",", 6, ",")));
				match.setScoreAll(getText(matchinfo, 3, "'", 4, "'"));
				match.setScoreHalf(getText(matchinfo, 5, "'", 6, "'"));
				match.setRankHost(getText(matchinfo, 7, "'", 8, "'"));
				match.setRankGuest(getText(matchinfo, 9, "'", 10, "'"));
				match.setRedHost(Integer.valueOf(getText(matchinfo, 18, ",", 19, ",")));
				match.setRedGuest(Integer.valueOf(getText(matchinfo, 19, ",", 20, ",")));
				match.setRound(round);

				matchs.add(match);
			}
		}

		return matchs;
	}

	/**
	 * 获取博彩公司信息
	 */
	public List<Company> getCompanies(String str) {

		List<Company> companies = new ArrayList<Company>();

		String pattern = "\\[[^\\[\\]]+\\]";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(str);

		while (m.find()) {
			String companyInfo = m.group(0);

			Company company = new Company();
			company.setId(Integer.valueOf(getText(companyInfo, 1, "[", 1, ",")));
			company.setName(getText(companyInfo, 1, "'", 2, "'"));

			String type = getText(companyInfo, 2, ",", 1, "]");
			company.setType(type.equals("1,0") ? "主流公司" : (type.equals("0,1") ? "交易所" : "非交易所"));

			companies.add(company);
		}
		return companies;
	}

	/**
	 * 分离某一联赛中所有球队的信息
	 */

	public List<Team> getTeamInfo(String str) {

		List<Team> teams = new ArrayList<Team>();

		String pattern = "arrTeam.*";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(str);
		String teamInfo = "";
		if (m.find()) {
			teamInfo = m.group(0);
		}

		String pattern1 = "\\[[^\\[\\]]*\\]";
		Pattern r1 = Pattern.compile(pattern1);
		Matcher m1 = r1.matcher(teamInfo);

		List<String> strs = new ArrayList<String>();
		while (m1.find()) {
			strs.add(m1.group(0));
		}

		for (String strr : strs) {
			Team team = new Team();
			team.setId(Integer.valueOf(getText(strr, 1, "[", 1, ",")));
			team.setName(getText(strr, 1, "'", 2, "'"));
			team.setNameTra(getText(strr, 3, "'", 4, "'"));
			team.setNameEng(getText(strr, 5, "'", 6, "'"));
			team.setSeasonLeagueId(getText(str, 7, "'", 8, "'") + "-" + Integer.valueOf(getText(str, 1, "[", 1, ",")));
			teams.add(team);

		}
		return teams;
	}

	/**
	 * 获取联赛的详细信息，其中包括其下所有球队
	 */
	public League getLeagueInfo(String str) {
		League league = new League();

		league.setId(Integer.valueOf(getText(str, 1, "[", 1, ",")));
		league.setNameAll(getText(str, 1, "'", 2, "'"));
		league.setNameAllTra(getText(str, 3, "'", 4, "'"));
		league.setNameAllEng(getText(str, 5, "'", 6, "'"));
		league.setRound(Integer.valueOf(getText(str, 7, ",", 8, ",")));
		league.setName(getText(str, 13, "'", 14, "'"));
		league.setNameTra(getText(str, 15, "'", 16, "'"));
		league.setNameEng(getText(str, 17, "'", 18, "'"));
		league.setDescription(getText(str, 19, "'", 20, "'").replaceAll("[ |　]", " ").trim().replace("&nbsp;", ""));

		List<Team> teams = getTeamInfo(str);
		league.setTeams(teams);

		return league;
	}

	/**
	 * 分离出不同的国家，其中已包括分别的所有联赛
	 */
	public List<Country> getCountryInfos(String str) {
		String pattern = "arr\\[[^\\]]+\\] = \\[[^\\]]+\\]";

		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(str);

		List<String> strs = new ArrayList<String>();
		while (m.find()) {
			strs.add(m.group(0));
		}

		List<Country> countries = new ArrayList<Country>();
		for (String ss : strs) {
			countries.add(getCountryInfo(ss));
		}

		return countries;
	}

	/**
	 * 分离出某一个国家的信息，包括其下所有的联赛
	 */
	public Country getCountryInfo(String str) {

		Country country = new Country();

		country.setId(Integer.valueOf(getText(str, 1, "[", 1, "]")) + 1);

		country.setName(getText(str, 3, "\"", 4, "\""));

		List<League> leagues = new ArrayList<League>();

		int count = getCount(str, "\"") - 8;

		for (int i = 9; i < count + 9; i += 2) {
			String temp = getText(str, i, "\"", i + 1, "\"");
			League league = new League();
			league.setId(Integer.valueOf(temp.substring(0, getIndex(temp, 1, ","))));
			league.setName(getText(temp, 1, ",", 2, ","));
			league.setLeagueType(getText(temp, 2, ",", 3, ",").equals("1") ? "联赛" : "杯赛");
			league.setHasSub(getText(temp, 3, ",", 4, ",").equals("0") ? "无" : "有");
			league.setSeason(temp.substring(getIndex(temp, 4, ",") + 1));
			league.setCountryId(Integer.valueOf(getText(str, 1, "[", 1, "]")) + 1);
			leagues.add(league);
		}

		country.setLeagues(leagues);

		return country;
	}

	/**
	 * 获取某一字符串中第n个字符串的位置到第m个字符串位置之间的内容
	 */
	public static String getText(String str, int st, String stChar, int ed, String edChar) {

		return str.substring(getIndex(str, st, stChar) + 1, getIndex(str, ed, edChar));
	}

	/**
	 * 获取某字符串第n此出现的位置
	 */
	public static int getIndex(String str, int st, String Char) {

		if (st == 0) {
			try {
				throw new Exception("至少从1开始");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (st == 1) {
			return str.indexOf(Char);
		}
		return str.indexOf(Char, getIndex(str, st - 1, Char) + 1);
	}

	/**
	 * 统计字符串中某一匹配字符串出现的个数。
	 */
	public int getCount(String str, String c) {
		int length = str.length();
		int count = 0;
		for (int i = 0; i < length; i++) {
			if (str.substring(i, i + 1).equals(c)) {
				count++;
			}
		}

		return count;
	}
}
