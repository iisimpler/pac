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
import com.pac.model.Handicap;
import com.pac.model.Match;
import com.pac.model.OverUnder;
import com.pac.model.PageUrl;
import com.pac.model.X1x2;
import com.pac.util.GetUtil;
import com.pac.util.JdbcUtil;
import com.pac.util.ServiceUtil;

public class ZoudiPageProcessor implements PageProcessor {

	// private Site site = Site.me().setCharset("GBK").setRetryTimes(3).setSleepTime(3000).setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:46.0) Gecko/20100101 Firefox/46.0");
	private Site site = Site.me().setCharset("GBK").setRetryTimes(3).setSleepTime(1000).setUserAgent("Baiduspider");

	private Logger logger = LoggerFactory.getLogger(getClass());

	private GetUtil getUtil = new GetUtil();

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {
		List<Handicap> handicaps = getUtil.getHandicaps(page);
		if (handicaps.size()==0) {
			ServiceUtil.updatePageUrl(new PageUrl(page.getUrl().toString(),"无走地让球数据"));
			return;
		}
		ServiceUtil.updatePageUrl(new PageUrl(page.getUrl().toString(),"OK"));
		handicaps.forEach(handicap->ServiceUtil.updateHandicap(handicap));
		
		List<OverUnder> overUnders = getUtil.getOverUnders(page);
		if (overUnders.size()==0) {
			ServiceUtil.updatePageUrl(new PageUrl(page.getUrl().toString(),"无走地大小球数据"));
			return;
		}
		ServiceUtil.updatePageUrl(new PageUrl(page.getUrl().toString(),"OK"));
		overUnders.forEach(overUnder->ServiceUtil.updateOverUnder(overUnder));
		
		List<X1x2> x1x2s = getUtil.getX1x2s(page);
		if (x1x2s.size()==0) {
			ServiceUtil.updatePageUrl(new PageUrl(page.getUrl().toString(),"无走地欧赔数据"));
			return;
		}
		ServiceUtil.updatePageUrl(new PageUrl(page.getUrl().toString(),"OK"));
		x1x2s.forEach(x1x2->ServiceUtil.updateX1x2(x1x2));;
	}

	// public void startOdds() throws JMException {
	public static void main(String[] args) throws JMException {

		MySpider mySpider = MySpider.create(new ZoudiPageProcessor()).setDownloader(new MyDownloader()).thread(5);

		List<Match> matchs = JdbcUtil.getMatchsForZoudi();
		
		for (Match match : matchs) {
			Integer matchId = match.getId();
			mySpider.addUrl("http://vip.win007.com/changeDetail/3in1Odds.aspx?id=" + matchId + "&companyid=3");
			ServiceUtil.updatePageUrl(new PageUrl("http://vip.win007.com/changeDetail/3in1Odds.aspx?id=" + matchId + "&companyid=3", "new"));
		}

		mySpider.start();
	}
}
