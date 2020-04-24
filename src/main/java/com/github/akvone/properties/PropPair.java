package com.github.akvone.properties;

import lombok.Data;

@Data
public class PropPair {

  private final String yamlFile;
  private final boolean required;

  public static PropPair of(String yamlFile, boolean required) {
    return new PropPair(yamlFile, required);
  }
}
