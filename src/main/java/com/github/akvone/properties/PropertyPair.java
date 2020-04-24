package com.github.akvone.properties;

import lombok.Data;

@Data
public class PropertyPair {

  private final String yamlFile;
  private final boolean required;

  public static PropertyPair of(String yamlFile, boolean required) {
    return new PropertyPair(yamlFile, required);
  }
}
