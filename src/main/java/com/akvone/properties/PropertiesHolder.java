package com.akvone.properties;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class PropertiesHolder {

  private final CombinedConfiguration configuration;

  private PropertiesHolder(CombinedConfiguration configuration) {
    this.configuration = configuration;
    configuration.setThrowExceptionOnMissing(true);
  }

  public static PropertiesHolder create(String defaultYamlFile, String userYamlFile) {
    try {
      Configurations configurations = new Configurations();
      YAMLConfiguration defaultConfig =
          configurations.fileBased(YAMLConfiguration.class, defaultYamlFile);
      YAMLConfiguration userConfig = configurations.fileBased(YAMLConfiguration.class, userYamlFile);
      CombinedConfiguration config = new CombinedConfiguration();
      config.addConfiguration(userConfig);
      config.addConfiguration(defaultConfig);

      return new PropertiesHolder(config);
    } catch (ConfigurationException e) {
      throw new IllegalStateException("Can't create PropertiesHolder", e);
    }
  }

  public String get(String prefix, String key) {
    return configuration.getString(String.format("%s.%s", prefix, key));
  }
}
