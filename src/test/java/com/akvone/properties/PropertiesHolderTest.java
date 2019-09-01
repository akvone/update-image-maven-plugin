package com.akvone.properties;

import static org.junit.Assert.*;

import org.junit.Test;

public class PropertiesHolderTest {

  @Test
  public void test() {
    PropertiesHolder propertiesHolder = PropertiesHolder.create("config/default.yaml", "config/user.yaml");

    assertEquals("openshift", propertiesHolder.get("cloudProvider", "type"));
  }
}