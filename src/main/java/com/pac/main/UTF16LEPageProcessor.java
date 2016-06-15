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
import com.pac.model.OddsMap;
import com.pac.model.PageUrl;
import com.pac.util.GetUtil;
import com.pac.util.JdbcUtil;
import com.pac.util.ServiceUtil;

public class UTF16LEPageProcessor implements PageProcessor {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	private Site site = Site.me().setCharset("UTF-16LE").setCycleRetryTimes(3).setRetryTimes(3).setSleepTime(100);

	private GetUtil getUtil = new GetUtil();

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {

		String info = page.getRawText();

		// 获取某一比赛和赔率的对应关系
		List<OddsMap> oddsMaps = getUtil.getOddsMaps(info);
		if (oddsMaps.size()==0) {
			ServiceUtil.updatePageUrl(new PageUrl(page.getUrl().toString(), "解析失败"));
			logger.error("解析失败-"+page.getUrl());
			return;
		}
		ServiceUtil.updatePageUrl(new PageUrl(page.getUrl().toString(), "OK"));
		for (OddsMap oddsMap : oddsMaps) {
			ServiceUtil.updateOddsMap(oddsMap);
		}
	}

	// public void startAll() throws JMException {
	public static void main(String[] args) throws JMException {
		// 有的赔率映射页面是UTF-16LE编码 需要单独抓取
		MySpider oddsMapSpider = MySpider.create(new UTF16LEPageProcessor()).setDownloader(new MyDownloader()).thread(10);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", "error:No match found");
		List<PageUrl> pageUrls = JdbcUtil.select(new PageUrl(), map);

		for (PageUrl pageUrl : pageUrls) {
			oddsMapSpider.addUrl(pageUrl.getName());
		}

		oddsMapSpider.start();
	}
}
