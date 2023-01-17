package com.vi.tenantservice.api.config;

import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheManagerConfig {

  public static final String APPLICATION_SETTINGS_CACHE = "applicationSettingsCache";

  @Value("${cache.applicationsettings.configuration.maxEntriesLocalHeap}")
  private long applicationSettingsMaxEntriesLocalHeap;

  @Value("${cache.applicationsettings.configuration.eternal}")
  private boolean applicationSettingsEternal;

  @Value("${cache.applicationsettings.configuration.timeToIdleSeconds}")
  private long applicationSettingsTimeToIdleSeconds;

  @Value("${cache.applicationsettings.configuration.timeToLiveSeconds}")
  private long applicationSettingsTimeToLiveSeconds;

  @Bean
  public CacheManager cacheManager() {
    return new EhCacheCacheManager(ehCacheManager());
  }

  @Bean(destroyMethod = "shutdown")
  public net.sf.ehcache.CacheManager ehCacheManager() {
    var config = new net.sf.ehcache.config.Configuration();
    config.addCache(buildApplicationSettingsCacheConfiguration());
    return net.sf.ehcache.CacheManager.newInstance(config);
  }

  private CacheConfiguration buildApplicationSettingsCacheConfiguration() {
    var topicCacheConfiguration = new CacheConfiguration();
    topicCacheConfiguration.setName(APPLICATION_SETTINGS_CACHE);
    topicCacheConfiguration.setMaxEntriesLocalHeap(applicationSettingsMaxEntriesLocalHeap);
    topicCacheConfiguration.setEternal(applicationSettingsEternal);
    topicCacheConfiguration.setTimeToIdleSeconds(applicationSettingsTimeToIdleSeconds);
    topicCacheConfiguration.setTimeToLiveSeconds(applicationSettingsTimeToLiveSeconds);
    return topicCacheConfiguration;
  }
}
