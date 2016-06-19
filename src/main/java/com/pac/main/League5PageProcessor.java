package com.pac.main;

import java.util.List;

import javax.management.JMException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import com.pac.extend.MyDownloader;
import com.pac.extend.MySpider;
import com.pac.model.Odds;
import com.pac.model.OddsMap;
import com.pac.model.PageUrl;
import com.pac.util.GetUtil;
import com.pac.util.JdbcUtil;
import com.pac.util.ServiceUtil;

public class League5PageProcessor implements PageProcessor {

	// private Site site = Site.me().setCharset("GBK").setRetryTimes(3).setSleepTime(3000).setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:46.0) Gecko/20100101 Firefox/46.0");
	private Site site = Site.me().setCharset("UTF-8").setRetryTimes(3).setSleepTime(10).setUserAgent("Baiduspider");

	private Logger logger = LoggerFactory.getLogger(getClass());

	private GetUtil getUtil = new GetUtil();

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {
		
		List<Odds> oddsList = getUtil.getOddsListFromMobile(page);

		if (oddsList.size() == 0) {
			ServiceUtil.updatePageUrl(new PageUrl(page.getUrl().toString(), "无赔率数据"));
			return;
		}
		ServiceUtil.updatePageUrl(new PageUrl(page.getUrl().toString(), "OK"));
		oddsList.forEach(odds -> ServiceUtil.updateOdds(odds));

	}

	// public void startOdds() throws JMException {
	public static void main(String[] args) throws JMException {

		MySpider mySpider = MySpider.create(new League5PageProcessor()).setDownloader(new MyDownloader()).thread(50);

		List<OddsMap> oddsMapsWithPage = JdbcUtil.getOddsMaps5League();
		for (OddsMap oddsMap : oddsMapsWithPage) {

			Integer matchId = oddsMap.getMatchId();
			Integer companyId = oddsMap.getCompanyId();

			mySpider.addUrl("http://m.win007.com/1x2Detail.aspx?scheid=" + matchId + "&cId=" + companyId);
			ServiceUtil.updatePageUrl(new PageUrl("http://m.win007.com/1x2Detail.aspx?scheid=" + matchId + "&cId=" + companyId, "new"));
		}

		mySpider.start();
	}
}
