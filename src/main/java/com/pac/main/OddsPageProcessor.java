package com.pac.main;

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

//	private Site site = Site.me().setCharset("GBK").setRetryTimes(3).setSleepTime(3000).setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:46.0) Gecko/20100101 Firefox/46.0");
	private Site site = Site.me().setCharset("GBK").setRetryTimes(3).setSleepTime(500).setUserAgent("Baiduspider");

	private GetUtil getUtil = new GetUtil();

	private static Logger logger = LoggerFactory.getLogger(StartPageProcessor.class);

	private static List<OddsMap> oddsMapsWithPage;

	private int pageIndex = 1;

	private int pageSize = 10;

	private int flag = pageSize;
	
	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {
		
		flag--;

		String info = page.getRawText();
		String urlStr = page.getUrl().toString();

		try {
			if (info.contains("访问频率超出限制。")) {
				ServiceUtil.updatePageUrl(new PageUrl(urlStr, "频率限制，未抓取"));
				OddsPageProcessor.logger.error("访问频率超出限制。将线程休眠10min-" + page.getUrl());
				Thread.sleep(10 * 60 * 1000);
				return;
			}
			List<Odds> oddsList = getUtil.getOddsList(info);
			if (oddsList.size() == 0) {
				ServiceUtil.updatePageUrl(new PageUrl(urlStr, "无赔率数据"));
				OddsPageProcessor.logger.warn("无赔率数据。" + page.getUrl());
				return;
			}
			if (oddsList.size()<10) {
				OddsPageProcessor.logger.error("赔率列表数据较少。将线程休眠1s-" + page.getUrl());
				Thread.sleep(1000);
			}
			ServiceUtil.updatePageUrl(new PageUrl(urlStr, "OK"));

			Integer matchId = Integer.valueOf(GetUtil.getText(urlStr, 2, "=", 2, "&"));
			Integer companyId = Integer.valueOf(GetUtil.getText(urlStr, 3, "=", 3, "&"));
			Integer yearMatch = Integer.valueOf(GetUtil.getText(urlStr, 4, "=", 4, "&"));
			Integer mouthMatch = Integer.valueOf(urlStr.substring(GetUtil.getIndex(urlStr, 5, "=")+1));
			
			
			for (Odds odds : oddsList) {
				Integer mouthOdds = Integer.valueOf(odds.getTime().substring(5, 6));

				if (mouthOdds > mouthMatch) {
					odds.setTime(odds.getTime().replace("0000", yearMatch + ""));
				} else {
					odds.setTime(odds.getTime().replace("0000", yearMatch - 1 + ""));
				}
				odds.setCompanyId(companyId);
				odds.setMatchId(matchId);
				
				ServiceUtil.updateOdds(odds);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (flag == 0) {
				flag = pageSize;
				pageIndex++;
				getOddsMaps(null,page,pageIndex,pageSize);
			}
		}
	}

	// public void startOdds() throws JMException {
	public static void main(String[] args) throws JMException {

		MySpider mySpider = MySpider.create(new OddsPageProcessor()).setDownloader(new MyDownloader()).thread(2);

		getOddsMaps(mySpider,null,1,10);

		mySpider.run();
	}

	private static void getOddsMaps(MySpider mySpider,Page page,int pageIndex,int pageSize) {
		oddsMapsWithPage = JdbcUtil.getOddsMapsWithPage(pageIndex, pageSize);
		for (OddsMap oddsMap : oddsMapsWithPage) {
			
			Integer matchId = oddsMap.getMatchId();

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", matchId);
			String matchTime = JdbcUtil.select(new Match(), map).get(0).getTime();
			Integer yearMatch = Integer.valueOf(matchTime.substring(0, 4));
			Integer mouthMatch = Integer.valueOf(matchTime.substring(5, 7));
			
			if (mySpider!=null&&page==null) {
				mySpider.addUrl("http://op.win007.com/OddsHistory.aspx?id=" + oddsMap.getId()+"&matchId="+matchId+"&companyId="+oddsMap.getCompanyId()+"&yearMatch="+yearMatch+"&mouthMatch="+mouthMatch);
			}
			if (mySpider==null&&page!=null) {
				page.addTargetRequest("http://op.win007.com/OddsHistory.aspx?id=" + oddsMap.getId()+"&matchId="+matchId+"&companyId="+oddsMap.getCompanyId()+"&yearMatch="+yearMatch+"&mouthMatch="+mouthMatch);
			}
			ServiceUtil.updatePageUrl(new PageUrl("http://op.win007.com/OddsHistory.aspx?id=" + oddsMap.getId()+"&matchId="+matchId+"&companyId="+oddsMap.getCompanyId()+"&yearMatch="+yearMatch+"&mouthMatch="+mouthMatch, "new"));
		}
	}

}
