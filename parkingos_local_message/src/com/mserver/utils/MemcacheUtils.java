package com.mserver.utils;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;



/**
 * memcached���ߣ�������²�Ʒ��֧����������ѯ���ƺ��� 
 * @author Administrator
 *
 */
@SuppressWarnings("unchecked")
@Repository
public class MemcacheUtils {

	@Autowired
	private CacheXMemcache cacheXMemcache;
	


	/**
	 * @param д���շ�Ա����
	 * @return
	 */
	public  Long doParkerTokentimCache(String key,Long value,String updateFlag) {
		final Long value1 = value;
		return (Long) cacheXMemcache.doCachedTask(new CachedTask<Long>(key, updateFlag) {
			public Long run() {
				return value1;
			}
		});
	}
	

	/**
	 * @param д���շ�Աtoken
	 * @return
	 */

	public  Map<String,String > doParkUserTokenCache(String key,Map<String,String > value,String updateFlag) {
		final Map<String,String > value1 = value;
		return (Map<String,String >) cacheXMemcache.doCachedTask(new CachedTask<Map<String,String >>(key, updateFlag) {
			public Map<String,String > run() {
				return value1;
			}
		});
	}
	
	public   Map<Long ,String> doMapLongStringCache(String key,
			Map<Long ,String> map,String updateFlag) {
		final Map<Long ,String> map2 = map;
		return (Map<Long ,String>) cacheXMemcache.doCachedTask(new CachedTask<Map<Long ,String>>(key, updateFlag) {
			public Map<Long ,String> run() {
				return map2;
			}
		});
	}
}
