package com.pac.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.pac.model.OddsMap;
import com.pac.model.Company;
import com.pac.model.Country;
import com.pac.model.League;
import com.pac.model.Match;
import com.pac.model.PageUrl;
import com.pac.model.Team;

public class ServiceUtil {

	public static void main(String[] args) throws Exception {
		Country country = new Country();
		country.setId(108);
		country.setName("英格兰111");
		country.setNameTra("英格蘭222");

		// List<Country> select = select(country);
		// for (Country country2 : select) {
		// System.out.println(country2.getNameTra());
		// }

		League league = new League();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("hasSub", "无");
		System.out.println(new Gson().toJson(JdbcUtil.select(league, map)));
	}

	public synchronized static int updateTeam(Team team) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", team.getId());
		
		List<Team> teams = JdbcUtil.select(team, map);
		
		if (teams.size() != 0) {// 已有记录就更新
			team.setSeasonLeagueId(team.getSeasonLeagueId() + "," + teams.get(0).getSeasonLeagueId());
			return JdbcUtil.update(team);
		} else { // 没有就插入
			return JdbcUtil.insert(team);
		}
	}
	
	public static int updateCountry(Country country) {
		return JdbcUtil.insert(country);
	}
	
	public static int updateOddsMap(OddsMap oddsMap) {
		return JdbcUtil.insert(oddsMap);
	}
	
	public static int updateCompany(Company company) {
		return JdbcUtil.insert(company);
	}
	
	public synchronized static int updateLeague(League league) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", league.getId());
		
		List<League> leagues = JdbcUtil.select(league, map);
		
		if (leagues.size() != 0) {// 已有记录就更新
			if (leagues.get(0).getNameTra()!=null) { // 已经更新过，直接返回
				return 0;
			}
			return JdbcUtil.update(league);
		} else { // 没有就插入
			return JdbcUtil.insert(league);
		}
	}
	
	public static int updateMatch(Match match) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", match.getId());
		if (JdbcUtil.select(match, map).size()>0) {
			return 0;
		}
		return JdbcUtil.insert(match);
	}
	public static int updatePageUrl(PageUrl pageUrl) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", pageUrl.getName());
		
		List<PageUrl> pageUrls = JdbcUtil.select(pageUrl, map);
		if (pageUrls.size()>0) {
			PageUrl selectPageurl = pageUrls.get(0);
			if (selectPageurl.getStatus().equals(pageUrl.getStatus())) {
				return 0;
			} else {
				pageUrl.setId(selectPageurl.getId());
				JdbcUtil.update(pageUrl);
			}
		}
		return JdbcUtil.insert(pageUrl);
	}
}
