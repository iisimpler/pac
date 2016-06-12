package com.pac.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pac.model.Country;
import com.pac.model.League;
import com.pac.model.Team;

public class GetUtil {
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
