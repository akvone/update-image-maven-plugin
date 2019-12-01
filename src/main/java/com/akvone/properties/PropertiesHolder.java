package com.akvone.properties;

import java.util.HashMap;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class PropertiesHolder {

  private final CombinedConfiguration configuration;

  private PropertiesHolder(CombinedConfiguration configuration) {
    this.configuration = configuration;
    configuration.setThrowExceptionOnMissing(true);
  }

  public static PropertiesHolder create(String defaultYamlFile, String userYamlFileRoot, String userYamlFileSpecific) {
    Configurations configurations = new Configurations();
    Configuration defaultConfig = createConfig(configurations, defaultYamlFile, true);
    Configuration userConfigRoot = createConfig(configurations, userYamlFileRoot, false);
    Configuration userConfigSpecific = createConfig(configurations, userYamlFileSpecific, false);
    CombinedConfiguration config = new CombinedConfiguration();
    // Order from most to least preferable
    config.addConfiguration(userConfigRoot);
    config.addConfiguration(userConfigSpecific);
    config.addConfiguration(defaultConfig);

    return new PropertiesHolder(config);
  }

  private static Configuration createConfig(Configurations configurations, String yamlFile, boolean required) {
    try {
      System.out.println("Try to read file " + yamlFile);
      return configurations.fileBased(YAMLConfiguration.class, yamlFile);
    } catch (ConfigurationException e) {
      System.err.println("Some problems with file " + yamlFile);
      if (required) {
        throw new IllegalStateException("Can't create PropertiesHolder.", e);
      } else {
        System.err.println("Try to proceed without it");
        return new MapConfiguration(new HashMap<>()); // TODO: create empty configuration
      }
    }
  }

  public String get(String prefix, String key) {
    return configuration.getString(String.format("%s.%s", prefix, key));
  }
}
