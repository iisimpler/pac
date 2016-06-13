package com.pac.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pac.model.Company;
import com.pac.model.Country;
import com.pac.model.League;
import com.pac.model.Match;
import com.pac.model.Odds;
import com.pac.model.OddsMap;
import com.pac.model.Team;

public class GetUtil {
	
	/**
	 * 获取赔率
	 */
	public List<Odds> getOddsList(String str) {
		
		List<Odds> oddsList = new ArrayList<Odds>();
		
		Matcher m = Pattern.compile("<tr[^\u4e3b]*?</tr>").matcher(str);
		
//		List<String> oddsTrs = new ArrayList<String>();
		while (m.find()) {
			String oddsTr = m.group(0);
			Odds odds = new Odds();
			
			Float hostOdds = Float.valueOf(getText(oddsTr, 4, ">", 5, "<"));
			Float drawOdds = Float.valueOf(getText(oddsTr, 10, ">", 11, "<"));
			Float guestOdds = Float.valueOf(getText(oddsTr, 16, ">", 17, "<"));
			
			Float returnRate = hostOdds*drawOdds*guestOdds/(hostOdds*drawOdds+hostOdds*guestOdds+drawOdds*guestOdds);
			
			Float hostRate = returnRate/hostOdds;
			Float drawRate = returnRate/drawOdds;
			Float guestRate = returnRate/guestOdds;
			
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
			try {
				odds.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("1970-"+getText(oddsTr, 34, ">", 35, "<").replace("&nbsp; ", "")+":00"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
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
				
				try {
					match.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(getText(matchinfo, 1, "'", 2, "'")+":00"));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
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
			company.setType(type.equals("1,0")?"主流公司":(type.equals("0,1")?"交易所":"非交易所"));
			
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
			league.setLeagueType(getText(temp, 2, ",", 3, ",").equals("1")?"联赛":"杯赛");
			league.setHasSub(getText(temp, 3, ",", 4, ",").equals("0")?"无":"有");
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
