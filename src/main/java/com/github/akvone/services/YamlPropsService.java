package com.github.akvone.services;

import static com.github.akvone.properties.PropertyPair.of;

import com.github.akvone.properties.PropertyPair;
import com.github.akvone.properties.PropertiesHolder;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.SystemUtils;

public class YamlPropsService {

  private static final String USER_FILE_LOCATION_SPECIFIC = "gitignore/config.yaml";
  private static final String USER_FILE_LOCATION_ROOT = "../config.yaml";
  private static final String OS_DEFAULT_FILE_LOCATION_TEMPLATE = "config/OS/%s-default.yaml";
  private static final String DEFAULT_FILE_LOCATION = "config/default.yaml";

  public PropertiesHolder createPropertiesHolder() {
    List<PropertyPair> prioritizedListOfYamlLocations = Arrays.asList(
        of(USER_FILE_LOCATION_SPECIFIC, false),
        of(USER_FILE_LOCATION_ROOT, false),
        of(buildOSDefaultYamlFileLocation(), true),
        of(DEFAULT_FILE_LOCATION, true)
    );

    return PropertiesHolder.create(prioritizedListOfYamlLocations);
  }

  private String buildOSDefaultYamlFileLocation() {
    String os = getOSSupportedName();

    return String.format(OS_DEFAULT_FILE_LOCATION_TEMPLATE, os);
  }

  /**
   * Currently only Windows and Linux are supported (See config/OS folder)
   */
  String getOSSupportedName() {
    return SystemUtils.IS_OS_WINDOWS ? "Windows" : "Linux";
  }
}
