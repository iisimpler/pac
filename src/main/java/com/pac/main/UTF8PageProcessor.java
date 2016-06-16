package com.pac.main;

import java.util.ArrayList;
import java.util.List;

import javax.management.JMException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import com.pac.extend.MyDownloader;
import com.pac.extend.MySpider;
import com.pac.model.Country;
import com.pac.model.League;
import com.pac.model.Match;
import com.pac.model.OddsMap;
import com.pac.model.PageUrl;
import com.pac.model.Team;
import com.pac.util.GetUtil;
import com.pac.util.ServiceUtil;

public class UTF8PageProcessor implements PageProcessor {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

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
		
		// 确定当前页面有相应数据
		if (page.getStatusCode()!=200) {
			ServiceUtil.updatePageUrl(new PageUrl(page.getUrl().toString(),page.getStatusCode()+""));
			return;
		}
		
		if (info.contains("对不起！你查看的页面不存在")) {
			ServiceUtil.updatePageUrl(new PageUrl(page.getUrl().toString(), "页面不存在"));
			return;
		}
		
		ServiceUtil.updatePageUrl(new PageUrl(page.getUrl().toString(), "OK"));
		
		
		// 简体国家页面
		if (page.getUrl().toString().equals("http://zq.win007.com/jsData/infoHeader.js")) {
			countries = getUtil.getCountryInfos(info);
			// 请求队列加入繁体国家页面链接
			page.addTargetRequest("http://zq.win007.com/jsData/infoHeaderFn.js");
			
			ServiceUtil.updatePageUrl(new PageUrl("http://zq.win007.com/jsData/infoHeaderFn.js", "new"));
		}
		// 繁体国家页面
		if (page.getUrl().toString().equals("http://zq.win007.com/jsData/infoHeaderFn.js")) {
			countriesTra = getUtil.getCountryInfos(info);

			// 将国家信息和国家内包含的联赛信息插入数据库
			for (Country country : countries) {
				for (Country countryTra : countriesTra) {
					if (country.getId() == countryTra.getId()) {
						country.setNameTra(countryTra.getName());
						ServiceUtil.updateCountry(country);
					}
				}
//				if (country.getId()!=1) {
//					return;
//				}
				List<League> leagues = country.getLeagues();
				
				for (League league : leagues) {
//					if (league.getId()!=36) {
//						return;
//					}
					if (league.getId()==922) {//922是土乙女，数据不准确，排除
						return;
					}
					ServiceUtil.updateLeague(league);
					// 无子联赛的联赛
					if (league.getHasSub().equals("无") && league.getLeagueType().equals("联赛")) {
						int id = league.getId();
						String[] season = league.getSeason().split(",");
						for (int i = 0; i < season.length; i++) {
//							if (!season[i].equals("2015-2016")) {
//								continue;
//							}
							page.addTargetRequest("http://zq.win007.com/jsData/matchResult/" + season[i] + "/s" + id + ".js");
							
							ServiceUtil.updatePageUrl(new PageUrl("http://zq.win007.com/jsData/matchResult/" + season[i] + "/s" + id + ".js", "new"));
						}
					}
				}
			}
		}

		// 无子联赛的联赛
		if (page.getUrl().toString().contains("http://zq.win007.com/jsData/matchResult/")) {

			// 更新联赛的信息
			League league = getUtil.getLeagueInfo(info);
			ServiceUtil.updateLeague(league);
			List<Team> teams = league.getTeams();
			for (Team team : teams) {
				ServiceUtil.updateTeam(team);
			}
			
			// 获取联赛里的比赛
			List<Match> matchs = getUtil.getMaths(info);
			
			for (Match match : matchs) {
				if (match.getRound()!=1) {
					return;
				}
				ServiceUtil.updateMatch(match);
				page.addTargetRequest("http://1x2.nowscore.com/"+match.getId()+".js");
				ServiceUtil.updatePageUrl(new PageUrl("http://1x2.nowscore.com/"+match.getId()+".js", "new"));
			}
		}
		
		//获取某一比赛和赔率的对应关系
		if (page.getUrl().toString().contains("http://1x2.nowscore.com/")) {
			
			List<OddsMap> oddsMaps = getUtil.getOddsMaps(info);
			for (OddsMap oddsMap : oddsMaps) {
				ServiceUtil.updateOddsMap(oddsMap);
			}
		}
	}

	public void startAll() throws JMException {
		Logger logger = LoggerFactory.getLogger(StartPageProcessor.class);

		// 从简体中文国家页面抓起
		MySpider countrySpider = MySpider.create(new UTF8PageProcessor()).setDownloader(new MyDownloader()).addUrl("http://zq.win007.com/jsData/infoHeader.js").thread(10);
		ServiceUtil.updatePageUrl(new PageUrl("http://zq.win007.com/jsData/infoHeader.js", "new"));
		countrySpider.start();
		

		logger.info("==================================赔率抓取开始==================================");
	}

}
