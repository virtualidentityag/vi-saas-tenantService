package com.vi.tenantservice.api.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheManagerConfig {

  //  public static final String APPLICATION_SETTINGS_CACHE = "applicationSettingsCache";
  //
  //  @Value("${cache.applicationsettings.configuration.maxEntriesLocalHeap}")
  //  private long applicationSettingsMaxEntriesLocalHeap;
  //
  //  @Value("${cache.applicationsettings.configuration.eternal}")
  //  private boolean applicationSettingsEternal;
  //
  //  @Value("${cache.applicationsettings.configuration.timeToIdleSeconds}")
  //  private long applicationSettingsTimeToIdleSeconds;
  //
  //  @Value("${cache.applicationsettings.configuration.timeToLiveSeconds}")
  //  private long applicationSettingsTimeToLiveSeconds;
  //
  //  @Bean
  //  public CacheManager cacheManager() {
  //    return new EhCacheCacheManager(ehCacheManager());
  //  }
  //
  //  @Bean(destroyMethod = "shutdown")
  //  public CacheManager ehCacheManager() {
  //    var config = new Configuration();
  //
  //    EhcacheManager manager = new EhcacheManager(buildApplicationSettingsCacheConfiguration());
  //
  //    config.addCache();
  //    return CacheManager.newInstance(config);
  //  }

  //  private Configuration buildApplicationSettingsCacheConfiguration() {
  //    var topicCacheConfiguration = new Configuration();
  //    topicCacheConfiguration.setName(APPLICATION_SETTINGS_CACHE);
  //    topicCacheConfiguration.setMaxEntriesLocalHeap(applicationSettingsMaxEntriesLocalHeap);
  //    topicCacheConfiguration.setEternal(applicationSettingsEternal);
  //    topicCacheConfiguration.setTimeToIdleSeconds(applicationSettingsTimeToIdleSeconds);
  //    topicCacheConfiguration.setTimeToLiveSeconds(applicationSettingsTimeToLiveSeconds);
  //    return topicCacheConfiguration;
  //  }
}
