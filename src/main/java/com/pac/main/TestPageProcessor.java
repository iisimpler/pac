package com.pac.main;

import java.util.List;

import javax.management.JMException;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import com.google.gson.Gson;
import com.pac.model.BetMap;
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

		String info = page.getRawText();
		
		System.out.println(page.getStatusCode());
		
		List<BetMap> betMaps = getUtil.getBetMaps(info);
		System.out.println(new Gson().toJson(betMaps));
		for (BetMap betMap : betMaps) {
			JdbcUtil.update(betMap);
		}

	}

	public static void main(String[] args) throws JMException {
		Spider.create(new TestPageProcessor()).addUrl("http://1x2.nowscore.com/1130596.js").thread(1).run();
	}

}
