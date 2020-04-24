package com.github.akvone.properties;

import java.util.HashMap;
import java.util.List;
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

  /**
   * @param prioritizedListOfYamlLocations ordered from greatest to smallest priority
   */
  public static PropertiesHolder create(List<PropertyPair> prioritizedListOfYamlLocations) {
    Configurations confFactory = new Configurations();
    CombinedConfiguration combinedConfig = new CombinedConfiguration();

    prioritizedListOfYamlLocations.stream()
        .map(propPair -> createConfig(confFactory, propPair.getYamlFile(), propPair.isRequired()))
        .forEach(combinedConfig::addConfiguration);

    return new PropertiesHolder(combinedConfig);
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
        return createEmptyConfiguration();
      }
    }
  }

  private static MapConfiguration createEmptyConfiguration() {
    return new MapConfiguration(new HashMap<>());
  }

  public String get(String prefix, String key) {
    return configuration.getString(String.format("%s.%s", prefix, key));
  }
}
