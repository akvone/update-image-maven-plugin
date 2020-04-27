package com.github.akvone.properties;

import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

@Slf4j
public class PropertiesHolder {

  private final CombinedConfiguration configuration;

  private PropertiesHolder(CombinedConfiguration configuration) {
    this.configuration = configuration;
    configuration.setThrowExceptionOnMissing(true);
  }


  /**
   * @param prioritizedListOfYamlLocations ordered from greatest to smallest priority
   * @return Fully constructed {@link PropertiesHolder}
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
      log.info("Read file {}", yamlFile);
      return configurations.fileBased(YAMLConfiguration.class, yamlFile);
    } catch (ConfigurationException e) {
      if (required) {
        log.warn("Some problems with file {}", yamlFile);
        throw new IllegalStateException("Can't create PropertiesHolder.", e);
      } else {
        log.info("Some problems with optional config file {}. Try to proceed without it", yamlFile);
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
