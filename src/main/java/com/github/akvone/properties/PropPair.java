package com.github.akvone.properties;

public class PropPair {
  private final String yamlFile;
  private final boolean required;

  public PropPair(String yamlFile, boolean required) {
    this.yamlFile = yamlFile;
    this.required = required;
  }

  public static PropPair of(String yamlFile, boolean required){
    return new PropPair(yamlFile, required);
  }

  public String getYamlFile() {
    return yamlFile;
  }

  public boolean isRequired() {
    return required;
  }
}
