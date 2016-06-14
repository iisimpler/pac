package com.pac.main;

import java.util.List;

import javax.management.JMException;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import com.pac.extend.MyDownloader;
import com.pac.model.Company;
import com.pac.model.PageUrl;
import com.pac.util.GetUtil;
import com.pac.util.ServiceUtil;

public class StartPageProcessor implements PageProcessor {

	private Site site = Site.me().setCharset("GBK").setRetryTimes(3).setSleepTime(1000);

	private GetUtil getUtil = new GetUtil();

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {
		
		ServiceUtil.updatePageUrl(new PageUrl(page.getUrl().toString(), "OK"));

		String info = page.getRawText();

		List<Company> companies = getUtil.getCompanies(info);
		companies.forEach(t -> ServiceUtil.updateCompany(t));

	}

	public static void main(String[] args) throws JMException {
		// 博彩公司页面是GBK，所以先获取GBK，再启动其他
		Spider spider = Spider.create(new StartPageProcessor()).setDownloader(new MyDownloader()).addUrl("http://op.win007.com/companies.js").thread(1);
		
		ServiceUtil.updatePageUrl(new PageUrl("http://op.win007.com/companies.js", "new"));
		
		spider.run();

//		new UTF8PageProcessor().startAll();
	}

}
