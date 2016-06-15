package com.pac.main;

import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.JMException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import com.pac.extend.MyDownloader;
import com.pac.extend.MySpider;
import com.pac.model.Match;
import com.pac.model.Odds;
import com.pac.model.OddsMap;
import com.pac.model.PageUrl;
import com.pac.util.GetUtil;
import com.pac.util.JdbcUtil;
import com.pac.util.ServiceUtil;

public class OddsPageProcessor implements PageProcessor {

	private Site site = Site.me().setCharset("GBK").setRetryTimes(5).setSleepTime(500).setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:46.0) Gecko/20100101 Firefox/46.0");

	private GetUtil getUtil = new GetUtil();

	private static Logger logger = LoggerFactory.getLogger(StartPageProcessor.class);

	private static List<OddsMap> oddsMapsWithPage;

	private int pageIndex = 1;

	private int pageSize = 100;

	private int flag = pageSize;
	
	private long period = Clock.systemUTC().millis();//计时用
	

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {
		flag--;
		
		long periodTemp = Clock.systemUTC().millis();
		System.out.println(periodTemp-period);
		if (periodTemp-period<1000) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		String info = page.getRawText();

		if (info.contains("访问频率超出限制。")) {
			ServiceUtil.updatePageUrl(new PageUrl(page.getUrl().toString(), "频率限制，未抓取"));
			page.addTargetRequest(page.getUrl().toString());
			try {
				
				if (periodTemp-period<60*1000) {
					OddsPageProcessor.logger.error("访问频率超出限制。将线程休眠60s-" + page.getUrl().toString());
					Thread.sleep(10*60*1000);
				} else if (periodTemp-period<3*60*1000) {
					OddsPageProcessor.logger.error("访问频率超出限制。将线程休眠3min-" + page.getUrl().toString());
					Thread.sleep(5*60*1000);
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return;
		}
		ServiceUtil.updatePageUrl(new PageUrl(page.getUrl().toString(), "OK"));

		List<Odds> oddsList = getUtil.getOddsList(info);

		Integer oddsMapId = Integer.valueOf(page.getUrl().toString().substring(41));

		for (Odds odds : oddsList) {

			for (OddsMap oddsMap : oddsMapsWithPage) {

				if (oddsMapId.equals(oddsMap.getId())) {

					Integer matchId = oddsMap.getMatchId();

					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", matchId);

					String matchTime = JdbcUtil.select(new Match(), map).get(0).getTime();
					Integer yearMatch = Integer.valueOf(matchTime.substring(0, 4));
					Integer mouthMatch = Integer.valueOf(matchTime.substring(5, 6));

					Integer mouthOdds = Integer.valueOf(odds.getTime().substring(5, 6));

					if (mouthOdds > mouthMatch) {
						odds.setTime(odds.getTime().replace("0000", yearMatch + ""));
					} else {
						odds.setTime(odds.getTime().replace("0000", yearMatch - 1 + ""));
					}
					odds.setCompanyId(oddsMap.getCompanyId());
					odds.setMatchId(matchId);
				}
			}

			ServiceUtil.updateOdds(odds);
			period = Clock.systemUTC().millis();
			
		}

		if (flag == 0) {
			flag = pageSize;
			pageIndex++;
			oddsMapsWithPage = JdbcUtil.getOddsMapsWithPage(pageIndex, pageSize);
			for (OddsMap oddsMap : oddsMapsWithPage) {
				page.addTargetRequest("http://op.win007.com/OddsHistory.aspx?id=" + oddsMap.getId());
				ServiceUtil.updatePageUrl(new PageUrl("http://op.win007.com/OddsHistory.aspx?id=" + oddsMap.getId(), "new"));
			}
		}
	}

	// public void startOdds() throws JMException {
	public static void main(String[] args) throws JMException {

		MySpider mySpider = MySpider.create(new OddsPageProcessor()).setDownloader(new MyDownloader()).thread(3);

		oddsMapsWithPage = JdbcUtil.getOddsMapsWithPage(1, 100);
		for (OddsMap oddsMap : oddsMapsWithPage) {
			mySpider.addUrl("http://op.win007.com/OddsHistory.aspx?id=" + oddsMap.getId());
			ServiceUtil.updatePageUrl(new PageUrl("http://op.win007.com/OddsHistory.aspx?id=" + oddsMap.getId(), "new"));
		}

		mySpider.start();
	}

}
