package com.pac.main;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import com.google.gson.Gson;
import com.pac.extend.MyDownloader;
import com.pac.model.Odds;
import com.pac.model.OddsMap;
import com.pac.util.GetUtil;
import com.pac.util.JdbcUtil;

public class TestPageProcessor implements PageProcessor {

	private Site site = Site.me().setRetryTimes(3).setCharset("GBK").setSleepTime(1000)
			.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");

	private GetUtil getUtil = new GetUtil();

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {

		String info = page.getRawText();

		List<Odds> oddsList = getUtil.getOddsList(info);
		
		System.out.println(new Gson().toJson(oddsList));

	}

	public static void main(String[] args) throws Exception {
		
		List<OddsMap> oddsMapsWithPage = JdbcUtil.getOddsMapsWithPage(1, 2);
		
		Spider spider = Spider.create(new StartPageProcessor()).setDownloader(new MyDownloader()).thread(1);
		
		for (OddsMap oddsMap : oddsMapsWithPage) {
			spider.addUrl("http://op.win007.com/OddsHistory.aspx?id="+oddsMap.getId());
		}

		spider.run();
		
		
		
//		DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//		LocalDateTime dt = LocalDateTime.parse("2016-06-14 18:20:10", f);
//		String str = dt.format(f);
//
//		Odds odds = new Odds();
//		odds.setTime("0000-06-14 18:20:10");
//		JdbcUtil.insert(odds);
		
	}

}
