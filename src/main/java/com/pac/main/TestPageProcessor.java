package com.pac.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import com.google.gson.Gson;
import com.pac.extend.MyDownloader;
import com.pac.model.Match;
import com.pac.model.Odds;
import com.pac.model.OddsMap;
import com.pac.util.GetUtil;
import com.pac.util.JdbcUtil;
import com.pac.util.ServiceUtil;

public class TestPageProcessor implements PageProcessor {

	private Site site = Site.me().setCharset("GBK").setRetryTimes(3).setSleepTime(1000).setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:46.0) Gecko/20100101 Firefox/46.0");

	private GetUtil getUtil = new GetUtil();

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {
		
		String info = page.getRawText();
		
		List<Odds> oddsList = getUtil.getOddsList(info);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", 1130596);
		int year = JdbcUtil.select(new Match(), map).get(0).getTime().getYear();

		for (Odds odds : oddsList) {
//			odds.getTime().setYear(year);
			ServiceUtil.updateOdds(odds);
		}
		
		System.out.println(new Gson().toJson(oddsList));

	}

	public static void main(String[] args) throws Exception {
		Spider spider = Spider.create(new TestPageProcessor()).setDownloader(new MyDownloader()).thread(1);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("matchId", 1130596);
		map.put("companyId", "115");
		
		Integer oddsId = JdbcUtil.select(new OddsMap(), map).get(0).getId();
		spider.addUrl("http://op.win007.com/OddsHistory.aspx?id="+oddsId);
		
		spider.run();
	}

}
