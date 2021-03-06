package com.carlguo2016.smartutil.api;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.carlguo2016.smartutil.net.HttpUtil;

/**
 * 新浪生成短链接
 * 
 */
public class ShortURLUtil {

	private static final String SERVICE_URL = "https://api.weibo.com/2/short_url/shorten.json";
	private static final Logger LOGGER = Logger.getLogger(ShortURLUtil.class);
	private static final String AppKey = "4202790003";


	/**
	 * 新浪短链接
	 * 
	 * @param longUrl
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String shortenXinLang(String longUrl) {
		String shortUrl = null;

		if (longUrl != null && !StringUtils.isEmpty(longUrl)) {
			String param = "source=" + AppKey + "&url_long=" + longUrl;
			String response = null;
			try {
				response = HttpUtil.sendPost(SERVICE_URL, param);
				JSONObject backJson = JSONObject.parseObject(response);
				if (backJson.containsKey("urls")) {
					// 生成成功
					JSONArray urls = backJson.getJSONArray("urls");
					shortUrl = ((JSONObject) urls.get(0)).getString("url_short");
				} else {
					// 生成失败
					LOGGER.error("shorten error for longUrl=" + longUrl + " with response: " + response);
				}
			} catch (Exception e) {
				LOGGER.error("生成短链接失败", e);
			}
		}

		return shortUrl;
	}

}
