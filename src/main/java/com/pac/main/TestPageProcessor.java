package com.pac.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.JMException;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.processor.PageProcessor;

import com.google.gson.Gson;
import com.pac.model.League;
import com.pac.model.Team;
import com.pac.util.GetUtil;
import com.pac.util.JdbcUtil;

public class TestPageProcessor implements PageProcessor {

	private Site site = Site.me().setCharset("UTF-8").setRetryTimes(3).setSleepTime(1000);

	private GetUtil getUtil = new GetUtil();

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {

		if (page.getUrl().toString().equals("http://zq.win007.com")) {
			League league = new League();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("hasSub", "无");
			map.put("leagueType", "联赛");
			List<League> leaguesNoSub = null;

			leaguesNoSub = JdbcUtil.select(league, map);

			for (League league2 : leaguesNoSub) {
				int id = league2.getId();
				String[] season = league2.getSeason().split(",");
				for (int i = 0; i < season.length; i++) {
					page.addTargetRequest("http://zq.win007.com/jsData/matchResult/" + season[i] + "/s" + id + ".js");
				}
			}
		}

		if (page.getUrl().toString().contains("http://zq.win007.com/jsData/matchResult/")) {
			String info = page.getRawText();
			League leagues = getUtil.getLeagueInfo(info);

			try {
				JdbcUtil.update(leagues);
			} catch (Exception e) {
				e.printStackTrace();
			}

			List<Team> teams = leagues.getTeams();
			
			System.out.println(new Gson().toJson(teams));
			// JdbcUtil.updateTeam(teams);
		}

	}

	public static void main(String[] args) throws JMException {
		// Spider spider = Spider.create(new TestPageProcessor()).addUrl("http://zq.win007.com").thread(10);
		Spider spider = Spider.create(new TestPageProcessor()).addUrl("http://zq.win007.com/jsData/matchResult/2015-2016/s36.js").thread(1);
		SpiderMonitor.instance().register(spider);
		spider.start();
	}

}
