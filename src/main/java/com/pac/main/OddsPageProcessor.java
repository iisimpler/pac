package com.pac.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.JMException;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import com.google.gson.Gson;
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

	private Site site = Site.me().setCharset("GBK").setRetryTimes(3).setSleepTime(100).setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:46.0) Gecko/20100101 Firefox/46.0");

	private GetUtil getUtil = new GetUtil();

	private static List<OddsMap> oddsMapsWithPage;

	private int pageIndex = 1;

	private int pageSize = 2;

	private int flag = pageSize;

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {

		flag--;
		
		ServiceUtil.updatePageUrl(new PageUrl(page.getUrl().toString(), "OK"));

		String info = page.getRawText();

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
		}

		System.out.println(new Gson().toJson(oddsList));

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

	public void startOdds() throws JMException {
		MySpider mySpider = MySpider.create(new OddsPageProcessor()).setDownloader(new MyDownloader()).thread(10);

		oddsMapsWithPage = JdbcUtil.getOddsMapsWithPage(pageIndex, pageSize);
		for (OddsMap oddsMap : oddsMapsWithPage) {
			mySpider.addUrl("http://op.win007.com/OddsHistory.aspx?id=" + oddsMap.getId());
			ServiceUtil.updatePageUrl(new PageUrl("http://op.win007.com/OddsHistory.aspx?id=" + oddsMap.getId(), "new"));
		}

		mySpider.start();
	}

}
