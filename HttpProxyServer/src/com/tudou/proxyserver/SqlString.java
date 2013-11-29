package com.tudou.proxyserver;

public class SqlString {
	public static final String SelectRadomProxy="select proxy from http_proxy order by rand() limit 1",
			SelectRadomProxyByCity="select proxy from http_proxy where location=? order by rand() limit 1",
			SelectDistinctCities="select distinct location from http_proxy where location is not null group by location";
}
