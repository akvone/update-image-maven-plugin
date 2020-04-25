package com.github.akvone.services;

import static org.junit.jupiter.api.Assertions.*;

import com.github.akvone.properties.PropertiesHolder;
import org.junit.jupiter.api.Test;

public class YamlPropsServiceTest {

  private PropertiesHolder propsHolder;

  @Test
  public void windowsDefaultTest() {
    initializePropertiesHolder("Windows");

    assertCommonDefaults();
    assertDockerHost("npipe:////./pipe/docker_engine");
  }

  @Test
  public void linuxDefaultTest() {
    initializePropertiesHolder("Linux");

    assertCommonDefaults();
    assertDockerHost("unix:///var/run/docker.sock");
  }

  private void assertCommonDefaults() {
    assertEquals("amazon", propsHolder.get("cloudProvider", "type"));
    assertEquals("localhost:8080", propsHolder.get("artifactory", "url"));
  }


  private void assertDockerHost(final String expected) {
    assertEquals(expected, propsHolder.get("docker", "host"));
  }

  private void initializePropertiesHolder(String os) {
    propsHolder = new YamlPropsService() {
      @Override
      String getOSSupportedName() {
        return os;
      }
    }.createPropertiesHolder();
  }

}