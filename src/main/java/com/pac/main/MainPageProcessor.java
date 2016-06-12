package com.pac.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.JMException;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import com.pac.model.Country;
import com.pac.model.League;
import com.pac.model.PageUrl;
import com.pac.model.Team;
import com.pac.util.GetUtil;
import com.pac.util.JdbcUtil;

public class MainPageProcessor implements PageProcessor {

	private Site site = Site.me().setCharset("UTF-8").setCycleRetryTimes(3).setRetryTimes(3).setSleepTime(100);

	private List<Country> countries = new ArrayList<Country>();
	private List<Country> countriesTra = new ArrayList<Country>();

	private GetUtil getUtil = new GetUtil();

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {

		String info = page.getRawText();

		// 确定当前 page 页面没有被爬过
		PageUrl pageUrl = new PageUrl();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", page.getUrl().toString());

		if (JdbcUtil.select(pageUrl, map).size() != 0) {
			return;
		}

		// 确定当前页面有相应数据
		if (info.contains("对不起！你查看的页面不存在")) {

			JdbcUtil.update(new PageUrl(page.getUrl().toString(), "页面不存在"));
			return;
		} else {

			JdbcUtil.update(new PageUrl(page.getUrl().toString(), "成功"));
		}
		// 简体国家页面
		if (page.getUrl().toString().equals("http://zq.win007.com/jsData/infoHeader.js")) {
			countries = getUtil.getCountryInfos(info);
			// 请求队列加入繁体国家页面链接
			page.addTargetRequest("http://zq.win007.com/jsData/infoHeaderFn.js");
		}
		// 繁体国家页面
		if (page.getUrl().toString().equals("http://zq.win007.com/jsData/infoHeaderFn.js")) {
			countriesTra = getUtil.getCountryInfos(info);

			// 将国家信息和国家内包含的联赛信息插入数据库
			for (Country country : countries) {
				for (Country countryTra : countriesTra) {
					if (country.getId() == countryTra.getId()) {
						country.setNameTra(countryTra.getName());
						JdbcUtil.update(country);
					}
				}
				List<League> leagues = country.getLeagues();
				for (League league : leagues) {
					JdbcUtil.update(league);

					// 无子联赛的联赛
					if (league.getHasSub().equals("无") && league.getLeagueType().equals("联赛")) {
						int id = league.getId();
						String[] season = league.getSeason().split(",");
						for (int i = 0; i < season.length; i++) {
							page.addTargetRequest("http://zq.win007.com/jsData/matchResult/" + season[i] + "/s" + id + ".js");
						}
					}
				}
			}

		}

		// 无子联赛的联赛
		if (page.getUrl().toString().contains("http://zq.win007.com/jsData/matchResult/")) {

			League leagues = getUtil.getLeagueInfo(info);

			JdbcUtil.update(leagues);

			List<Team> teams = leagues.getTeams();
			JdbcUtil.updateTeam(teams);
		}

	}

	public static void main(String[] args) throws JMException {
		// 从简体中文国家页面抓起
		Spider countrySpider = Spider.create(new MainPageProcessor()).addUrl("http://zq.win007.com/jsData/infoHeader.js").thread(10);
		countrySpider.start();

		// Spider teamspider = Spider.create(new TestPageProcessor()).thread(10);
		// SpiderMonitor.instance().register(teamspider);
		// teamspider.run();
	}

}
