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

public class TestPageProcessor implements PageProcessor {

	// private Site site = Site.me().setCharset("GBK").setRetryTimes(3).setSleepTime(3000).setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:46.0) Gecko/20100101 Firefox/46.0");
	private Site site = Site.me().setCharset("UTF-8").setRetryTimes(3).setSleepTime(10).setUserAgent("Baiduspider");

	private Logger logger = LoggerFactory.getLogger(getClass());

	private static List<OddsMap> oddsMapsWithPage;

	private GetUtil getUtil = new GetUtil();

	private int pageIndex = 1;

	private int pageSize = 10000;

	private int flag = pageSize;

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {

		flag--;
		try {
			List<Odds> oddsList = getUtil.getOddsListFromMobile(page);

			if (oddsList.size() == 0) {
				ServiceUtil.updatePageUrl(new PageUrl(page.getUrl().toString(), "无赔率数据"));
				return;
			}
			ServiceUtil.updatePageUrl(new PageUrl(page.getUrl().toString(), "OK"));
			oddsList.forEach(odds -> ServiceUtil.updateOdds(odds));

		} finally {
			if (flag < 5000) {
				flag = pageSize+flag;
				pageIndex++;
				getOddsMaps(null, page, pageIndex, pageSize);
			}
		}

	}

	// public void startOdds() throws JMException {
	public static void main(String[] args) throws JMException {

		MySpider mySpider = MySpider.create(new TestPageProcessor()).setDownloader(new MyDownloader()).thread(50);

		getOddsMaps(mySpider, null, 1, 10000);

		mySpider.run();
	}

	private static void getOddsMaps(MySpider mySpider, Page page, int pageIndex, int pageSize) {
		oddsMapsWithPage = JdbcUtil.getOddsMapsWithPage(pageIndex, pageSize);
		for (OddsMap oddsMap : oddsMapsWithPage) {

			Integer matchId = oddsMap.getMatchId();
			Integer companyId = oddsMap.getCompanyId();

			if (mySpider != null && page == null) {
				mySpider.addUrl("http://m.win007.com/1x2Detail.aspx?scheid=" + matchId + "&cId=" + companyId);
			}
			if (mySpider == null && page != null) {
				page.addTargetRequest("http://m.win007.com/1x2Detail.aspx?scheid=" + matchId + "&cId=" + companyId);
			}
			ServiceUtil.updatePageUrl(new PageUrl("http://m.win007.com/1x2Detail.aspx?scheid=" + matchId + "&cId=" + companyId, "new"));
		}
	}
}
