package com.pac.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.JMException;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import com.pac.model.Company;
import com.pac.model.PageUrl;
import com.pac.util.GetUtil;
import com.pac.util.JdbcUtil;

public class StartPageProcessor implements PageProcessor {

	private Site site = Site.me().setCharset("GBK").setRetryTimes(3).setSleepTime(1000);

	private GetUtil getUtil = new GetUtil();

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {

		// 确定当前 page 页面没有被爬过
		PageUrl pageUrl = new PageUrl();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", page.getUrl().toString());
		if (JdbcUtil.select(pageUrl, map).size() != 0) {
			return;
		}

		String info = page.getRawText();

		List<Company> companies = getUtil.getCompanies(info);
		companies.forEach(t -> JdbcUtil.update(t));

	}

	public static void main(String[] args) throws JMException {
		// 博彩公司页面是GBK，所以先获取GBK，再启动其他
		Spider.create(new StartPageProcessor()).addUrl("http://op.win007.com/companies.js").thread(1).run();

		new AllPageProcessor().startAll();
	}

}
