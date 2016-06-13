package com.pac.main;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import com.google.gson.Gson;
import com.pac.extend.MyDownloader;
import com.pac.model.Odds;
import com.pac.util.GetUtil;

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
		System.out.println(new Gson().toJson(oddsList));

	}

	public static void main(String[] args) throws Exception {
		Spider.create(new TestPageProcessor()).setDownloader(new MyDownloader()).addUrl("http://op.win007.com/OddsHistory.aspx?id=57208268").thread(1).run();
	}

}
