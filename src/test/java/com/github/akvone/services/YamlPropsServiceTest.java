package com.github.akvone.services;

import static org.junit.Assert.*;

import com.github.akvone.properties.PropertiesHolder;
import org.junit.Test;

public class YamlPropsServiceTest {

  private PropertiesHolder propsHolder;

  @Test
  public void windowsDefaultTest() {
    initializePropertiesHolder("Windows");

    assertCommonDefaults();
    assertDockerHost("tcp://127.0.0.1:2375");
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